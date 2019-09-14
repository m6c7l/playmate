/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.text;

public enum DELIMITER {

	BR		("<br />"),
    CR		("\r"),
	LF		("\n"),
	CR_LF	("\r\n");

	private String delimiter = null;
	
	private DELIMITER(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String toString() {
		return delimiter;
	}
	
}