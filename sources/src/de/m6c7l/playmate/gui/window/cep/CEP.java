/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.cep;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.m6c7l.lib.gui.TOOLBOX;
import de.m6c7l.lib.gui.graphics.CanvasPane;
import de.m6c7l.lib.util.NAUTIC;
import de.m6c7l.playmate.gui.side.assets.AssetHookChooser;
import de.m6c7l.playmate.gui.side.assets.AssetSelectChooser;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.Environment;
import de.m6c7l.playmate.main.ITEMS;
import de.m6c7l.playmate.main.Selectable;
import de.m6c7l.playmate.main.VALUE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CEP extends CanvasPane implements FocusListener {
	
    private static final Font       FONT                        = new Font("Monospaced", Font.PLAIN, 12);
    
	public static final BasicStroke STROKE 						= new BasicStroke(1.5f);
	public static final BasicStroke STROKE_MARK					= new BasicStroke(1.75f);
	
	public static final Color       COLOR_BG                    = new Color(28, 31, 34);
	public static final Color		COLOR_TEXT 					= new Color(250,250,250);
	public static final Color		COLOR_GRID 					= Color.GRAY;
	public static final Color 		COLOR_LABEL 				= Color.GRAY.brighter();
	public static final Color 		COLOR_HEADING 				= new Color(250,250,250);
	public static final Color		COLOR_WATERFALL				= new Color(61,167,227);
	public static final Color		COLOR_WATERFALL_MARK		= COLOR_WATERFALL.brighter();

	public static final Color		COLOR_INTERCEPT				= new Color(250, 160, 0);
	public static final BasicStroke STROKE_INTERCEPT			= new BasicStroke(3.0f);
	
	private static final int 		PADDING_LEFT 				= 50;
	private static final int 		PADDING_TOP 				= 30;
	private static final int 		PADDING_BOTTOM_RIGHT 		= 20;
	
	private Environment world = null;
	
	private ITEMS.TIME timeAxisSpan = null;
	private ITEMS.BEARING bearingCenter = null;
	
	private int bearing = 0;
	
	private BufferedImage image = null;
	
	private long lastTime = 0;
	private long lastTimeRef = 0;
	
	private Asset lastHooked = null;
	private Asset lastSelected = null;
	
	private boolean redraw = true;
	
	private JPopupMenu popup = null;
	private ArrayList<Label> labels = null;
	
	private Asset rollover = null;
	
	public CEP(Environment model) {
		
		super();
		this.world = model;
        
        this.setBearingCenter(ITEMS.BEARING.BEARING_000);
        this.setTimeSpan(ITEMS.TIME.MINUTES_25);
        
        this.labels = new ArrayList<Label>();
        
		/*
		 * change look of cursor while moving over assets
		 */
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				applyPickCursor(e.getPoint());
			}
		});
		
		/*
		 * choose a vessel by line of sound in cep
		 */
        this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()!=MouseEvent.BUTTON1) return;
				if (rollover!=null) {
				    if (!rollover.isSelected() && rollover.getID()==null && world.isExercising()) {
				        rollover.setSelected(true);
				    } else if (!rollover.isSelected() && !world.isExercising()) {
					    rollover.setSelected(true);
					} else {
						if (!rollover.isHooked()) {
							rollover.setHooked(true);					
						} else {
							for (int i=0; i<world.getAssetCount(); i++) {
								world.getAsset(i).setHooked(false);
							}							
						}
					}
				} else { // unselect all
                    for (int i=0; i<world.getAssetCount(); i++) {
                        world.getAsset(i).setSelected(false);
                    }				        
				}
			}
		});
        
		this.addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {
				redraw = true;
			}
			public void componentResized(ComponentEvent e) {
				redraw = true;
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		
		popup = new JPopupMenu();
        final JTextField field = new JTextField(10);
        field.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode()==KeyEvent.VK_ENTER) {
					popup.setVisible(false);
				}
			}
		});
        popup.insert(field, 0);
        popup.addPopupMenuListener(new PopupMenuListener() {
        	Point loc = null;
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            	field.setText("");
            	loc = getMousePosition();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        field.requestFocusInWindow();
                        field.selectAll();
                    }
                });
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            	if (field.getText().length()>0) {
            		Long t = getTime(loc);
            		Double b = getBearing(loc);
                	if ((t!=null) && (b!=null)) {      		
                		labels.add(new Label(t,b,field.getText()));            		
                	}
            	}
            }
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

		this.setComponentPopupMenu(popup);
		
	}

	public void setHighlight(Asset asset) {
		redraw = redraw || (asset!=rollover);
		rollover = asset;
	}
	
	public void free() {
		this.removeAll();
		this.setComponentPopupMenu(null);
		this.world = null;
		this.lastHooked = null;
		this.lastSelected = null;
		this.popup = null;
		this.labels.clear();
		this.labels = null;
		this.rollover = null;
		super.free();
	}
	
	public BufferedImage getCanvas() {

		if ((redraw) || (world.getTime()!=lastTime) || world.isStopped()) {
			
			boolean changed = lastTimeRef!=getTimeReference() ||
									lastHooked!=world.getHooked() ||
										lastSelected!=world.getSelected();
			
			boolean freezed = (world.isStopped()) &&
									(lastTime!=world.getTime());
			
			redraw = redraw || freezed || changed; 
			
			if (redraw) {
				
				redraw = false;
				BufferedImage target = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D)target.getGraphics();	
		        g.setFont(FONT);
			    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    g.setColor(COLOR_BG);
			    g.fillRect(0, 0, getWidth(), getHeight());
				paintCEP(g);
				if (paintMotion(g)) {
					paintWaterfalls(g);			
				}
				g.dispose();
				image = target;
				
			} else if (lastTime!=world.getTime()) {

				Graphics2D g = (Graphics2D)image.getGraphics();
			    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    
				if (paintHeading(g)) {
					paintBearings(g);			
				}
				g.dispose();
				
			}
			
			lastTimeRef = getTimeReference();
			lastHooked = (world.getHooked() instanceof Asset) ? (Asset)world.getHooked() : null;
			lastSelected = (world.getSelected() instanceof Asset) ? (Asset)world.getSelected() : null;
			
			lastTime = world.getTime();
			
		}

		BufferedImage overlay = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)overlay.getGraphics();
		g.drawImage(image,0,0,null);		
		paintCursor(g);
		paintLabels(g);
		g.dispose();
		
		return overlay;
		
	}

	public void setTimeSpan(ITEMS.TIME time) {
		this.timeAxisSpan = time;
		redraw = true;
	}
	
	public ITEMS.TIME getTimeSpan() {
		return this.timeAxisSpan;
	}
	
	public void setBearingCenter(ITEMS.BEARING bearing) {
		this.bearingCenter = bearing;
		this.bearing = (int)NAUTIC.getBearingOpposite((bearing.value()+360)%360);
		redraw = true;
	}
	
	public ITEMS.BEARING getBearingCenter() {
		return this.bearingCenter;
	}
	
	public long getTimeReference() {
		int tSpan = getTimeSection();
    	long tBase = (world.getTime()/tSpan)*tSpan;
    	return tBase;
	}

	private int getTimeSection() {
    	int[] m = new int[] {20,10,5,2,1};
    	int tSpan = 0; while (timeAxisSpan.value()/m[tSpan]<5) { tSpan++; } tSpan = m[tSpan]*60;
    	return tSpan;
	}

	private int getTimeSectionCount(int timeSection) {
    	int lines = timeAxisSpan.value()/(timeSection/60);
    	return lines;
	}
	
	private int getTimePeriod() {
		int sec = getTimeSection();
		int count = getTimeSectionCount(sec);
		return sec*count;
	}
	
	private long getTimeStart() {
		return getTimeReference()+getTimeSection();
	}
	
	private long getTimeEnd() {
		return getTimeStart()-getTimePeriod();
	}
	
	public Double getBearing(Point panel) {
		Point ulc = getULC();
		Point lrc = getCEPLRC();
		Point mp = panel;
		if ((mp.x>=ulc.x) && (mp.y>=ulc.y) && (mp.x<=lrc.x) && (mp.y<=lrc.y)) {
			int b = mp.x-ulc.x;
			double w = getCEPSize().getWidth();
			double bper = b/w;
			return ((bearing+(360*bper))+360)%360;
		}
		return null;
	}

	public Long getTime(Point panel) {
		Point ulc = getULC();
		if (isCEP(panel)) {
			int b = panel.y-ulc.y;
			double h = getCEPSize().getHeight();
			double hper = b/h;
			long base = getTimePeriod();
			return getTimeStart()-(long)Math.round(base*hper);
		}
		return null;
	}

	public Point getLocation(double bearing, long time) {
		long t = getTimeStart();
		int p = getTimePeriod();
		if ((time<=t) && (time>=t-getTimePeriod())) {
			double w = getCEPSize().getWidth();
			double h = getCEPSize().getHeight();
			double wper = ((NAUTIC.getAngleDifference(this.bearing,bearing,0)+360)%360)/360;
			double hper = (t-time)/(p*1.0);
			return new IPoint(PADDING_LEFT+w*wper,PADDING_TOP+h*hper).getLocation();
		}
		return null;
	}

	private boolean isCEP(Point panel) {
		Point ulc = getULC();
		Point lrc = getCEPLRC();
		return ((panel.x>=ulc.x) && (panel.y>=ulc.y) && (panel.x<=lrc.x) && (panel.y<=lrc.y));
	}
		
	private Point getULC() {
		return new Point(PADDING_LEFT,PADDING_TOP);
	}
	
	private Point getCEPLRC() {
		return new Point(this.getWidth()-PADDING_BOTTOM_RIGHT,this.getHeight()-PADDING_BOTTOM_RIGHT);
	}
	
	private Dimension getCEPSize() {
    	int bearingPxRange = this.getWidth()-PADDING_BOTTOM_RIGHT-PADDING_LEFT;
    	int timePxRange = this.getHeight()-PADDING_BOTTOM_RIGHT-PADDING_TOP;
		return new Dimension(bearingPxRange,timePxRange);
	}
	
	// draw stuff
	
	private void drawHeading(Graphics g, Point p) {
  	    g.drawLine(p.x,p.y,p.x,p.y);
	}
	
	private boolean paintHeading(Graphics g) {
    	if (world.getObserver()!=null) {
    		Asset u = world.getObserver();
    		g.setColor(COLOR_HEADING);   
       		((Graphics2D)g).setStroke(STROKE);   
    		int seconds = (timeAxisSpan.value()/30)+1;
    		long start = (((lastTime-1)/seconds)+1)*seconds;
   			for (long t=start; t<world.getTime(); t=t+seconds) {
	   	       	drawHeading(g,getLocation(u.getHeading(t),t));
   			}
   			return true;
   		}
    	return false;
	}
	
	private boolean paintMotion(Graphics g) {
    	if (world.getObserver()!=null) {
    		Asset u = world.getObserver();
    		g.setColor(COLOR_HEADING);   
       		((Graphics2D)g).setStroke(STROKE);   	
    		int seconds = (timeAxisSpan.value()/30)+1;
    		long start = (((Math.max(getTimeEnd(),u.getOrientation().firstTime())-1)/seconds)+1)*seconds;
   			for (long t=start; t<world.getTime(); t=t+seconds) {
	   	       	drawHeading(g,getLocation(u.getHeading(t),t));
   			}
   			return true;
   		}
    	return false;
	}
	
	private Color shift(Color c, double v) {
		double d = Math.log10(v)*0.5;
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		r = (int)(r-r*d);
		g = (int)(g-g*d);
		b = (int)(b-b*d);
		return new Color(r,g,b);
	}
	
	private void drawBearing(Graphics g, Point p, Asset u, long t) {
		if (p==null) return;
		boolean shift = true;
		if (u!=rollover) {
  			((Graphics2D)g).setStroke(STROKE);
	 		g.setColor(COLOR_WATERFALL);				
		} else {
			((Graphics2D)g).setStroke(STROKE_MARK);
	 		g.setColor(COLOR_WATERFALL_MARK);
	 		shift = false;
		}
		if (shift) {
			Double rbrg = world.getObserver().getBearingRelative(t,u);
			Double absbrg = Math.abs(rbrg);
			if (absbrg>135) {
				int bad = (int)(absbrg-135);
				g.setColor(shift(g.getColor(),(bad/5.0)+1));
			}                   								
		}
		g.drawLine(p.x,p.y,p.x,p.y);
	}
	
	private void paintBearing(Graphics g, Asset u, long t) {
		if ((u.isStuck(t)) || (!world.getObserver().isAudible(t,u))) return;
		Double brg = world.getObserver().getBearing(t,u);
		if (brg!=null) {
			drawBearing(g,getLocation(brg,t),u,t);
		}   
	}
	
	private void paintBearings(Graphics g) {
    	for (int i=0; i<world.getAssetCount(); i++) {
    		Asset u = world.getAsset(i);
	    	if (u.isWatercraft() && (!u.isObserver()) && (u.isExisting())) {
	    		int seconds = (timeAxisSpan.value()/30)+1;
	    		long start = (((lastTime-1)/seconds)+1)*seconds;
	    		for (long t=start; t<world.getTime(); t=t+seconds) {
   	    			paintBearing(g,u,t);
   	    		}
   			}
    	}
	}
	
	private void paintWaterfalls(Graphics g) {
		Asset last = rollover;
		int seconds = (timeAxisSpan.value()/30)+1;
    	for (int i=0; i<world.getAssetCount(); i++) {
    		Asset u = world.getAsset(i);
    		if (u.isWatercraft() && (!u.isObserver()) && (u.isExisting()) && (u!=last)) {
        		long start = (((Math.max(getTimeEnd(),Math.max(u.getTrack().firstTime(),world.getObserver().getTrack().firstTime()))-1)/seconds)+1)*seconds;
       			for (long t=start; t<world.getTime(); t=t+seconds) {
        			paintBearing(g,u,t);
        		}    	
    		}
    	}
    	if (last!=null) {
    		long start = (((Math.max(getTimeEnd(),Math.max(last.getTrack().firstTime(),world.getObserver().getTrack().firstTime()))-1)/seconds)+1)*seconds;
   			for (long t=start; t<world.getTime(); t=t+seconds) {
    			paintBearing(g,last,t);
    		}     
    	}
	}
	
