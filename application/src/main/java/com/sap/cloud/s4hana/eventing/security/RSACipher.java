package com.sap.cloud.s4hana.eventing.security;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sap.cloud.s4hana.eventing.core.exceptions.SAPSecurityException;

@Service
public interface RSACipher {
   
    static final Logger log = LoggerFactory.getLogger(RSACipher.class); 

    PublicKey getPublicKey();
       
    /**
     * @param object {@link Serializable} object to be encrypted after serialization
     * @return encrypted URL-safe string in Base64 encoding 
     */
    String encrypt(final Serializable object) throws SAPSecurityException;
    
    /**
     * @param encrypted URL-safe string in Base64 encoding
     * @param <T> return type
     * @return decrypted {@link Serializable} object
     */
    <T extends Serializable> T decrypt(final String encrypted) throws SAPSecurityException;
    
    /**
	 * Removes extra characters that can be present in private or public key
     */
    static String removeExtraCharacters(String keyString, String algorithm, boolean isPrivateKey) {
        final String keyType = isPrivateKey ? algorithm + " PRIVATE" : "PUBLIC";
        
        return keyString
                .replace("-----BEGIN " + keyType + " KEY-----", "")
                .replace("-----END " + keyType + " KEY-----", "")
                .replace(" ", "");
    }
    
    static SAPSecurityException logAndWrap(final String message, final Exception e) {
        log.error(message, e);
        return new SAPSecurityException(message, e);
    }
    
    static SAPSecurityException logAndWrap(NoSuchAlgorithmException e, String algorithm) {
        final String message = "Cannot initialize RSACipher: " + algorithm + " algorithm doesn't exist. "
                + "Please check RSACipher.ALGORITHM constant value";
        return logAndWrap(message, e);
    }

}
