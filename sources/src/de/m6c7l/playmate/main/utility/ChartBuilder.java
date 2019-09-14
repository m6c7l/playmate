/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.utility;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.net.URI;

import javax.swing.ImageIcon;

import de.m6c7l.lib.gui.TOOLBOX.ColorImage;
import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.gui.window.chart.Chart;
import de.m6c7l.playmate.main.io.XMLChart;

public class ChartBuilder {
	
	private XMLChart xmlchart = null;
	
	public ChartBuilder(XMLChart chart) {
		this.xmlchart = chart;
	}
	
	public String toString() {
		return this.xmlchart.toString();
	}
	
	public Chart create() throws Exception {

		Chart chartPane = null;
		
		if (isUseable()) {

			String file = xmlchart.getFile();
			
			Color cWater = xmlchart.getColorWater();
			Color cLand = xmlchart.getColorLand();
			
			Double zoomMin = xmlchart.getScaleDown();
			Double zoomMax = xmlchart.getScaleUp();
			
			Image iicon = null;
			
			Position pos0 = xmlchart.getReferencePosition(0);
			Position pos1 = xmlchart.getReferencePosition(1);
			
			Point poi0 = xmlchart.getReferencePoint(0);
			Point poi1 = xmlchart.getReferencePoint(1);

			if ((pos0!=null) && (pos1!=null)) {
				
				boolean l = true;
				boolean u = true;
				
				if ((poi0!=null) && (poi1!=null)) {					
					if (poi0.x>poi1.x) {
						l = false;
					}
					if (poi0.y>poi1.y) {						
						u = false;
					}					
				} else {
					poi1 = null;
					if (pos0.isSouthern(pos1)) {
						u = false;
					}					
				}

				if (file!=null) {
					
					File f = new File(new URI(xmlchart.getImageDirectory().toURI() + file));
					
//					if (!f.toString().toLowerCase().endsWith(".svg")) {
						iicon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(f.toString())).getImage();
//					} else {
//						iicon = SVGImage.rasterize(f);
//					}

					if (poi1==null) {	
						if (u) {
							poi0 = new Point(0,0);
							poi1 = new Point(iicon.getWidth(null)-1,iicon.getHeight(null)-1);					
						} else {										
							poi1 = new Point(0,0);
							poi0 = new Point(iicon.getWidth(null)-1,iicon.getHeight(null)-1);		
						}
					}
					
				} else {

					int width = 0;
					int height = 0;
						
					if (poi1==null) {
						
						Position ul = null;						
						Position ur = null;						
						Position lr = null;						
						
						if (u) {
							ul = new Position(pos0.getLatitude(),pos0.getLongitude());
							ur = new Position(pos0.getLatitude(),pos1.getLongitude());
							lr = new Position(pos1.getLatitude(),pos1.getLongitude());
						} else {
							ul = new Position(pos1.getLatitude(),pos1.getLongitude());
							ur = new Position(pos1.getLatitude(),pos0.getLongitude());						
							lr = new Position(pos0.getLatitude(),pos0.getLongitude());
						}

						width = (int)(ul.getRhumbLine(ur,(byte)+1).getDistance()/50);
						height = (int)(ur.getRhumbLine(lr,(byte)-1).getDistance()/50);

						if (poi1==null) {
							if (u) {
								poi0 = new Point(0,0);
								poi1 = new Point(width-1,height-1);
							} else {
								poi1 = new Point(0,0);
								poi0 = new Point(width-1,height-1);
							}
						}						
						
					} else {
						
						width = Math.abs(poi1.x-poi0.x);
						height = Math.abs(poi1.y-poi0.y);
						
					}

					iicon = new ColorImage(width,height,cWater,true);	
					
				}

				if (l) {
					if (u) {
						chartPane = new Chart(iicon,poi0,pos0,poi1,pos1,cWater,cLand,zoomMin,zoomMax);							
					} else {
						chartPane = new Chart(iicon,pos1,poi1,pos0,poi0,cWater,cLand,zoomMin,zoomMax);													
					}					
				} else {
					if (u) {
						chartPane = new Chart(iicon,pos0,poi0,pos1,poi1,cWater,cLand,zoomMin,zoomMax);	
					} else {
						chartPane = new Chart(iicon,poi1,pos1,poi0,pos0,cWater,cLand,zoomMin,zoomMax);																				
					}	
				}
				
				return chartPane;
				
			}
						
		}
		
		throw new Exception("xml is invalid");
		
	}
	
	private boolean isUseable() {
		return (xmlchart.getName()!=null) && (xmlchart.getColorWater()!=null) &&
					(xmlchart.getReferenceCount()==2) &&
						(xmlchart.getScaleUp()!=null) && (xmlchart.getScaleDown()!=null);		
	}

	
}