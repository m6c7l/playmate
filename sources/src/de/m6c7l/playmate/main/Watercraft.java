/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;

import de.m6c7l.lib.util.MATH;
import de.m6c7l.lib.util.NAUTIC;
import de.m6c7l.lib.util.geo.Position;

public abstract class Watercraft extends Asset {

	public Watercraft(
			World world,
			Object id,
			Position position,
			double maxSpeed,
			double maxDepth,
			double draft,
			boolean military,
			boolean sonar,
			double length,
			double beam) {
		
		super(	world,
				id,
				position,
				0,
				maxSpeed,
				-Math.abs(maxDepth),
				-Math.abs(draft),
				military,
				sonar,
				length,
				beam);
	}
	
	public abstract double getHeight();

	public Position getOutlook(long time) {
		Position p = getPosition(time);
		return new Position(p.getLatitude(),p.getLongitude(),p.getAltitude()+getHeight()*0.85);
	}
	
	public Position getOutlook() {
		return getOutlook(getTime());
	}
	
	public double getDraft() {
		return Math.abs(super.getAltitudeMaximum());
	}
	
	public double getDepthMaximum() {
		return Math.abs(super.getAltitudeMinimum());
	}
	
	protected double getSpeedYaw() {
		long lt = this.getTrack().lastTime();
		double r1 = this.getRateOfTurn(lt)/60.0;
		double r = Math.abs(r1);
		double a1 = NAUTIC.getAngleDifference(
				this.getHeading(lt),
				this.getHeadingFinal(),
				this.getRotation());
		double a = Math.abs(a1);
		double vl = this.getSpeed(lt)/this.getLength()*10;		
		double vt = this.getSpeed(lt)/this.getDraft();
		double base = Math.abs(Math.sqrt(vt+vl)*2.0);
		boolean chg = (Math.abs((r1/r)*2+(a1/a)*3)==1);
		if (!chg) {
			if (a>25) {
				return (base+r*19)/20.0;
			} else if (MATH.compare(a,0,3)!=0) {
				return base*(Math.sin((a+1)/180.0*Math.PI)*2.0);
			} else {
				return base;
			}
		} else {
			return 0;
		}
	}
	
	protected double getSpeedThrottle() {
		return Math.abs((this.getSpeedMaximum()/this.getLength())*2.0);
	}

	protected double getSpeedBrake() {
		return this.getSpeedThrottle()*(10.0/Math.sqrt(this.getLength()));
	}

	/*
	 * go deep circle
	 */
	
	public int getGoDeepCircle() {
		double gdc = ((getSpeedMaximum()*1.943844492)+7)*(100.0/3.0);
		int x = (((int)gdc)/100)*100;
		double y = Math.round(((gdc-x)/100.0));
		return (int)(x+(y*100));
	}
	
	/*
	 * drawable
	 */
	
	@Override
	public Point draw(Canvasable canvas) {
		
		Point point = super.draw(canvas);
		
		Graphics2D g2d = canvas.getPaintbox();
		
		initialize(g2d);
		
		if ((point!=null) && (!canvas.isMagnified())) {
			
			int size = getDrawSize();

			if (!isDived()) {

				if (this.isMilitary()) {
					g2d.drawLine(point.x - size, point.y, point.x, point.y + size);
					g2d.drawLine(point.x - size, point.y, point.x, point.y - size);
					g2d.drawLine(point.x, point.y - size, point.x + size, point.y);
					g2d.drawLine(point.x + size, point.y, point.x, point.y + size);
				} else {
					size = (int)((size/1.41)+0.5);
					g2d.drawLine(point.x - size, point.y - size, point.x - size, point.y + size); // left
					g2d.drawLine(point.x - size, point.y - size, point.x + size, point.y - size); // top
					g2d.drawLine(point.x + size, point.y - size, point.x + size, point.y + size); // right
					g2d.drawLine(point.x + size, point.y + size, point.x - size, point.y + size); // bottom
				}			
				
			} else {

				if (this.isMilitary()) {
					g2d.drawLine(point.x - size, point.y, point.x, point.y + size);
					g2d.drawLine(point.x + size, point.y, point.x, point.y + size);
				} else {
					size = (int)((size/1.41)+0.5);
					g2d.drawLine(point.x - size, point.y, point.x - size, point.y + size); // left
					g2d.drawLine(point.x + size, point.y, point.x + size, point.y + size); // right
					g2d.drawLine(point.x + size, point.y + size, point.x - size, point.y + size); // bottom
				}
				
			}
			
		}
		
		if (canvas.isMagnified()) {
			
			Position pos = this.getPosition();
			
			double l = this.getLength();
			double b = this.getWidth();
			
			double brg = this.getHeading();
			
			int a = 45;
			double bc = b/2.0;
			double lbb = Math.sin(a/180.0*Math.PI)*bc;
			double ba = Math.sqrt(lbb*lbb+bc*bc);
			
			double bp = b/2.0;
			double bs = b-bp;
			double ls = l/2.0;
			
			Position cs = pos.getPositionRhumbLine((brg+180)%360,ls);
			Position ps = cs.getPositionRhumbLine((brg-90)%360,bp);
			Position ss = cs.getPositionRhumbLine((brg+90)%360,bs);
			Position pb = ps.getPositionRhumbLine(brg,l-lbb);
			Position sb = ss.getPositionRhumbLine(brg,l-lbb);
			Position cb = pb.getPositionRhumbLine(brg+(90-a),ba);
			
			Point _ps = canvas.getPoint(ps);
			Point _ss = canvas.getPoint(ss);
			Point _pb = canvas.getPoint(pb);
			Point _sb = canvas.getPoint(sb);
			Point _cb = canvas.getPoint(cb);
			
			g2d.drawLine(_ps.x,_ps.y,_ss.x,_ss.y);
			g2d.drawLine(_ps.x,_ps.y,_pb.x,_pb.y);
			g2d.drawLine(_ss.x,_ss.y,_sb.x,_sb.y);
			g2d.drawLine(_pb.x,_pb.y,_cb.x,_cb.y);
			g2d.drawLine(_sb.x,_sb.y,_cb.x,_cb.y);
			
		} else {
			
			g2d.setStroke(new BasicStroke(2.0f));
			
			if (this.isSelected()) {

				long t = (this.getTime()/60)*60;
				while ((t>=this.getTrack().firstTime()) && (this.getTime()<t+(60*6))) {
					Point p = canvas.getPoint(this.getPosition(t));
					g2d.drawLine(p.x, p.y, p.x, p.y);
					t = t - 60;		
				}
								
			}
			
		}
				
		return point;
		
	}
	
}