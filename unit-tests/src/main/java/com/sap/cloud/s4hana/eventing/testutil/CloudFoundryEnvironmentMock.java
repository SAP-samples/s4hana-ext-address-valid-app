package com.sap.cloud.s4hana.eventing.testutil;

import static org.mockito.Mockito.when;

import java.net.URI;

import javax.annotation.Nonnull;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.s4hana.eventing.security.CloudRSACipher;
import com.sap.cloud.s4hana.eventing.sendmail.Email;
import com.sap.cloud.s4hana.eventing.sendmail.SessionConfig;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationType;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.testutil.MockDestination;
import com.sap.cloud.sdk.testutil.MockUtil;

/**
 * JUnit {@link ClassRule} that mocks CloudFoundry environment for local
 * testing.
 * <p>
 * If would be sufficient to use {@link MockUtil} to mock destinations, but
 * since it doesn't support mocking destinations of type
 * {@link DestinationType.MAIL} we use {@link EnvironmentVariables} class that
 * does so via system environment variable {@code destinations}.
 * 
 * @see MockUtil
 * @see EnvironmentVariables
 */
public class CloudFoundryEnvironmentMock extends ExternalResource {
	
	public class RSAKeyPairStorageDestination {
		// key size = 2048 bit
		public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY----- MIIBITANBgkqhkiG9w0BAQEFAAOCAQ4AMIIBCQKCAQB2W3B960rwfgVVOPaGyfPN teUI9DNkoKuNz77f+4mYsvSHGdKYaFTeVDHyDcKEslVli36GBi0ZMqSZy+12MTcq j+4Xbe7/OhyTGPVHdptuOV8wpr9UDyJd3JUUfb93Z0SOgPi5HscPeKn+Mo8EhMNg Kj6ivDHDMefl9Iz3gBijAFWIPD+K6WUPtLZdl1dCtFTWhGNRNJPFEq2KyXJExxx2 75gnnLiAEOhz8/Hv1NlU66g914RN2kXbmCy5i3FQaiur7/ozZ800g6+0HjgR4609 auXtO9yQhmEbDStZcQT/rDx2de76gYmgngtGp15idNipiHQUQGjhXxPkiwxWo24t AgMBAAE= -----END PUBLIC KEY-----";
		public static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY----- MIIEogIBAAKCAQB2W3B960rwfgVVOPaGyfPNteUI9DNkoKuNz77f+4mYsvSHGdKY aFTeVDHyDcKEslVli36GBi0ZMqSZy+12MTcqj+4Xbe7/OhyTGPVHdptuOV8wpr9U DyJd3JUUfb93Z0SOgPi5HscPeKn+Mo8EhMNgKj6ivDHDMefl9Iz3gBijAFWIPD+K 6WUPtLZdl1dCtFTWhGNRNJPFEq2KyXJExxx275gnnLiAEOhz8/Hv1NlU66g914RN 2kXbmCy5i3FQaiur7/ozZ800g6+0HjgR4609auXtO9yQhmEbDStZcQT/rDx2de76 gYmgngtGp15idNipiHQUQGjhXxPkiwxWo24tAgMBAAECggEAO7ZWQrD61eo+doiO l+I7hoVGUAYkB5in9JvjCM221bszcxWsEghRCPohWKVohdoTC3AdHTDwf831DUK9 QuwBkQ+cZ2WnNkzzLaNh2/QS5sE7LShzTs1z4TbwX64xcuQ+ykZ3Na45b6zI1t5b GUSB7Xpir2eTE4+SF4oRtKyDwXgwvLeYy4g4Me1TEeY2sSSd6P6HHvxPTOUEscT+ qTGam3J3eoBrnrdzYqa9bCO06Wzsch2cSA9oNCe720qgK2Nu7io3pGLlWAPjR1Me jg5uVwD2KedRr1wdX6TiAhvtZ+HmTWxUYLi4alMbGnpOJWC6JpS35nsZpKjKyC7F dnz9AQKBgQDEoNcD/RS4T5JtCMwcBWWZ32wa/LK1ww9Xz+MVTTCxEBwPOQ5mgQnM 7KI2sw1vjsn4Jz20ewEAmVBg01gDA78atWNOrSxepudiMCcbZZ04nSLhKdWMcSsP CAXdQ4tOahDCcVbrCrd49jCu/7p/rOad2culuNCamOrydMmwC6PcxQKBgQCaGFNp vMYXBhV87wQGuAxGiR27fyhgYhx4bRbr0N9q/ayNOq57YQ5GvyxRm0qYUD6hanPK 8sgMjkgrBDEcvT+FBvRv8stOYi5FYFymHBni5mdCL6ERE8p3lMbAEFayR/B5GJyt vQVFejXR29mp4F18eUcBzhC56A4uUAeeh08ySQKBgQC/pRRsqsr7KXNKGnz2AryY UQ8I7qswRUI7EEAAjw+aRF1U6QOEHNj0ht9D+RhrVvy7hWO6+PhtIQEanmL17Tzi R1DXaioIxxxWsyAqr8uhVmacly2wiMyzBdrRYffxIafcHKPeyr8wgqPIrS+VKWxN Be5bQ9ruE4VLc1Dw6Sd5EQKBgGbucuGBoIyGmtnuSrE2RbTkKIEzKkRfpQboT9RL nMG6G4XnOd/ecAvf7XnBkq11hLCMDDb3v5tre1eIoEnDPGfewm75Lsb4HFJpUNoA kWYYY8c5S4y9vJPuRGjNQSwrk71ALIB9b8T+Im9BPP8ViTz5Qe45uMCF+tsCyGQj nEpJAoGARC2AE4JviJ2ucJBMHTJHzFnIz9w3K/jmUl78rtUgLHC1yY8jTqUDDG/j /BekZdFLe0OfrUaoBtZSDP65tdE/6kyukWB0bPQgVUdSJE0VNAqnmuw1DsM1da9A WA4VGR0J86YL6UH9pIdLVwV4YFF8n0BnwBEgRLLTX1253QsOzo4= -----END RSA PRIVATE KEY-----";