//	private void drawIntercept(Graphics g, Point p, Asset u, long t) {
//		if (p==null) return;
//		if (u!=rollover) {
//  			((Graphics2D)g).setStroke(STROKE_INTERCEPT);
//	 		g.setColor(COLOR_INTERCEPT);				
//		} else {
//			((Graphics2D)g).setStroke(STROKE_INTERCEPT);
//	 		g.setColor(COLOR_INTERCEPT);
//		}
//		g.drawLine(p.x,p.y,p.x,p.y);
//	}
	
//	private void paintIntercept(Graphics g, Asset u, long t) {
//		if ((u.isStuck(t)) || (!u.hasPulse(t,world.getObserver()))) return;
//		Double brg = world.getObserver().getBearing(t,u);
//		if (brg!=null) {
//			drawIntercept(g,getLocation(brg,t),u,t);
//		}   
//	}
	
	private void paintCEP(Graphics g) {
		
    	int bearingPxRange = this.getWidth()-PADDING_BOTTOM_RIGHT-PADDING_LEFT;
    	int timePxRange = this.getHeight()-PADDING_BOTTOM_RIGHT-PADDING_TOP;
    
    	g.setColor(COLOR_GRID);
    	
    	g.drawLine( // oben hor
    			PADDING_LEFT,
    			PADDING_TOP,
    			this.getWidth()-PADDING_BOTTOM_RIGHT,
    			PADDING_TOP); 
    	g.drawLine( // unten hor
    			PADDING_LEFT,
    			this.getHeight()-PADDING_BOTTOM_RIGHT,
    			this.getWidth()-PADDING_BOTTOM_RIGHT,
    			this.getHeight()-PADDING_BOTTOM_RIGHT); 
    	
    	for (int i=1; i<=35; i++) { // teilstriche oben
    		int x = PADDING_LEFT+(int)Math.round(bearingPxRange*(i/36.0));
    		g.drawLine(
    				x,
    				PADDING_TOP,
    				x,
    				PADDING_TOP+10);
    	}

    	for (int i=0; i<=4; i++) { // peilungen oben
    		int x = PADDING_LEFT+(int)Math.round(bearingPxRange*(i/4.0));
        	g.setColor(COLOR_LABEL);
    		g.drawString(
    				B((bearing+(i*90))%360),
    				x-10,
    				PADDING_TOP-10);
        	g.setColor(COLOR_GRID);
    		g.drawLine(
    				x,
    				PADDING_TOP,
    				x,
    				this.getHeight()-PADDING_BOTTOM_RIGHT);
    	}
    	
       	int tSpan = getTimeSection();
    	long tBase = getTimeReference();
    	int sections = getTimeSectionCount(tSpan);	
    	long t = tBase+tSpan;
    	
    	for (int i=0; i<=sections; i++) { // zeitachse
    		int y = PADDING_TOP+(int)Math.round(timePxRange*(i/(sections*1.0)));
        	g.setColor(COLOR_LABEL);
    		g.drawString(
    				T(t),
    				PADDING_LEFT-40,
    				y+5);
        	g.setColor(COLOR_GRID);
    		g.drawLine(
    				PADDING_LEFT,
    				y,
    			 	PADDING_LEFT+bearingPxRange,
    				y);
    		t = t - tSpan;
    	}
    	
	}
	
	private void paintCursor(Graphics g) {
		
		boolean sel = false;
		Container c = getParent();
		while ((c!=null) && (!sel)) {
			if ((c instanceof JInternalFrame) && (((JInternalFrame)c).isSelected())) sel = true;
			c = c.getParent();
		}
		
    	Point p = getMousePosition();
    	
    	int w = this.getWidth()-PADDING_BOTTOM_RIGHT-PADDING_LEFT;
    	int h = this.getHeight()-PADDING_BOTTOM_RIGHT-PADDING_TOP;
    	
    	boolean drawCursor = false;
    	
    	if (sel && (p.x>PADDING_LEFT) && (p.y>PADDING_TOP) && (p.x<w+PADDING_LEFT) && (p.y<h+PADDING_TOP)) {
        	g.setColor(COLOR_GRID);
    		TOOLBOX.drawDashedLine(
    				(Graphics2D)g,
    				PADDING_LEFT,
    				p.y,
    			 	PADDING_LEFT+w,
    				p.y,
    				2,
    				10);
    		TOOLBOX.drawDashedLine(
    				(Graphics2D)g,
    				p.x,
    				PADDING_TOP,
    				p.x,
    				PADDING_TOP+h,
    				2,
    				10);
    		drawCursor = true;
    	}
    	
    	if ((rollover!=null) && (drawCursor)) {

			Asset temp = rollover;
			Long t = getTime(p);
			Double b = getBearing(p);

			if (!rollover.isObserver()) {

				Double brg = world.getObserver().getBearing(t,temp);
				if (brg!=null) {

					double br = world.getObserver().getBearingRate(t,temp);
					double x = Math.abs(br/10);
					double bfrom = b-x-2;
					double bto = b+x+2;
					double aob = world.getObserver().getAngleOnBow(t,temp);

					if ((brg>bfrom) && (brg<bto)) {

						Double rbrg = world.getObserver().getBearingRelative(t,temp);

						boolean badsector = (Math.abs(rbrg)>=150);
						boolean owncourse = world.getObserver().getRateOfTurn(t)>30.0;

						String[] sbr = VALUE.BEARINGRATE(br);
						String[] stbrg = VALUE.TRUEBEARING(brg);
						String[] srbrg = VALUE.BEARING(rbrg);

						int rh = !world.isExercising() ? 145 : (!badsector ? 85 : 65);
						int xoff = (this.getWidth()-this.getMousePosition().getX())>150 ? +15 : -95;
						int yoff = 15;

						Rectangle rec = new Rectangle(p.x+xoff,p.y+yoff,80,rh);
						((Graphics2D)g).setPaint(new Color(0,0,0,128));
						((Graphics2D)g).fill(rec);

						g.setColor(COLOR_TEXT);

						g.setFont(g.getFont().deriveFont(Font.BOLD));

						g.drawString(
								temp.getID()+"",
								p.x+xoff+5,
								p.y+yoff+15);

						g.setFont(g.getFont().deriveFont(Font.PLAIN));

						g.drawString(
								VALUE.TIME(t),
								p.x+xoff+5,
								p.y+yoff+35);

						if (!world.isExercising()) {

							g.drawString(
									stbrg[0],
									p.x+xoff+5,
									p.y+yoff+55);

							g.drawString(
									srbrg[0] + " " + srbrg[1],
									p.x+xoff+5,
									p.y+yoff+75);

							g.drawString(
									sbr[0] + " " + sbr[1],
									p.x+xoff+5,
									p.y+yoff+95);

							double d = world.getObserver().getRange(t,temp);
							String[] sd = VALUE.RANGE(d);

							g.drawString(
									aob!=0 ? (aob!=180 ? (aob>0 ? "R "+VALUE.INTEGER(aob) : "L "+VALUE.INTEGER(Math.abs(aob))) : "0") : "180",
									p.x+xoff+5,
									p.y+yoff+115);

							g.drawString(
									sd[0] + " " + sd[1],
									p.x+xoff+5,
									p.y+yoff+135);

						} else if ((!badsector) && (!owncourse)) {

							g.drawString(
									stbrg[0],
									p.x+xoff+5,
									p.y+yoff+55);

							g.drawString(
									sbr[0] + " " + sbr[1],
									p.x+xoff+5,
									p.y+yoff+75);

						} else {

							g.drawString(
									"-",
									p.x+xoff+5,
									p.y+yoff+55);

							g.drawString(
									"-",
									p.x+xoff+5,
									p.y+yoff+75);

						}

					}
				}

			} else {

				double spd = world.getObserver().getSpeed(t);

				int rh = 25;
				int xoff = (this.getWidth()-this.getMousePosition().getX())>150 ? +15 : -95;
				int yoff = 15;

				Rectangle rec = new Rectangle(p.x+xoff,p.y+yoff,70,rh);
				((Graphics2D)g).setPaint(new Color(0,0,0,128));
				((Graphics2D)g).fill(rec);

				g.setColor(COLOR_TEXT);
				String[] sp = VALUE.SPEED(spd);

				g.drawString(
						sp[0] + " " + sp[1],
						p.x+xoff+5,
						p.y+yoff+15);

				g.setFont(g.getFont().deriveFont(Font.PLAIN));

			}

    	}
    	
	}
	
	private void paintLabels(Graphics g) {
		g.setColor(COLOR_LABEL);  	
		Font ftemp = g.getFont();
		g.setFont(g.getFont().deriveFont(Font.BOLD));
    	for (int i=0; i<labels.size(); i++) {
    		ArrayList<Label> temp = labels;
    		for (int j=0; j<temp.size(); j++) {
        		Label l = temp.get(j);
           		if ((getTimeStart()>=l.time) && (getTimeStart()-getTimePeriod()<l.time)) {
           			Point p = getLocation(temp.get(j).bearing,temp.get(j).time);
           			if (p!=null)
           				g.drawString(
           					temp.get(j).text,
            				p.x+5,
            				p.y+15);
        		}    				
    		}    		
    	}
		g.setFont(ftemp);
	}
	
	private Asset getAsset(Point p) {
    	Double b = getBearing(p);
    	Long t = getTime(p);
    	Asset obs = world.getObserver();
    	if ((b!=null) && (t!=null) && (obs!=null) && (t<=world.getTime()) && (t>=obs.getTrack().firstTime())) {
    		Double brg = null;
    		for (int i=0; i<world.getAssetCount(); i++) {
    			Asset temp = world.getAsset(i);
    			if (temp.isExisting(t)) {
    	        	double x = 0;
        			if (!temp.isObserver()) {
            			brg = obs.getBearing(t,temp);
        				if ((temp.isStuck(t)) || (!obs.isAudible(t,temp))) brg = null;
    	    			double br = obs.getBearingRate(t,temp);
    	    			x = Math.abs(br/10)+3;
        			} else {
        				brg = temp.getHeading(t);
        				double rot = obs.getRateOfTurn(t);
    	    			x = Math.abs(rot/10)+3;
        			}
                	double bfrom = b-x;
                	double bto = b+x;
    				if ((brg!=null) && (brg>bfrom) && (brg<bto)) {
    					return temp;
    				}    				
    			}
    		}
    	}
    	return null;
	}
	
	private void applyPickCursor(Point p) {
		setHighlight(getAsset(p));
		if (rollover!=null) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			applyDefaultCursor(p);					
		}
	}
	
	private void applyDefaultCursor(Point p) {
		if (isCEP(p)) {
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));			
		} else {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
    public String toString() {
    	return "CEP";
    }
    
	protected boolean save(File file) throws IOException {
		return ImageIO.write(this.getCanvas(),"png", file);
	}

	private static String B(int bearing) {
		return (bearing<100 ? (bearing<10 ? "00" : "0") : "") + bearing;
	}

	public static String T(long time) {
		String s = String.valueOf(VALUE.TIME(time));
		return s.substring(0,s.lastIndexOf(":"));
	}
	
	public boolean isInitialized() {
		return (getSize().width>0) && (getSize().height>0);
	}
	
	private static class Label {
		
		private long time = 0;
		private double bearing = 0;
		private String text = null;
		
		public Label(long time, double bearing, String text) {
			this.time = time;
			this.bearing = bearing;
			this.text = text;
		}
		
	}

	@Override
	public void focusGained(FocusEvent e) {
		Selectable s = null;
		if (e.getSource() instanceof AssetSelectChooser) {
			s = world.getSelected();
		} else if (e.getSource() instanceof AssetHookChooser) {
			s = world.getHooked();
		}
		if ((s!=null) && (s instanceof Asset)) {
			setHighlight((Asset)s);	
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		setHighlight(null);
	}
	
}
