/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.crypt;

import java.security.InvalidKeyException;

public class VigenereCrypt {
	 
	public static ALPHABET ASCII_NON_CONTROL				= ALPHABET.ASCII_NON_CONTROL;
	public static ALPHABET ASCII_DIGIT 						= ALPHABET.ASCII_DIGIT;
	public static ALPHABET ASCII_LETTER 					= ALPHABET.ASCII_LETTER;
	public static ALPHABET ASCII_DIGIT_LETTER_LOWERCASE 	= ALPHABET.ASCII_DIGIT_LETTER_LOWERCASE;
	public static ALPHABET ASCII_DIGIT_LETTER_UPPERCASE 	= ALPHABET.ASCII_DIGIT_LETTER_UPPERCASE;
	public static ALPHABET ASCII_HEX_LOWERCASE 				= ALPHABET.ASCII_HEX_LOWERCASE;
	public static ALPHABET ASCII_HEX_UPPERCASE 				= ALPHABET.ASCII_HEX_UPPERCASE;
	public static ALPHABET ASCII_DIGIT_LETTER 				= ALPHABET.ASCII_DIGIT_LETTER;
	
	private ALPHABET abc = null;
	private char[] key = null;
	
	public VigenereCrypt(ALPHABET abc) {
		this.abc = abc;
	}
	
	public void setKey(String key) throws InvalidKeyException {
		if ((key==null) || (!this.abc.contains(key.toCharArray()))) throw new InvalidKeyException("key is invalid: " + key);
	    this.key = key.toCharArray();
	}
	
	public char[] getAlphabet() {
		return this.abc.getAlphabet();
	}
	
	public String encode(char[] plain) throws IllegalArgumentException, InvalidKeyException {
		return encode(new String(plain));
	}
	
	public String encode(String plain) throws IllegalArgumentException, InvalidKeyException {
		if ((plain==null) || (!abc.contains(plain.toCharArray()))) throw new IllegalArgumentException("argument does not fit to alphabet: " + plain);
		return new String(code(plain.toCharArray(),true));
	}
	
	public String decode(char[] crypt) throws InvalidKeyException, IllegalArgumentException {
		return encode(new String(crypt));
	}
	
	public String decode(String crypt) throws InvalidKeyException, IllegalArgumentException {
		if ((crypt==null) || (!abc.contains(crypt.toCharArray()))) throw new IllegalArgumentException("argument does not fit to alphabet: " + crypt);
		return new String(code(crypt.toCharArray(),false));
	}
	
	private char[] code(char[] arg, boolean encode) throws InvalidKeyException {
		if ((this.key==null) || (this.key.length==0)) throw new InvalidKeyException("key is invalid: " + cToS(key));
		char[] output = new char[arg.length];
		for (int i=0; i<arg.length; i++) {
			int result = (abc.indexOf(arg[i]) + (abc.indexOf(key[i % key.length]) * (encode ? +1 : -1)) + abc.getAlphabet().length) % abc.getAlphabet().length;
			output[i] = getAlphabet()[result];
		}
		return output;
	}

	private String cToS(char[] chars) {
		if (chars==null) return "null";
		if (chars.length==0) return "";
		return new String(chars);
	}
	
    private enum ALPHABET {

    	ASCII_NON_CONTROL								(	0,		0,		0),
    	ASCII_DIGIT										(	10,		0,		0),
    	ASCII_LETTER									(	0,		26, 	26),
    	ASCII_DIGIT_LETTER_LOWERCASE					(	10,		26,		0),
    	ASCII_DIGIT_LETTER_UPPERCASE					(	10,		0,		26),
    	ASCII_HEX_LOWERCASE								(	10,		6,		0),
    	ASCII_HEX_UPPERCASE								(	10,		0,		6),
    	ASCII_DIGIT_LETTER								(	10,		26,		26);
        
    	public char[] value = null;
    	
    	private ALPHABET(int digits, int lowercase, int uppercase) {
    		int len = digits+lowercase+uppercase;
    		if (len==0) len = 95; // nur druckbare ASCII
    		int n = 0;
    		value = new char[len];
    		if (len<95) {
        		for (int i=0; i<digits; i++) value[i+n] = (char)((byte)'0'+i); n = n + digits;
            	for (int i=0; i<lowercase; i++) value[i+n] = (char)((byte)'a'+i); n = n + lowercase;
            	for (int i=0; i<uppercase; i++) value[i+n] = (char)((byte)'A'+i); n = n + uppercase;			
    		} else {
    			for (int i=0; i<128; i++) {
    				if ((!Character.isISOControl((char)i)) && (n<len)) { value[n] = (char)i; n++; }
    			}
    		}
    	}

    	public char[] getAlphabet() {
    		return value;
    	}
    	
    	public int indexOf(char c) {
    		for (int i=0; i<value.length; i++) {
    			if (value[i]==c) return i;
    		}
    		return -1;
    	}
    	
    	public boolean contains(char[] arg) {
    		if (arg==null) return false;
    		for (int i=0; i<arg.length; i++) {
    			if (indexOf(arg[i])==-1) return false;
    		}
    		return true;
    	}
    	
    }	
 
}