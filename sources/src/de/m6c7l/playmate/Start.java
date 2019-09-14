/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.m6c7l.lib.gui.dialog.ExceptionDialog;
import de.m6c7l.lib.gui.splash.Splash;
import de.m6c7l.lib.gui.splash.SplashTask;
import de.m6c7l.lib.util.MATH;
import de.m6c7l.lib.util.file.FileUtils;
import de.m6c7l.lib.util.jar.JarUtils;
import de.m6c7l.lib.util.sound.Sound;
import de.m6c7l.lib.util.timer.Delay;
import de.m6c7l.playmate.gui.App;
import de.m6c7l.playmate.gui.AppModel;
import de.m6c7l.playmate.gui.AppUI;
import de.m6c7l.playmate.main.World;
import de.m6c7l.playmate.main.io.XMLAssetReader;
import de.m6c7l.playmate.main.io.XMLChartReader;
import de.m6c7l.playmate.main.io.XMLSettingsReader;

public class Start {
    
	private static URI uriAssets = null;
	private static URI uriCharts = null;
	private static URI uriSplash = null;
	private static URI uriSound = null;
	private static URI uriSettings = null;
	private static URI uriBase = null;

   private static UIManager.LookAndFeelInfo getPreferredLookAndFeel() {
        final String preferredClassNames[] = {
                "MetalLookAndFeel",
                "NimbusLookAndFeel",
        };
        final UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
        for (final String preferredClassName : preferredClassNames) {
            for (UIManager.LookAndFeelInfo anInstalled : installed) {
                if (anInstalled.getClassName().endsWith(preferredClassName)) {
                    return anInstalled;
                }
            }
        }
        return null;
    }
    
    private static boolean setCustomLookAndFeel(String[] lookAndFeelClassNames) {
        for (final String lookAndFeelClassName : lookAndFeelClassNames) {
            if (!UIManager.getLookAndFeel().getClass().getName().equals(lookAndFeelClassName)) {
                try {
                    UIManager.setLookAndFeel(lookAndFeelClassName);
                    return true;
                } catch (Exception exception) {
                }
            } else {
                return true;
            }
        }
        return false;
    }
    
	public static void main(String[] args) throws MalformedURLException {
		new Start();
	}
	
	public Start() {

		try {

			File jarFile = new File(FileUtils.toURI(JarUtils.getJAR(Start.class)));
			uriBase = jarFile.getParentFile().toURI();
			
			uriAssets = new URI(uriBase + "data/assets.xml");
			uriCharts = new URI(uriBase + "data/charts.xml");
            uriSettings = new URI(uriBase + "data/settings.xml");
            
			uriSplash = FileUtils.toURI(ClassLoader.getSystemResource("resources/image/splash.png"));
			uriSound = FileUtils.toURI(ClassLoader.getSystemResource("resources/sound/background.wav"));

			// BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;   
			// BeautyEyeLNFHelper.translucencyAtFrameInactive =  false;
		    
			if (!setCustomLookAndFeel(new String[] {
				
				// "net.infonode.gui.laf.InfoNodeLookAndFeel",
				// "com.seaglasslookandfeel.SeaGlassLookAndFeel",
				// "org.jb2011.lnf.beautyeye.BeautyEyeLookAndFeelCross",
				// "com.alee.laf.WebLookAndFeel",
				// "com.jtattoo.plaf.aero.AeroLookAndFeel",
				// "com.lipstikLF.LipstikLookAndFeel",
				// "de.muntjak.tinylookandfeel.TinyLookAndFeel",
				// "com.pagosoft.plaf.PgsLookAndFeel",
				// "com.nilo.plaf.nimrod.NimRODLookAndFeel",
				// "com.jgoodies.looks.plastic.Plastic3DLookAndFeel",
				// "com.jgoodies.looks.plastic.PlasticXPLookAndFeel",
				// "com.jgoodies.looks.plastic.PlasticLookAndFeel",
				
				})) {
				
				UIManager.LookAndFeelInfo prefLAF = getPreferredLookAndFeel();
				if (prefLAF != null) {
					UIManager.setLookAndFeel(prefLAF.getClassName());
				} else {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				}
			}
			
		    UIManager.put("RootPane.setupButtonVisible", false);
			UIManager.put("TabbedPane.tabAreaInsets", new javax.swing.plaf.InsetsUIResource(2,2,2,2));
			
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					
					Splash splash = null;
					try {
						splash = new Splash(new ImageIcon(uriSplash.toURL()));
					} catch (MalformedURLException e) {
						new ExceptionDialog(ExceptionDialog.ERROR, Start.class, e).setVisible(true);
					}
					
					int delay = 333;
					
					SplashTask[] tasks = new SplashTask[] {

							/* 0 */	new SplashTask(
										"Settings ...",
										XMLSettingsReader.class,
										new Class[] { URI.class },
										new Object[] { uriSettings }),
										
							/* 1 */	new SplashTask(
										new Delay((int)Math.max(50,Math.min(MATH.getGaussian(delay,delay/2.0),delay*2))),
										"execute", new Class[] {}, new Object[] {}),
										
							/* 2 */	new SplashTask(
										"Objects ...",
										XMLAssetReader.class,
										new Class[] { URI.class },
										new Object[] { uriAssets }),
											
							/* 3 */	new SplashTask(
										new Delay((int)Math.max(50,Math.min(MATH.getGaussian(delay,delay/2.0),delay*2))),
										"execute", new Class[] {}, new Object[] {}),

							/* 4 */	new SplashTask(
										"Charts ...",
										XMLChartReader.class,
										new Class[] { URI.class },
										new Object[] { uriCharts }),
											
							/* 5 */	new SplashTask(
										new Delay((int)Math.max(50,Math.min(MATH.getGaussian(delay,delay/2.0),delay*2))),
										"execute", new Class[] {}, new Object[] {}),

							/* 6 */	new SplashTask(
										"Ambience ...",
										Sound.class,
										new Class[] { URI.class },
										new Object[] { uriSound },
										true),
											
							/* 7 */	new SplashTask(
										new Delay((int)Math.max(50,Math.min(MATH.getGaussian(delay,delay/2.0),delay*2))),
										"execute", new Class[] {}, new Object[] {}),

							/* 8 */	new SplashTask(
										"Environment ...",
										World.class,
										new Class[] {},
										new Object[] {}),
													
							/* 9 */	new SplashTask(
										new Delay((int)Math.max(50,Math.min(MATH.getGaussian(delay,delay/2.0),delay*2))),
										"execute", new Class[] {}, new Object[] {}),

							/* 10*/	new SplashTask(
										"Simulator ...",
										AppModel.class,
										new Class[] {World.class, XMLChartReader.class, XMLAssetReader.class, XMLSettingsReader.class, Sound.class},
										new Object[] {splash.get(8), splash.get(4), splash.get(2), splash.get(0), splash.get(6)}),
											
							/* 11*/	new SplashTask(
										AppUI.class,
										new Class[] {AppModel.class},
										new Object[] {splash.get(10)}),										

							/* 12*/	new SplashTask(
										App.class,
										new Class[] {AppUI.class, URI.class, URI.class},
										new Object[] {splash.get(11), uriSettings, uriBase}),	
												
					};						

					splash.add(tasks);
					splash.execute();
					
				}
			});

		} catch (Exception e) {
			new ExceptionDialog(ExceptionDialog.ERROR, Start.class, e).setVisible(true);
		}

	}
		
}
