/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.tree;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class BasicTree extends JTree {

	private BasicTreeModel btm = null;

	public BasicTree(BasicTreeModel model) {
		this(model,true);
	}

	public BasicTree(BasicTreeModel model, boolean singleClick) {
		
		super(model);

		this.btm = model;

		ToolTipManager.sharedInstance().registerComponent(this);
		
		this.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		
		if (singleClick) {
			this.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {
					if ((e.getClickCount()==1) && (e.getButton()==MouseEvent.BUTTON1)) {
						if (isCollapsed(getRowForLocation(e.getX(), e.getY()))) {
							expandRow(getRowForLocation(e.getX(), e.getY()));
						} else {
							collapseRow(getRowForLocation(e.getX(), e.getY()));
						}					
					}
				}
			});			
		}
		
		this.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {

				BasicTreeNode node = (BasicTreeNode)getLastSelectedPathComponent();
				if (node==null) return;
				
				int index = btm.getNodes().indexOf(node);
				if ((index>-1) && ((node instanceof BasicRadioTreeNode) || (node instanceof BasicCheckTreeNode))) {
					btm.setSelected(node,!node.isSelected());
				}
				
				//getSelectionModel().removeSelectionPaths(getSelectionPaths());
				
				firePropertyChange(JTree.ROOT_VISIBLE_PROPERTY,!isRootVisible(),isRootVisible());
				
			}
		});

		this.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				BasicTreeNode node = (BasicTreeNode)event.getPath().getLastPathComponent();
				if (node==null) return;
				if (!node.isEnabled()) {
					throw new ExpandVetoException(event);
				}
			}	
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
			}
		});
		
	}

	public void free() {
		super.setModel(null);
		super.setCellRenderer(null);
		this.btm = null;
	}
	
	@Override
	public String getToolTipText(MouseEvent evt) {
	    if (getRowForLocation(evt.getX(), evt.getY()) == -1) return null;
	    TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
	    TreeNode node = (TreeNode)curPath.getLastPathComponent();
	    if (node instanceof BasicTreeNode) {
	    	return ((BasicTreeNode) node).getTooltipText();
	    }
	    return null;
	}
	
	public void setModel(BasicTreeModel model) {
		super.setModel(model);
	}
	
	public void setCellRenderer(BasicTreeCellRenderer renderer) {
		super.setCellRenderer(renderer);
	}
	
	public void expand(BasicTreeNode node, boolean children) {
		TreePath t = new TreePath(node.getPath());
		if (children) expand(t,true); else this.expandPath(t);
	}
	
	public void collapse(BasicTreeNode node, boolean children) {
		TreePath t = new TreePath(node.getPath());
		if (children) expand(t,false); else this.collapsePath(t);
	}

	private void expand(TreePath parent, boolean expand) {
	    TreeNode node = (TreeNode) parent.getLastPathComponent();
	    for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
	        TreeNode n = (TreeNode) e.nextElement();
		    TreePath path = parent.pathByAddingChild(n);
		    expand(path, expand);
	    }
	    if (expand) {
	        this.expandPath(parent);
	    } else {
	        this.collapsePath(parent);
	    }
	}

}