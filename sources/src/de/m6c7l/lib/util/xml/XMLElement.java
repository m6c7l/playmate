/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.xml;

import java.util.ArrayList;
import java.util.Hashtable;

import de.m6c7l.lib.util.text.DELIMITER;

public class XMLElement {
		
	private static final String EOL = DELIMITER.CR_LF.toString();
	
	private Hashtable<String,XMLAttribute> attr = null;
	private ArrayList<XMLElement> elem = null;

	private String name = null;
	private String text = null;
	private XMLElement parent = null;

	protected XMLElement(String name, XMLElement parent) {
		this.elem = new ArrayList<XMLElement>();
		this.attr = new Hashtable<String,XMLAttribute>();
		this.parent = parent;
		this.setName(name);
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if ((name!=null) && (name.length()>0))
			this.name = this.validate(name);
		else
			this.name = "";
	}
	
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		if ((text!=null) && (text.length()>0))
			this.text = text;
		else
			this.text = "";
	}
	
	public XMLElement getParent() {
		return this.parent;
	}

	public boolean hasChildren() {
		return this.elem.size()>0;
	}
	
	public Double getDouble() {
		try {
			return Double.valueOf(this.getText());
		} catch (NumberFormatException e) {}
		return null;
	}
	
	public Integer getInteger() {
		try {
			return Integer.valueOf(this.getText());
		} catch (NumberFormatException e) {}
		return null;
	}
	
	public Boolean getBoolean() {
		return Boolean.valueOf(this.getText());
	}
	
	public int getAttributeCount() {
		return this.attr.size();
	}
	
	public XMLAttribute getAttribute(String name) {
		String n = this.validate(name);
		return this.attr.get(n);
	}
	
	public boolean hasAttribute(String name) {
		return this.getAttribute(name)!=null;
	}
	
//	public XMLAttribute getAttribute(int index) {
//		return this.attr.get(index);
//	}
	
	public XMLAttribute setAttribute(String name, String value) {
		String n = this.validate(name);
		return this.attr.put(n,new XMLAttribute(n,value,this));
	}

	public XMLAttribute removeAttribute(String name) {
		String n = this.validate(name);
		return this.attr.remove(n);
	}

//	public XMLAttribute removeAttribute(int index) {
//		return this.attr.remove(index);
//	}
	
	public int getElementCount() {
		return this.elem.size();
	}
		
	public XMLElement addElement(String name) {
		XMLElement elem = new XMLElement(name,this);
		if (!this.elem.add(elem)) return null;
		return elem;
	}	

	public XMLElement getElement(int index) {
		return this.elem.get(index);
	}
	
	public XMLElement[] getElement(String name) {
		ArrayList<XMLElement> temp = new ArrayList<XMLElement>();
		for (int i=0; i<this.elem.size(); i++) {
			if (this.elem.get(i).getName().equals(name))
				temp.add(this.elem.get(i));
		}
		return (XMLElement[])temp.toArray(new XMLElement[temp.size()]);
	}

	public XMLElement firstElement(String name) {
		XMLElement[] temp = this.getElement(name);
		if (temp.length==0) return null;
		return temp[0];
	}
	
	public boolean hasElement(String name) {
		return getElement(name).length>0;
	}
	
	public boolean removeElement(XMLElement element) {
		return this.elem.remove(element);
	}
	
	public XMLElement removeElement(int index) {
		return this.elem.remove(index);
	}

	private String validate(String s) {
		return s.replaceAll("@","_").replaceAll(" ","_").replaceAll(";","_");
	}
	
	public boolean equals(Object elem) {
		if (elem instanceof XMLElement) {
			return this.toString().equals(((XMLElement)elem).toString());
		}
		return false;
	}
	
	public String toString() {
		return this.toStringHelp(0);
	}

	private String toStringHelp(int _tiefe) {
		boolean hasChilds = true;
		boolean hasContent = true;
		if (this.elem.size()==0) hasChilds = false;
		if ((this.getText()==null) || (this.getText().length()==0)) hasContent = false;
		StringBuffer vo = new StringBuffer();
		for (int i=0; i<_tiefe; i++) vo.append("  ");
		StringBuffer sb = new StringBuffer();
		if (this.getName()!=null) {
			sb.append(vo);
			sb.append("<" + this.getName());
			for (int i=0; i<this.attr.size(); i++) {
				sb.append(" ");
				sb.append(this.attr.get(this.attr.keySet().toArray()[i]));
			}
			if ((!hasChilds) && (!hasContent)) {
				sb.append("/>");
				sb.append(EOL);
			} else {
				sb.append(">");
				if (hasChilds) {
					sb.append(EOL);
				}
				if (hasContent) {
					if (hasChilds) {
						sb.append("  ");
						sb.append(vo);
					}
					sb.append(this.getText());
					if (hasChilds) {
						sb.append(EOL);
					}						
				}
			}
		}
		for (int i=0; i<this.elem.size(); i++) {
			XMLElement elem = this.elem.get(i);
			sb.append(elem.toStringHelp(_tiefe+1));
		}
		if (this.getName()!=null) {

			if ((hasChilds) || (hasContent)) {
				if (hasChilds) {
					sb.append(vo);
				}
				sb.append("</" + this.getName() + ">");
				sb.append(EOL);			
			}
		}
		return sb.toString();
	}

}