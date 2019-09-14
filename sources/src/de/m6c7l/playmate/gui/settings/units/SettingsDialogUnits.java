/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings.units;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.LineBorder;

import de.m6c7l.lib.gui.component.ComboBox;
import de.m6c7l.lib.gui.component.FORMAT;
import de.m6c7l.playmate.main.ITEMS;

import java.awt.SystemColor;

public class SettingsDialogUnits extends JPanel {

	public SettingsDialogUnits() {
		
		setBorder(new LineBorder(SystemColor.windowBorder));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 10, 10, 10, 10};
		gridBagLayout.rowHeights = new int[]{10, 10, 10, 10, 10};
		setLayout(gridBagLayout);
		
		JLabel lblLaenge = new JLabel("Entfernung");
		
		GridBagConstraints gbc_lblLaenge = new GridBagConstraints();
		gbc_lblLaenge.anchor = GridBagConstraints.WEST;
		gbc_lblLaenge.insets = new Insets(0, 0, 5, 5);
		gbc_lblLaenge.gridx = 1;
		gbc_lblLaenge.gridy = 1;
		add(lblLaenge, gbc_lblLaenge);
		
		ComboBox cbLaenge = new ComboBox(FORMAT.SOMETHING,true,false);
		cbLaenge.setID(SettingModelUnits.ID_UNIT_LENGTH);
		cbLaenge.addItem(ITEMS.LENGTH.values());
		cbLaenge.setPrototype(ITEMS.LENGTH.typical());
		
		GridBagConstraints gbc_cbLaenge = new GridBagConstraints();
		gbc_cbLaenge.anchor = GridBagConstraints.WEST;
		gbc_cbLaenge.insets = new Insets(0, 0, 5, 5);
		gbc_cbLaenge.gridx = 3;
		gbc_cbLaenge.gridy = 1;
		add(cbLaenge, gbc_cbLaenge);
		
		JLabel lblGeschwindigkeit = new JLabel("Geschwindigkeit");
		
		GridBagConstraints gbc_lblGeschwindigkeit = new GridBagConstraints();
		gbc_lblGeschwindigkeit.anchor = GridBagConstraints.WEST;
		gbc_lblGeschwindigkeit.insets = new Insets(0, 0, 5, 5);
		gbc_lblGeschwindigkeit.gridx = 1;
		gbc_lblGeschwindigkeit.gridy = 2;
		add(lblGeschwindigkeit, gbc_lblGeschwindigkeit);
		
		ComboBox cbGeschwindigkeit = new ComboBox(FORMAT.SOMETHING,true,false);
		cbGeschwindigkeit.setID(SettingModelUnits.ID_UNIT_SPEED);
		cbGeschwindigkeit.addItem(ITEMS.SPEED.values());
		cbGeschwindigkeit.setPrototype(ITEMS.SPEED.typical());	
		
		GridBagConstraints gbc_cbGeschwindigkeit = new GridBagConstraints();
		gbc_cbGeschwindigkeit.anchor = GridBagConstraints.WEST;
		gbc_cbGeschwindigkeit.insets = new Insets(0, 0, 5, 5);
		gbc_cbGeschwindigkeit.gridx = 3;
		gbc_cbGeschwindigkeit.gridy = 2;
		add(cbGeschwindigkeit, gbc_cbGeschwindigkeit);

		JLabel lblHoehe = new JLabel("H\u00f6he");
		
		GridBagConstraints gbc_lblHoehe = new GridBagConstraints();
		gbc_lblHoehe.anchor = GridBagConstraints.WEST;
		gbc_lblHoehe.insets = new Insets(0, 0, 5, 5);
		gbc_lblHoehe.gridx = 1;
		gbc_lblHoehe.gridy = 3;
		add(lblHoehe, gbc_lblHoehe);
		
		ComboBox cbHoehe = new ComboBox(FORMAT.SOMETHING,true,false);
		cbHoehe.setID(SettingModelUnits.ID_UNIT_HEIGHT);
		cbHoehe.addItem(ITEMS.HEIGHT.values());
		cbHoehe.setPrototype(ITEMS.HEIGHT.typical());	
		
		GridBagConstraints gbc_cbHoehe = new GridBagConstraints();
		gbc_cbHoehe.anchor = GridBagConstraints.WEST;
		gbc_cbHoehe.insets = new Insets(0, 0, 5, 5);
		gbc_cbHoehe.gridx = 3;
		gbc_cbHoehe.gridy = 3;
		add(cbHoehe, gbc_cbHoehe);

	}

}
