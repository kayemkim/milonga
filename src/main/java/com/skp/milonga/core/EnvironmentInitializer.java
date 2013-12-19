package com.skp.milonga.core;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String PROPERTYSOURCE_APPCONFIG_NAME = "appConfig";

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try {
            ConfigurableEnvironment environment = context.getEnvironment();

            MutablePropertySources propertySources = environment.getPropertySources();

            List<Resource> resources = new ArrayList<Resource>();
            resources.add(new ClassPathResource("META-INF/milonga.xml"));

            PropertiesFactoryBean bean = new PropertiesFactoryBean();
            bean.setLocations(resources.toArray(new Resource[resources.size()]));
            bean.afterPropertiesSet();

            propertySources.addLast(new PropertiesPropertySource(PROPERTYSOURCE_APPCONFIG_NAME, bean.getObject()));
        } catch (IOException e) {
            throw new ApplicationContextException("environment initialization failed!", e);
        }
    }

}