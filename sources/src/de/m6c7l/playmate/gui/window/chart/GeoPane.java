/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.chart;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.border.Border;

import de.m6c7l.lib.gui.TOOLBOX;
import de.m6c7l.lib.gui.graphics.ImagePane;
import de.m6c7l.lib.util.geo.Angle;
import de.m6c7l.lib.util.geo.Position;

public abstract class GeoPane extends ImagePane {
	
	public static CORNER UL = CORNER.UL;
	public static CORNER LL = CORNER.LL;
	public static CORNER UR = CORNER.UR;
	public static CORNER LR = CORNER.LR;
	
	private Hashtable<Point,Position> htRP = null;

	private int width = 0;
	private int height = 0;

	private Position center = null;
	private Position ulcPos = null;
	private Position llcPos = null;
	private Position urcPos = null;
	private Position lrcPos = null;
	
	private double gridEdgeDistance = 0;
	private double gridZoom = 0;
	private boolean gridShow = true;

	private double deltaLat = 0;
	private double deltaLon = 0;
	
	private double magnifyDetailed = 1.0;
	
	private boolean isOverlapping = false;
	private boolean throughDateLine = false;

	private Color gridColor = null;
	private Color textColor = null;
	
	private GeoPane(Image image, Color grid, Color text, Border border) throws Exception {
		
		super(image,20,true,true);
		
		this.htRP = new Hashtable<Point,Position>();	
		
		this.gridColor = grid;
		this.textColor = text;
		
		this.setBorder(border);
		
	}

	// ####
	
	public GeoPane(
			Image image,
			Position urPos,
			Point ur,
			Position llPos,
			Point ll,
			double scaleDown,
			double scaleUp,
			Color grid,
			Color text,
			Border border) throws Exception {
		this(
				image,
				new Point(ll.x,ur.y),
				new Position(urPos.getLatitude(),llPos.getLongitude()),
				new Point(ur.x,ll.y),
				new Position(llPos.getLatitude(),urPos.getLongitude()),
				scaleDown,
				scaleUp,
				grid,
				text,
				border);
	}
	
