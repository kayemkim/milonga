package com.skp.milonga.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.skp.milonga.config.annotation.EnableMilonga;
import com.skp.milonga.test.MilongaTestConfig;

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
@WebAppConfiguration
@ContextConfiguration(/*loader = WebContextLoader.class, */locations = { "classpath:testApplicationContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class MilongaIntegrationXmlConfigTest {

	@Autowired
    protected WebApplicationContext wac;
	
	protected MockMvc mockMvc;
	
	@Before
    public void mockSetup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.build();
	}
	
	@Test
	public void sessionAndRedirectTest() throws Exception {
		mockMvc.perform(
				get("/login").param("userId", "abc@sk.com").param("password","1111"))
				//.andExpect(status().isOk())
				.andExpect(request().sessionAttribute("userId", "abc@sk.com"))
				.andExpect(redirectedUrl("/blog?result=succeeded"))
				.andExpect(cookie().value("mail", "abc@sk.com"));
	}
	
	@Test
	public void pathVariableBindingTest() throws Exception {
		mockMvc.perform(
				get("/pathvariable/foo"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("pathVariable", "foo"));
	}
	
	@Test
	public void contentNegotiationForJson() throws Exception {
		mockMvc.perform(
				get("/json/foo.json"))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"playerName\":\"foo\"}"));
	}
	
	@Test
	public void redirectTest() throws Exception {
		mockMvc.perform(
				get("/redirectTest"))
				//.andExpect(status().isOk())
				.andExpect(redirectedUrl("http://www.google.com"));
	}
	
	@Test
	public void refreshJavascript() throws Exception {
		mockMvc.perform(
				get("/platform"))
				.andExpect(status().isOk())
				.andExpect(view().name("platform"))
				.andExpect(forwardedUrl("/WEB-INF/views/platform.jsp"))
				.andExpect(model().attribute("platform", "Atmos Code"));
	}
}
