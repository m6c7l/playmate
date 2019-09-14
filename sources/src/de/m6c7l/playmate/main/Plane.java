/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.Graphics2D;
import java.awt.Point;

import de.m6c7l.lib.util.geo.Position;

public class Plane extends Aircraft {

	public Plane(
			World world,
			Object id,
			Position position,
			double minSpeed,
			double maxSpeed,
			double ceiling,
			boolean military,
			double length,
			double width) {
		
		super(	world,
				id,
				position,
				minSpeed,
				maxSpeed,
				ceiling,
				military,
				length,
				width);
	}

	protected double getSpeedYaw() {
		long lt = super.getTrack().lastTime();
		double base = (1091*Math.tan(30.0/180.0*Math.PI)*0.514);
		double v = this.getSpeed(lt)/2.0;
		if (v==0) return 0.0;
		return Math.abs(base/v);
	}
	
	protected double getSpeedThrottle() {
		return this.getSpeedMaximum()/100.0;
	}
	
	protected double getSpeedBrake() {
		return this.getSpeedMaximum()/75.0;
	}
	
	protected double getSpeedPitchPositive() {
		return this.getSpeedMaximum()/10.0;
	}

	protected double getSpeedPitchNegative() {
		return this.getSpeedMaximum()/7.0;
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
				
			double brg = this.getHeading();
			
			int a = 45;
			double bc = b/2.0;
			double lbb = Math.sin(a/180.0*Math.PI)*bc;
			double ba = Math.sqrt(lbb*lbb+bc*bc);
			
			double bp = b/2.0;
			double bs = b-bp;
			double ls = l/2.0;
			double rc = l*0.1;
			
			double x = 25;
			
			double ws = this.getWidth()*0.4;
			double lfw = Math.sin(x/180.0*Math.PI)*ws;
			double lfwx = Math.sqrt(lfw*lfw+ws*ws);
			
			Position cr = pos.getPositionRhumbLine(brg,rc);	
			Position cs = pos.getPositionRhumbLine((brg+180)%360,ls);
			Position ps = cs.getPositionRhumbLine((brg-90)%360,bp);
			Position ss = cs.getPositionRhumbLine((brg+90)%360,bs);
			
			Position psfwc = cr.getPositionRhumbLine((brg-90)%360,bc);
			psfwc = psfwc.getPositionRhumbLine((brg+180)%360,ws/4.0);
			Position ssfwc = cr.getPositionRhumbLine((brg+90)%360,bc);
			ssfwc = ssfwc.getPositionRhumbLine((brg+180)%360,ws/4.0);
			Position psfw = psfwc.getPositionRhumbLine((brg-90)%360,ws);
			Position ssfw = ssfwc.getPositionRhumbLine((brg+90)%360,ws);
			
			Position psfwx = psfw.getPositionRhumbLine(brg+(90-x),lfwx);
			Position ssfwx = ssfw.getPositionRhumbLine(brg-(90-x),lfwx);
			
			Position pb = ps.getPositionRhumbLine(brg,l-lbb);
			Position sb = ss.getPositionRhumbLine(brg,l-lbb);
			Position cb = pb.getPositionRhumbLine(brg+(90-a),ba);
			
			Point _psfwc = canvas.getPoint(psfwc);
			Point _ssfwc = canvas.getPoint(ssfwc);
			Point _psfw = canvas.getPoint(psfw);
			Point _ssfw = canvas.getPoint(ssfw);
			Point _psfwx = canvas.getPoint(psfwx);
			Point _ssfwx = canvas.getPoint(ssfwx);
			
			Point _pb = canvas.getPoint(pb);
			Point _sb = canvas.getPoint(sb);
			Point _cb = canvas.getPoint(cb);
			Point _cs = canvas.getPoint(cs);
			
			g2d.drawLine(_cs.x,_cs.y,_pb.x,_pb.y);
			g2d.drawLine(_cs.x,_cs.y,_sb.x,_sb.y);
			g2d.drawLine(_pb.x,_pb.y,_cb.x,_cb.y);
			g2d.drawLine(_sb.x,_sb.y,_cb.x,_cb.y);
			
			g2d.drawLine(_psfwc.x,_psfwc.y,_psfw.x,_psfw.y);
			g2d.drawLine(_ssfwc.x,_ssfwc.y,_ssfw.x,_ssfw.y);
			g2d.drawLine(_psfw.x,_psfw.y,_psfwx.x,_psfwx.y);
			g2d.drawLine(_ssfw.x,_ssfw.y,_ssfwx.x,_ssfwx.y);
			
		}
				
		return point;
		
	}
	
}
