package com.sap.cloud.s4hana.eventing.sendmail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sap.cloud.s4hana.eventing.testutil.CloudFoundryEnvironmentMock;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FreemarkerConfigTest {
	
	@ClassRule
	public static CloudFoundryEnvironmentMock environmentMock = new CloudFoundryEnvironmentMock();
	
	@Autowired
	public Configuration freemarkerConfig;
	
	@Test
	public void testConfigInjection() {
		assertThat("Injected configuration", freemarkerConfig, is(not(nullValue())));
	}
	
	/**
	 * Dummy data model to test FreeMarker {@code test.ftlh} template
	 * <p>
	 * Has two parameters: {@code worldParameter} and {@code booleanParameter}
	 * that are accessible via getters in the template.
	 *
	 */
	public static class WorldDataModel {
		
		private String world;
		
		public WorldDataModel(String worldParameter) {
			world = worldParameter;
		}
		
		/**
		 * Getter method for {@code worldParameter} bean property (as per
		 * JavaBeans spec.)
		 * <p>
		 * @return {@code worldParameter} bean property value.
		 */
		public String getWorldParameter() {
			return world;
		}
		
		/**
		 * Getter method for {@code booleanParameter} bean property (as per
		 * JavaBeans spec.)
		 * <p>
		 * Note that getters prefixed with "is" may only be used for properties
		 * of simple {@code boolean} type. E.g:
		 * <br>
		 * {@code public boolean isSimpleBoolean()}
		 * <p>
		 * Getters for properties of boxed {@link Boolean} type should only be
		 * prefixed with "get".
		 * <p>
		 * 
		 * @return {@code true}
		 */
		public Boolean getBooleanParameter() {
			return true;
		}
	}
	
	@Test
	public void testTemplateWithWorld()
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, 
			IOException, TemplateException {
		
		testTemplate("World");
	}
	
	@Test
	public void testTemplateWithErük()
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, 
			IOException, TemplateException {
		
		testTemplate("Erük");
	}

	public void testTemplate(final String world) throws TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, TemplateException {
		
		// Given a test template in the folder specified in
		// FreemarkerConfig.PATH_TO_TEMPLATES_FOLDER constant
		// e.g. from /src/test/resources/templates/ if it is /templates/
		final Template template = freemarkerConfig.getTemplate("test.ftlh");
		
		// When the template is processed using dummy WorldDataModel
		final StringWriter resultWriter = new StringWriter();
		template.process(new WorldDataModel(world), resultWriter);
		String result = resultWriter.toString();
		
		// Then the result is Hello, World!
		assertThat("template processing result", result, is("Hello, " + world + "!"));
	}

}
