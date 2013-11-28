package com.skp.milonga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.skp.milonga.servlet.handler.AtmosRequestMappingInfoStorage;
import com.skp.milonga.servlet.handler.HandlerMappingInfoStorage;

@Configuration
public class MilongaConfig extends WebMvcConfigurerAdapter {
	
	@Bean(name="atmosRequestMappingInfoStorage")
	public HandlerMappingInfoStorage atmosRequestMappingInfoStorage() {
		return new AtmosRequestMappingInfoStorage();
	}
	
}
