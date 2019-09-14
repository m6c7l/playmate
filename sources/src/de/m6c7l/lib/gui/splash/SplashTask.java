/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.splash;

import java.lang.reflect.InvocationTargetException;

public class SplashTask {

	private String description = null;
	private Class<?> aclass = null;
    private Object anobject = null;
    private String method = null;
    private Class<?>[] paramtypes = null;
    private Object[] params = null;
    private boolean vetoException = false;
    
    public SplashTask(
			Class<?> aclass, 
			Class<?>[] paramtypes,
			Object[] params,
			boolean vetoException) {
    	this(aclass, paramtypes, params);
    	this.vetoException = vetoException;
    }
    
    public SplashTask(
			Class<?> aclass, 
			Class<?>[] paramtypes,
			Object[] params) {
    	this.aclass = aclass;
		this.paramtypes = paramtypes;
		this.params = params;
    }
    
    public SplashTask(
    		String description,
			Class<?> aclass, 
			Class<?>[] paramtypes,
			Object[] params,
			boolean vetoException) {
    	this(description, aclass, paramtypes, params);
    	this.vetoException = vetoException;
    }
    
    public SplashTask(
    		String description,
			Class<?> aclass, 
			Class<?>[] paramtypes,
			Object[] params) {
    	this(aclass,paramtypes,params);
    	this.description = description;
    }
    
    public SplashTask(
			Class<?> aclass, 
			String method,
			Class<?>[] paramtypes,
			Object[] params,
			boolean vetoException) {
    	this(aclass, method, paramtypes, params);
    	this.vetoException = vetoException;
    }
    
    public SplashTask(
			Class<?> aclass, 
			String method,
			Class<?>[] paramtypes,
			Object[] params) {
    	this.aclass = aclass;
    	this.method = method;
		this.paramtypes = paramtypes;
		this.params = params;
    }
    
    public SplashTask(
    		String description,
			Class<?> aclass,
			String method,
			Class<?>[] paramtypes,
			Object[] params,
			boolean vetoException) {
    	this(description, aclass, method, paramtypes, params);
    	this.vetoException = vetoException;
    }
    
    public SplashTask(
    		String description,
			Class<?> aclass,
			String method,
			Class<?>[] paramtypes,
			Object[] params) {
    	this(aclass,method,paramtypes,params);
    	this.description = description;
    }
    
    public SplashTask(
			Object anobject, 
			String method, 
			Class<?>[] paramtypes,
			Object[] params,
			boolean vetoException) {
    	this(anobject, method, paramtypes, params);
    	this.vetoException = vetoException;
    }
    
    public SplashTask(
			Object anobject, 
			String method, 
			Class<?>[] paramtypes,
			Object[] params) {
    	this.anobject = anobject;
		this.method = method;
		this.paramtypes = paramtypes;
		this.params = params;
    }

    public SplashTask(
			String description,
			Object anobject, 
			String method, 
			Class<?>[] paramtypes,
			Object[] params,
			boolean vetoException) {
    	this(description, anobject, method, paramtypes, params);
    	this.vetoException = vetoException;
    }
    
    public SplashTask(
    		String description,
    		Object anobject, 
    		String method, 
    		Class<?>[] paramtypes,
    		Object[] params) {
    	this(anobject,method,paramtypes,params);
    	this.description = description;
    }

    protected Object execute() throws
    			IllegalArgumentException,
    				SecurityException,
    					IllegalAccessException,
    						InvocationTargetException,
    							NoSuchMethodException,
    								InstantiationException { 
    	
    	for (int i=0; i<this.params.length; i++) {
    		if (this.params[i] instanceof Splash.Transfer) {
   				this.params[i] = ((Splash.Transfer)this.params[i]).get();
    		}
    	}
    	
    	if (this.aclass!=null) {
    		if (this.method==null) {
            	return this.aclass.getConstructor(this.paramtypes).
            			newInstance(this.params);    			
    		} else {
        		return this.aclass.
                      	getMethod(this.method, this.paramtypes).
                      		invoke(null, this.params);
    		}
    	} else if (anobject!=null) {
    		return this.anobject.getClass().
                  	getMethod(this.method, this.paramtypes).
                  		invoke(this.anobject, this.params);
    	}
    	return null;
    }    

    public boolean hasException() {
    	return this.vetoException;
    }
    
    public String toString() {
    	if (this.description!=null)
    		return description;
    	return "";
    }
    
}