/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.TOOLBOX;
import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.main.Aircraft;
import de.m6c7l.playmate.main.Asset;
import de.m6c7l.playmate.main.Canvasable;
import de.m6c7l.playmate.main.Hookable;
import de.m6c7l.playmate.main.Locateable;
import de.m6c7l.playmate.main.Route;
import de.m6c7l.playmate.main.Selectable;
import de.m6c7l.playmate.main.Surface;
import de.m6c7l.playmate.main.Watercraft;
import de.m6c7l.playmate.main.Waypoint;

public class Chart extends GeoPane implements Canvasable, Surface {
	
	private static final byte SURFACE_COAST = +2; 	
	private static final byte SURFACE_LAND = +1; 
	private static final byte SURFACE_WATER = -1; 
	private static final byte SURFACE_UNKNOWN = 0; 

	private static int nextSerial = 0;
	private int serial = 0;
	
	private Graphics2D g = null;
	private byte[][] surface = null;
	
	private ArrayList<Locateable> locateable = null;
    private ArrayList<Locateable> waypoints = null;
    
	private Selectable focus = null;
	private Selectable move = null;

	private Ruler ruler = null;
	private Selectable rollover = null;
	
	private CopyOnWriteArrayList<ChangeListener> changeListeners = null;

	public Chart(Image image, Position urPos, Point ur, Position llPos, Point ll, Color water, Color land, double minZoom, double maxZoom) throws Exception {
		super(image,urPos,ur,llPos,ll,minZoom,maxZoom,water.darker(),water.darker(),null);
		init(water,land);
	}
	
	public Chart(Image image, Point ul, Position ulPos, Point lr, Position lrPos, Color water, Color land, double minZoom, double maxZoom) throws Exception {		
		super(image,ul,ulPos,lr,lrPos,minZoom,maxZoom,water.darker(),water.darker(),null);
		init(water,land);
	}
	
	public boolean isDragable() {
		return rollover == null;
	}
	
	public boolean isPickable(Point p) {
		rollover = getSelectable(p);
		return rollover != null;
	}
	