	public GeoPane(
			Image image,
			Point ul,
			Position ulPos,
			Point lr,
			Position lrPos,
			double scaleDown,
			double scaleUp,
			Color grid,
			Color text,
			Border border) throws Exception {
		
		this(image, grid, text, border);
		
		width = image.getWidth(null);
		height = image.getHeight(null);
		
		int a = -1;
		int b = -1;

		a = this.setReference(ul,ulPos);
		b = this.setReference(lr,lrPos);	
		
		if (this.getReferenceCount()<2) throw new Exception("reference points must be at different corners");
		if (this.getReferenceAreaOppositeArea(a)!=b) throw new Exception("reference points must be at opposite corners");
		
		if (Math.min(a,b)!=0) { // 1,2
			
			Point urPoint = this.getReferencePoint(UR);
			Position urPosition = this.getReferencePosition(UR);
			Point llPoint = this.getReferencePoint(LL);
			Position llPosition = this.getReferencePosition(LL);
			
			this.setReference(
					new Point(llPoint.x,urPoint.y),
					new Position(urPosition.getLatitude(),llPosition.getLongitude()));
			this.setReference(
					new Point(urPoint.x,llPoint.y),
					new Position(llPosition.getLatitude(),urPosition.getLongitude()));
			
		} 
			
		Point ulPoint = this.getReferencePoint(UL);
		Position ulPosition = this.getReferencePosition(UL);
		Point lrPoint = this.getReferencePoint(LR);
		Position lrPosition = this.getReferencePosition(LR);
		
		int deltaY = lrPoint.y-ulPoint.y;
		double deltaLat = Math.abs(lrPosition.getLatitude()-ulPosition.getLatitude());
		double ratioLatToY = deltaLat/deltaY;
		double posLatTop = ulPosition.getLatitude()+ulPoint.y*ratioLatToY;		
		double posLatBottom = lrPosition.getLatitude()-(this.height-1-lrPoint.y)*ratioLatToY;
		
		int deltaX = lrPoint.x-ulPoint.x;
		double deltaLon = lrPosition.getLongitude()-ulPosition.getLongitude();
		if (deltaLon<0) deltaLon = deltaLon + 360;
		double ratioLonToX = deltaLon/deltaX;
		double posLonLeft = ulPosition.getLongitude()-ulPoint.x*ratioLonToX;
		double posLonRight = lrPosition.getLongitude()+(this.width-1-lrPoint.x)*ratioLonToX;	

		this.ulcPos = new Position(posLatTop,posLonLeft);
		this.lrcPos = new Position(posLatBottom,posLonRight);
		this.llcPos = new Position(lrcPos.getLatitude(),ulcPos.getLongitude());
		this.urcPos = new Position(ulcPos.getLatitude(),lrcPos.getLongitude());
		
		this.deltaLat = Math.abs(posLatBottom-posLatTop);
		this.deltaLon = posLonRight-posLonLeft;
		if (this.deltaLon < 0) {
			throughDateLine = true;
			this.deltaLon = 360+this.deltaLon;
		}
		
		double diffOrig = lrPosition.getLongitude()-ulPosition.getLongitude();
		double diffCorn = lrcPos.getLongitude()-ulcPos.getLongitude();
		
		if (diffCorn<diffOrig) {
			isOverlapping = true;
		}

		this.setMagnifierMinimum(scaleDown);
		this.setMagnifierMaximum(scaleUp);
	
		magnifyDetailed = (ulcPos.getRhumbLine(urcPos,(byte)1).getDistance()/width+
							ulcPos.getRhumbLine(llcPos,(byte)1).getDistance()/height)/4.0;
		
		center = Position.lerp(ulcPos, lrcPos, 0.5f);
		
		// ####
		
		setMagnifier(Math.sqrt((getMagnifierMaximum()+getMagnifierMinimum())));
		
		/*
		 * Cursor bei Bewegung ohne Maustaste
		 */
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				applyPickCursor(e.getPoint());
			}
		});

		/*
		 *  springe zu Position mit mittlerer Maustaste
		 */
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				
				if (e.getButton()!=MouseEvent.BUTTON2) return;			
				gotoPosition(getPosition(convertPanel(getMousePosition())));
				
			}
			public void mouseReleased(MouseEvent e) {
			}
		});
		
	}
	
	public void gotoPosition(Position pos) {
		Position p = getPositionCenter();
		jump(
				convertPanel(convertImage(p)),
				convertPanel(convertImage(pos)));		
	}
	
	protected void applyDragCompleted(Point p) {
		applyPickCursor(p);
	}
	
	protected void applyDragStarted(Point p) {
		applyPutCursor(p);
	}

	public abstract boolean isPickable(Point p);

	protected void applyPickCursor(Point p) {
		if (isPickable(p)) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			applyDefaultCursor(p);					
		}
	}

	protected void applyDefaultCursor(Point p) {
		if (isCanvas(p)) {
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));			
		} else {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	protected void applyPutCursor(Point p) {
		setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}
	
	@Override
	public BufferedImage getCanvas() {

		BufferedImage canvas = super.getCanvas();
		if (canvas==null) return null;
		
		Graphics2D g = canvas.createGraphics();
		
		if ((gridShow) && (getSize().width>200) && (getSize().height>200)) {
			
			Position ul = getPositionCanvas(UL);
			Position lr = getPositionCanvas(LR);
			
			double due = getGridGap();
			
			double lonTo = lr.getLongitude();
			double lonFrom = ul.getLongitude();
			double dLon = lonTo - lonFrom;
			if (dLon<0) {
				dLon = 360+dLon;
			}
			
			if ((ul!=null) && (lr!=null) && (due>0)) {
			
				Position p1 = null;
				Position p2 = null;
				
				double lon = (((int)(lonFrom/due))*due);
				double endLon = lonFrom+dLon; 
				
				while (lon < endLon) {
					
					p1 = new Position(ul.getLatitude(),lon);
					p2 = new Position(lr.getLatitude(),lon);
					
					Point from = convertCanvas(p1).getLocation();
					Point to = convertCanvas(p2).getLocation();
					
					g.setColor(gridColor);					
					TOOLBOX.drawDashedLine(g,from.x,from.y,to.x,to.y,2,4);
					//g.drawLine(from.x,from.y,to.x,to.y);
					
					g.setColor(textColor);
					g.drawString(Angle.toString(lon),to.x+5,to.y-5);						
					
					lon = lon + due;
					
				}

				double lat = (((int)(lr.getLatitude()/due))*due);
				double endLat = ul.getLatitude();
				
				while (lat<endLat) {
					
					p1 = new Position(lat,lonFrom);
					p2 = new Position(lat,endLon);
					
					Point from = convertCanvas(p1).getLocation();
					Point to = convertCanvas(p2).getLocation();

					g.setColor(gridColor);		
					TOOLBOX.drawDashedLine(g,from.x,from.y,to.x,to.y,2,4);
					//g.drawLine(from.x,from.y,to.x,to.y);

					g.setColor(textColor);
					g.drawString(Angle.toString(lat),from.x+5,from.y-5);	
					
					lat = lat + due;
					
				}
				
			}

		}
		
		g.dispose();
		return canvas;

	}
	
	public boolean contains(IPoint image) {
		Point p = image.getLocation();
		return (!(((p.x>=width) || (p.x<0)) || ((p.y>=height) || (p.y<0))));
	}
	
	public boolean contains(Position position) {
		if (position==null) return false;
		double lon = position.getLongitude();
		double lat = position.getLatitude();
		boolean t1 = lon>=llcPos.getLongitude();
		boolean t2 = lon<=urcPos.getLongitude();
		boolean t3 = (lat>=llcPos.getLatitude()) && (lat<=urcPos.getLatitude());
		boolean t4 = llcPos.getLongitude()<=urcPos.getLongitude();
		return (t1 && t2 && t3 && t4) || ((t1 | t2) && t3 && !t4);
	}
	
	public IPoint convertCanvas(Position position) {
		return super.convertCanvas(this.convertImage(position));
	}
	
	public IPoint convertImage(Position position) {
		
		if (position==null) return null;

		double lon = position.getLongitude();
		if ((throughDateLine) && (Math.abs(ulcPos.getLongitude()-lon)>=180)) lon = lon+360;

		double diffPosLat = -(position.getLatitude()-ulcPos.getLatitude());
		double diffPosLon = lon-ulcPos.getLongitude();

		if ((isOverlapping) && (diffPosLon<0)) diffPosLon = (lon+360)-ulcPos.getLongitude();

		double posX = (width-1)*(diffPosLon/deltaLon);
		double posY = (height-1)*(diffPosLat/deltaLat);

		return new IPoint(posX,posY);
		
	}

	public Position getPositionOrigin() {
		return center;
	}

	public Position getPositionCenter() {
		Position ul = getPositionCanvas(UL);
		Position lr = getPositionCanvas(LR);
		if ((lr!=null) && (ul!=null)) {
			return Position.lerp(ul, lr, 0.5f);
		}
		return null;
	}
	
	public boolean isGrained() {
		return this.magnifyDetailed<this.getMagnifier();
	}

	public void setShowGrid(boolean show) {
		gridShow = show;
	}

	public boolean isShowGrid() {
		return gridShow;
	}

	public double getGridGap() {
		if (gridZoom!=getMagnifier()) {
			gridZoom = getMagnifier();
			double distance = getGridGapMaximum();
			if (distance!=0) {
				double d = distance/1852.0;
				double[] x = {60.0, 20.0, 10.0, 5.0, 2.0, 1.0, 1/2.0, 1/6.0, 1/12.0, 1/30.0, 1/60.0, 1/120.0, 1/300.0, 1/600.0, 1/1200.0, 1/3000.0};
				double y = x[0];
				int i = 0;
				while ((y*60.0>d) && (i<x.length)) {
					y = x[i];
					i++;
				}
				if (i>0) i--;
				if (i<x.length) {
					y = x[i];
					gridEdgeDistance = y;					
				} else {
					gridEdgeDistance = 0;
				}
			}			
		}
		return gridEdgeDistance;
	}
	
	private double getGridGapMaximum() {
		Position ul = getPositionCanvas(UL);
		Position lr = getPositionCanvas(LR);
		if ((lr!=null) && (ul!=null)) {
			return ul.getRhumbLine(lr).getDistance()/5.0;
		}
		return 0;
	}
	
	public Position getPositionCanvas(CORNER corner) {
		IBox r = new IBox(getCanvasLocation(), getCanvasSize());
		if (corner==CORNER.UL) {
			return getPosition(convertImage(new IPoint(r.getX1(),r.getY1()).getLocation()));			
		
		} else if (corner==CORNER.LR) {
			return getPosition(convertImage(new IPoint(r.getX2(),r.getY2()).getLocation()));		

		} else if (corner==CORNER.UR) {
			return getPosition(convertImage(new IPoint(r.getX2(),r.getY1()).getLocation()));		
		
		} else if (corner==CORNER.LL) {
			return getPosition(convertImage(new IPoint(r.getX1(),r.getY2()).getLocation()));			
		
		}
		return null;
	}

	public Position getPosition(Point canvas) {
		return getPosition(convertImage(convertPanel(canvas)));
	}
	
	public Position getPosition(IPoint image) {

		if (image==null) return null;

		double fy = image.getY()/(height-1);
		double dlat = deltaLat*fy;
		double posLat = ulcPos.getLatitude()-dlat;
		
		double fx = image.getX()/(width-1);
		double dlon = deltaLon*fx;
		double posLon = ulcPos.getLongitude()+dlon;
		
		return new Position(posLat, posLon);
		
	}
	
	public Position getPosition(CORNER corner) {
		if (corner==CORNER.UL) {
			return ulcPos;
		} else if (corner==CORNER.LR) {
			return lrcPos;
		} else if (corner==CORNER.UR) {
			return urcPos;
		} else if (corner==CORNER.LL) {
			return llcPos;
		}
		return null;
	}
	
	/*
	 * Referenzpunkte in Referenzgebieten für geografische Koordinaten
	 * +-------+-------+
	 * | 0=UL  | 1=UR  |
	 * +-------+-------+
	 * | 2=LL  | 3=LR  |
	 * +-----+---------+
	 * Anzahl der Referenzpunkte hängt von der Anzahl schon hinzugefügter Punkte ab:
	 * -> sobald der 4. Punkt hinzugefügt wurde, erhöht sich die Feldanzahl von 4 auf 9.
	 * -> sobald der 9. Punkt hinzugefügt wurde, erhöht sich die Feldanzahl von 9 auf 16.
	 * ...
	 */
	
	public Position getReferencePosition(CORNER corner) {
		if (corner==CORNER.UL) {
			return this.getReferencePosition(0);			
		} else if (corner==CORNER.LR) {
			return this.getReferencePosition(this.getReferenceAreaCount()-1);
		} else if (corner==CORNER.UR) {
			return this.getReferencePosition((int)Math.sqrt(this.getReferenceAreaCount())-1);
		} else if (corner==CORNER.LL) {
			return this.getReferencePosition(this.getReferenceAreaCount()-(int)Math.sqrt(this.getReferenceAreaCount()));
		}
		return null;
	}

	public Point getReferencePoint(CORNER corner) {
		if (corner==CORNER.UL) {
			return this.getReferencePoint(0);			
		} else if (corner==CORNER.LR) {
			return this.getReferencePoint(this.getReferenceAreaCount()-1);
		} else if (corner==CORNER.UR) {
			return this.getReferencePoint((int)Math.sqrt(this.getReferenceAreaCount())-1);
		} else if (corner==CORNER.LL) {
			return this.getReferencePoint(this.getReferenceAreaCount()-(int)Math.sqrt(this.getReferenceAreaCount()));
		}
		return null;
	}
	
	private int setReference(Point point, Position position) {
		int area = this.getReferenceArea(point);
		if (area!=-1) {
			if (this.getReferencePoint(area)!=null)
				this.htRP.remove(getReferencePoint(area));
			this.htRP.put(point,position);
		}
		return area;
	}
	
	private Point getReferencePoint(int area) {
		if (area>=this.getReferenceAreaCount()) throw new IndexOutOfBoundsException(area + " >= " + this.getReferenceAreaCount());
		if (area<0) throw new IndexOutOfBoundsException(area + " < 0");
		Point[] p = this.htRP.keySet().toArray(new Point[this.htRP.size()]);
		for (int i=0; i<p.length; i++) if (area==getReferenceArea(p[i])) return p[i];
		return null;
	}
	
	private Position getReferencePosition(int area) {
		Point p = this.getReferencePoint(area);
		if (p!=null) return this.htRP.get(p);
		return null;
	}
	
	private int getReferenceCount() {
		return this.htRP.size();
	}
	
	private int getReferenceAreaCount() {
		int i = 1;
		do { i++; } while (i*i<=this.getReferenceCount()); // min. 4 (2x2)
		return i*i;
	}
	
	private int getReferenceArea(Point point) {
		int d = (int)Math.sqrt(getReferenceAreaCount());
		for (int r=0; r<d; r++) {
			for (int c=0; c<d; c++) {
				Point ul = new Point(c*getReferenceAreaWidth(),r*getReferenceAreaHeight());
				Point lr = new Point(ul.x+getReferenceAreaWidth(),ul.y+getReferenceAreaHeight());
				if ((point.x>=ul.x) && (point.x<=lr.x) && (point.y>=ul.y) && (point.y<=lr.y)) {
					return d*r+c;
				}
			}
		}
		return -1;
	}
	
	private int getReferenceAreaWidth() {
		int d = (int)Math.sqrt(getReferenceAreaCount());
		return (int)Math.round(getImage().getWidth(null)/(d*1.0));
	}
	
	private int getReferenceAreaHeight() {
		int d = (int)Math.sqrt(getReferenceAreaCount());
		return (int)Math.round(getImage().getHeight(null)/(d*1.0));
	}
	
	private int getReferenceAreaOppositeArea(int area) {
		if (area>=this.getReferenceAreaCount()) throw new IndexOutOfBoundsException(area + ">=" + this.getReferenceAreaCount());
		if (area<0) throw new IndexOutOfBoundsException(area + "<0");
		int d = (int)Math.sqrt(getReferenceAreaCount());
		boolean ulc = (area%d==0) && (area/d==0);
		boolean urc = (area%d==d-1) && (area/d==0);
		boolean llc = (area%d==0) && (area/d==d-1);
		boolean lrc = (area%d==d-1) && (area/d==d-1);
		if (ulc) return getReferenceAreaCount()-1;
		if (llc) return d-1;
		if (lrc) return 0;
		if (urc) return getReferenceAreaCount()-1-(d-1);
		return -1;
	}
	
    private enum CORNER {

    	UL		(),
        LL		(),
    	UR		(),
    	LR		();

    }
    
}