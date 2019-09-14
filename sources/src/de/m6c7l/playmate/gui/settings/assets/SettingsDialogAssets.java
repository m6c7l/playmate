/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings.assets;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.LineBorder;

import de.m6c7l.lib.gui.component.CheckBox;
import de.m6c7l.lib.gui.component.ComboBox;
import de.m6c7l.lib.gui.component.FORMAT;
import de.m6c7l.playmate.gui.settings.SettingsModel;
import de.m6c7l.playmate.main.ITEMS;

import java.awt.SystemColor;

public class SettingsDialogAssets extends JPanel {

	public SettingsDialogAssets() {
		
		setBorder(new LineBorder(SystemColor.windowBorder));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout.columnWidths = new int[]{10, 10, 10, 10, 10};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout.rowHeights = new int[]{10, 10, 10, 10, 10, 10, 10, 10};
		setLayout(gridBagLayout);
		
		JLabel lblVector = new JLabel("Fahrtvektor");
		GridBagConstraints gbc_lblVector = new GridBagConstraints();
		gbc_lblVector.anchor = GridBagConstraints.WEST;
		gbc_lblVector.insets = new Insets(0, 0, 5, 5);
		gbc_lblVector.gridx = 1;
		gbc_lblVector.gridy = 1;
		add(lblVector, gbc_lblVector);
		
		ComboBox cbTrace = new ComboBox(FORMAT.SOMETHING, true, false);
		cbTrace.setID(SettingsModel.ID_VALUE_TRACE);
		cbTrace.addItem(ITEMS.MOTION_VECTOR.values());
		cbTrace.setPrototype(ITEMS.MOTION_VECTOR.typical());	
		
		GridBagConstraints gbc_cbTrace = new GridBagConstraints();
		gbc_cbTrace.anchor = GridBagConstraints.WEST;
		gbc_cbTrace.insets = new Insets(0, 0, 5, 5);
		gbc_cbTrace.gridx = 3;
		gbc_cbTrace.gridy = 1;
		add(cbTrace, gbc_cbTrace);
		
		JLabel lblTrace = new JLabel("Vergangenheitsspur");
		GridBagConstraints gbc_lblTrace = new GridBagConstraints();
		gbc_lblTrace.anchor = GridBagConstraints.WEST;
		gbc_lblTrace.insets = new Insets(0, 0, 5, 5);
		gbc_lblTrace.gridx = 1;
		gbc_lblTrace.gridy = 2;
		add(lblTrace, gbc_lblTrace);
		
		ComboBox cbVector = new ComboBox(FORMAT.SOMETHING, true, false);
		cbVector.setID(SettingsModel.ID_VALUE_VECTOR);
		cbVector.addItem(ITEMS.MOTION_VECTOR.values());
		cbVector.setPrototype(ITEMS.MOTION_VECTOR.typical());
		
		GridBagConstraints gbc_cbVector = new GridBagConstraints();
		gbc_cbVector.anchor = GridBagConstraints.WEST;
		gbc_cbVector.insets = new Insets(0, 0, 5, 5);
		gbc_cbVector.gridx = 3;
		gbc_cbVector.gridy = 2;
		add(cbVector, gbc_cbVector);
		
		JLabel lblShowVectors = new JLabel("wahrer Fahrtvektor");
		
		GridBagConstraints gbc_lblShowVectors = new GridBagConstraints();
		gbc_lblShowVectors.anchor = GridBagConstraints.WEST;
		gbc_lblShowVectors.insets = new Insets(0, 0, 5, 5);
		gbc_lblShowVectors.gridx = 1;
		gbc_lblShowVectors.gridy = 4;
		add(lblShowVectors, gbc_lblShowVectors);
		
		CheckBox cbShowVectors = new CheckBox();
		cbShowVectors.setID(SettingsModel.ID_SHOW_VECTORS);
		
		GridBagConstraints gbc_cbShowVectors = new GridBagConstraints();
		gbc_cbShowVectors.anchor = GridBagConstraints.WEST;
		gbc_cbShowVectors.insets = new Insets(5, 0, 5, 5);
		gbc_cbShowVectors.gridx = 3;
		gbc_cbShowVectors.gridy = 4;
		add(cbShowVectors, gbc_cbShowVectors);
		
		JLabel lblShowTrace = new JLabel("Vergangenheitsspur");
		
		GridBagConstraints gbc_lblShowTrace = new GridBagConstraints();
		gbc_lblShowTrace.anchor = GridBagConstraints.WEST;
		gbc_lblShowTrace.insets = new Insets(0, 0, 5, 5);
		gbc_lblShowTrace.gridx = 1;
		gbc_lblShowTrace.gridy = 5;
		add(lblShowTrace, gbc_lblShowTrace);
		
		CheckBox cbShowTrace = new CheckBox();
		cbShowTrace.setID(SettingsModel.ID_SHOW_TRACES);
		
		GridBagConstraints gbc_cbShowTrace = new GridBagConstraints();
		gbc_cbShowTrace.anchor = GridBagConstraints.WEST;
		gbc_cbShowTrace.insets = new Insets(5, 0, 5, 5);
		gbc_cbShowTrace.gridx = 3;
		gbc_cbShowTrace.gridy = 5;
		add(cbShowTrace, gbc_cbShowTrace);
		
	}

}
