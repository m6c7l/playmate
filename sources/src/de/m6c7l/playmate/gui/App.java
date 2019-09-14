/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;

import de.m6c7l.lib.gui.dialog.ExceptionDialog;
import de.m6c7l.lib.gui.dialog.FileChooser;
import de.m6c7l.playmate.gui.settings.SettingsDialog;
import de.m6c7l.playmate.gui.settings.assets.SettingsDialogAssets;
import de.m6c7l.playmate.gui.settings.environment.SettingsDialogEnvironment;
import de.m6c7l.playmate.gui.settings.units.SettingsDialogUnits;
import de.m6c7l.playmate.main.Saveable;
import de.m6c7l.playmate.main.VALUE;
import de.m6c7l.playmate.main.World;
import de.m6c7l.playmate.main.io.XMLSettingsWriter;

public class App {
	
	private AppUI ui = null;

	private URI uriSetup = null;
	private URI uriJAR = null;
	
	
	public App(AppUI ui, URI setup, URI jar) throws Exception {
		
		this.ui = ui;
		
		this.uriSetup = setup;
		this.uriJAR = jar;
		
		initMenu();
		initWindow();
		
		this.ui.setVisible(true);

		applySetup();
		
	}
	
	private void applySetup() {
		
		VALUE.LU = this.ui.getModel().getSetupDisplay().getLength().toString();
		VALUE.LF = this.ui.getModel().getSetupDisplay().getLength().factor();
		
		VALUE.SU = this.ui.getModel().getSetupDisplay().getSpeed().toString();
		VALUE.SF = this.ui.getModel().getSetupDisplay().getSpeed().factor();
		
		VALUE.AU = this.ui.getModel().getSetupDisplay().getHeight().toString();
		VALUE.AF = this.ui.getModel().getSetupDisplay().getHeight().factor();
		
	}
	
	private void terminate() {

		AppModel model = ui.getModel();
		XMLSettingsWriter xml = new XMLSettingsWriter(uriSetup);
		try {
			xml.write("settings",model.getSetupDisplay().getValues());
		} catch (IOException e) {
			new ExceptionDialog(ExceptionDialog.WARNING, App.class, e).setVisible(true);
		}
		System.exit(0);
	}
	
	private void initWindow() {
		
		this.ui.addWindowListener(new WindowListener() {
			
			public void windowClosing(WindowEvent arg0) {
				terminate();
			}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
		
	}
	
	private void initMenu() {
		
		this.ui.getMenuItemExit().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				Object[] button = new Object[] {"Yes", "No"};
		        int option = JOptionPane.showOptionDialog(
		                		ui,
		                		"Quit?",
		                		null,
		                        JOptionPane.YES_NO_OPTION, 
		                        JOptionPane.QUESTION_MESSAGE,
		                        null,
		                        button,
		                        button[0]);                          
                if (option == JOptionPane.YES_OPTION) { 
    				terminate();
                }
			}
		});

		this.ui.getMenuItemNewEmpty().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {		
				
				Object[] button = new Object[] {"Yes", "No"};
		        int option = JOptionPane.showOptionDialog(
		                		ui,
		                		"Reset?",
		                		null,
		                        JOptionPane.YES_NO_OPTION, 
		                        JOptionPane.QUESTION_MESSAGE,
		                        null,
		                        button,
		                        button[0]);                          
                if (option == JOptionPane.YES_OPTION) { 
                	
    				try {
    					
    					Rectangle rec = ui.getBounds();
    					
    					ui.setEnabled(false);
    					
    					AppModel model = ui.getModel();
    					
    					ui.free();
    					
    					AppUI appui = new AppUI(model.reset());
    					
    					ui.dispose();
    					
    					ui = appui;
    					
    					initMenu();
    					initWindow();
    					
    					ui.setBounds(rec);
    					ui.setVisible(true);
    					
    					applySetup();
    					
    					Runtime.getRuntime().gc();
    					
    				} catch (Exception e1) {
    					
    					new ExceptionDialog(ExceptionDialog.ERROR, App.class, e1).setVisible(true);
    					
    				}
                }                
				
			}
		});
		
		this.ui.getMenuItemSaveImage().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if (ui.getViewSelected()!=null) {
		
						FileChooser fc = null;
						try {
							
							fc = new FileChooser(new File(uriJAR));
							fc.addChoosableFileFilter("png","PNGs");
							fc.setAcceptAllFileFilterUsed(false);
							
							if (fc.showSaveDialog(ui)==FileChooser.APPROVE) {
								
								if (ui.getViewSelected() instanceof Saveable) {
									((Saveable)ui.getViewSelected()).save(fc.getSelectedFile());
								}
									
							}
							
						} catch (Exception e1) {
							new ExceptionDialog(ExceptionDialog.WARNING, App.class, e1).setVisible(true);
						}
			
				}
				
			}
		});
		
		this.ui.getMenuItemExercise().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				World world = ui.getModel().getWorld();
				
				if (!((world.getObserver()!=null) && (world.getAssetCount()>1))) {	
					ui.getMenuItemExercise().setSelected(false);				
				}

				boolean exer = ui.getMenuItemExercise().isSelected();
				
				if (exer) {
					world.setSelected(world.getObserver());
					world.setHooked(null);
					ui.getTabbed().setSelectedIndex(1);
				}

				world.setExercising(exer);	
				ui.getTabbed().setEnabledAt(0,!exer);
				
				ui.getMenuItemInfoAssets().setEnabled(!exer);
				
			}
		});
		
		this.ui.getMenuItemSound().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ui.getMenuItemSound()==null) return;
				if (ui.getMenuItemSound().isSelected()) {
					ui.getModel().getSound().loop();
				} else {
					ui.getModel().getSound().stop();					
				}
			}
		});
		
		this.ui.getMenuItemUnits().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SettingsDialog setup = new SettingsDialog(ui,"Display",new SettingsDialogUnits(),null);
				setup.setVisible(true);
			}
		});
		
		this.ui.getMenuItemAssets().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SettingsDialog setup = new SettingsDialog(ui,"Objects",new SettingsDialogAssets(),null);
				setup.setVisible(true);
			}
		});
		
		this.ui.getMenuItemEnvironment().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SettingsDialog setup = new SettingsDialog(ui,"Environment",new SettingsDialogEnvironment(),null);
				setup.setVisible(true);
			}
		});

		this.ui.getMenuEditDelete().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ui.getModel().removeAsset(ui.getModel().getWorld().getSelected());
			}
		});

	}

}
