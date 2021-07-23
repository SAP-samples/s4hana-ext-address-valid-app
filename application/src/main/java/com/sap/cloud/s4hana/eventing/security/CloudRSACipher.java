package com.sap.cloud.s4hana.eventing.security;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPException;
import com.sap.cloud.s4hana.eventing.core.exceptions.SAPSecurityException;
import com.sap.cloud.sdk.cloudplatform.connectivity.GenericDestination;

/**
 * Default cipher to be used in a productive environment (Cloud Foundry).
 * <p>
 * Please note that encryption is performed using pure RSA algorithm for the
 * sake of simplicity. It limits the maximum amount of encrypted data to roughly
 * 1/8 of the key size. E.g. if key size is 4096 bit then only 501 bytes can be
 * encrypted.
 * <p>
 * Normally you would use symmetric encryption algorithm (e.g. AES) to encrypt
 * the data and use RSA to encrypt and decrypt AES key.
 * 
 * @see LocalRSACipher
 */
@Service
public class CloudRSACipher implements RSACipher {
	
	/**
	 * Encryption algorithm
	 * 
	 */
	public static final String ALGORITHM = "RSA";
	
	/**
	 * SAP Business Technology Platform (BTP) destination to store RSA key pair
	 * 
	 * @see CloudRSACipher#PRIVATE_KEY_PROPERTY
	 * @see CloudRSACipher#PUBLIC_KEY_PROPERTY
	 */
	public static final String DEFAULT_DESTINATION_NAME = "RSAKeyPairStorage";
	
	/**
	 * SAP Business Technology Platform (BTP) destination property in which the public key is stored
	 *
	 * @see CloudRSACipher#PRIVATE_KEY_PROPERTY
	 * @see CloudRSACipher#DEFAULT_DESTINATION_NAME
	 */
	public static final String PUBLIC_KEY_PROPERTY = "mail.user";
	
	/**
	 * SAP Business Technology Platform (BTP) destination property in which the private key is stored
	 *
	 * @see CloudRSACipher#PUBLIC_KEY_PROPERTY
	 * @see CloudRSACipher#DEFAULT_DESTINATION_NAME
	 */
	public static final String PRIVATE_KEY_PROPERTY = "mail.password";
	
	private final KeyPair keyPair;
	
	private final NoRequestContextDestinationAccessor noRequestContextDestinationAccessor;

	/**
	 * @param destinationName
	 *            The name of SAP Business Technology Platform (BTP) destination that stores RSA key
	 *            pair. If the provided value is blank (null or empty even when
	 *            trimmed), then {@link CloudRSACipher#DEFAULT_DESTINATION_NAME}
	 *            is used as a default value.
	 */
	@Autowired
	public CloudRSACipher(NoRequestContextDestinationAccessor noRequestContextDestinationAccessor,
			@Value("${security.rsaCipher.destination:}") String destinationName) throws SAPException {
		
		this.noRequestContextDestinationAccessor = noRequestContextDestinationAccessor;
		
		if (StringUtils.isBlank(destinationName)) {
			destinationName = DEFAULT_DESTINATION_NAME;
		}
		keyPair = getKeyPairFromDestination(destinationName, ALGORITHM);
	}

	/**
	 * Creates {@link KeyPair} from SAP Business Technology Platform (BTP) destination.
	 * 
	 * @see CloudRSACipher#DEFAULT_DESTINATION_NAME
	 * @see CloudRSACipher#PUBLIC_KEY_PROPERTY
	 * @see CloudRSACipher#PRIVATE_KEY_PROPERTY
	 */
	public KeyPair getKeyPairFromDestination(final String destinationName, final String algorithm) {
		// to overcome common key conversion issues 
		Security.addProvider(new BouncyCastleProvider());

		// get serialized keys from the corresponding destination properties
		final GenericDestination destination = noRequestContextDestinationAccessor.getGenericDestination(destinationName);
		
    	final Map<String, String> properties = destination.getPropertiesByName();
    	String privateKeyString = properties.get(PRIVATE_KEY_PROPERTY);
    	String publicKeyString = properties.get(PUBLIC_KEY_PROPERTY);
		
		try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            privateKeyString = RSACipher.removeExtraCharacters(privateKeyString, algorithm, /* isPrivateKey = */ true);
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
            final PrivateKey privateKey = keyFactory.generatePrivate(keySpecPKCS8);

            publicKeyString = RSACipher.removeExtraCharacters(publicKeyString, algorithm, /* isPrivateKey = */ false);
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
            final PublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
            
            return new KeyPair(publicKey, privateKey);
		} catch (NoSuchAlgorithmException e) {
			throw RSACipher.logAndWrap(e, ALGORITHM);
		} catch (InvalidKeySpecException | IllegalArgumentException e) {
			final String message = String.format("Cannot initialize RSACipher: One of the keys or both keys are invalid"
					+ "Please check %s destination's %s property for public key and %s property for private key",
					DEFAULT_DESTINATION_NAME, PUBLIC_KEY_PROPERTY, PRIVATE_KEY_PROPERTY);
			throw RSACipher.logAndWrap(message, e);
		}
	}

    public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}

    @VisibleForTesting
	protected PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}
	
	public String encrypt(final Serializable object) throws SAPSecurityException {
	    try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);  
            cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
            
            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(SerializationUtils.serialize(object)));
        } catch (NoSuchAlgorithmException e) {
            throw RSACipher.logAndWrap(e, ALGORITHM);
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IllegalArgumentException e) {
            throw RSACipher.logAndWrap("Security exception when trying to encrypt access token", e);
        } catch (SerializationException e) {
            throw RSACipher.logAndWrap("Serialization exception when trying to encrypt access token", e);
        } 
	}
	
	public <T extends Serializable> T decrypt(final String encrypted) throws SAPSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
            
            return SerializationUtils.deserialize(cipher.doFinal(Base64.getUrlDecoder().decode(encrypted)));
        } catch (NoSuchAlgorithmException e) {
            throw RSACipher.logAndWrap(e, ALGORITHM);
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IllegalArgumentException e) {
            throw RSACipher.logAndWrap("Security exception when trying to decrypt access token", e);
        } catch (SerializationException e) {
            throw RSACipher.logAndWrap("Serialization exception when trying to decrypt access token", e);
        }
	}
		
}
