/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.component;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FORMAT {

	DATE_DDMMJJJJ					("^(0[1-9]|[12][0-9]|3[01])\\.{0,1}(0[1-9]|1[012])\\.{0,1}[1-2]\\d{3}$",	Date.class),
	DATE_JJJJMMDD					("^[1-2]\\d{3}-{0,1}(0[1-9]|1[012])-{0,1}(0[1-9]|[12][0-9]|3[01])$",		Date.class),
	
	NUMBER_FLOAT_COMMA				("^(?:[\\-]\\d+)?\\d*(?:[,]\\d+)?$",										Float.class),
	NUMBER_FLOAT_POSITIVE_COMMA		("^[0-9]\\d*(?:[,]\\d+)?$",													Float.class),

	NUMBER_FLOAT_POINT				("^(?:[\\-]\\d+)?\\d*(?:[\\.]\\d+)?$",										Float.class),
	NUMBER_FLOAT_POSITIVE_POINT		("^[0-9]\\d*(?:[\\.]\\d+)?$",												Float.class),

	NUMBER_INTEGER					("^(?:[\\-]\\d+)?\\d*$",													Integer.class),
	NUMBER_INTEGER_POSITIVE			("^[0-9]\\d*$",																Integer.class),

	IP_ADDRESS						("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$",				String.class),
	EMAIL_ADDRESS					("^[\\w.-]+\\@(?:[\\w-]+\\.)+([a-zA-Z]{2,3})$",								String.class),
	DOMAIN_NAME						("([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+([a-zA-Z]{2,3})$",						String.class),
	
	NUMBER_BINARY					("^[01]+$",																	String.class),
	NUMBER_OCTAL					("^[0-7]+$",																String.class),
	NUMBER_HEXADECIMAL				("^[0-9A-Fa-f]+$",															String.class),
	
	PASSWORD_MIN_10_CHARS			(".{10,}",																	String.class),
	PASSWORD_MIN_8_CHARS			(".{8,}",																	String.class),
	PASSWORD_MIN_6_CHARS			(".{6,}",																	String.class),
	
	SOMETHING						(".{1,}",																	String.class),
	EVERYTHING						(".{0,}",																	String.class);
	
	private String strRegExp = null;
	private Class<?> clsDatentyp = null;
	
	FORMAT(final String regex, Class<?> type) {
		this.strRegExp = regex;
		this.clsDatentyp = type;
    }

	public String getFormat() {
		return this.strRegExp;
	}

	public Class<?> getType() {
		return this.clsDatentyp;
	}

	public boolean match(String text) {
		 String patt = this.getFormat();
	     Matcher m = Pattern.compile(patt).matcher(text);
	     return m.find();
	}

}