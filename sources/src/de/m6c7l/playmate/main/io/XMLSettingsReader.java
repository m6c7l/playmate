/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.m6c7l.lib.util.xml.XMLDocument;
import de.m6c7l.lib.util.xml.XMLElement;

public class XMLSettingsReader {
	
	private File file = null;
	private Hashtable<Object,Object> values = null;

	public XMLSettingsReader(File file) throws IOException, ParserConfigurationException, SAXException {
		this.file = file;
		this.values = new Hashtable<Object,Object>();
		this.read();
	}
	
	public XMLSettingsReader(URI uri) throws IOException, ParserConfigurationException, SAXException {
		this(new File(uri));
	}

	public void free() throws IOException, ParserConfigurationException, SAXException {
		read();
	}
	
	public void read() throws IOException, ParserConfigurationException, SAXException {		
		this.values.clear();
		XMLDocument xml = XMLDocument.read(this.file);
		read(xml);
	}

	private void read(XMLElement elem) throws IOException, ParserConfigurationException, SAXException {		
		if (!elem.hasChildren()) {
			XMLElement e = elem;
			String id = e.getName()!=null ? e.getName() : "";
			while ((e.getParent()!=null) && (e.getParent().getParent()!=null)) {
				id = (e.getParent().getName()!=null ? e.getParent().getName() + "." : "") + id; 
				e = e.getParent();
			}
			this.values.put(
					id,
					elem.getText()!=null ? elem.getText() : "");			
		} else {
			for (int i=0; i<elem.getElementCount(); i++) {
				read(elem.getElement(i));
			}			
		}
	}

	public Hashtable<Object,Object> getValues() {
		Hashtable<Object,Object> temp = new Hashtable<Object,Object>();
		Object[] keys = values.keySet().toArray();
		for (int i=0; i<keys.length; i++) {
			temp.put(keys[i],values.get(keys[i]));
		}
		return temp;
	}
	
	public File getFile() {
		return this.file;
	}

}
