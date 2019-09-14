/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.tactical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import de.m6c7l.lib.gui.graphics.CanvasPane;
import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.gui.AppModel;
import de.m6c7l.playmate.gui.window.chart.Chart;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.Canvasable;
import de.m6c7l.playmate.main.Environment;
import de.m6c7l.playmate.main.VALUE;

public class Tactical extends CanvasPane implements Canvasable, ItemListener {
	
    private static final int        SCALE                       = 16000;
    private static final Font       FONT                        = new Font("Monospaced", Font.PLAIN, 13);
    
    public static final BasicStroke STROKE                      = new BasicStroke(1.5f);
    
    public static final Color       COLOR_BG                    = new Color(28,31,34);
    public static final Color       COLOR_COMPASS               = Color.GRAY;
    
    public static final Color       COLOR_TIME                  = new Color(220,221,221);
    public static final Color       COLOR_BG_TIME               = new Color(50,57,62);
        
    public static final Color       COLOR_LABEL                 = new Color(236,190,42);
    public static final Color       COLOR_LABEL_BG              = new Color(44,49,53);

    public static final Color       COLOR_TEXT                  = new Color(127,179,71);

    private static final double     CIRCLE_DIAMETER_FACTOR      = 0.75;
    
	private AppModel model = null;	
	private Environment world = null;	   
	private BufferedImage image = null;
	    
    private boolean redraw = true;
	private long lastTime = 0;
	private Position lastPos = null;
    private Double lastHdg = null;
	private Double lastSpd = null;
    private Double lastAlt = null;
    
    private BufferedImage contour = null;
    private Position contPos = null;

    private int radius = 0;
    private int scale = 0;
    
	public Tactical(AppModel model) {        
        super();
        
        this.model = model;
        this.world = model.getWorld();
        this.scale = SCALE;
        
        this.addComponentListener(new ComponentListener() {
            public void componentShown(ComponentEvent e) {
                redraw = true;
                contPos = null;
            }
            public void componentResized(ComponentEvent e) {
                redraw = true;
                contPos = null;
            }
            public void componentMoved(ComponentEvent e) {}
            public void componentHidden(ComponentEvent e) {}
        });

        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

	}

    @Override
    public boolean isInitialized() {
        return (getSize().width>0) && (getSize().height>0);
    }

    @Override
    public BufferedImage getCanvas() {
        
        if ((redraw) || (world.getTime()!=lastTime) || world.isStopped()) {
            
            boolean changed = winceObserver();
            
            boolean freezed = (world.isStopped()) && (lastTime!=world.getTime());
            
            redraw = redraw || freezed || changed; 
            
            if (redraw) {
                
                redraw = false;
                BufferedImage target = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D)target.getGraphics();                
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setFont(FONT);
                g.setColor(COLOR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                drawOwnShipData(g);
                drawCompassRose(g);
                g.dispose();
                image = target;
                
            } else if (lastTime!=world.getTime()) {

                Graphics2D g = (Graphics2D)image.getGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setFont(FONT);
                drawOwnShipData(g);
                g.dispose();
                
            }
            
            lastTime = world.getTime();
            
        }

        BufferedImage overlay = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D)overlay.getGraphics();
        g.drawImage(image,0,0,null);
        
        if (world.getObserver()!=null) {            
            if ((contPos==null) || (contPos.getRhumbLine(world.getObserver().getPosition()).getDistance() > 100)) { // refresh chart contour
                contour = createChartOverlay(image.getWidth(),image.getHeight());
                contPos = world.getObserver().getPosition();
            }
            g.drawImage(contour,0,0,null);
        }
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(FONT);
        drawCursorData(g);
        g.dispose();
        
