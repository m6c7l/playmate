/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.assets;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.container.LabelPanel;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.VALUE;
import de.m6c7l.playmate.main.World;

public class AssetDataSelected extends JPanel implements ChangeListener, ItemListener {
	
	private World world = null;
	private LabelPanel panel = null;
	
	private Asset selected = null;
	private AssetChooser chooser = null;
	
	public AssetDataSelected(World model) {

		this.world = model;
		this.setLayout(new BorderLayout());
		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		chooser = new AssetSelectChooser(model);
		chooser.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		this.add(chooser,BorderLayout.NORTH);
		
		panel = new LabelPanel(new int[] {2, 2, 3}, 3, new int[]{-1,+1,-1}, 3, 9, 6);
		panel.setBrackets(new String[] {" [ ", " ] "});
		
		JPanel temp = new JPanel();
		temp.setBackground(SystemColor.text);
		temp.setLayout(new BorderLayout());
		temp.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		temp.add(panel,BorderLayout.NORTH);
		
		this.add(temp,BorderLayout.CENTER);
		
		this.world.addChangeListener(this);
		this.world.addItemListener(this);
		
		init();
		reset();

	}
	
	public void free() {
		
		this.world.removeChangeListener(this);
		this.world.removeItemListener(this);
		
		chooser.free();
		chooser = null;
		
		selected = null;
		world = null;
		
	}
	
	private void applyItem() {
		selected = world.getSelected();
		applyChange();
	}
		
	private void applyChange() {

		if (!this.isShowing()) return; // cursor flickering
		
		if (selected==null) {
			reset();
			panel.setEnabled(false);
			return;
		} else {
			panel.setEnabled(true);						
		}		
		
		Asset temp = selected;
		
		String[] pos = VALUE.POSITION(temp.getPosition());
		String[] cog = VALUE.HEADING(temp.getCourseOverGround());
		String[] sog = VALUE.SPEED(temp.getSpeedOverGround());
		String[] hdg = VALUE.HEADING(temp.getHeading());
		String[] spd = VALUE.SPEED(temp.getSpeed());
		String[] alt = temp.isWatercraft() ? VALUE.DRAFT(temp.getAltitude()) : VALUE.ALTITUDE(temp.getAltitude());
		
		init();
		panel.setColumn(1,new String[] { 	pos[0],	pos[2],	cog[0],	sog[0],	hdg[0],	spd[0],	alt[0]	});
		panel.setColumn(2,new String[] { 	pos[1],	pos[3],	cog[1],	sog[1],	hdg[1],	spd[1],	alt[1]	});
		
	}
	
	private void init() {
		
		panel.setColumn(0,new String[] {	"LAT",	"LON",	"COG",	"SOG",	"HDG",	"SPD",	"ALT"	});	
		
	}
	
	private void reset() {

		panel.clearColumn(1);
		panel.clearColumn(2);
		
	}
	
	public AssetChooser getChooser() {
		return this.chooser;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		applyChange();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		applyItem();
	}

}
