/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.main.world;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.layout.GBC;
import de.m6c7l.playmate.main.VALUE;
import de.m6c7l.playmate.main.World;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WorldControl extends JPanel implements ChangeListener {

	private WorldControl self = null;
	
	private WorldControlElements tim = null;
	private WorldControlElements qui = null;
	
	private World world = null;
	
	private JLabel lblTime = null;	
	private JButton btnStartStop = null;
	
	public WorldControl(World world) {
		
		this.self = this;
		this.world = world;
		
		tim = new WorldControlElements(WorldControlElements.TIMELINE,world);
		qui = new WorldControlElements(WorldControlElements.QUICKMOTION,world);
		
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		tim.setVisible(true);
		qui.setVisible(false);
		
		tim.setMinimum(0);
		tim.setMaximum(world.getTimeLast());
		tim.setValue(world.getTime());
		
		qui.setMinimum(1);
		qui.setMaximum(world.getMaximumTimeSpeedup());
		qui.setValue(world.getTimeSpeedup());
		
		btnStartStop = new JButton("Start");	
		btnStartStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JButton btn = (JButton)e.getSource();
				if (self.world.isStopped()) {
					if (self.world.start()) startIt();
				} else {
					if (self.world.stop()) stopIt();
				}
			}
		});
		
		// ***
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0};
		
		setLayout(gridBagLayout);

		GBC gbc_btn = new GBC(0).setInsets(5,5).setFill(GBC.FILL.VERTICAL);
		add(btnStartStop, gbc_btn);
		
		// ***
		
		GBC gbc_panel = new GBC(1).setFill(GBC.FILL.HORIZONTAL).setInsets(0,3);
		add(tim, gbc_panel);
		add(qui, gbc_panel);
		
		// ***
		
		lblTime = new JLabel(VALUE.TIME(world.getTimeLast()));
		lblTime.setDoubleBuffered(true);
		
		GBC gbc_lbl = new GBC(2).setInsets(5,10,5,10).setFill(GBC.FILL.VERTICAL);
		add(lblTime, gbc_lbl);
		
		world.addChangeListener(this);		
		world.addChangeListener(tim);
		world.addChangeListener(qui);

	} 
	
	public void free() {
		
		world.removeChangeListener(tim);
		world.removeChangeListener(qui);
		world.removeChangeListener(this);
		
		world = null;
		
		tim.free();
		qui.free();
		
	}
	
	private void startIt() {
		btnStartStop.setText("Stop");
		tim.setVisible(false);
		qui.setVisible(true);
		qui.setValue(world.getTimeSpeedup());
		
//		if (world.isExercising()) {
//			world.setTimeSpeedup(1);
//			qui.setEnabled(false);
//		} else {
//			qui.setEnabled(true);
//		}
	}
	
	private void stopIt() {
		btnStartStop.setText("Start");
		tim.setVisible(true);
		qui.setVisible(false);
		tim.setValue(world.getTime());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (world.isStarted()) {
			lblTime.setText(VALUE.TIME(world.getTime()));
			if (world.isTimePresent())
				lblTime.setForeground(new JLabel().getForeground());
			else
				lblTime.setForeground(Color.red.brighter());	
		} else {
			if (!tim.isVisible()) {
				stopIt();
			}
		}
	}
	
}
