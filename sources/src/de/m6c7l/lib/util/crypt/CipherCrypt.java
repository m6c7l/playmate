/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.crypt;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CipherCrypt {

	public static ALGORITHM AES = ALGORITHM.AES;
	public static ALGORITHM DES = ALGORITHM.DES;
	
	private ALGORITHM alg = null;
	private Key key = null;
	
	private Cipher cipher = null;
	
	public CipherCrypt(ALGORITHM alg) throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.alg = alg;
		this.cipher = Cipher.getInstance(alg.id);
	}

	public void setKey(String key) {
	    this.key = new SecretKeySpec(Arrays.copyOf(key.getBytes(),alg.keySize),alg.id);
	}
	
	public String encode(String plain) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		this.cipher.init(Cipher.ENCRYPT_MODE,this.key);	
		byte[] bplain = plain.getBytes();
		byte[] bcrypt = cipher.doFinal(bplain);
		return DatatypeConverter.printBase64Binary(bcrypt);
	}
	
	public String decode(String crypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE,this.key);
		byte[] bcrypt = DatatypeConverter.parseBase64Binary(crypt);
		byte[] bplain = cipher.doFinal(bcrypt);
		return new String(bplain);
	}
	
    private enum ALGORITHM {

    	DES		("DES",  8),
        AES		("AES", 16);
        
    	private String id = null;
    	private int keySize = 0;
    	
    	private ALGORITHM(String id, int keySize) {
    		this.id = id;
    		this.keySize = keySize;
    	}
    	
    }
    
}
