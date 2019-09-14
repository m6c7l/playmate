/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.splash;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.m6c7l.lib.gui.dialog.ExceptionDialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JProgressBar;

public class Splash extends JWindow {
	
	private JProgressBar progbar = null;
	private ArrayList<SplashTask> tasks = null;
	private Vector<Object> results = null;
	private Splash self = null;
	
	public Splash(final ImageIcon image) {
		
		self = this;

		progbar = new JProgressBar() {
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				return new Dimension(super.getPreferredSize().width,(int)(d.height*1.1));
			}
		};
		tasks = new ArrayList<SplashTask>();
		results = new Vector<Object>();
				
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JLabel label = new JLabel(image);		
		//label.setBorder(progbar.getBorder()); // FEHLER!!!! <- ist ja noch nicht realisiert <- Nimbus und EDT nehmens übel
				
		pane.add(label, BorderLayout.CENTER);
		pane.add(progbar, BorderLayout.SOUTH);

		setContentPane(pane);
				
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width/2)-(image.getIconWidth()/2),(screen.height/2)-(image.getIconHeight()/2));
		pack();

	}
	
	public void add(SplashTask task) {
		tasks.add(task);
	}
	
	public void add(SplashTask[] tasks) {
		for (int i=0; i<tasks.length; i++) {
			add(tasks[i]);
		}
	}
	
	public void execute() {
		setSteps(tasks.size());
		progbar.setValue(0);
		new Work(self).execute();
	}
	
	private void setSteps(final int steps) {
		progbar.setMinimum(0);
		progbar.setMaximum(steps-1);
	}
	
	private void setStep(final int step) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progbar.setValue(step);
			}
		});
	}

	private void setMessage(final String message) {
		String msg = message;
		if (msg == null) {
			msg = "";
			progbar.setStringPainted(false);
		} else {
			progbar.setStringPainted(true);
		}
		progbar.setString(msg);
	}

	public void setVisible(final boolean visible) {
		if (visible) toFront(); else toBack();
		super.setVisible(visible);
	}

	public Object get(int index) {
		if (index>=this.results.size())
			return new Transfer(this,index);
		return this.results.get(index);
	}
	
	protected static class Work extends SwingWorker<Object,String> { // first for doInBackground(), second for process()
		
		private Splash splash = null;
		private Exception ex = null;
		
		public Work(Splash splash) {
			this.splash = splash;
			this.splash.setVisible(true);
			this.splash.results.setSize(splash.tasks.size());
		}
		
		@Override
		protected void process(List<String> chunks) { // invoked by publish()
			if (!chunks.get(chunks.size()-1).equals("")) {
				splash.setMessage(chunks.get(chunks.size()-1));
			}
		}
		
		@Override
		protected Object doInBackground() { // can return something, its get()-able then
			for (int i=0; i<splash.tasks.size(); i++) {
				try {
					splash.results.set(i,splash.tasks.get(i).execute());
				} catch (Exception e) {
					e.printStackTrace();
					if (!splash.tasks.get(i).hasException()) {
						ex = e;
						return null;						
					}
				}
				splash.setStep(i);
				publish(splash.tasks.get(i).toString());											

			}
			return null;
		}
		
		@Override
		protected void done() { // invoked by doInBackground()
			splash.setVisible(false);
			if (ex!=null) {
				new ExceptionDialog(ExceptionDialog.ERROR, Splash.class, ex).setVisible(true);
			}
			splash.dispose();
		}
		
	}
	
	protected static class Transfer {

		private int index = 0;
		private Splash splash = null;
		
		public Transfer(Splash splash, int index) {
			this.index = index;
			this.splash = splash;
		}
		
		public Object get() {
			return this.splash.get(index);
		}

	}
	
}
