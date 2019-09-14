/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.dnd;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.TreePath;

import de.m6c7l.lib.gui.tree.BasicTreeNode;
import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.gui.AppModel;
import de.m6c7l.playmate.gui.AppUI;
import de.m6c7l.playmate.gui.window.chart.Chart;
import de.m6c7l.playmate.gui.window.chart.ChartView;
import de.m6c7l.playmate.main.Asset;

public class BasicTreeDragListener implements DragSourceListener, DragGestureListener, ListDataListener {

	private DragSource source = null;
	private AppUI ui = null;
	
	private Asset drag = null;
	private boolean dragging = false;

	private Hashtable<Object, BasicTreeNode> dragged = null;

	public BasicTreeDragListener(AppUI ui) {
		this.ui = ui;
		dragged = new Hashtable<Object, BasicTreeNode>();
		source = new DragSource();
	    source.createDefaultDragGestureRecognizer(ui.getTree(), DnDConstants.ACTION_LINK, this);
		ui.getModel().getWorld().addListDataListener(this);
	}

	/*
	 *  ListListener
	 */

	public void contentsChanged(ListDataEvent e) {}
	public void intervalAdded(ListDataEvent e) {}
	public void intervalRemoved(ListDataEvent e) {
		ArrayList<Object> active = new ArrayList<Object>();
		for (int i=0; i<ui.getModel().getWorld().getAssetCount(); i++) {
			Object id = ui.getModel().getWorld().getAsset(i).getID();
			id = id != null ? id.toString() : "";
			active.add(id);
		}
		Object[] sent = dragged.keySet().toArray();
		for (int i=0; i<sent.length; i++) {
			if (!active.contains(sent[i])) {
				dragged.get(sent[i]).setEnabled(true);
				dragged.remove(sent[i]);
			}
		}
	}

	public void free() {
		this.ui = null;
		this.source = null;
		this.drag = null;
	}
	
	public boolean isDragging() {
		return dragging;
	}
	
	private Position updateDrag(DragSourceContext e) {
		Chart pane = ((ChartView)ui.getViewSelected()).getChart();
		Point mp = pane.getMousePosition();
		Position p = pane.getPosition(pane.convertImage(mp));
		if (p!=null) {
			if (drag!=null) {
				drag.setPosition(p);
			} else {
				try {
					DragSourceContext dsc = e;
					Transferable tr = dsc.getTransferable();
					if (tr.isDataFlavorSupported(CreateableTransfer.createable)) {
						Createable data = (Createable)tr.getTransferData(CreateableTransfer.createable);
						AppModel model = ui.getModel();
						if (mp!=null) {
							try {
								drag = data.create(model.getWorld(),p);
							} catch (Exception ex) { }
						}
					}
				} catch (IOException ex) {
				} catch (UnsupportedFlavorException ex) {
				}
			}
		}
		return p;
	}

	public void dragDropEnd(DragSourceDropEvent e) {
		if ((drag!=null) && (e.getDropSuccess() && e.getDropAction() == DnDConstants.ACTION_LINK)) {
			Point c = e.getDragSourceContext().getTrigger().getDragOrigin();
			BasicTreeNode node = (BasicTreeNode)ui.getTree().getPathForLocation(c.x,c.y).getLastPathComponent();
			node.setEnabled(false);
			dragged.put(drag.getID() != null ? drag.getID().toString() : "", node);
			drag.free();
			drag = null;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ui.getTree().repaint();	
				}
			});
		}
		dragging = false;
		//ui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void dragEnter(DragSourceDragEvent e) {
		if (!(ui.getViewSelected() instanceof ChartView)) return;
		DragSourceContext dsc = e.getDragSourceContext();
		updateDrag(dsc);
	}
	
	public void dragOver(DragSourceDragEvent e) {
		if (!(ui.getViewSelected() instanceof ChartView)) return; 
		DragSourceContext dsc = e.getDragSourceContext();
		Position p = updateDrag(dsc);
		if ((p==null) || (drag==null)) return;
		Chart pane = ((ChartView) ui.getViewSelected()).getChart();
		if (((drag.isWatercraft()) && (pane.isWater(p))) || (drag.isAircraft())) {
			//ui.setCursor(DragSource.DefaultLinkDrop);
			dsc.setCursor(DragSource.DefaultLinkDrop);
		} else {
			//ui.setCursor(DragSource.DefaultLinkNoDrop);
			dsc.setCursor(DragSource.DefaultLinkNoDrop);
		}
	}
		
	public void dragExit(DragSourceEvent e) {
		//ui.setCursor(DragSource.DefaultLinkNoDrop);
		e.getDragSourceContext().setCursor(DragSource.DefaultLinkNoDrop);
	}
	
	public void dropActionChanged(DragSourceDragEvent e) {}
	
	public void dragGestureRecognized(DragGestureEvent e) { // drag startet
		dragging = false;
		if ((ui.getModel().getWorld().isExercising()) || (!(ui.getViewSelected() instanceof ChartView))) return;
		Point clickPoint = e.getDragOrigin();
		TreePath path = ui.getTree().getPathForLocation(clickPoint.x, clickPoint.y);
		if (path == null) return;
		BasicTreeNode draggedNode = (BasicTreeNode) path.getLastPathComponent();
		if ((draggedNode.isEnabled()) && (draggedNode.getUserObject() instanceof Createable)) {
			CreateableTransfer trans = new CreateableTransfer((Createable)(draggedNode.getUserObject()));
			//source.startDrag(e, null, new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB), new Point(), trans, this);	
			source.startDrag(e,DragSource.DefaultLinkNoDrop, new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB), new Point(), trans, this);
			//ui.setCursor(DragSource.DefaultLinkNoDrop);
			dragging = true;
		}
	}
	
}
