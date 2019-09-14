/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	private XMLElement root = null;
	private XMLElement element = null;
	
	protected XMLHandler(XMLElement root) {
		super();
		this.root = root;
	}

    public void startDocument() throws SAXException {}

    public void endDocument() throws SAXException {}

	public void startElement(
			String namespaceURI,
			String localName,
            String qName,
            Attributes attrs) throws SAXException {
		if (this.element==null) 
			this.element = this.root;
		else {
			this.element = this.element.addElement(qName);
		}
		this.element.setName(qName);
		for (int i=0; i<attrs.getLength(); i++) {
			this.element.setAttribute(attrs.getQName(i),attrs.getValue(i));
		}
	}

	public void endElement(
		  String namespaceURI,
          String localName,
          String qName ) throws SAXException {
		this.element = this.element.getParent();
	}

	public void characters( char[] buf, int offset, int len ) throws SAXException {
		this.element.setText(new String(buf, offset, len).trim());
	}
    	
}