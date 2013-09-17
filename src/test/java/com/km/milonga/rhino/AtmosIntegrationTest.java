package com.km.milonga.rhino;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.km.milonga.rhino.mvctest.WebContextLoader;

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
public class AtmosIntegrationTest {

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webApplicationContextSetup(
				webApplicationContext).build();
	}

	@Test
	public void connectTestUrl() throws Exception {
		mockMvc.perform(get("/platform")).andExpect(status().isOk())
				.andExpect(view().name("platform"))
				.andExpect(forwardedUrl("/WEB-INF/views/platform.jsp"))
				.andExpect(model().attribute("platform", "Atmos Code"));		
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
}
