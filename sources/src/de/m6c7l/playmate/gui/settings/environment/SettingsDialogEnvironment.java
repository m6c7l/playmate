/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.settings.environment;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.border.LineBorder;

import de.m6c7l.lib.gui.component.FORMAT;
import de.m6c7l.lib.gui.component.TextField;

public class SettingsDialogEnvironment extends JPanel {

	public SettingsDialogEnvironment() {
		
		setBorder(new LineBorder(SystemColor.windowBorder, 1, true));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 10, 10, 10, 10, 0};
		gridBagLayout.rowHeights = new int[]{10, 10, 10, 10, 10, 10, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblStromrichtung = new JLabel(" Stromrichtung");
		GridBagConstraints gbc_lblStromrichtung = new GridBagConstraints();
		gbc_lblStromrichtung.anchor = GridBagConstraints.WEST;
		gbc_lblStromrichtung.insets = new Insets(0, 0, 5, 5);
		gbc_lblStromrichtung.gridx = 1;
		gbc_lblStromrichtung.gridy = 1;
		add(lblStromrichtung, gbc_lblStromrichtung);

		TextField tfStromrichtung = new TextField(FORMAT.NUMBER_INTEGER_POSITIVE,true);
		tfStromrichtung.setPrototype("000");
		tfStromrichtung.setID(SettingsModelEnvironment.ID_VALUE_CURRENT_SET);
		GridBagConstraints gbc_tfStromrichtung = new GridBagConstraints();
		gbc_tfStromrichtung.anchor = GridBagConstraints.WEST;
		gbc_tfStromrichtung.insets = new Insets(0, 0, 5, 5);
		gbc_tfStromrichtung.gridx = 3;
		gbc_tfStromrichtung.gridy = 1;
		add(tfStromrichtung, gbc_tfStromrichtung);
		
		JLabel lblStromstaerke = new JLabel(" Stromgeschwindigkeit [m/s]");
		GridBagConstraints gbc_lblStromstaerke = new GridBagConstraints();
		gbc_lblStromstaerke.anchor = GridBagConstraints.WEST;
		gbc_lblStromstaerke.insets = new Insets(0, 0, 5, 5);
		gbc_lblStromstaerke.gridx = 1;
		gbc_lblStromstaerke.gridy = 2;
		add(lblStromstaerke, gbc_lblStromstaerke);
		
		TextField tfStromstaerke = new TextField(FORMAT.NUMBER_FLOAT_POSITIVE_POINT,true);
		tfStromstaerke.setPrototype("0.0");
		tfStromstaerke.setID(SettingsModelEnvironment.ID_VALUE_CURRENT_DRIFT);
		GridBagConstraints gbc_tfStromstaerke = new GridBagConstraints();
		gbc_tfStromstaerke.anchor = GridBagConstraints.WEST;
		gbc_tfStromstaerke.insets = new Insets(0, 0, 5, 5);
		gbc_tfStromstaerke.gridx = 3;
		gbc_tfStromstaerke.gridy = 2;
		add(tfStromstaerke, gbc_tfStromstaerke);
		
		JLabel lblSichtweite = new JLabel(" Sichtweite [m]");
		GridBagConstraints gbc_lblSichtweite = new GridBagConstraints();
		gbc_lblSichtweite.anchor = GridBagConstraints.WEST;
		gbc_lblSichtweite.insets = new Insets(0, 0, 5, 5);
		gbc_lblSichtweite.gridx = 1;
		gbc_lblSichtweite.gridy = 3;
		add(lblSichtweite, gbc_lblSichtweite);
		
		TextField tfSichtweite = new TextField(FORMAT.NUMBER_INTEGER_POSITIVE,true);
		tfSichtweite.setPrototype("00000");
		tfSichtweite.setID(SettingsModelEnvironment.ID_VALUE_VISUAL_RANGE);
		GridBagConstraints gbc_tfSichtweite = new GridBagConstraints();
		gbc_tfSichtweite.anchor = GridBagConstraints.WEST;
		gbc_tfSichtweite.insets = new Insets(0, 0, 5, 5);
		gbc_tfSichtweite.gridx = 3;
		gbc_tfSichtweite.gridy = 3;
		add(tfSichtweite, gbc_tfSichtweite);
		
		JLabel lblAkustischeReichweite = new JLabel(" Akustische Reichweite [m]");
		GridBagConstraints gbc_lblAkustischeReichweite = new GridBagConstraints();
		gbc_lblAkustischeReichweite.anchor = GridBagConstraints.WEST;
		gbc_lblAkustischeReichweite.insets = new Insets(0, 0, 5, 5);
		gbc_lblAkustischeReichweite.gridx = 1;
		gbc_lblAkustischeReichweite.gridy = 4;
		add(lblAkustischeReichweite, gbc_lblAkustischeReichweite);
		
		TextField tfAkustischeReichweite = new TextField(FORMAT.NUMBER_INTEGER_POSITIVE,true);
		tfAkustischeReichweite.setPrototype("00000");
		tfAkustischeReichweite.setID(SettingsModelEnvironment.ID_VALUE_ACOUSTIC_RANGE);
		GridBagConstraints gbc_tfAkustischeReichweite = new GridBagConstraints();
		gbc_tfAkustischeReichweite.anchor = GridBagConstraints.WEST;
		gbc_tfAkustischeReichweite.insets = new Insets(0, 0, 5, 5);
		gbc_tfAkustischeReichweite.gridx = 3;
		gbc_tfAkustischeReichweite.gridy = 4;
		add(tfAkustischeReichweite, gbc_tfAkustischeReichweite);
		
	}

}
