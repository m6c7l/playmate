/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class TOOLBOX {

	private TOOLBOX() {}
	
	public static Dimension getScreenSize() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();		
		return new Dimension(width, height);
	}
	
	public static Point[] getLine(Point start, Point end) { // Bresenham-Geraden-Algorithmus
		if (start.equals(end)) return new Point[] {start};
		int dx = end.x-start.x;
		int dy = end.y-start.y;
		int deltaX = Math.abs(dx);
		int deltaY = Math.abs(dy);
		int x = start.x;
		int y = start.y;
		int signX = (dx>0 ? +1 : (dx<0 ? -1 : 0));
		int signY = (dy>0 ? +1 : (dy<0 ? -1 : 0));
		Point[] line = new Point[Math.max(deltaX,deltaY)+1];
		line[0] = new Point(start.x,start.y);
		int n = 1;
		if (deltaX>=deltaY) { // x ist laufvariable
			int e = deltaX-2*deltaY;
			while (x!=end.x) {
				x = x + signX;
				if (e>0) { // y bleibt so
					e = e - 2*deltaY;
				} else {
					y = y + signY;
					e = e + 2 * (deltaX-deltaY);
				}
				line[n] = new Point(x,y);
				n++;
			}
		} else { // y ist laufvariable
			int e = deltaY-2*deltaX;
			while (y!=end.y) {
				y = y + signY;
				if (e>0) { // x bleibt so
					e = e - 2*deltaX;
				} else {
					x = x + signX;
					e = e + 2 * (deltaY-deltaX);
				}
				line[n] = new Point(x,y);
				n++;
			}
		}
		return line;
	}
	
	public static void drawDashedLine(Graphics2D g, int x1, int y1, int x2, int y2, double dashlength, double spacelength) {
		if ((x1 == x2) && (y1 == y2)) {
			g.drawLine(x1, y1, x2, y2);
			return;
		}
		double linelength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		//double yincrement = (y2 - y1) / (linelength / (dashlength + spacelength));
		double xincdashspace = (x2 - x1) / (linelength / (dashlength + spacelength));
		double yincdashspace = (y2 - y1) / (linelength / (dashlength + spacelength));
		double xincdash = (x2 - x1) / (linelength / (dashlength));
		double yincdash = (y2 - y1) / (linelength / (dashlength));
		int counter = 0;
		for (double i = 0; i < linelength - dashlength; i += dashlength + spacelength) {
			g.drawLine((int) (x1 + xincdashspace * counter),
					(int) (y1 + yincdashspace * counter), (int) (x1 + xincdashspace * counter + xincdash), (int) (y1 + yincdashspace * counter + yincdash));
			counter++;
		}
		if ((dashlength + spacelength) * counter <= linelength)
			g.drawLine((int) (x1 + xincdashspace * counter), (int) (y1 + yincdashspace * counter), x2, y2);
	}
	
	public static Dimension getSize(int cols, Font font) {
		Canvas c = new Canvas();
		FontMetrics fm = c.getFontMetrics(font);
		int height = fm.getMaxAscent()+fm.getMaxDescent()+fm.getLeading();
		int width = getMaxCharWidth(fm,"ABCDEFGHIJKLMNOPQRSTUVWXYZ")*cols;
		return new Dimension(width,height);
	}
	
	private static int getMaxCharWidth(FontMetrics fm, String s) {
		int max = 0;
		for (int i=0; i<s.length(); i++) {
			int x = fm.charWidth(s.charAt(i));
			if (x>max) max = x;
		}
		return max;
	}
	
	public static Dimension getSize(String text, Font font) {
		Canvas c = new Canvas();
		FontMetrics fm = c.getFontMetrics(font);
		int height = fm.getMaxAscent()+fm.getMaxDescent()+fm.getLeading();
		int width = fm.stringWidth(text);
		return new Dimension(width,height);
	}
	
	public static Image getGray(Image image) {
	    final int w = image.getWidth(null);
	    final int h = image.getHeight(null);
	    BufferedImage buffered = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
	    Graphics bg = buffered.getGraphics();
		bg.drawImage(image,0,0,null);
		bg.dispose();
		for (int y = 0; y < buffered.getHeight(); y++) {
		    for(int x = 0; x < buffered.getWidth(); x++) {
			    int rgb = filterRGB(buffered.getRGB(x,y));
			    buffered.setRGB(x,y,rgb);
		    }			
		}
		return buffered;
	}
	
	public static Icon getGray(Icon icon) {
	    final int w = icon.getIconWidth();
	    final int h = icon.getIconHeight();
	    BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
	    Graphics g = image.getGraphics();
	    icon.paintIcon(null, g, 0, 0);
	    g.dispose();
	    return new ImageIcon(getGray(image));
	}
	
    private static int filterRGB(int rgb) {
        // Find the average of red, green, and blue.
        float avg = (((rgb >> 16) & 0xff) / 255f +
                     ((rgb >>  8) & 0xff) / 255f +
                      (rgb        & 0xff) / 255f) / 3;
        // Pull out the alpha channel.
        float alpha = (((rgb >> 24) & 0xff) / 255f);
        // Calculate the average.
        // Sun's formula: Math.min(1.0f, (1f - avg) / (100.0f / 35.0f) + avg);
        // The following formula uses less operations and hence is faster.
        avg = Math.min(1.0f, 0.35f + 0.65f * avg);
        // Convert back into RGB.
       return (int) (alpha * 255f) << 24 |
              (int) (avg   * 255f) << 16 |
              (int) (avg   * 255f) << 8  |
              (int) (avg   * 255f);
    }
    
	public static Image getGrayQuality(Image image) {
		
	    final int w = image.getWidth(null);
	    final int h = image.getHeight(null);
	    
	    BufferedImage buffered = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
	    Graphics bg = buffered.getGraphics();
		bg.drawImage(image,0,0,null);
		bg.dispose();
		
	    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
	    ColorConvertOp op = new ColorConvertOp(cs, null);
	    BufferedImage grayImage = op.filter(buffered, null);
		
		return grayImage;
	}
	
	public static Icon getGrayQuality(Icon icon) {
		if (icon==null) return null;
	    final int w = icon.getIconWidth();
	    final int h = icon.getIconHeight();
	    BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
	    Graphics g = image.getGraphics();
	    icon.paintIcon(null, g, 0, 0);
	    g.dispose();
	    return new ImageIcon(getGrayQuality(image));
	}

	public static final class ColorImage extends BufferedImage {

		private Color color = null;

		public ColorImage(int width, int height, Color bgColor, boolean gradient) {
			super(width, height, BufferedImage.TYPE_INT_RGB);
			this.color = bgColor;
			this.initComponent(this.getGraphics(), gradient);
		}

		public ColorImage(int width, int height, Color bgColor) {
			super(width, height, BufferedImage.TYPE_INT_RGB);
			this.color = bgColor;
			this.initComponent(this.getGraphics(), false);
		}

		public void initComponent(Graphics g, boolean grad) {
			Graphics2D g2 = (Graphics2D)g;
			int w = getWidth();
			int h = getHeight();
			int width = 15;
			if (grad) {
				GradientPaint gradient = new GradientPaint(
						0, 0,
						new Color(
								Math.min(color.getRed()+width, 255),
								Math.min(color.getGreen()+width, 255),
								Math.min(color.getBlue()+width, 255)
						),
						w, h,
						new Color(
								Math.max(color.getRed()-width, 0),
								Math.max(color.getGreen()-width, 0),
								Math.max(color.getBlue()-width, 0)
						),
						true);
				g2.setPaint(gradient);
			} else {
				g2.setColor(color);
			}
			g2.fillRect(0, 0, w, h);
		}

	}

}
