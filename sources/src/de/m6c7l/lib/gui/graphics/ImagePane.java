/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

public abstract class ImagePane extends CanvasPane  {

	private Image image = null;

	private IPoint imageOffset = null;
	private IDimension imageSize = null;
	
	private IPoint canvasOffset = null;
	private IDimension canvasSize = null;
	
	private double zoom = 1.0;
	private double minZoom = zoom;
	private double maxZoom = zoom;
	
	private Point drag = null;
	
	public ImagePane(Image image) {
		this(image,30);
	}

	public ImagePane(Image image, int fixedFPS) {
		this(image, fixedFPS, false);
	}

	public ImagePane(Image image, int fixedFPS, boolean dragable) {
		this(image, fixedFPS, dragable, false);
	}

	public ImagePane(Image image, int fixedFPS, boolean dragable, boolean zoomable) {

		super(fixedFPS);
		
		this.image = image;
		
		this.imageOffset = new IPoint();
		this.canvasOffset = new IPoint();
		
		this.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				resize();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});

		if (zoomable) {
			
			this.addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					setMagnifier(getMagnifier() + (-e.getWheelRotation() * getMagnifier()/10.0));
				}
			});			
		}
		
		if (dragable) {
			
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.getButton()!=MouseEvent.BUTTON1) return;
					if (isDragable()) {
						drag = convertPanelOffset(e.getPoint()).getLocation();
						applyDragStarted(e.getPoint());
					}
				}
				public void mouseReleased(MouseEvent e) {
					if ((drag==null) || (e.getButton()!=MouseEvent.BUTTON1)) return;
					drag = null;
					applyDragCompleted(e.getPoint());
				}
			});
			
			this.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					if (drag==null) return;
					if (isCanvas(e.getPoint())) {
						drag(drag, e.getPoint());
					}
				}
			});
			
		}
		
		resize();
	
	}
	
	private IPoint convertPanelOffset(Point panel) {
		return new IPoint(
				(panel.x-canvasOffset.x*zoom),
				(panel.y-canvasOffset.y*zoom));
	}
	
	public abstract boolean isDragable();
	
	protected abstract void applyDragStarted(Point p);	
	protected abstract void applyDragCompleted(Point p);
	
	public BufferedImage getCanvas() {

		if (isResizeReady()) {
			
			Dimension cnvSiz = canvasSize.getSize();			
			BufferedImage img = new BufferedImage(cnvSiz.width,cnvSiz.height,BufferedImage.TYPE_INT_RGB);
			
			Graphics2D img2D = img.createGraphics();

			Point imgOfs = imageOffset.getLocation();
			Dimension imgSiz = imageSize.getSize();
			
			img2D.drawImage(getImage(), imgOfs.x, imgOfs.y, imgSiz.width, imgSiz.height, null);
			img2D.dispose();
			
			return img;
			
		}
		
		return null;
	}
	
	protected void jump(Point from, Point to) {
		
		Point ofs = convertPanelOffset(to).getLocation();
		
		int x = from.x - ofs.x;
		int y = from.y - ofs.y;
		
		validate(new IPoint(x/zoom,y/zoom));
		
	}

	
	private void drag(Point drag, Point current) {
		
		int x = current.x - drag.x;
		int y = current.y - drag.y;
		
		validate(new IPoint(x/zoom,y/zoom));
		
	}

	private void resize() {
		
		canvasSize = getCanvasSize(zoom);
		imageSize = getImageSize(zoom);
		
		validate(canvasOffset);
		
	}
	
	private void validate(IPoint ofs) {
		
		IDimension cnvSiz = canvasSize; 
		IDimension imgSiz = imageSize;

		IPoint idoulc = getCanvasOffset(cnvSiz,imgSiz,new IPoint(0,0));
		IPoint idolrc = getCanvasOffset(cnvSiz,imgSiz,new IPoint(-(imgSiz.width-canvasSize.width),-(imgSiz.height-canvasSize.height)));

		IPoint ulc = getImageOffset(cnvSiz, imgSiz, ofs);
		IPoint lrc = new IPoint((canvasSize.width-1-ulc.x),(canvasSize.height-1-ulc.y));
		
		canvasOffset = ofs;
		if (ulc.x> 0) canvasOffset.x = idoulc.x;		
		if (ulc.y> 0) canvasOffset.y = idoulc.y;	
		if (lrc.x> imgSiz.width-1) canvasOffset.x = idolrc.x;
		if (lrc.y> imgSiz.height-1) canvasOffset.y = idolrc.y;	
		
		imageOffset = getImageOffset(canvasSize, imageSize, canvasOffset);
		
	}
	
	/*
	 * image
	 */
	
	public final Image getImage() {
		return image;
	}
	
	public boolean isCanvas(Point panel) { // liegt Punkt auf Panel auch auf Leinwand
		IPoint a = getCanvasLocation();
		return (panel.x>=a.x) &&
				(panel.y>=a.y) &&
				 (panel.x< a.x+canvasSize.width) &&
				  (panel.y< a.y+canvasSize.height);
	}
	
	protected IPoint getCanvasLocation() { // nur wenn Leinwand kleiner als Panel interessant
		return new IPoint(
				(getSize().width-canvasSize.width)/2.0,
				(getSize().height-canvasSize.height)/2.0);
	}
	
	protected IDimension getCanvasSize() { // nie groesser als Panel
		return this.getCanvasSize(this.zoom);
	}
	
	public Dimension getPreferredSize() {
		return getCanvasSize().getSize();
	}

	/*
	 * conversions
	 */

	public IPoint convertImage(Point panel) {
		if ((panel!=null)) {
			IPoint cnvPos = getCanvasLocation();
			return new IPoint(
					(panel.x-cnvPos.x-imageOffset.x)/zoom,
					(panel.y-cnvPos.y-imageOffset.y)/zoom);	
		}
		return null;
	}
	
	public IPoint convertImage(IPoint canvas) {
		if ((canvas!=null)) {
			return new IPoint(
					(canvas.x-imageOffset.x)/zoom,
					(canvas.y-imageOffset.y)/zoom);	
		}
		return null;
	}
	
	public IPoint convertCanvas(IPoint image) {
		if (image!=null) {
			return new IPoint(
					image.x*zoom+imageOffset.x,
					image.y*zoom+imageOffset.y);				
		}
		return null;
	}
	
	public IPoint convertCanvas(Point panel) {
		return this.convertCanvas(this.convertImage(panel));
	}
	
	public Point convertPanel(Point canvas) {
		IPoint cnvPos = getCanvasLocation();
		return new IPoint(cnvPos.x+canvas.x, cnvPos.y+canvas.y).getLocation();
	}
	
	public Point convertPanel(IPoint image) {
		return convertPanel(convertCanvas(image).getLocation());
	}
	
	/*
	 * helper
	 */

	private IPoint getImageOffset(IDimension canvas, IDimension image, IPoint offset) {
		return new IPoint( 
				(-(image.width-canvas.width)/2.0)+((offset.x+0.5)*zoom),
				(-(image.height-canvas.height)/2.0)+((offset.y+0.5)*zoom));
	}
	
	private IPoint getCanvasOffset(IDimension canvas, IDimension image, IPoint offset) {			
		return new IPoint( 
				-(((-(image.width-canvas.width)/2.0)-(offset.x-0.5)))/zoom,
				-(((-(image.height-canvas.height)/2.0)-(offset.y-0.5)))/zoom);
	}
	
	private IDimension getImageSize(double zoom) {
		return new IDimension(
				this.getImage().getWidth(null)*zoom,
				this.getImage().getHeight(null)*zoom);
	}
	
	private IDimension getCanvasSize(double zoom) {
		IDimension imgSiz = this.getImageSize(zoom);
		return new IDimension(
						Math.min(
								getSize().width,
								imgSiz.getWidth()),
						Math.min(
								getSize().height,
								imgSiz.getHeight()));			
	}
	
	/*
	 * init
	 */
	
	public boolean isInitialized() {
		return isBuildReady() && isResizeReady();
	}

	private boolean isResizeReady() {
		return (canvasSize!=null) && (canvasSize.width>0) && (canvasSize.height>0);
	}
	
	private boolean isBuildReady() {
		return (getSize().width>0) && (getSize().height>0);
	}
	
	/*
	 * scale
	 */
	
	public void setMagnifierMinimum(double value) {
		this.minZoom = value;
		setMagnifier(this.zoom);
	}
	
	public void setMagnifierMaximum(double value) {
		this.maxZoom = value;
		setMagnifier(this.zoom);
	}
	
	public void setMagnifier(double value) {
		double s = value;
		if (s<this.minZoom) s = this.minZoom; else if (s>this.maxZoom) s = maxZoom;
		if (s!=this.zoom) {
			this.zoom = s;
			resize();
		}
	}
	
	public double getMagnifier() {
		return zoom;
	}
	
	public boolean isMagnified() {
		return this.zoom!=1.0;
	}
	
	public double getMagnifierMinimum() {
		return this.minZoom;
	}
	
	public double getMagnifierMaximum() {
		return this.maxZoom;
	}
		
}
