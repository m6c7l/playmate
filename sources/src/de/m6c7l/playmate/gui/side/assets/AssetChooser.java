/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.side.assets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.m6c7l.lib.gui.layout.GBC;
import de.m6c7l.playmate.main.World;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class AssetChooser extends JPanel implements ItemListener {
	
	protected World world = null;
	protected int index = -1;
	
	protected JLabel lblView = null;
	
	public AssetChooser(World model) {

		this.world = model;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		lblView = new JLabel(" ");
		lblView.setFont(lblView.getFont().deriveFont(Font.BOLD));
		GBC gbc_lbl = new GBC(0).setFill(GBC.FILL.VERTICAL).setInsets(5,5);
		add(lblView, gbc_lbl);
		
		this.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {
				fireFocusEvent(false);
			}
			public void mouseEntered(MouseEvent e) {
				fireFocusEvent(true);	
			}
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON1) {
					setIndex(true);
				} else if (e.getButton()==MouseEvent.BUTTON3) {
					setIndex(false);
				}				
				fireFocusEvent(true);	
			}
		});
		
		world.addItemListener(this);
		
	}

	private void setIndex(boolean next) {
		setIndex(next,0);
	}

	public abstract void setIndex(boolean next, int attempts);
	
	public void free() {
		world.removeItemListener(this);
		world = null;
	}
	
	public abstract void apply();
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	public void itemStateChanged(ItemEvent e) {
		apply();
	}
	
	/*
	 * focus listener
	 */
		  
	private void fireFocusEvent(boolean entered) {
		FocusListener[] fl = getFocusListeners();
		FocusEvent e = new FocusEvent(this,FocusEvent.RESERVED_ID_MAX);
		if (entered) {
			for (FocusListener l : fl) {
				l.focusGained(e);	
			}							
		} else {
			for (FocusListener l : fl) {
				l.focusLost(e);	
			}			
		}
	}

}
