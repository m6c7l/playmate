/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;

public abstract class SimpleDialog extends BasicDialog {

	private static ResourceBundle rb = null;
	
	private JPanel content = null;
	private JPanel buttons = null;
	private JButton buttonClose = null;
	private JPanel bottom = null;
	
	private JPanel header = null;
	private JPanel body = null;
	private JPanel footer = null;
	
	public SimpleDialog(String title, boolean resizeable, boolean modal) {
		super(title, resizeable, modal);
		this.initialize();
	}

	public SimpleDialog(Dialog owner, String title, boolean resizeable, boolean modal) {
		super(owner, title, resizeable, modal);
		this.initialize();
	}

	public SimpleDialog(Frame owner, String title, boolean resizeable, boolean modal) {
		super(owner, title, resizeable, modal);
		this.initialize();
	}

	public SimpleDialog(Window owner, String title, boolean resizeable, boolean modal) {
		super(owner, title, resizeable, modal);
		this.initialize();
	}

	public SimpleDialog(Container owner, String title, boolean resizeable, boolean modal) {
		super(owner, title, resizeable, modal);
		this.initialize();
	}

	private void initialize() {
		rb = ResourceBundle.getBundle("resources."+
				//SimpleDialog.class.getPackage().getName()+"."+
				SimpleDialog.class.getSimpleName(),Locale.getDefault());
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setContentPane(getContent());
	}
	
	public final JPanel getContent() {
		if (content == null) {
			content = new JPanel();
			content.setLayout(new BorderLayout());
			setHeader(null);
			setBody(null);
			content.add(getBottom(), BorderLayout.SOUTH);
		}
		return content;
	}
	
	private JPanel getBottom() {
		if (bottom == null) {
			bottom = new JPanel();
			bottom.setLayout(new BorderLayout());
			setFooter(null);
			bottom.add(getButtons(), BorderLayout.EAST);
		}
		return bottom;
	}

	private JPanel getButtons() {
		if (buttons == null) {
			GridBagConstraints gridBagConstraintsButton = new GridBagConstraints();
			gridBagConstraintsButton.gridx = 0;
			gridBagConstraintsButton.insets = new Insets(5,10,10,10);
			gridBagConstraintsButton.fill = GridBagConstraints.BOTH;
			gridBagConstraintsButton.anchor = GridBagConstraints.WEST;
			buttons = new JPanel();
			buttons.setLayout(new GridBagLayout());
			buttons.add(getButtonClose(),gridBagConstraintsButton);
		}
		return buttons;
	}
	
	private JButton getButtonClose() {
		if (buttonClose == null) {
			buttonClose = new JButton();
			buttonClose.setText(rb.getString("BUTTON_CLOSE"));
			buttonClose.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return buttonClose;
	}

	public final void setHeader(JPanel panel) {
		this.getContent().remove(getHeader());
		this.header = panel;
		this.getContent().add(getHeader(), BorderLayout.NORTH);
	}

	public final void setBody(JPanel panel) {
		this.getContent().remove(getBody());
		this.body = panel;
		this.getContent().add(getBody(), BorderLayout.CENTER);
	}

	public final void setFooter(JPanel panel) {
		this.getBottom().remove(getFooter());
		this.footer = panel;
		this.getBottom().add(getFooter(), BorderLayout.CENTER);
	}

	public final JPanel getHeader() {
		if (header==null) header = new JPanel();
		return header;
	}
	
	public final JPanel getBody() {
		if (body==null) body = new JPanel();
		return body;
	}
	
	public final JPanel getFooter() {
		if (footer==null) footer = new JPanel();
		return footer;		
	}
	
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		this.getButtonClose().setEnabled(value);
	}
	
}