        return overlay;

    }

    private void drawCompassRose(Graphics2D g) {

        g.setColor(COLOR_COMPASS);

        double shrink = 0.0;
        int psi = 0;
        int ps_ = 0;
        int pso = this.getSize().height;
        while (pso * 2 > this.getSize().height) {
            psi = (int)((this.getSize().width / (2 + shrink)) / (1 / (CIRCLE_DIAMETER_FACTOR - (CIRCLE_DIAMETER_FACTOR / 10.0))));
            ps_ = (int)((this.getSize().width / (2 + shrink)) / (1 / (CIRCLE_DIAMETER_FACTOR)));
            pso = (int)((this.getSize().width / (2 + shrink)) / (1 / (CIRCLE_DIAMETER_FACTOR + (CIRCLE_DIAMETER_FACTOR / 30.0))));
            shrink += 0.25;
        }
        
        radius = ps_;
        
        Point point = new Point(this.getSize().width / 2 - 1,this.getSize().height / 2 - 1);
        
        g.drawOval(point.x-ps_, point.y-ps_, ps_*2, ps_*2);

        int xi = point.x;
        int yi = point.y-psi;

        int x = point.x;
        int y = point.y-ps_;
        
        int xo = point.x;
        int yo = point.y-pso;
        
        int u = point.x;
        int v = point.y;
        
        for (int i=0; i<72; i++) {
            
            double beta = i/2.0;
            
            double sinbeta = Math.sin((beta*10)/180.0*Math.PI);
            double cosbeta = Math.cos((beta*10)/180.0*Math.PI);
            
            int xs = (int)((xi-u)*cosbeta-(yi-v)*sinbeta + u);
            int ys = (int)((xi-u)*sinbeta+(yi-v)*cosbeta + v); 
            
            int x1 = (int)((x-u)*cosbeta-(y-v)*sinbeta + u);
            int y1 = (int)((x-u)*sinbeta+(y-v)*cosbeta + v); 

            int x2 = (int)((xo-u)*cosbeta-(yo-v)*sinbeta + u);
            int y2 = (int)((xo-u)*sinbeta+(yo-v)*cosbeta + v); 
            
            if (i%6==0) {
                drawBearingText(g,i*5,(xs*2+x2)/3,(ys*2+y2)/3);
            }
            
            g.drawLine(x1,y1,x2,y2);
            
        }

    }
    
    private void drawBearingText(Graphics2D g, int bearing, int x, int y) {
        g.drawString(new DecimalFormat("000").format(bearing),x-10,y+5);
    }
	
	private void drawOwnShipData(Graphics2D g) {
	    	    
	    int width = 136;
		int textheight = 15;
		int rowheight = 16;
		int border = 10;
		int textspacing = rowheight-textheight;
		
        g.setColor(COLOR_LABEL_BG);
        int hlr = textspacing+(textheight*7) + border/2;            
        g.fillRect(border, border, width, hlr);

        g.setColor(COLOR_BG_TIME);
        g.fillRect(border, border, width, rowheight);

        Asset u = this.world.getObserver();

    	if (u!=null) {    		
            
        	g.setColor(COLOR_LABEL);
            g.drawString("Hdg           deg",  border, border+textspacing+(textheight*2));
            g.drawString("Spd           m/s",  border, border+textspacing+(textheight*3));
            g.drawString("Rev           RPM",  border, border+textspacing+(textheight*4));
            g.drawString("Dep           m",    border, border+textspacing+(textheight*5));
            g.drawString("Pos ",               border, border+textspacing+(textheight*6));
            
            g.setColor(COLOR_TIME);
            g.drawString(VALUE.TIME(world.getTime()), border+30, border+textspacing+(textheight*1)-3);

        	g.setColor(COLOR_TEXT);
            String hdg = VALUE.HEADING(u.getHeading())[0];
            String spd = VALUE.SPEED(u.getSpeed())[0];
            String dep = VALUE.RANGE(-u.getAltitude())[0];
            g.drawString("          " + hdg,    border, border+textspacing+(textheight*2));
            g.drawString("          " + spd,    border, border+textspacing+(textheight*3));       	
            g.drawString("          " + dep,    border, border+textspacing+(textheight*5));         
        	g.drawString("    " + u.getPosition().toStringLatitude(), border, border+textspacing+(textheight*6));
        	g.drawString("   " + u.getPosition().toStringLongitude(), border, border+textspacing+(textheight*7));
        	
    	}
	}
    
	private void drawCursorData(Graphics2D g) {
	    
	    int width = 136;
		int textheight = 15;
		int rowheight = 16;
		int border = 10;
		int textspacing = rowheight-textheight;

        g.setColor(COLOR_LABEL_BG);
        int hul = getHeight()-(border+textspacing+(textheight*3));
        g.fillRect(border, hul, width, textspacing+(textheight*3) + border/2);

        Asset u = this.world.getObserver();

        if (u!=null) {      
            
        	Position mp = this.getPosition(this.getMousePosition());
        	if (mp==null) return;
        	
        	double rng = u.getPosition().getRhumbLine(mp).getDistance();
        	double brg = u.getPosition().getRhumbLine(mp).getBearing();
        	
        	g.setColor(COLOR_TEXT);
        	String _brg = VALUE.TRUEBEARING(brg)[0];
        	String[] _rng = VALUE.RANGE(rng);
        	g.drawString("        " + _brg,             border, getHeight()-(border+textspacing+(textheight*2)));
        	g.drawString("        " + _rng[0],          border, getHeight()-(border+textspacing+(textheight*1)));
        	g.drawString("        " + (scale/1000),     border, getHeight()-(border+textspacing+(textheight*0)));

            g.setColor(COLOR_LABEL);
            g.drawString("Brg           deg",           border, getHeight()-(border+textspacing+(textheight*2)));
            g.drawString("Rng           " + _rng[1],    border, getHeight()-(border+textspacing+(textheight*1)));
            g.drawString("Sca           km",            border, getHeight()-(border+textspacing+(textheight*0)));

    	}
	}

    private boolean isTactical(Point panel) {
        Point ulc = getULC();
        Point lrc = getLRC();
        return ((panel.x>=ulc.x) && (panel.y>=ulc.y) && (panel.x<=lrc.x) && (panel.y<=lrc.y));
    }
        
    private Point getULC() {
        int a = Math.min(this.getWidth(), this.getHeight());
        return new Point((this.getWidth()-a)/2, (this.getHeight()-a)/2);
    }
    
    private Point getLRC() {
        int a = Math.min(this.getWidth(), this.getHeight());
        return new Point(a+(this.getWidth()-a)/2, a+(this.getHeight()-a)/2);
    }

    @Override
    public Position getPosition(Point point) {
        if (!isTactical(point)) return null;
        int wc = this.getWidth() / 2 - 1; 
        int hc = this.getHeight()  / 2 - 1;
        int dx = point.x - wc;
        int dy = hc - point.y;
        int dlat = (int)((1.0 * scale / radius) * dy);
        int dlon = (int)((1.0 * scale / radius) * dx);        
        return world.getObserver().getPosition().getPositionRhumbLine(0,dlat).getPositionRhumbLine(90,dlon);
    }
    
    public boolean winceObserver() {
        boolean res = false;
        if (this.world.getObserver()!=null) {
            res =    (this.world.getObserver().getPosition()!=lastPos) ||
                     (this.world.getObserver().getHeading()!=lastHdg) ||
                     (this.world.getObserver().getSpeed()!=lastSpd) ||
                     (this.world.getObserver().getAltitude()!=lastAlt);
            lastPos = this.world.getObserver().getPosition();
            lastHdg = this.world.getObserver().getHeading();
            lastSpd = this.world.getObserver().getSpeed();
            lastAlt = this.world.getObserver().getAltitude();
        }
        return res;
    }
    
    @Override
    public void itemStateChanged(ItemEvent arg0) {
        redraw = true;
    }
    
    protected boolean save(File file) throws IOException {
        return ImageIO.write(this.getCanvas(),"png", file);     
    }
       
    public String toString() {
        return "Tactical";
    }
    
    public void free() {
        this.removeAll();
        this.setComponentPopupMenu(null);
        this.world = null;
        super.free();
    }
        
	private BufferedImage createChartOverlay(int width, int height) {
	
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    
		Asset ownship = world.getObserver();
		if (ownship==null) return bi;

        int[] argb = new int[4];        
        argb[0] = Color.white.getRed();
        argb[1] = Color.white.getGreen();
        argb[2] = Color.white.getBlue();
        argb[3] = 255;
                	  
        int tacRadiusPxMax = Math.min(width, height) / 2;
        
        Chart chart = model.getChart(ownship.getPosition());
        
        Point ulPxTac = new Point(width/2-tacRadiusPxMax, height/2-tacRadiusPxMax);
        Point lrPxTac = new Point(width/2+tacRadiusPxMax, height/2+tacRadiusPxMax);

        int dxPxTac = lrPxTac.x - ulPxTac.x;
        int dyPxTac = lrPxTac.y - ulPxTac.y;
        
        Position ulPosTac = this.getPosition(ulPxTac); 
        Position lrPosTac = this.getPosition(lrPxTac); 

        Point ulPxCha = chart.convertImage(ulPosTac).getLocation();
        Point lrPxCha = chart.convertImage(lrPosTac).getLocation();
        
        double dxPxCha = lrPxCha.x - ulPxCha.x;
        double dyPxCha = lrPxCha.y - ulPxCha.y;
        
		WritableRaster r = bi.getRaster();
	    for (int y=ulPxCha.y; y<lrPxCha.y; y++) {
	    	for (int x=ulPxCha.x; x<lrPxCha.x; x++) {
	    	    //if (chart.isCoast(new Point(x,y))) {
		        if (chart.isCoast(chart.convertPanel(new IPoint(x,y)))) {
	                int px = (int)(ulPxTac.x + dxPxTac * ((x - ulPxCha.x) / dxPxCha));
	                int py = (int)(ulPxTac.y + dyPxTac * ((y - ulPxCha.y) / dyPxCha));
	                r.setPixel(px, py, argb);
		        }
	    	}
	    }
	    
	    bi.setData(r);	    
	    return bi;
	}

    @Override
    public Point getPoint(Position position) {
        return null;
    }

    @Override
    public Graphics2D getPaintbox() {
        return null;
    }

    @Override
    public boolean isMagnified() {
        return false;
    }

}
