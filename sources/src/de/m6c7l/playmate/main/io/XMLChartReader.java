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

public class XMLChartReader {

	private File file = null;
	private File imgFile = null;
	private ArrayList<XMLChart> values = null;

	public XMLChartReader(File file) throws IOException, ParserConfigurationException, SAXException {
		this.values = new ArrayList<XMLChart>();
		this.file = file;
		this.imgFile = new File(
				file.getParent() +
				System.getProperty("file.separator") +
				file.getName().substring(0,file.getName().indexOf(".")) +
				System.getProperty("file.separator"));
		this.read();
	}
	
	public XMLChartReader(URI uri) throws IOException, ParserConfigurationException, SAXException {
		this(new File(uri));
	}
	
	public void read() throws IOException, ParserConfigurationException, SAXException {
		values.clear();
		XMLDocument xml = XMLDocument.read(file);                                                   
		XMLElement[] xmlAsset = xml.getElement("chart");
		for (int i=0; i<xmlAsset.length; i++) {
			this.values.add(new XMLChart(xmlAsset[i],imgFile));
		}
	}

	public ArrayList<XMLChart> getValues() {
		ArrayList<XMLChart> temp = new ArrayList<XMLChart>();
		for (int i=0; i<this.values.size(); i++) {
			temp.add(values.get(i));
		}
		return temp;
	}
	
	public File getFile() {
		return this.file;
	}

}
