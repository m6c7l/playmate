/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;

import de.m6c7l.lib.util.xml.XMLDocument;
import de.m6c7l.lib.util.xml.XMLElement;

public class XMLSettingsWriter {

	private File file = null;
	
	public XMLSettingsWriter(File file) {
		this.file = file;
	}

	public XMLSettingsWriter(URI uri) {
		this(new File(uri));
	}
	
	public void write(String body, Hashtable<Object,Object> values) throws IOException {
		XMLDocument xml = new XMLDocument(body);
		for (int i=0; i<values.size(); i++) {
			Object k = values.keySet().toArray()[i];
			String[] sk = k.toString().split("\\.");
			XMLElement e = xml;
			for (int j=0; j<sk.length; j++) {
				if (e.hasElement(sk[j])) {
					e = e.firstElement(sk[j]);
				} else {
					e = e.addElement(sk[j]);
				}
			}
			e.setText(values.get(k).toString());					
		}
		xml.write(file);
	}
	
	public File getFile() {
		return this.file;
	}
	
}
