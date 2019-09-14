/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import de.m6c7l.lib.util.text.DELIMITER;

public class XMLDocument extends XMLElement {

	private static String header = "<?xml version='1.0' encoding='UTF-8'?>";
	
	private static String EOL = DELIMITER.CR_LF.toString();

	public static XMLDocument read(File file) throws IOException, ParserConfigurationException, SAXException {
		if (!file.exists()) throw new IOException("no such file: " + file.getName());
		if (file.length()==0) throw new IOException("file is empty: " + file.getName());
		XMLDocument doc = new XMLDocument(null);
		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		saxParser.parse(file,new XMLHandler(doc));	
		return doc;
	}

	public XMLDocument(String root) {
		super(root,null);	
	}
	
	public void write(File file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
	    out.write(this.toString());
	    out.flush();
	    out.close();
	}

	public String toString() {
		return
			header +
			EOL +
			EOL +
			super.toString();
	}

}