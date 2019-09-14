/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.tree;

import java.util.ArrayList;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class BasicTreeModel extends DefaultTreeModel {

	protected ArrayList<BasicTreeNode> nodes = null;

	public BasicTreeModel(BasicTreeNode root) {
		super(root);
		if (root!=null) root.model = this;
		this.nodes = new ArrayList<BasicTreeNode>();
	}

	public void free() {
		while (nodes.size()>0) {
			nodes.get(0).free();
			nodes.remove(0);
		}
	}
	
	public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren) {
		for (int i=0; i<removedChildren.length; i++) {
			BasicTreeNode toRemove = (BasicTreeNode)removedChildren[i];
			toRemove.free();
			nodes.remove(toRemove);					
		}
		super.nodesWereRemoved(node, childIndices, removedChildren);
	}
	
	public void nodesWereInserted(TreeNode node, int[] childIndices) {
		for (int i=0; i<childIndices.length; i++) {
			nodes.add((BasicTreeNode)(node.getChildAt(childIndices[i])));					
		}
		super.nodesWereInserted(node, childIndices);
	}
	
	public BasicTreeNode getRoot() {
		Object root = super.getRoot();
		if (root!=null) {
			return (BasicTreeNode)root;			
		}
		return null;
	}
	
	public String toString() {
		return nodes.toString();
	}
	
	protected ArrayList<BasicTreeNode> getNodes() {
		return this.nodes;
	}
	
	protected void setSelected(BasicTreeNode node, boolean selected) {
		if (node instanceof BasicRadioTreeNode) {
			if (!node.isSelected() && selected && node.isEnabled()) {
				Object group = ((BasicRadioTreeNode)node).getGroup();
				for (int i=0; i<nodes.size(); i++) {
					if (nodes.get(i) instanceof BasicRadioTreeNode) {
						if (((BasicRadioTreeNode)nodes.get(i)).getGroup()==group) {
							nodes.get(i).selected = false;
						}						
					}
				}
				node.selected = true;				
			}
		} else {
			node.selected = selected;	
		}
	}
	
}
