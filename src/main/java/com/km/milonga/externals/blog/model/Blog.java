package com.km.milonga.externals.blog.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "blog")
public class Blog {
	
	@JsonProperty("email")
	private String id;
	
	@JsonIgnore
	private String title;
	
	//@XmlAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	//@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