		public static final String NOT_MATCHING_PUBLIC_KEY = "-----BEGIN PUBLIC KEY----- MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMZDVGrYZR8goEugWCV1eQq7q0 pU/XKwyQSL8NAhA94lLnH7Gz2HkQ4mdV/7u1SAALmrzh67mbtGEU4kmp/VQBUKbK OYwvdITtOk7FLshx0LK+rg+JEnjeMo9hAW8rTW4/WUGHnn5/4S4M8N+xurEZJJQL iKz8BQBhrSSDsVHAwQIDAQAB -----END PUBLIC KEY-----";
	}
	
	public class DefaultMailDestination {
		public static final String USER = "noreply@example.com";
		public static final String PASSWORD = "12345";
		public static final String TRANSPORT_PROTOCOL = "smtps";
		public static final String SMTPS_HOST = "smtp.example.com";
		public static final String SMTPS_PORT = "465";
		public static final String SMTPS_AUTH = "true";
		public static final String FROM = "From Service <" + USER + ">";
	}
	
	/**
	 * This is needed only because {@link MockDestination#uri} is annotated
	 * with @{@link Nonnull}
	 */
	private static final URI DUMMY_NON_NULL_URI = URI.create("any.non-null.url.to.make.mockutil.happy");
	
	private static final MockUtil mockUtil = new MockUtil();
	
	public MockUtil getMockUtil() {
		return mockUtil;
	}
	
	private Destination erpDestinationMock;
	private Destination rsaKeyPairStorageDestinationMock;
	private Destination mailDestinationMock;

	@Override
	protected void before() throws Throwable {
		mockUtil.mockDefaults();
		
		// SAP S/4HANA Cloud destination config parameters are taken from
		// `systems.yml` and `credentials.yml` files in the
		// `unit-tests/src/main/resources` directory
		erpDestinationMock = mockUtil.mockErpDestination();
		
		rsaKeyPairStorageDestinationMock = mockUtil.mockDestination(MockDestination.builder().
			name(CloudRSACipher.DEFAULT_DESTINATION_NAME).
			uri(DUMMY_NON_NULL_URI).
			property(CloudRSACipher.PRIVATE_KEY_PROPERTY, RSAKeyPairStorageDestination.PRIVATE_KEY).
			property(CloudRSACipher.PUBLIC_KEY_PROPERTY, RSAKeyPairStorageDestination.PUBLIC_KEY).
			build());
		
		when(rsaKeyPairStorageDestinationMock.getDestinationType()).thenReturn(DestinationType.MAIL);
		
		mailDestinationMock = mockUtil.mockDestination(MockDestination.builder().
				name(SessionConfig.DEFAULT_MAIL_DESTINATION_NAME).
				uri(DUMMY_NON_NULL_URI).
				property("mail.user", DefaultMailDestination.USER).
				property("mail.password", DefaultMailDestination.PASSWORD).
				property("mail.transport.protocol", DefaultMailDestination.TRANSPORT_PROTOCOL).
				property("mail.smtps.host", DefaultMailDestination.SMTPS_HOST).
				property("mail.smtps.port", DefaultMailDestination.SMTPS_PORT).
				property("mail.smtps.auth", DefaultMailDestination.SMTPS_AUTH).
				property(Email.DESTINATION_PROPERTY_FROM, DefaultMailDestination.FROM).
				build());
		
		when(mailDestinationMock.getDestinationType()).thenReturn(DestinationType.MAIL);
	}
	
	public WireMockRule mockS4Hana() {
		return mockUtil.mockErpServer();
	}

	public Destination getErpDestinationMock() {
		return erpDestinationMock;
	}

	public void setErpDestinationMock(Destination erpDestinationMock) {
		this.erpDestinationMock = erpDestinationMock;
	}

	public Destination getRsaKeyPairStorageDestinationMock() {
		return rsaKeyPairStorageDestinationMock;
	}
	
	public ErpConfigContext getErpConfigContext() {
		return new ErpConfigContext(erpDestinationMock.getName());
	}

}
