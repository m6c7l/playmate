/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.scenario;

import javax.swing.JPanel;

import de.m6c7l.lib.gui.tree.BasicRadioTreeNode;
import de.m6c7l.lib.gui.tree.BasicTreeModel;
import de.m6c7l.lib.gui.tree.BasicTreeNode;
import de.m6c7l.playmate.gui.window.chart.Chart;
import de.m6c7l.playmate.gui.window.chart.ChartView;
import de.m6c7l.playmate.main.io.XMLAsset;
import de.m6c7l.playmate.main.utility.AssetBuilder;
import de.m6c7l.playmate.main.utility.OwnShipBuilder;

public class TreeModel extends BasicTreeModel {
	
	private BasicTreeNode rootNode = null;
	
	private BasicTreeNode chartNode = null;
	private BasicTreeNode assetNode = null;
	
	private BasicTreeNode assetMilitaryNode = null;
	private BasicTreeNode assetCivilNode = null;
	
	private BasicTreeNode assetMilitaryVesselNode = null;
	private BasicTreeNode assetMilitarySubmarineNode = null;
	private BasicTreeNode assetMilitaryPlaneNode = null;
	private BasicTreeNode assetMilitaryHelicopterNode = null;
	
	private BasicTreeNode assetCivilVesselNode = null;
	private BasicTreeNode assetCivilPlaneNode = null;
	private BasicTreeNode assetCivilHelicopterNode = null;
	
	public TreeModel() {
		
		super(new BasicTreeNode("World",true));

		rootNode = this.getRoot();
			
		chartNode = new BasicTreeNode("Charts",true);
		
		assetNode = new BasicTreeNode("Assets",true);
		assetNode.add(new BasicTreeNode(new OwnShipBuilder(),false));
		
		assetMilitaryNode = new BasicTreeNode("military",true);
		
		assetMilitaryVesselNode = new BasicTreeNode("Vessel",true);
		assetMilitarySubmarineNode = new BasicTreeNode("Submarine",true);
		assetMilitaryPlaneNode = new BasicTreeNode("Plane",true);
		assetMilitaryHelicopterNode = new BasicTreeNode("Helicopter",true);
		
		assetCivilNode = new BasicTreeNode("civil",true);
		
		assetCivilVesselNode = new BasicTreeNode("Vessel",true);
		assetCivilPlaneNode = new BasicTreeNode("Plane",true);
		assetCivilHelicopterNode = new BasicTreeNode("Helicopter",true);
		
		assetNode.add(assetMilitaryNode);
		assetMilitaryNode.add(assetMilitaryVesselNode);
		assetMilitaryNode.add(assetMilitarySubmarineNode);
		assetMilitaryNode.add(assetMilitaryPlaneNode);
		assetMilitaryNode.add(assetMilitaryHelicopterNode);
		
		assetNode.add(assetCivilNode);
		assetCivilNode.add(assetCivilVesselNode);
		assetCivilNode.add(assetCivilPlaneNode);
		assetCivilNode.add(assetCivilHelicopterNode);
		
		/*
		 * ---
		 */
		
		rootNode.add(chartNode);
		rootNode.add(assetNode);

	}

	public void setChartsEnabled(boolean enabled) {
		for (int i=0; i<chartNode.getChildCount(); i++) {
			((BasicTreeNode)(chartNode.getChildAt(i))).setEnabled(enabled);
		}
	}

	public void free() {
		super.free();
		assetNode.removeAllChildren();
		assetNode.removeAllChildren();
		rootNode.removeAllChildren();
	}
	
	/*
	 * node
	 */
	
	public BasicTreeNode getNodeAsset() {
		return assetNode;
	}

	public BasicTreeNode getNodeChart() {
		return chartNode;
	}
	
	public BasicTreeNode getNodeAssetMilitary() {
		return assetMilitaryNode;
	}
	
	public BasicTreeNode getNodeAssetCivil() {
		return assetCivilNode;
	}
	
	public BasicTreeNode getNodeAssetMilitaryVessel() {
		return assetMilitaryVesselNode;
	}
	
	public BasicTreeNode getNodeAssetMilitarySubmarine() {
		return assetMilitarySubmarineNode;
	}
	
	public BasicTreeNode getNodeAssetMilitaryPlane() {
		return assetMilitaryPlaneNode;
	}
	
	public BasicTreeNode getNodeAssetMilitaryHelicopter() {
		return assetMilitaryHelicopterNode;
	}
	
	public BasicTreeNode getNodeAssetCivilVessel() {
		return assetCivilVesselNode;
	}
	
	public BasicTreeNode getNodeAssetCivilPlane() {
		return assetCivilPlaneNode;
	}
	
	public BasicTreeNode getNodeAssetCivilHelicopter() {
		return assetCivilHelicopterNode;
	}
	
	/*
	 * pane
	 */
	
	public void addPanel(JPanel pane) {
		if (pane instanceof ChartView) {
			BasicRadioTreeNode newNode = new BasicRadioTreeNode(pane,chartNode);
			newNode.setTooltipText(createTooltip(((ChartView)pane).getChart()));
			chartNode.add(newNode);
		} else {
			BasicRadioTreeNode newNode = new BasicRadioTreeNode(pane,rootNode);
			rootNode.add(newNode);
		}
	}

	public boolean isPanel(BasicTreeNode node) {
		return getPanel(node)!=null;
	}
	
	public JPanel getPanel(BasicTreeNode node) {
		if (node.hasUserObject() && node.isEnabled()) {
			if (node.getUserObject() instanceof JPanel) {
				return (JPanel)node.getUserObject();
			}
		}
		return null;
	}

	/*
	 * asset -> unit
	 */
	
	public void addXMLAsset(XMLAsset u) {
		BasicTreeNode node = null;
		if (u.isMilitary()) {
			if (u.isVessel()) 		node = this.assetMilitaryVesselNode; 		else
			if (u.isSubmarine()) 	node = this.assetMilitarySubmarineNode; 		else
			if (u.isPlane()) 		node = this.assetMilitaryPlaneNode; 			else
			if (u.isHelicopter()) 	node = this.assetMilitaryHelicopterNode;
		} else {
			if (u.isVessel()) 		node = this.assetCivilVesselNode; 			else
			if (u.isPlane()) 		node = this.assetCivilPlaneNode; 			else
			if (u.isHelicopter()) 	node = this.assetCivilHelicopterNode;
		}
		if (node!=null) {
			BasicTreeNode newNode = new BasicTreeNode(new AssetBuilder(u),false);
			newNode.setTooltipText(createTooltip(u));
			
			node.add(newNode);

		}
	}

	public boolean isXMLAsset(BasicTreeNode node) {
		return getXMLAsset(node)!=null;
	}
	
	public XMLAsset getXMLAsset(BasicTreeNode node) {
		if (node.hasUserObject()) {
			if (node.getUserObject() instanceof XMLAsset) {
				return (XMLAsset)node.getUserObject();
			}
		}
		return null;
	}
	
	/*
	 * tooltip
	 */
	
	private String createTooltip(XMLAsset asset) {
		return 	"<html>" +
				"<table>" +
				"<tr><td></td>" + "<td>" + asset + "</td></tr>" +
				"</table>" +
				"</html>";
	}
	
	private String createTooltip(Chart chart) {
		return 	"<html>" +
				"<table>" +
				"<tr><td></td>" + "<td>" + chart.getPositionOrigin() +	"</td></tr>" +
				"</table>" +
				"</html>";
	}


}
