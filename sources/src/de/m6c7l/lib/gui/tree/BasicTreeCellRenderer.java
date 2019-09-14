/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.tree;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.m6c7l.lib.gui.TOOLBOX;

public class BasicTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private int HGAP = 0;
	private int VGAP = 0;
	private boolean gray = false;
	
	public BasicTreeCellRenderer() {
		this( 4, 4, false);
	}

	public BasicTreeCellRenderer(int hgap, int vgap) {
		this(hgap,vgap,false);
	}
	
	public BasicTreeCellRenderer(boolean gray) {
		this( 4, 4, gray);
	}
	
	public BasicTreeCellRenderer(int hgap, int vgap, boolean gray) {
		this.HGAP = hgap;
		this.VGAP = vgap;
		this.gray = gray;
	}
	
	public int getGapHorizonal() {
		return HGAP;
	}

	public int getGapVertical() {
		return VGAP;
	}
    
	public Component getTreeCellRendererComponent(
			JTree tree, Object value,
			boolean sel, boolean expanded,
			boolean leaf, int row,
			boolean hasFocus) {	

		boolean isLeaf = leaf; 
				
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			isLeaf = isLeaf && !node.getAllowsChildren();
		}
		
		super.getTreeCellRendererComponent(tree, value, sel, expanded, isLeaf, row, hasFocus);

		Component label = null;
		BasicTreeNode n = null;			
		
		if (value instanceof BasicTreeNode) {
			
			n = (BasicTreeNode)value;				

			Icon icon = getIcon();
			if (gray) icon = TOOLBOX.getGray(icon);

			if (value instanceof BasicCheckTreeNode) {
				JCheckBox c = new JCheckBox();	
				c.setSelected(n.isSelected());
				c.setEnabled(n.isEnabled());
				c.setText(value.toString());
				label = c;
			} else if (value instanceof BasicRadioTreeNode) {
				JRadioButton r = new JRadioButton();
				r.setSelected(n.isSelected());			
				r.setEnabled(n.isEnabled());
				r.setText(value.toString());
				label = r;
			} else {
				JLabel l = new JLabel();
				l.setEnabled(n.isEnabled());
				l.setText(value.toString());
				l.setIcon(icon);
				l.setIconTextGap(getIconTextGap());
				label = l;
			}

			label.setBackground(getBackground());

			label.setPreferredSize(
					new Dimension(
							label.getPreferredSize().width+VGAP,
							getPreferredSize().height+HGAP));
			
		}
				
		return label;

	}

}
