package com.skp.milonga.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingMilongaConfiguration.class)
public @interface EnableMilonga {
	
	String[] locations() default {"WEB-INF/js"};
	
	boolean autoRefreshable() default false;

}
