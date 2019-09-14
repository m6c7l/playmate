/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.scenario;

import javax.swing.Icon;

import de.m6c7l.lib.gui.TOOLBOX;
import de.m6c7l.lib.gui.tree.BasicTreeCellRenderer;

public class TreeRenderer extends BasicTreeCellRenderer {

	private Icon closed = null;
	private Icon leaf = null;
	private Icon open = null;
	
	public TreeRenderer() {
		super();
	}
	
	public void setClosedIcon(Icon newIcon) {
		closed = null;
		super.setClosedIcon(newIcon);
	}
	
	public void setLeafIcon(Icon newIcon) {
		leaf = null;
		super.setLeafIcon(newIcon);
	}

	public void setOpenIcon(Icon newIcon) {
		open = null;
		super.setOpenIcon(newIcon);
	}
	
	public Icon getClosedIcon() {
		if (closed==null) {
			closed = TOOLBOX.getGrayQuality(super.getClosedIcon());
		}
		return closed;
	}
	
	public Icon getLeafIcon() {
		if (leaf==null) {
			leaf = TOOLBOX.getGrayQuality(super.getLeafIcon());
		}
		return leaf;
	}
	
	public Icon getOpenIcon() {
		if (open==null) {
			open = TOOLBOX.getGrayQuality(super.getOpenIcon());
		}
		return open;
	}
	
}
