/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.cep;

import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import de.m6c7l.lib.gui.component.ComboBox;
import de.m6c7l.lib.gui.component.FORMAT;
import de.m6c7l.lib.gui.layout.GBC;
import de.m6c7l.playmate.main.ITEMS;
import de.m6c7l.playmate.main.VALUE;

public class CEPHeader extends JPanel implements MouseMotionListener {
	
	private static final String SEP = " \u00B7 ";
	
	private JLabel lblString = null;
	
	private Double bearing = null;
	private Long time = null;
	
	private CEP cep = null;
	
	private ComboBox cbScale = null;
	private ComboBox cbBearing = null;
	
	public CEPHeader(CEP pane) {
		
		this.cep = pane;
		
		Border border = new EtchedBorder(EtchedBorder.LOWERED, null, null);
		setBorder(border);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		
		GBC gbc_lbl = new GBC(0).setFill(GBC.FILL.VERTICAL).setInsets(5,10);	
		String[] b = VALUE.TRUEBEARING(0.0);
		this.lblString = new JLabel(VALUE.TIME(cep.getTimeReference()) + SEP + b[0]);
		add(lblString, gbc_lbl);
		
		GBC gbc_cbBearing = new GBC(1).setFill(GBC.FILL.VERTICAL).setInsets(5,5,5,5);
		cbBearing = new ComboBox(FORMAT.SOMETHING,false,false);
		cbBearing.addItem(ITEMS.BEARING.values());
		cbBearing.setSelectedItem(cep.getBearingCenter());
		add(cbBearing, gbc_cbBearing);
		cbBearing.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (e.getItem() instanceof ITEMS.BEARING) {
						cep.setBearingCenter((ITEMS.BEARING)e.getItem());
					}
				}
			}
		});
		
		GBC gbc_cbScale = new GBC(2).setFill(GBC.FILL.VERTICAL).setInsets(5,0,5,5);
		cbScale = new ComboBox(FORMAT.SOMETHING,false,false);
		cbScale.addItem(ITEMS.TIME.values());
		cbScale.setSelectedItem(cep.getTimeSpan());
		add(cbScale, gbc_cbScale);
		cbScale.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (e.getItem() instanceof ITEMS.TIME) {
						cep.setTimeSpan((ITEMS.TIME)e.getItem());
					}
				}
			}
		});
		
		apply();
		
		this.cep.addMouseMotionListener(this);

	} 
	
	public void free() {
		this.cep.removeMouseMotionListener(this);
		this.cep = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		this.bearing = cep.getBearing(e.getPoint());
		this.time = cep.getTime(e.getPoint());
		apply();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.bearing = cep.getBearing(e.getPoint());
		this.time = cep.getTime(e.getPoint());
		apply();
	}
	
	private void apply() {
		if (this.bearing!=null) {
			lblString.setEnabled(true);
			String[] b = VALUE.TRUEBEARING(this.bearing);
			lblString.setText(VALUE.TIME(this.time) + SEP + b[0]);
		} else {			
			lblString.setEnabled(false);
			//lblString.setText(" ");	
		}
	}

}
