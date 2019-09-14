/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.main.asset;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.layout.GBC;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.World;

import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.awt.event.ItemEvent;

public class AssetControl extends JPanel implements ItemListener, ChangeListener {

	private AssetControlElements hdg = null;
	private AssetControlElements spd = null;
	private AssetControlElements dep = null;
	private AssetControlElements hei = null;
	private AssetControlOptionElements son = null;
	private AssetControlOptionElements noi = null;

	private JRadioButton rdbtnHDG = null;
	private JRadioButton rdbtnSPD = null;
	private JRadioButton rdbtnHEI = null;
	private JRadioButton rdbtnDEP = null;
	private JRadioButton rdbtnNOI = null;
	private JRadioButton rdbtnSON = null;
	
	private JToggleButton rotateP = null;
	private JToggleButton rotateS = null;	
	
	private World world = null;
	private int panelHeight = 0;
	
	private Hashtable<Asset,JRadioButton> selectLookup; // improve: assign AssetControl for any object
	
	public AssetControl(final World world, int panelHeight) {
		
		this.selectLookup = new Hashtable<Asset,JRadioButton>();
		
		this.world = world;
		this.panelHeight = panelHeight;
		
		hdg = new AssetControlElements(AssetControlElements.HEADING);
		spd = new AssetControlElements(AssetControlElements.SPEED);
		dep = new AssetControlElements(AssetControlElements.DEPTH);
		hei = new AssetControlElements(AssetControlElements.HEIGHT);
		son = new AssetControlOptionElements(AssetControlOptionElements.SONAR);
		noi = new AssetControlOptionElements(AssetControlOptionElements.NOISE);

		rotateP = new JToggleButton("<");
		rotateS = new JToggleButton(">");
		
		rotateP.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Asset p = hdg.getAsset();
				if (p==null) return;
				if (e.getStateChange()==ItemEvent.SELECTED) {
					rotateS.setSelected(false);
					p.setRotation(-1);
				} else {
					if (!rotateS.isSelected()) p.setRotation(0);					
				}
			}
		});
		
		rotateS.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Asset p = hdg.getAsset();
				if (p==null) return;
				if (e.getStateChange()==ItemEvent.SELECTED) {
					rotateP.setSelected(false);
					p.setRotation(+1);
				} else {
					if (!rotateP.isSelected()) p.setRotation(0);
				}
			}
		});
		
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		//										   hdg  spd  dep  hei  son  noi  val    +    -
		//											 0    1    2    3    4    5    6    7    8
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0};
		
		setLayout(gridBagLayout);
		
		// ***
		
		rdbtnHDG = new JRadioButton("Heading");
		rdbtnHDG.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (world.getSelected()!=null) selectLookup.put(world.getSelected(),rdbtnHDG);
					hdg.setVisible(true);
					rotateP.setVisible(true);
					rotateS.setVisible(true);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {	
					hdg.setVisible(false);
					rotateP.setVisible(false);
					rotateS.setVisible(false);
				}
			}
		});
		GBC gbc_rdbtnHDG = new GBC(0).setInsets(5,5,5,5).setFill(GBC.FILL.VERTICAL);
		add(rdbtnHDG, gbc_rdbtnHDG);
		
		// ***
		
		rdbtnSPD = new JRadioButton("Speed");
		rdbtnSPD.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (world.getSelected()!=null) selectLookup.put(world.getSelected(),rdbtnSPD);
					spd.setVisible(true);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {	
					spd.setVisible(false);
				}
			}
		});
		GBC gbc_rdbtnSPD = new GBC(1).setInsets(5,5,5,5).setFill(GBC.FILL.VERTICAL);
		add(rdbtnSPD, gbc_rdbtnSPD);
		
		// ***
		
		rdbtnDEP = new JRadioButton("Depth");
		rdbtnDEP.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (world.getSelected()!=null) selectLookup.put(world.getSelected(),rdbtnDEP);
					dep.setVisible(true);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {	
					dep.setVisible(false);
				}
			}
		});	
		GBC gbc_rdbtnDEP = new GBC(2).setInsets(5,5,5,5).setFill(GBC.FILL.VERTICAL);
		add(rdbtnDEP, gbc_rdbtnDEP);
		
		// ***
		
		rdbtnHEI = new JRadioButton("Altitude");
		rdbtnHEI.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (world.getSelected()!=null) selectLookup.put(world.getSelected(),rdbtnHEI);
					hei.setVisible(true);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {	
					hei.setVisible(false);
				}
			}
		});
		GBC gbc_rdbtnHEI = new GBC(3).setInsets(5,5,5,5).setFill(GBC.FILL.VERTICAL);
		add(rdbtnHEI, gbc_rdbtnHEI);
		
		// ***
		
		rdbtnSON = new JRadioButton("Sonar");
		rdbtnSON.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (world.getSelected()!=null) selectLookup.put(world.getSelected(),rdbtnSON);
					son.setVisible(true);
					son.setEnabled(false);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {	
					son.setVisible(false);
				}
			}
		});
		GBC gbc_rdbtnSON = new GBC(4).setInsets(5,5,5,5).setFill(GBC.FILL.VERTICAL);
		add(rdbtnSON, gbc_rdbtnSON);

		rdbtnNOI = new JRadioButton("Noise");
		rdbtnNOI.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (world.getSelected()!=null) selectLookup.put(world.getSelected(),rdbtnNOI);
					noi.setVisible(true);
					noi.setEnabled(false);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					noi.setVisible(false);
				}
			}
		});
		GBC gbc_rdbtnNOI = new GBC(5).setInsets(5,5,5,5).setFill(GBC.FILL.VERTICAL);
		add(rdbtnNOI, gbc_rdbtnNOI);
		
		// ***
		
		GBC gbc_panel = new GBC(6).setInsets(5,5,5,5).setFill(GBC.FILL.HORIZONTAL);
		
		hdg.setVisible(false);
		spd.setVisible(false);
		dep.setVisible(false);
		hei.setVisible(false);
		noi.setVisible(false);
		son.setVisible(false);
		
		add(hdg, gbc_panel);
		add(spd, gbc_panel);
		add(dep, gbc_panel);
		add(hei, gbc_panel);
		add(noi, gbc_panel);
		add(son, gbc_panel);
		
		GBC gbc_toggleP = new GBC(7).setInsets(5,0,5,0).setFill(GBC.FILL.BOTH);
		add(rotateP,gbc_toggleP);
		
		GBC gbc_toggleS = new GBC(8).setInsets(5,5,5,10).setFill(GBC.FILL.BOTH);
		add(rotateS,gbc_toggleS);
		
		world.addItemListener(this);
		world.addChangeListener(spd);
		
		world.addChangeListener(this);
		
	    ButtonGroup group = new ButtonGroup();
	    group.add(rdbtnHDG);
	    group.add(rdbtnSPD);
	    group.add(rdbtnDEP);
	    group.add(rdbtnHEI);
		group.add(rdbtnNOI);
	    group.add(rdbtnSON);
	    
	    rdbtnHDG.setEnabled(false);
	    rdbtnSPD.setEnabled(false);
	    rdbtnDEP.setVisible(false);
	    rdbtnHEI.setVisible(false);
		rdbtnNOI.setVisible(false);
	    rdbtnSON.setVisible(false);
	    
	    rdbtnHDG.setSelected(true); 
	    
	    rotateP.setEnabled(false);
	    rotateS.setEnabled(false);

	} 

	public void free() {

		selectLookup.clear();
				
		world.removeItemListener(this);
		world.removeChangeListener(spd);
		
		world = null;
		
		hdg.free();
		spd.free();
		dep.free();
		hei.free();
		noi.free();
		son.free();
		
	}
	
    public void stateChanged(ChangeEvent e) {
        Asset p = hdg.getAsset();
        if (p!=null) {
            if (p.getRotation()==0) {
                rotateP.setSelected(false);
                rotateS.setSelected(false);
            }
        }
    }
	   
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		Asset asset = world.getSelected();
		
		hdg.setAsset(asset);
		spd.setAsset(asset);
		dep.setAsset(asset);
		hei.setAsset(asset);
		noi.setAsset(asset);
		son.setAsset(asset);
		
		if ((e.getStateChange()==ItemEvent.SELECTED) && (asset!=null)) {
			
			if (asset.isSubmarine()) {
			    rdbtnDEP.setVisible(true);
			    rdbtnHEI.setVisible(false);
			} else if (asset.isHelicopter() || asset.isPlane()) {
			    rdbtnDEP.setVisible(false);
			    rdbtnHEI.setVisible(true);
			} else {
			    rdbtnDEP.setVisible(false);
			    rdbtnHEI.setVisible(false);
			}
			
		    rdbtnSON.setVisible(asset.hasSonar());
			rdbtnNOI.setVisible(asset.isWatercraft());

			rdbtnSON.setEnabled(false);
			rdbtnNOI.setEnabled(false);

		    rdbtnHDG.setEnabled(true);
		    rdbtnSPD.setEnabled(true);
		    
		    rotateP.setEnabled(true);
		    rotateS.setEnabled(true);
		    
		    if (asset.getRotation()!=0) {
			    if (asset.getRotation()<0) {
			    	rotateP.setSelected(true);
			    } else {
				    rotateS.setSelected(true);			    	
			    }
		    } else {
		    	rotateP.setSelected(false);
			    rotateS.setSelected(false);			    			    	
		    }
		    
		} else if (e.getStateChange()==ItemEvent.DESELECTED) {
			
			rdbtnHDG.setEnabled(false);
		    rdbtnSPD.setEnabled(false);
		    rdbtnDEP.setVisible(false);
		    rdbtnHEI.setVisible(false);
			rdbtnNOI.setVisible(false);
		    rdbtnSON.setVisible(false);
		    
		    rotateP.setEnabled(false);
		    rotateS.setEnabled(false);
		 
		    rotateP.setSelected(false);
		    rotateS.setSelected(false);

		}
		
		JRadioButton temp = asset!=null ? selectLookup.get(asset) : null;
		if (temp==null) temp = rdbtnHDG;
		temp.setSelected(true);
	    
	    this.revalidate();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width,panelHeight);
	}
	
}
