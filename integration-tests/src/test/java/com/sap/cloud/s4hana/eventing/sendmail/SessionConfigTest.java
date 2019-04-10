package com.sap.cloud.s4hana.eventing.sendmail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.mail.Session;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SessionConfigTest {
	
	@ClassRule
	public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();

	@Autowired
	private Session injectedSession;
	
	@Test
	public void testSessionIsInjectedViaAutowired() {
		assertThat("Injected Session", injectedSession, is(not(nullValue())));
	}
	
}
