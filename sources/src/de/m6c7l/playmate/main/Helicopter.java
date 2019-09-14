/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Graphics2D;
import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public class Helicopter extends Aircraft {

	public Helicopter(
			World world,
			Object id,
			Position position,
			double maxSpeed,
			double ceiling,
			boolean military,
			double length,
			double width) {
		
		super(	world,
				id,
				position,
				0,
				maxSpeed,
				ceiling,
				military,
				length,
				width);
	}

	protected double getSpeedYaw() {
		long lt = super.getTrack().lastTime();
		double base = this.getSpeedMaximum()/25.0;
		double vperc = (this.getSpeed(lt)/this.getSpeedMaximum())/2.0;
		return Math.abs(base*(1-vperc));
	}
	
	protected double getSpeedThrottle() {
		return this.getSpeedMaximum()/50.0;
	}
	
	protected double getSpeedBrake() {
		return this.getSpeedMaximum()/25.0;
	}
	
	protected double getSpeedPitchPositive() {
		return this.getSpeedMaximum()/5.0;
	}
	
	protected double getSpeedPitchNegative() {
		return this.getSpeedMaximum()/6.0;
	}
	
	/*
	 * drawable
	 */
	
	@Override
	public Point draw(Canvasable canvas) {
		
		if (!isDrawable()) return null;
		
		Point point = super.draw(canvas);
		
		if (canvas.isMagnified()) {
			
			Graphics2D g2d = canvas.getPaintbox();
			initialize(g2d);
			
			Position pos = this.getPosition();
			
			double l = this.getLength();
			double b = this.getWidth()*0.125;
			double r = this.getWidth();
			double brg = this.getHeading();
			
			int a = 45;
			double bc = b/2.0;
			double lbb = Math.sin(a/180.0*Math.PI)*bc;
			double ba = Math.sqrt(lbb*lbb+bc*bc);
			
			double bp = b/2.0;
			double bs = b-bp;
			double ls = l/2.0;
			double rc = l*0.25;
			
			double ws = this.getWidth()*0.4;
			
			Position cr = pos.getPositionRhumbLine(brg,rc);
			Position cs = pos.getPositionRhumbLine((brg+180)%360,ls);
			Position ps = cs.getPositionRhumbLine((brg-90)%360,bp);
			Position ss = cs.getPositionRhumbLine((brg+90)%360,bs);
			
			Position psfwc = pos.getPositionRhumbLine((brg-90)%360,bc);
			psfwc = psfwc.getPositionRhumbLine((brg+180)%360,ws/4.0);
			Position ssfwc = pos.getPositionRhumbLine((brg+90)%360,bc);
			ssfwc = ssfwc.getPositionRhumbLine((brg+180)%360,ws/4.0);
			
			Position pb = ps.getPositionRhumbLine(brg,l-lbb);
			Position sb = ss.getPositionRhumbLine(brg,l-lbb);
			Position cb = pb.getPositionRhumbLine(brg+(90-a),ba);

			Point _pb = canvas.getPoint(pb);
			Point _sb = canvas.getPoint(sb);
			Point _cb = canvas.getPoint(cb);
			Point _cs = canvas.getPoint(cs);
			
			g2d.drawLine(_cs.x,_cs.y,_pb.x,_pb.y);
			g2d.drawLine(_cs.x,_cs.y,_sb.x,_sb.y);
			g2d.drawLine(_pb.x,_pb.y,_cb.x,_cb.y);
			g2d.drawLine(_sb.x,_sb.y,_cb.x,_cb.y);
			
			Position ulo = cr.getPositionRhumbLine(315,r/2.0*1.41);
			Position lro = cr.getPositionRhumbLine(135,r/2.0*1.41);
			
			Point _ulo = canvas.getPoint(ulo);
			Point _lro = canvas.getPoint(lro);
			
			g2d.drawOval(_ulo.x, _ulo.y, _lro.x-_ulo.x, _lro.y-_ulo.y);
						
		}
				
		return point;
		
	}
	
}
