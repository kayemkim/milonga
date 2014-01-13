/*
 * Copyright 2014 K.M. Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skp.milonga.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.skp.milonga.test.model.Player;

public abstract class AbstractMilongaIntegrationTest {
	
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
	
	@Test
	public void applicationContextUsingTest() throws Exception {
		mockMvc.perform(
				get("/applicationContextTest"))
				.andExpect(status().isOk())
				.andExpect(content().string("org.springframework.web.servlet.view.InternalResourceViewResolver"));
	}
	
	@Test
	public void httpMethodTest() throws Exception {
		Player player = new Player();
		player.setPlayerName("mets");
		
		mockMvc.perform(
				post("/httpMethodTest").param("playerName", player.getPlayerName()))
				.andExpect(status().isOk())
				.andExpect(content().string(player.homerun()));
		
		mockMvc.perform(
				get("/httpMethodTest"))
				.andExpect(status().isOk());
	}

}
