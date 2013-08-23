package com.km.milonga.rhino;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

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

		/*mockMvc.perform(get("/library")).andExpect(status().isOk())
				.andExpect(content().string("{\"library\":\"rhino\"}"));
		
		mockMvc.perform(get("/create_response")).andExpect(status().isOk())
				.andExpect(content().string("Hello Response!"));
		
		mockMvc.perform(get("/add_cookie")).andExpect(status().isOk())
				.andExpect(cookie().value("userId", "metsmania"));*/
	}
	
	@Test
	public void sessionAndRedirectTest() throws Exception {
		mockMvc.perform(
				get("/login").param("userId", "abc@sk.com").param("password","1111"))
				.andExpect(status().isOk())
				.andExpect(request().sessionAttribute("userId", "abc@sk.com"))
				.andExpect(redirectedUrl("/blog?result=succeeded"));
	}

}
