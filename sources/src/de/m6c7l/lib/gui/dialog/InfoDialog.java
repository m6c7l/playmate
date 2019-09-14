/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.dialog;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.GridBagConstraints;

import java.awt.Insets;
import java.awt.SystemColor;

public class InfoDialog extends SimpleDialog {

	private JPanel image = null;
	private JPanel info = null;
	private JPanel additional = null;
	private JLabel imageLabel = null;
	
	private JScrollPane scrollPane = null;
	private JTextArea textArea = null;
	
    private Dimension pref = null;
    
	public InfoDialog(String title) {
		super(title,false,true);
		init();
	}
    
	public InfoDialog(Dialog owner, String title) {
		super(owner,title,false,true);
		init();
	}
	
	public InfoDialog(Frame owner, String title) {
		super(owner,title,false,true);
		init();
	}
	
	public InfoDialog(Window owner, String title) {
		super(owner,title,false,true);
		init();
	}
	
	public InfoDialog(Container owner, String title) {
		super(owner,title,false,true);
		init();
	}

	private void init() {
		super.setHeader(getInfoHeader());
		super.setBody(getInfoBody());
		super.setFooter(getInfoFooter());
		this.pref = new Dimension(super.getPreferredSize());
		super.setPreferredSize(null);
		this.pref.setSize(this.pref.width,super.getPreferredSize().height);
		super.setPreferredSize(this.pref);
	}
	
	public void setText(String text) {
		int h = getTextArea().getFontMetrics(getTextArea().getFont()).getHeight();
		if (text!=null) {
			getTextArea().setText(text);
			getTextArea().setCaretPosition(0);
			int n = getTextArea().getLineCount()/5*4;
			this.setPreferredSize(
					new Dimension(
							this.getPreferredSize().width,
							(h*n)+this.getPreferredSize().height));
		} else {
			int n = getTextArea().getLineCount()/5*4;
			this.setPreferredSize(
					new Dimension(
							this.getPreferredSize().width,
							this.getPreferredSize().height-(h*n)));
			getTextArea().setText(null);
		}
	}
	
	public void setImage(ImageIcon imageIcon) {
		if (imageIcon!=null) {
			this.getIconLabel().setIcon(imageIcon);
			this.setPreferredSize(
					new Dimension(
							imageIcon.getIconWidth(),
							imageIcon.getIconHeight()+this.getPreferredSize().height));
		} else {
			Icon icon = this.getIconLabel().getIcon();
			if (icon!=null) {
				this.setPreferredSize(
						new Dimension(
								this.pref.width,
								this.getPreferredSize().height-icon.getIconHeight()));
				this.getIconLabel().setIcon(null);
			}
		}
	}

	private JPanel getInfoHeader() {
		if (image == null) {
			image = new JPanel();
			image.setLayout(new BorderLayout());
			image.add(getIconLabel(), BorderLayout.CENTER);
		}
		return image;
	}
	
	private JPanel getInfoBody() {
		if (info == null) {	
			GridBagConstraints gridBagConstraintsContent = new GridBagConstraints();
			gridBagConstraintsContent.gridx = 0;
			gridBagConstraintsContent.insets = new Insets(10,10,5,10);
			gridBagConstraintsContent.fill = GridBagConstraints.BOTH;
			gridBagConstraintsContent.anchor = GridBagConstraints.WEST;
			gridBagConstraintsContent.weightx = 1.0;
			gridBagConstraintsContent.weighty = 1.0;
			gridBagConstraintsContent.gridy = 0;
			info = new JPanel();
			info.setLayout(new GridBagLayout());
			info.setBorder(BorderFactory.createLineBorder(this.getContent().getBackground(),1));
			info.add(getScrollPane(),gridBagConstraintsContent);
		}
		return info;
	}
	
	private JLabel getIconLabel() {
		if (imageLabel == null) {	
			imageLabel = new JLabel();
			imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return imageLabel;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getTextArea());
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBackground(getTextArea().getBackground());
			scrollPane.setBorder(BorderFactory.createLineBorder(getTextArea().getBackground(), 5));
		}
		return scrollPane;
	}

	private JTextArea getTextArea() {
		if (textArea == null) {
			Color bg = SystemColor.text;//this.getContent().getBackground();
			//bg = new Color((bg.getRed()+255)/2,(bg.getGreen()+255)/2,(bg.getBlue()+255)/2);
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setFocusable(false);
			textArea.setBorder(BorderFactory.createLineBorder(bg, 5));
			textArea.setBackground(bg);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
		}
		return textArea;
	}
	
	private JPanel getInfoFooter() {
		if (additional == null) {
			additional = new JPanel();
			additional.setLayout(new GridBagLayout());
		}
		return additional;
	}

}
