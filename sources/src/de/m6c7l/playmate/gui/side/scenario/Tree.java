/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.scenario;

import java.awt.Dimension;

import javax.swing.tree.TreeSelectionModel;

import de.m6c7l.lib.gui.tree.BasicTree;

public class Tree extends BasicTree {
	
	private TreeModel model = null;
	
	public Tree(TreeModel model) {
		
		super(model);
		
		this.model = model;
		
		this.setCellRenderer(new TreeRenderer());
		
		this.setRootVisible(false);
	    this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		this.setDragEnabled(false); // deactivate AWT-DnD!
		
	    model.reload();
	    
		this.expand(model.getRoot(),true);
		
		this.collapse(model.getNodeAssetCivilHelicopter(),false);
		this.collapse(model.getNodeAssetCivilPlane(),false);
		this.collapse(model.getNodeAssetCivilVessel(),false);
		this.collapse(model.getNodeAssetMilitaryHelicopter(),false);
		this.collapse(model.getNodeAssetMilitaryPlane(),false);
		this.collapse(model.getNodeAssetMilitarySubmarine(),false);
		this.collapse(model.getNodeAssetMilitaryVessel(),false);
		

	}

	public Dimension getPreferredScrollableViewportSize() {
		return super.getMinimumSize();
	}

	public TreeModel getModel() {
		return this.model;
	}
	
}
