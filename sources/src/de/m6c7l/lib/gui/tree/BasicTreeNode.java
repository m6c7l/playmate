/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.tree;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

public class BasicTreeNode extends DefaultMutableTreeNode {
	
	private String tooltipText = null;
	private boolean enabled = true;
	
	protected boolean selected = false;
	protected BasicTreeModel model = null;
	
	public BasicTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject,allowsChildren);
	}
	
	protected void free() {
		this.setUserObject(null);
		this.model = null;
	}
	
	public void add(BasicTreeNode node) {
		super.add(node);
		if (this.model!=null) {
			setModel(this,node);
		}
	}
	
	public void remove(BasicTreeNode child) {	
		if (this.model!=null) {
			this.model.removeNodeFromParent(child);
		}
		super.remove(child);
	}

	public void removeAllChildren() {
	    if (this.model!=null) {
	    	while (this.getChildCount()>0) {
	    		this.model.removeNodeFromParent((BasicTreeNode)this.getChildAt(0));
	    	}
	    }
		super.removeAllChildren();
	}
	
	public void removeFromParent() {
		if (this.model!=null) {
			this.model.removeNodeFromParent(this);	
		}
		super.removeFromParent();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setSelected(boolean selected) {
		if ((this.model!=null)) {
			model.setSelected(this,selected);
		}
	}
	
	public boolean isSelected() {
		return this.selected;
	}

	public boolean hasUserObject() {
		return this.getUserObject()!=null;
	}
	
	public void setTooltipText(String text) {
		this.tooltipText = text;
	}

	public String getTooltipText() {
		return this.tooltipText!=null ? this.tooltipText : null;
	}
	
	public String toString() {
		return this.hasUserObject() ? this.getUserObject().toString() : "";
	}

	private void setModel(BasicTreeNode parent, BasicTreeNode node) {
		node.model = parent.model;
		model.insertNodeInto(node,parent,parent.getIndex(node));
	    for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
	        BasicTreeNode child = (BasicTreeNode) e.nextElement();
		    setModel(node,child);
	    }
	}
	
}
