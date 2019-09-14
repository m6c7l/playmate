/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.xml;

public class XMLAttribute {
	
	private String name = null;
	private String value = null;
	private XMLElement element = null;

	protected XMLAttribute(String name, String value, XMLElement element) {
		this.element = element;
		this.value = value;
		this.setName(name);
	}
	
	public XMLElement getElement() {
		return this.element;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if ((name!=null) && (name.length()>0)) {
			this.name = this.validate(name);		
		} else {
			this.name = "";
		}
	}
	
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		if ((value!=null) && (value.length()>0)) 
			this.value = value;
		else
			this.value = "";
	}
		
	public Double getDouble() {
		try {
			return Double.valueOf(this.getValue());
		} catch (NumberFormatException e) {}
		return null;
	}
	
	public Integer getInteger() {
		try {
			return Integer.valueOf(this.getValue());
		} catch (NumberFormatException e) {}
		return null;
	}
	
	public Boolean getBoolean() {
		return Boolean.valueOf(this.getValue());
	}
	
	private String validate(String s) {
		return s.replaceAll("@","_").replaceAll(" ","_").replaceAll(";","_");
	}
	
	public String toString() {
		return this.getName().length()>0 ? this.getName() + "=\"" + this.getValue() + "\"" : "";
	}
	
}
