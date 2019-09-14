/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.tree;

public class BasicRadioTreeNode extends BasicTreeNode {
	
	private Object group = null;
	
	public BasicRadioTreeNode(Object userObject, Object group) {
		super(userObject, false);
		this.group = group; 
	}
	
	public Object getGroup() {
		return group;
	}

	protected void free() {
		super.free();
		this.group = null;
	}
	
}
