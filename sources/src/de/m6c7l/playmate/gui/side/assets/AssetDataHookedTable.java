/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.assets;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.container.LabelPanel;
import de.m6c7l.lib.util.NAUTIC;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.VALUE;
import de.m6c7l.playmate.main.World;

public class AssetDataHookedTable extends LabelPanel implements ChangeListener, ItemListener {
	
	private World world = null;
	private Asset hooked = null;
	
	public AssetDataHookedTable(World model) {

		super(new int[] {2, 4, 4, 3}, 3, new int[] {-1, +1, -1}, 3, 9, 6, true);
		
		this.world = model;
		
		this.world.addChangeListener(this);
		this.world.addItemListener(this);
		
		setBrackets(new String[] {" [ ", " ] "});
		
		init();
		reset();
		
	}

	public void free() {
		
		this.world.removeChangeListener(this);
		this.world.removeItemListener(this);
		
		hooked = null;
		world = null;
		
	}
	
	private void applyItem() {
		hooked = world.getHooked();
		applyChange();
	}
		
	private void applyChange() {

		if (!this.isShowing()) return; // cursor flickering

		Asset temp = hooked;
		Asset obs = world.getObserver();
		
		if ((temp==null) || (obs==null)) {
			reset();
			setEnabled(false);
			return;
		} else if (obs==hooked) {
			init();
			setEnabled(false);			
			return;
		} else {
			setEnabled(true);			
		}

		NAUTIC.CPA _cpa_ = obs.getCPA(temp);

		String[] bcpa = VALUE.TRUEBEARING(_cpa_.getBearing());
		String[] dcpa = VALUE.RANGE(_cpa_.isApproaching() ? _cpa_.getDistance(): null);
		String[] rbcpa = VALUE.BEARING(_cpa_.isApproaching() && _cpa_.getBearing() != null ? NAUTIC.getBearingRelative(_cpa_.getBearing(),obs.getHeading()) : null);
		String tcpa = VALUE.TIME(_cpa_.isApproaching() ? _cpa_.getTime() : null);
		String[] dot = VALUE.RANGE(obs.getDistanceOffTrack(temp));

		Double vbrg = obs.getBearing(temp);
			
		String[] aob = VALUE.ANGLEBOW(obs.getAngleOnBow(temp));
		String[] rng = VALUE.RANGE(obs.getRange(temp));
		String[] brg = VALUE.TRUEBEARING(vbrg);
		String[] rbrg = VALUE.BEARING(obs.getBearingRelative(temp));
		String[] br = VALUE.BEARINGRATE(obs.getBearingRate(temp));
	
		String[] rsa = VALUE.SPEED(obs.getSpeedResultantAcross(temp));
		String[] osa = VALUE.SPEED(obs.getSpeedOwnAcross(temp));
		String[] tsa = VALUE.SPEED(obs.getSpeedTargetAcross(temp));
			
		String[] rsl = VALUE.SPEED(obs.getSpeedResultantAlong(temp));
		String[] osl = VALUE.SPEED(obs.getSpeedOwnAlong(temp));
		String[] tsl = VALUE.SPEED(obs.getSpeedTargetAlong(temp));
		
		init();
		setColumn(1,new String[] { 	brg[0],	rng[0],		rbrg[0],	br[0],	aob[0],	dot[0],		dcpa[0],	bcpa[0],	rbcpa[0],	tcpa,	rsa[0],	osa[0],	tsa[0],	rsl[0],	osl[0],	tsl[0]});
		setColumn(2,new String[] { 	brg[1],	rng[1],		rbrg[1],	br[1],	null,	dot[1],		dcpa[1],	bcpa[1],	rbcpa[1],	null,	rsa[1],	osa[1],	tsa[1],	rsl[1],	osl[1],	tsl[1]});
		
	}
	
	private void init() {
		
		setColumn(0,new String[] {	"BRG",	"RNG",		"RBRG",		"BR",	"AOB",	"DOT",		"CPA",		"BCPA",		"RBCPA",	"TCPA",	"RSA",	"OSA",	"TSA",	"RSL",	"OSL",	"TSL"});
		
	}
	
	private void reset() {

		clearColumn(1);
		clearColumn(2);		

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
