package com.skp.milonga.rhino;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.FileWriter;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.skp.milonga.rhino.mvctest.WebContextLoader;
import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

/**
 * Milonga Integration Test 
 * 1. read test.js 
 * 2. register url-handler infos into memory 
 * 3. request /test 
 * 4. check the result
 * 
 * This is Spring MVC Test.
 * 
 * @author kminkim
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, locations = { "classpath:testApplicationContext.xml" })
public class MilongaIntegrationTest {

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webApplicationContextSetup(
				webApplicationContext).build();
	}
	
	
	@Test
	public void sessionAndRedirectTest() throws Exception {
		mockMvc.perform(
				get("/login").param("userId", "abc@sk.com").param("password","1111"))
				.andExpect(status().isOk())
				.andExpect(request().sessionAttribute("userId", "abc@sk.com"))
				.andExpect(redirectedUrl("/blog?result=succeeded"))
				.andExpect(cookie().value("mail", "abc@sk.com"));
	}
	
	
	/**
	 * @PathVariable example
	 * 
	 * @throws Exception
	 */
	@Test
	public void pathVariableBindingTest() throws Exception {
		mockMvc.perform(
				get("/pathvariable/foo"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("pathVariable", "foo"));
	}
	
	
	/**
	 * Class Object Binding example
	 * 
	 * @throws Exception
	 */
	@Test
	public void dataBindingTest() throws Exception {
		mockMvc.perform(
				get("/binding").param("playerName", "Wright"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("playerName", "Wright"));
	}
	
	
	@Test
	public void contentNegotiationForJson() throws Exception {
		System.out.println();
		mockMvc.perform(
				get("/json/foo.json"))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"playerName\":\"foo\"}"));
	}
	
	
	@Test
	public void javascriptStyleBinding() throws Exception {
		mockMvc.perform(
				get("/jsStyleBinding/foo/bar"))
				.andExpect(status().isOk())
				.andExpect(content().string("foobar"));
	}
	
	
	@Test
	public void javascriptSyleJavaObjectBinding() throws Exception {
		mockMvc.perform(
				get("/jsStyleJavaObjectBinding.json").param("playerName", "Wright"))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"playerName\":\"Wright\"}"));
	}
	
	
	@Test
	public void redirectTest() throws Exception {
		mockMvc.perform(
				get("/redirectTest"))
				.andExpect(status().isOk())
				.andExpect(redirectedUrl("http://www.google.com"));
	}
	
	@Test
	public void refreshJavascript() throws Exception {
		mockMvc.perform(get("/platform")).andExpect(status().isOk())
				.andExpect(view().name("platform"))
				.andExpect(forwardedUrl("/WEB-INF/views/platform.jsp"))
				.andExpect(model().attribute("platform", "Atmos Code"));
		
		// new js file added
		String locationOfFileToWrite = new File(".").getAbsolutePath()
				+ "/src/main/webapp/"
				+ webApplicationContext.getBean(
						AtmosRequestMappingHandlerMapping.class)
						.getUserSourceLocation() + "/test2.js";
		FileWriter writer = new FileWriter(locationOfFileToWrite);
		writer.write("Atmos.handler('/test', function() { return 'test'; });");
		writer.flush();
		writer.close();
		
		Thread.sleep(10000);
		
		mockMvc.perform(get("/platform")).andExpect(status().isOk())
				.andExpect(view().name("platform"))
				.andExpect(forwardedUrl("/WEB-INF/views/platform.jsp"))
				.andExpect(model().attribute("platform", "Atmos Code"));
		
		// check if new handler is registered
		mockMvc.perform(get("/test")).andExpect(status().isOk())
				.andExpect(content().string("test"));
		
		// delete new js file
		File fileToWrite = new File(locationOfFileToWrite);
		fileToWrite.delete();
	}
}