	private void init(Color water, Color land) throws Exception {

		this.serial = nextSerial++;
		this.locateable = new ArrayList<Locateable>();
        this.waypoints = new ArrayList<Locateable>();
		this.surface = this.initSurface(land,water);
		
		this.changeListeners = new CopyOnWriteArrayList<ChangeListener>();
		
		/*
		 * ruler for distance and angle measurements with rmb
		 */
		this.addMouseListener(new MouseAdapter() { 
			public void mousePressed(MouseEvent e) {
				if (e.getButton()!=MouseEvent.BUTTON3) return;
				ruler = new Ruler(getPosition(getRulerStart(e.getPoint())));
				applyDefaultCursor(e.getPoint());
			}
			public void mouseReleased(MouseEvent e) {
				if (e.getButton()!=MouseEvent.BUTTON3) return;
				ruler = null;
				applyPickCursor(e.getPoint());
			}
		});
		
		/*
		 * selection and highlighting of objects with lmb
		 */
		this.addMouseListener(new MouseAdapter(){
		    public void mouseClicked(MouseEvent e){
				if ((e.getButton()!=MouseEvent.BUTTON1) || (ruler!=null)) return; // dragOffset kann gesetzt sein!
				Selectable s = rollover;
				if (s!=null) {
			       	if (!s.isSelected()) {
						select(s);				        		
			       	} else {
			       		Hookable h = getHookable(e.getPoint());
						if (h!=null) {
				       		if (!h.isHooked()) {
								hook(h);				        			
				       		} else {
				       			hook(null);
				       		}								
						}
				    }
				} else { // unselect all
					select(null);
				}
		    }
		});
		
		/*
		 * drag with lmb
		 */
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton()!=MouseEvent.BUTTON1) return;
				Selectable s = rollover;
				if (s!=null) {
					move = s;
					applyPutCursor(e.getPoint());
				}
			}
			public void mouseReleased(MouseEvent e) {
				if ((move==null) || (e.getButton()!=MouseEvent.BUTTON1)) return;
				move = null;
				applyPickCursor(e.getPoint());
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (move==null) return;
				if (isCanvas(e.getPoint()) && ((!(move instanceof Watercraft)) || (isWater(e.getPoint())))) {
					Position p = getPosition(convertCanvas(e.getPoint()).getLocation());
					p.setAltitude(move.getPosition().getAltitude());
					move.setPosition(p);
				}
			}
		});

		/*
		 * set waypoints while ruler is active
		 */
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if ((ruler==null) || (focus==null) || (e.getButton()!=MouseEvent.BUTTON1)) return;
				if (isCanvas(e.getPoint())) {
					Position wp = getPosition(convertImage(e.getPoint()));
					if ((focus instanceof Watercraft) || (focus instanceof Aircraft)) {
						Asset asset = (Asset)focus;
						if (asset.getEnvironment().isStopped()) {
							asset.getRoute().addWaypoint(wp);
							put(asset.getRoute().lastWaypoint());
							ruler.setStart(wp);
						}
					} else if (focus instanceof Waypoint) {
						Route route = ((Waypoint)focus).getRoute();
						if (route.getAsset().getEnvironment().isStopped()) {
							route.addWaypoint(wp);
							put(route.lastWaypoint());
							ruler.setStart(wp);
						}
					}
				}
			}
		});

	}
	
	public void free() {
		this.changeListeners.clear();
		this.locateable.clear();
		this.waypoints.clear();
		this.ruler = null;
		this.move = null;
		this.focus = null;
		this.rollover = null;
		super.free();
	}
	
	/*
	 * mouse wheel
	 */
	
	private Point getRulerStart(Point panel) {
		Selectable[] select = getSelected();
		if (select.length>0) {
			return select[select.length-1].getPoint(this); // last = second = waypoint (selection logic)
		}
		return convertCanvas(panel).getLocation();
	}
	
	protected boolean save(File file) throws IOException {
		return ImageIO.write(this.getCanvas(),"png", file);		
	}
	
	private Selectable getSelectable(Point panel) {
		return this.getSelectable(panel,8);
	}
	
	private Selectable getSelectable(Point panel, int pixelRadius) {
		Point p = convertCanvas(panel).getLocation();
		for (int i=0; i<locateable.size(); i++)  {
			if (locateable.get(i) instanceof Selectable) {
				Selectable temp = ((Selectable)locateable.get(i));
				int x1 = p.x-pixelRadius;
				int x2 = p.x+pixelRadius;
				int y1 = p.y-pixelRadius;
				int y2 = p.y+pixelRadius;
				Point q = temp.getPoint(this);
				if ((q.x>=x1) && (q.y>=y1) && (q.x<=x2) && (q.y<=y2)) {
					return temp;
				}
			}
		}
		return null;
	}

	private Hookable getHookable(Point panel) {
		return this.getHookable(panel,8);
	}

	private Hookable getHookable(Point panel, int pixelRadius) {
		Point p = convertCanvas(panel).getLocation();
		for (int i=0; i<locateable.size(); i++)  {
			if (locateable.get(i) instanceof Hookable) {			    
				Hookable temp = ((Hookable)locateable.get(i));
				int x1 = p.x-pixelRadius;
				int x2 = p.x+pixelRadius;
				int y1 = p.y-pixelRadius;
				int y2 = p.y+pixelRadius;
				Point q = temp.getPoint(this);
				if ((q.x>=x1) && (q.y>=y1) && (q.x<=x2) && (q.y<=y2)) {
					return temp;
				}
			}
		}
		return null;
	}
	
	private void select(Selectable selectable) {
		focus = selectable;
		if (selectable!=null) {
			selectable.setSelected(true);
		} else {
			Selectable[] select = getSelected(); // first = vessel (selection logic)
			if (select.length>0) {
				select[0].setSelected(false);								
			}
		}
	}
	
	private void hook(Hookable hookable) {
		if (hookable!=null) {
			hookable.setHooked(true);
		} else {
			Hookable hook = getHooked();
			if (hook!=null) {
				hook.setHooked(false);				
			}
		}
	}
	
	private Selectable[] getSelected() {
		ArrayList<Selectable> arr = new ArrayList<Selectable>();
		for (int i=0; i<locateable.size(); i++) {
			Locateable o = locateable.get(i);
			if ((o instanceof Selectable) && ((Selectable)o).isSelected()) {
				arr.add((Selectable)o);
			}
		}
		return arr.toArray(new Selectable[arr.size()]);
	}
	
	private Hookable getHooked() {
		for (int i=0; i<locateable.size(); i++) {
			Locateable o = locateable.get(i);
			if ((o instanceof Hookable) && ((Hookable)o).isHooked()) {
				return (Hookable)o;
			}
		}
		return null;
	}
	
	public boolean put(Locateable obj) {
		boolean b = false;
		int index = this.locateable.indexOf(obj);
		if (index==-1) {
			b = this.locateable.add(obj);	
		} else {
			b = this.locateable.set(index,obj)!=null;
		}
		return b;
	}

	public boolean remove(Locateable obj) {
		int index = this.locateable.indexOf(obj);
		if (index>-1) {
	        this.removeGarbage();
		    return this.locateable.remove(obj);
		}
		return false;
	}

	private void removeGarbage() {
        ArrayList<Locateable> todel = new ArrayList<Locateable>();
        for (int i=0; i<this.locateable.size(); i++) {              
            if ((this.locateable.get(i) instanceof Waypoint) && (this.locateable.get(i).getPosition()==null)) {
                todel.add(this.locateable.get(i));
            }
        }
        for (int i=0; i<todel.size(); i++) {
            this.locateable.remove(todel.get(i));
        }	    
	}
	
	/*
	 * canvasable
	 */
	
	@Override
	public Point getPoint(Position position) {
		return this.convertCanvas(position).getLocation();
	}
	
	@Override
	public Graphics2D getPaintbox() {
		return (Graphics2D)g.create();
	}
	
	@Override
	public boolean isMagnified() {
		return isGrained();
	}
	
	@Override
	public BufferedImage getCanvas() {
		
		BufferedImage canvas = super.getCanvas();
		if (canvas==null) return null;
		
		g = canvas.createGraphics();
		g.setStroke(new BasicStroke(1f));
		
		for (int i=0; i<locateable.size(); i++)  {
			if (locateable.get(i) instanceof Asset) {
				((Asset)locateable.get(i)).draw(this);					
			}
		}
		
		if (ruler!=null) {
			ruler.setPosition(getPosition(convertImage(getMousePosition())));
			ruler.draw(this);
		}
		
		g.dispose();
		return canvas;

	}
	
	private boolean isLand(IPoint image) {
		if ((image==null) || (!contains(image))) return false;
		Point p = image.getLocation();
		return this.surface[p.x][p.y]>SURFACE_UNKNOWN;
	}
	
	private boolean isWater(IPoint image) {
		if ((image==null) || (!contains(image))) return false;
		Point p = image.getLocation();
		return this.surface[p.x][p.y]<SURFACE_UNKNOWN;
	}
	
	private boolean isCoast(IPoint image) {
		if ((image==null) || (!contains(image))) return false;
		Point p = image.getLocation();
		return this.surface[p.x][p.y]==SURFACE_COAST;
	}
	
	public boolean isLand(Position pos) {
		return this.isLand(this.convertImage(pos));
	}

	public boolean isWater(Position pos) {
		return this.isWater(this.convertImage(pos));
	}
	
	public boolean isCoast(Position pos) {
		return this.isCoast(this.convertImage(pos));
	}
	
	public boolean isLand(Point panel) {
		return this.isLand(this.convertImage(panel));
	}

	public boolean isWater(Point panel) {
		return this.isWater(this.convertImage(panel));
	}
	
	public boolean isCoast(Point panel) {
		return this.isCoast(this.convertImage(panel));
	}

	public boolean isCovert(Position pos1, Position pos2) {
		if ((pos1==null) || (pos2==null) || ((!contains(pos1)) && (!contains(pos2)))) return false;
		if (((!contains(pos1)) | (!contains(pos2))) &&
				(pos1.getRhumbLine(pos2).getDistance()>getPositionOrigin().getRhumbLine(getPosition(UL)).getDistance())) return true;
		IPoint image1 = this.convertImage(pos1);
		IPoint image2 = this.convertImage(pos2);	
		Point[] lineOfSight = TOOLBOX.getLine(image1.getLocation(),image2.getLocation());
        int w = super.getImage().getWidth(null);
        int h = super.getImage().getHeight(null);
		for (int i=0; i<lineOfSight.length; i++) {
			if ((lineOfSight[i].x>=0) && (lineOfSight[i].y>=0) && (lineOfSight[i].x<w) && (lineOfSight[i].y<h)) {
				if (surface[lineOfSight[i].x][lineOfSight[i].y]!=SURFACE_WATER) {
					return true;				
				}
			}
		}
		return false;
	}

    private byte[][] initSurface(Color land, Color water) {
    	
        int w = super.getImage().getWidth(null);
        int h = super.getImage().getHeight(null);
        
    	byte pixels[][] = new byte[w][h];
    			
        PixelGrabber grabber = new PixelGrabber(super.getImage(), 0, 0, w, h, false);
        try {
            if (!grabber.grabPixels()) return null;
        } catch (InterruptedException e) {
        	return null;
        }
        int[] pix = (int[]) grabber.getPixels();

        for (int x = w; x-- > 0; ) {
            for (int y = h; y-- > 0; ) {
            	int argb = pix[y * w + x];
            	if (like(new Color(argb),land)) {
                    pixels[x][y] = SURFACE_LAND;
            	} else if (like(new Color(argb),water)) {
                    pixels[x][y] = SURFACE_WATER;    
            	} else {
            		pixels[x][y] = SURFACE_UNKNOWN;
            	}
            }
        }
        for (int x = w-1; x-- > 1; ) {
            for (int y = h-1; y-- > 1; ) {
            	if (pixels[x][y]==SURFACE_LAND) {
                	if ((pixels[x][y+1]==SURFACE_WATER) ||
                			(pixels[x][y-1]==SURFACE_WATER) ||
                				(pixels[x+1][y]==SURFACE_WATER) ||
                					(pixels[x-1][y]==SURFACE_WATER)) {
                		pixels[x][y] = SURFACE_COAST;
                    }
            	}
            }
        }
        return pixels;
    }
    
    private static boolean like(Color a, Color b) {
    	return (b!=null) &&
    			(Math.abs(a.getRed()-b.getRed())<=20) &&
    			 (Math.abs(a.getGreen()-b.getGreen())<=20) &&
    			  (Math.abs(a.getBlue()-b.getBlue())<=20);
    }
    
	public boolean equals(Object o) {
		if ((o!=null) && (o instanceof Chart)) {
			Chart chartPane = (Chart)o;
			return chartPane.serial==this.serial;
		}
		return false;
	}
	
	public int hashCode() {
	    int hc = 19;
	    int hashMultiplier = 31;
	    hc = hc * hashMultiplier + this.serial;
	    return hc; 
	}
	
	public void setMagnifier(double value) {
		super.setMagnifier(value);
		fireChangeEvent();
	}
	
	/*
	 * change listener
	 */
	
	public void addChangeListener(ChangeListener l) {
		int index = this.changeListeners.indexOf(l);
		if (index==-1) {
			this.changeListeners.add(l);			
		} else {
			this.changeListeners.set(index,l);						
		}
	}

	public int getChangeListenerCount() {
		return this.changeListeners.size();
	}
	
	public void removeChangeListener(ChangeListener l) {
		this.changeListeners.remove(l);
	}
		  
	private void fireChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		if (changeListeners!=null) {
			for (ChangeListener l : changeListeners) {
				l.stateChanged(e);	
			}			
		}
	}
	
}
