/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public abstract class CanvasPane extends JPanel {

	private RepaintTask rpt = null;
	
	public CanvasPane() {
		this(30);
	}
	
	public CanvasPane(int fixedFPS) {
		super(null,true);
		this.rpt = new RepaintTask(this,fixedFPS);
	}
	
	public Point getMousePosition() {
		Point p = super.getMousePosition();
		if (p==null) {
			Point m = MouseInfo.getPointerInfo().getLocation();
			Point s = this.getLocationOnScreen();
			p = new Point(m.x-s.x,m.y-s.y);			
		}
		return p;
	}
	
	public void free() {
		this.rpt.cancel();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if (this.isDisplayable() && this.isVisible()) {
			BufferedImage view = getCanvas();
			if ((view!=null) && isInit(view)) {
				if (!isFilled(view)) super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				try {
					Point loc = getOffset(view);
					Dimension siz = getSize(view);
					g2d.drawImage(view, loc.x, loc.y, siz.width, siz.height, null);
				} finally {
					g2d.dispose();
				}
			}
		}
	}

	private Point getOffset(BufferedImage img) { 
		return new Point(
				(getSize().width-img.getWidth())/2,
				(getSize().height-img.getHeight())/2);
	}
	
	private Dimension getSize(BufferedImage img) { 
		return new Dimension(
				img.getWidth(),
				img.getHeight());
	}
	
	private boolean isFilled(BufferedImage img) {
		Point off = getOffset(img);
		return (off.x<=0) && (off.y<=0);
	}
	
	private boolean isInit(BufferedImage img) { 
		Dimension d = getSize(img);
		return (d.getWidth()>0) && (d.getHeight()>0);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	public abstract boolean isInitialized();
	public abstract BufferedImage getCanvas();
	
	private static class RepaintTask extends TimerTask {
		
		private CanvasPane panel = null;
	    private Timer timer = null;
	    private boolean isInit = false;

		public RepaintTask(CanvasPane panel, int fps) {
			this.panel = panel;
			this.timer = new Timer();
			this.timer.scheduleAtFixedRate(this,0,1000/fps);
		}
		
		public boolean cancel() {
			boolean b = super.cancel();
			if (b) {
				this.timer.cancel();
				this.timer.purge();
				this.panel = null;
			}
			return b;
		}
		
		@Override
		public void run() {
			final CanvasPane temp = panel;
			if (temp!=null) {
				if (!isInit) {
					isInit = temp.isInitialized();
				} else {
					temp.repaint();
				}				
			}
		}
		
	}
	
	/*
	 * helper
	 */
	
	public static class IPoint {

		protected double x = 0;
		protected double y = 0;
		
		public IPoint() {
			this(0.0, 0.0);
		}
		
		public IPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public double getX() {
			return x;
		}
		
		public double getY() {
			return y;
		}
		
		public Point getLocation() {
			return new Point((int)Math.rint(x),(int)Math.rint(y));
		}

		public String toString() {
			return "[x=" + ((int)(x*1000))/1000.0 + ", y=" + ((int)(y*1000))/1000.0 + "]";
		}

	}
	
	public static class IDimension  {
		
		protected double width = 0;
		protected double height = 0;
		
		public IDimension() {
			this(0.0, 0.0);
		}
		
		public IDimension(double width, double height) {
			this.width = width;
			this.height = height;
		}
		
		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		public Dimension getSize() {
			return new Dimension((int)Math.rint(width),(int)Math.rint(height));
		}
		
		public String toString() {
			return "[width=" + ((int)(width*1000))/1000.0 + ", height=" + ((int)(height*1000))/1000.0 + "]";
		}
		
	}

	public static class IBox {
		
		private IPoint p = null;
		private IDimension d = null;
		private IPoint q = null;
		
		public IBox() {
			this(0.0, 0.0, 0.0, 0.0);
		}
		
		public IBox(IPoint p, IDimension d) {
			this.p = p;
			this.d = d;
			this.q = new IPoint(
					d.width > 0 ? p.x + d.width-1 : p.x,
					d.height > 0 ? p.y + d.height-1 : p.y);
		}
		
		public IBox(double x, double y, double width, double height) {
			this(new IPoint(x,y), new IDimension(width, height));
		}
		
		public double getX1() {
			return p.x;
		}
		
		public double getY1() {
			return p.y;
		}

		public double getX2() {
			return q.x;
		}
		
		public double getY2() {
			return q.y;
		}

		public double getWidth() {
			return d.width;
		}

		public double getHeight() {
			return d.height;
		}

		public String toString() {
			return "[" + p.toString() + ", " + q.toString() + ", " + d.toString() + "]"; 
		}
		
	}
	
}
