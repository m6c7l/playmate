/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.m6c7l.lib.util.xml.XMLDocument;
import de.m6c7l.lib.util.xml.XMLElement;

public class XMLAssetReader {
	
	private File file = null;
	private ArrayList<XMLAsset> values = null;

	public XMLAssetReader(File file) throws IOException, ParserConfigurationException, SAXException {
		this.file = file;
		this.values = new ArrayList<XMLAsset>();
		this.read();
	}

	public XMLAssetReader(URI uri) throws IOException, ParserConfigurationException, SAXException {
		this(new File(uri));
	}
	
	public void read() throws IOException, ParserConfigurationException, SAXException {
		this.values.clear();
		XMLDocument xml = XMLDocument.read(file);
		XMLElement[] xmlAsset = xml.getElement("asset");
		for (int i=0; i<xmlAsset.length; i++) {
			this.values.add(new XMLAsset(xmlAsset[i]));
		}
	}
	
	public ArrayList<XMLAsset> getValues() {
		ArrayList<XMLAsset> temp = new ArrayList<XMLAsset>();
		for (int i=0; i<this.values.size(); i++) {
			temp.add(values.get(i));
		}
		return temp;
	}
	
	public File getFile() {
		return this.file;
	}
	
}
