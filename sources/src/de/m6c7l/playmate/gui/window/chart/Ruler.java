/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import de.m6c7l.lib.gui.TOOLBOX;
import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.main.Canvasable;
import de.m6c7l.playmate.main.Drawable;
import de.m6c7l.playmate.main.Locateable;
import de.m6c7l.playmate.main.VALUE;

public class Ruler implements Drawable, Locateable {

	private Position pos1 = null;
	private Position pos2 = null;
	
	public Ruler(Position position) {
		this.pos1 = position;
		this.pos2 = position;
	}
	
	public void free() {
		this.pos1 = null;
		this.pos2 = null;
	}
	
	@Override
	public Point getPoint(Canvasable canvas) {
		return canvas.getPoint(pos1);
	}

	@Override
	public Point draw(Canvasable canvas) {

		Graphics2D g = canvas.getPaintbox();

		Point from = canvas.getPoint(pos1);
		Point to = canvas.getPoint(pos2);

		g.setColor(Color.BLACK);

		if (from!=null && to!=null) {
			
			TOOLBOX.drawDashedLine(g,from.x,from.y,to.x,to.y,1*2,1*6);			
			Position a = pos2;
			Position b = pos1;
			
			if ((a!=null) && (b!=null)) {

				Position.RhumbLine ba = b.getRhumbLine(a);
				String[] dst = VALUE.RANGE(ba.getDistance());
				String[] brg = VALUE.TRUEBEARING(ba.getBearing());
				int x = from.x+(to.x-from.x)/2+10;
				int y = from.y+(to.y-from.y)/2-5;
				
        		Rectangle rec = new Rectangle(x-5,y-15,84,42);
        		g.setPaint(new Color(255,255,255,64));
        		g.fill(rec);
        		
        		g.setColor(Color.BLACK);
        		
				g.drawString(dst[0] + " " + dst[1], x, y);
				g.drawString(brg[0], x, y+20);
			
			}	
		}
		
		g.dispose();
		
		return to;

	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public int getDrawSize() {
		return 1;
	}

	@Override
	public Position getPosition() {
		return pos2;
	}
	
	@Override
	public boolean setPosition(Position position) {
		pos2 = position;
		return true;
	}
	
	public void setStart(Position position) {
		pos1 = position;
	}
	
}