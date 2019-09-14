/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.dialog;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public abstract class BasicDialog extends JDialog {
	
	public BasicDialog(
			String title,
			int width,
			int height,
			boolean resizeable,
			boolean modal) {
		super();
		this.initialize(title,width,height,resizeable,modal);
	}
	
	public BasicDialog(
			Dialog owner,
			String title,
			int width,
			int height,
			boolean resizeable,
			boolean modal) {
		super(owner);
		this.initialize(title,width,height,resizeable,modal);
	}
	
	public BasicDialog(
			Frame owner,
			String title,
			int width,
			int height,
			boolean resizeable,
			boolean modal) {
		super(owner);
		this.initialize(title,width,height,resizeable,modal);
	}
	
	public BasicDialog(
			Window owner,
			String title,
			int width,
			int height,
			boolean resizeable,
			boolean modal) {
		super(owner);
		this.initialize(title,width,height,resizeable,modal);
	}
	
	public BasicDialog(
			Container owner,
			String title,
			int width,
			int height,
			boolean resizeable,
			boolean modal) {
		this((Window)SwingUtilities.getAncestorOfClass(Window.class,owner),title,width,height,resizeable,modal);
	}
	
	public BasicDialog(
			String title,
			boolean resizeable,
			boolean modal) {
		super();
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		this.initialize(title,scr.width/3,(int)(scr.height/2.25),resizeable,modal);
	}
	
	public BasicDialog(
			Dialog owner,
			String title,
			boolean resizeable,
			boolean modal) {
		super(owner);
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		this.initialize(title,scr.width/3,(int)(scr.height/2.25),resizeable,modal);
	}
	
	public BasicDialog(
			Frame owner,
			String title,
			boolean resizeable,
			boolean modal) {
		super(owner);
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		this.initialize(title,scr.width/3,(int)(scr.height/2.25),resizeable,modal);
	}
	
	public BasicDialog(
			Window owner,
			String title,
			boolean resizeable,
			boolean modal) {
		super(owner);
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		this.initialize(title,scr.width/3,(int)(scr.height/2.25),resizeable,modal);
	}
	
	public BasicDialog(
			Container owner,
			String title,
			boolean resizeable,
			boolean modal) {
		this((Window)SwingUtilities.getAncestorOfClass(Window.class,owner),title,resizeable,modal);
	}
	
	private void initialize(
			String title,
			int width,
			int height,
			boolean resizeable,
			boolean modal) {
		this.setPreferredSize(new Dimension(width,height));
		this.setModal(modal);
		this.setTitle(title);
		this.setResizable(resizeable);
		this.setAlwaysOnTop(true);
	}
	
	@Override
	public void setPreferredSize(Dimension dimension) {
		super.setMinimumSize(dimension);
		super.setPreferredSize(dimension);
		super.pack();
		super.setLocationRelativeTo(super.getOwner());
	}
	
}