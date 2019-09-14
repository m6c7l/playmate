/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.dnd;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.gui.AppModel;
import de.m6c7l.playmate.gui.window.chart.Chart;
import de.m6c7l.playmate.gui.window.chart.ChartView;
import de.m6c7l.playmate.main.Asset;

public class ChartDropListener implements DropTargetListener {

	private ChartView view = null;
	private AppModel model = null;
	
	public ChartDropListener(ChartView view, AppModel model) {
		this.model = model;
		this.view = view;	
		this.view.setDropTarget(new DropTarget(view,this));
	}

	public void dragEnter(DropTargetDragEvent e) {}
	public void dragExit(DropTargetEvent e) {}
	public void dragOver(DropTargetDragEvent e) {}
	public void dropActionChanged(DropTargetDragEvent e) {}
	
	public void drop(DropTargetDropEvent e) {
		boolean dropped = false;
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported(CreateableTransfer.createable)) {
				e.acceptDrop(DnDConstants.ACTION_LINK);
				Createable data = (Createable)tr.getTransferData(CreateableTransfer.createable);
				Chart pane = view.getChart();
				Point mp = pane.getMousePosition();
				if (mp!=null) {
					Position p = pane.getPosition(pane.convertImage(mp));
					if (p!=null) {
						try {
							Asset u = data.create(model.getWorld(),p);
							if ((!u.isWatercraft()) || (pane.isWater(p))) {
								u.setSpeed(u.getSpeedMaximum()*0.6);
								model.addAsset(u);
								dropped = true;								
							}
						} catch (Exception ex) {
						}
					}
				}
			}
		} catch (IOException ex) {
		} catch (UnsupportedFlavorException ex) {
		}
        e.dropComplete(dropped);
	}

}
