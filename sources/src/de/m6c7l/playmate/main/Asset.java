/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.util.MATH;
import de.m6c7l.lib.util.NAUTIC;
import de.m6c7l.lib.util.geo.Position;

public abstract class Asset extends Entity implements Drawable, Hookable {
	
	public static final BasicStroke STROKE 				= new BasicStroke(1.5f);
	public static final BasicStroke STROKE_MARK			= new BasicStroke(1.5f);
	
	public static final Color 		COLOR_MARK 			= new Color(200, 50, 50);
	public static final Color 		COLOR_OWN 			= new Color( 10, 10,250).darker().darker(); 
	public static final Color 		COLOR_ENABLED 		= new Color( 10, 10,250).darker().darker();
	public static final Color 		COLOR_DISABLED 		= new Color( 90, 90, 90).darker();
		
	private World world = null;
	private Route route = null;

	private boolean sonar = false;
	private boolean military = false;
	private double length = 0;
	private double width = 0;

	private AlteringTask at = null;
	private LoopTask st = null;
	
	private ArrayList<Long> pulse = null;
	private int prt = 20;
	
	public Asset(
			World world,
			Object id,
			Position position,
			double minSpeed,
			double maxSpeed,
			double minAltitude,
			double maxAltitude,
			boolean military,
			boolean sonar,
			double length,
			double width) {
		
		super(	world,
				id,
				position,
				minSpeed,
				maxSpeed,
				minAltitude,
				maxAltitude);
		
		this.world = world;
		
		this.pulse = new ArrayList<Long>();
		
		this.military = military;
		this.sonar = sonar;
		
		this.length = length;
		this.width = width;

		this.route = new Route(this);
		
		this.at = new AlteringTask(this);
		this.world.addChangeListener(this.at);
		
		this.st = new LoopTask(this);
		this.world.addChangeListener(this.st);
		
	}

	public void free() {
		
		this.world.removeChangeListener(this.at);
		this.world.removeChangeListener(this.st);
		
		this.getSurfaceTask().free();
		this.getAlteringTask().free();
		this.getRoute().free();
		
		this.st = null;
		this.at = null;
		this.route = null;
		this.world = null;

		super.free();

	}
	
	public Route getRoute() {
		return this.route;
	}
	
	private AlteringTask getAlteringTask() {
		return this.at;
	}
	
	private LoopTask getSurfaceTask() {
		return this.st;
	}
	
	protected abstract Position getOutlook(long time);
	protected abstract Position getOutlook();
	
	protected abstract double getSpeedThrottle();
	protected abstract double getSpeedBrake();
	protected abstract double getSpeedPitchPositive();
	protected abstract double getSpeedPitchNegative();
	protected abstract double getSpeedYaw();
	
	public boolean isVisible(long time, Asset asset) {
		if (asset==null) return false;
		if (this.getOutlook(time).isVisible(asset.getOutlook(time))) {
			if (asset.isAircraft()) {
				double visus = ((asset.getLength()*0.25)/0.03*100);
				return this.getOutlook(time).getDistanceGreatCircle(asset.getPosition(time))<visus;
  			}
			return true;
		}
		return false;
	}
	
	public boolean isVisible(Asset asset) {
		return isVisible(getTime(),asset);
	}

	public boolean isAudible(long time, Asset asset) {
		if (asset==null) return false;
		if (asset instanceof Watercraft) {
			double vr = (asset.getSpeed(time)/asset.getSpeedMaximum())*0.9+0.1;
			double d = vr*world.getAcousticRange();
			if (asset.isMilitary()) {
				d = d * 0.75;
			}
			if (asset.isSubmarine()) {
				d = d * 0.5;
				if (asset.isDived(time)) {
					d = d * 0.5;				
				}
			} else if (asset.hasSonar()) {
				d = d * 0.5;					
			}
			return this.getPosition(time).getDistanceGreatCircle(asset.getPosition(time))<d;
		}		
		return false;
	}
	
	public boolean isAudible(Asset asset) {
		return isAudible(getTime(),asset);
	}
	
	public boolean isCovert(long time, Asset asset) {
		if (asset==null) return false;
		if (asset instanceof Watercraft) {
			return this.world.isCovert(this.getOutlook(time),asset.getPosition(time));
		}			
		return false;
	}
	
	public boolean isCovert(Asset asset) {
		return isCovert(getTime(),asset);
	}
	
	public boolean isObserver() {
		return world.getObserver()==this;
	}
	
	public boolean isMilitary() {
		return this.military;
	}
	
	public double getLength() {
		return length;
	}

	public double getWidth() {
		return width;
	}
	
	public boolean setPosition(Position position) {
		if (!isNew()) return false;
		getRoute().setOrigin(position);
		return getTrack().setPosition(this.getTimeElapsed(),position);
	}
	
	public int getRotation() {
		return this.at.getRotation();
	}
	
	public boolean hasSonar() {
		return this.sonar;
	}
	
	public void setPulseRepetitionTime(int value) {
		if (this.sonar) {
			this.prt = value;
		}
	}
	
	public boolean hasPulse(long time, Asset asset) {
		if (pulse.contains(time)) {
			double d = world.getAcousticRange();
			return (this.getPosition(time).getDistanceGreatCircle(asset.getPosition(time))<d*2);
		}
		return false;
	}
	
	/*
	 * setter
	 */
	
	public void setRotation(int rotation) {
		this.at.setRotation(rotation);
	}
	
	public boolean setHeading(double heading) {
		return this.getAlteringTask().setHeading(heading);
	}

	public boolean setSpeed(double speed) {
		if (this.st==null) this.st = new LoopTask(this);
		return this.getAlteringTask().setSpeed(speed);
	}

	public boolean setAltitude(double altitude) {
		return this.getAlteringTask().setAltitude(altitude);
	}

	/*
	 * getter
	 */
	
	public double getHeadingFinal() {
		return this.getAlteringTask().hdg;
	}
	
	public double getSpeedFinal() {
		return this.getAlteringTask().spd;
	}
	
	public double getAltitudeFinal() {
		return this.getAlteringTask().alt;
	}
	
	public double getHeadingStart() {
		return this.getAlteringTask()._hdg;
	}
	
	public double getSpeedStart() {
		return this.getAlteringTask()._spd;
	}
	
	public double getAltitudeStart() {
		return this.getAlteringTask()._alt;
	}
	
	/*
	 * is 
	 */
	
	public boolean isHeadingAltering() {
		return this.getHeading(this.getTime()-1)!=this.getHeading(this.getTime());
	}

	public boolean isSpeedAltering() {
		return this.getSpeed(this.getTime()-1)!=this.getSpeed(this.getTime());
	}

	public boolean isAltitudeAltering() {
		return this.getAltitude(this.getTime()-1)!=this.getAltitude(this.getTime());
	}
	
	public boolean isManeuvering() {
		return isHeadingAltering() || this.isSpeedAltering();
	}
	
	public boolean isStuck(long time) {
		Position pos = this.getPosition(time);
		if (this.isWatercraft()) {
			return (this.world.isCoast(pos) || (this.world.isLand(pos)));		
		}
		return false;
	}
	
	public boolean isStuck() {
		return this.isStuck(this.getTime());
	}
	
	/*
	 * more
	 */
	
	@Override
	public Point getPoint(Canvasable canvas) {
		return canvas.getPoint(this.getPosition());
	}
	
	@Override
	public boolean isDrawable() {
		return (!world.isExercising()) || (isObserver());
	}
	
	@Override
	public int getDrawSize() {
		return 12;
	}
	
	protected void initialize(Graphics2D g2d) {
		if (!this.isObserver()) {
			g2d.setColor(COLOR_ENABLED);										
		} else {
			g2d.setColor(COLOR_OWN);
		}
		if (this.isExisting()) {
			if (!this.isSelected()) {
				g2d.setStroke(STROKE);	
			} else {
				g2d.setStroke(STROKE_MARK);
				g2d.setColor(COLOR_MARK);
			}			
		} else {
			g2d.setStroke(STROKE);
			g2d.setColor(COLOR_DISABLED);		
		}
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     
	}
	
	@Override
	public Point draw(Canvasable canvas) {
		
		Point point = canvas.getPoint(this.getPosition());
		
		Graphics2D g2d = canvas.getPaintbox();
		int size = getDrawSize();

		initialize(g2d);
		
		g2d.drawLine(point.x-1, point.y-1, point.x+1, point.y+1);
		g2d.drawLine(point.x-1, point.y+1, point.x+1, point.y-1);
		
		if (!canvas.isMagnified()) {

			double mins = this.isAircraft() ? 0.25 : 6;
			
			// hdg / maxspd
			if (!isStuck()) {
				Position hdg6mins = this.getPosition().getPositionRhumbLine(
						this.getHeading(),
						this.getSpeedMaximum() * 60 * mins);
				BasicStroke temp = (BasicStroke)g2d.getStroke();			
				g2d.setStroke(new BasicStroke(temp.getLineWidth()/2.0f));
				Point phdg = canvas.getPoint(hdg6mins);
				g2d.drawLine(point.x,point.y,phdg.x,phdg.y);
				g2d.setStroke(temp);
			}
			
			// cog / sog
			Double cog = this.getCourseOverGround();
			if (cog!=null) {
				Position cog6mins = this.getPosition().getPositionRhumbLine(
						this.getCourseOverGround(),
						this.getSpeedOverGround() * 60 * mins);
				Point pcog = canvas.getPoint(cog6mins);
				g2d.drawLine(point.x,point.y,pcog.x,pcog.y);
			}	

			// not observer
			if (!this.isObserver()) {
				g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));				
				if (!this.isHooked()) {
					if (this.isExisting()) {
						g2d.setColor(COLOR_ENABLED);											
					} else {
						g2d.setColor(COLOR_DISABLED);					

					}
				} else {
					g2d.setColor(COLOR_MARK);		
				}

				// id
				g2d.drawString(
						(this.getID()!=null) ? this.getID() + "" : "",
						point.x + size + size / 4,
						point.y + size + size / 2);
				
			} else {

				int radius = (int)(size*(1/1.41));
				g2d.drawLine(point.x - radius, point.y - radius, point.x + radius, point.y + radius);
				g2d.drawLine(point.x - radius, point.y + radius, point.x + radius, point.y - radius);
				g2d.drawOval(point.x - size, point.y - size, (size * 2), (size * 2));
				
			}

			if (this.isObserver()) {
				point = null;
				
			} else {
				
			}
			
		}
		
		if (this.route!=null) this.route.draw(canvas);
		
		return point;
		
	}
	
	/*
	 * hookable
	 */
	
	@Override
	public boolean isHooked() {
		return (this==this.world.getHooked()) && (!isObserver());
	}
	
	@Override
	public boolean isHookable() {
	    return this.isExisting() && (!isObserver());
	}

	@Override
	public void setHooked(boolean hooked) {
		if ((hooked) && (this.isHookable())) {			
			this.world.setHooked(this);
		} else {
			this.world.setHooked(null);
		}
	}
	
	/*
	 * selectable
	 */
	
	@Override
	public boolean isSelected() {
		return this==this.world.getSelected();
	}
	
	@Override
	public boolean isSelectable() {
		return this.isExisting() && ((!world.isExercising()) || isObserver());
	}
	
	@Override
	public void setSelected(boolean selected) {
		route.setSelected(null);
		if ((selected) && (this.isSelectable())) {			
			this.world.setSelected(this);
		} else {
			this.world.setSelected(null);
		}
	}

	/*
	 * schedule
	 */
	
	private static class LoopTask implements ChangeListener {

		private Asset self = null;
		private long last = 0;
		
		public LoopTask(Asset asset) {
			this.self = asset;
		}
		
		public void free() {
			this.self = null;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			
			if ((self==null) || self.world.isStopped() || (self.getSpeed()==0)) return;
			
			long te = self.getTimeElapsed();
			
			if (te!=this.last) {
				
				long t = this.last;
				
				while (t<=te) {
					
					if (self.isStuck(t)) { 		// coast stuck?
						self.setSpeed(t,0);
						break;
					}
					
					if (self.prt!=0) { 			// sonar active?
						if (t%self.prt==0) {
							self.pulse.add(t);						
						}
					}
					
					t++;
					
				}
				
				this.last = te;
			}
			
		}
		
	}
	
	private static class AlteringTask implements ChangeListener {
		
		private long last = 0;
		
		private double _hdg = 0;
		private double hdg = 0;
		private long thdg = 0;                         
		private int td = 0;
		
		private double _spd = 0;
		private double spd = 0;
		private long tspd = 0;  
		
		private double _alt = 0;
		private double alt = 0;
		private long talt = 0;  
		
		private Asset self = null;
		
		public AlteringTask(Asset asset) {
			
			this.self = asset;
			this.last = self.getTimeElapsed();
			
			this.thdg = self.getTimeElapsed();
			this.hdg = self.getHeading(this.thdg);
			this._hdg = this.hdg;
			
			this.tspd = self.getTimeElapsed();
			this.spd = self.getSpeed(this.tspd);
			this._spd = this.spd;
			
			this.talt = self.getTimeElapsed();
			this.alt = self.getAltitude(this.talt);
			this._alt = this.alt;
			
		}
		
		public void free() {
			this.self = null;
		}
		
		public void setRotation(int rotation) {
			this.td = rotation;
		}
		
		public int getRotation() {
			return this.td;
		}
		
		private boolean setHeading(long time, double hdg) {
			this.thdg = time;
			this.hdg = (hdg+360)%360;
			return isHeadingAltering(time);
		}
		
		public boolean setHeading(double hdg) {
			boolean b = this.setHeading(self.getTimeElapsed(),hdg);
			if (self.isNew())
				return self.setHeading(this.thdg,this.hdg);			
			return b;
		}
		
		private boolean setSpeed(long time, double spd) {
			this.tspd = time;
			this.spd = spd;			
			return isSpeedAltering(time);
		}
		
		public boolean setSpeed(double spd) {
			boolean b = this.setSpeed(self.getTimeElapsed(),spd);
			if (self.isNew())
				return self.setSpeed(this.tspd,this.spd);			
			return b;
		}
		
		private boolean setAltitude(long time, double alt) {
			this.talt = time;
			this.alt = alt;			
			return isAltitudeAltering(time);
		}
		
		public boolean setAltitude(double alt) {
			boolean b = this.setAltitude(self.getTimeElapsed(),alt);
			if (self.isNew())
				return self.setAltitude(this.talt,this.alt);				
			return b;
		}
		
		private boolean isHeadingAltering(long t) {
			return (this.thdg<t) && (MATH.compare(self.getHeading(this.thdg),this.hdg,3)!=0);
		}
		
		private boolean isSpeedAltering(long t) {
			return (this.tspd<t) && (MATH.compare(self.getSpeed(this.tspd),this.spd,3)!=0);
		}
		
		private boolean isAltitudeAltering(long t) {
			return (this.talt<t) && (MATH.compare(self.getAltitude(this.talt),this.alt,3)!=0);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			
			if ((self==null) || self.world.isStopped() || (self.getTimeElapsed()==this.last)) return;
			
			last = self.getTimeElapsed();

			long t = Math.min(Math.min(this.thdg,this.tspd),this.talt);	
					
			double nhdg = this.hdg;
			double ohdg = self.getHeading(t);

			double nspd = this.spd;
			double ospd = self.getSpeed(t);
					
			double nalt = this.alt;
			double oalt = self.getAltitude(t);
			
			boolean update = false;
			
			while (t<=last) {
				
				if (self.isStuck(t)) break;
					
				update = updateAutopilot(t);
				if (update) nhdg = this.hdg;
						
				if (isHeadingAltering(t) || update) {
					double rot = self.getSpeedYaw();
					double d = NAUTIC.getAngleDifference(ohdg,nhdg,td);
					if (d<0) rot = rot*(-1);
					if (Math.abs(d)>Math.abs(rot)) {
						ohdg = ohdg + rot;
						self.setHeading(t,ohdg);
					} else {
						self.setRotation(0);
						self.setHeading(t,nhdg);
						_hdg = nhdg;
					}
				}

				update = updateSpeed(t);
				if (update) nspd = this.spd;
						
				if (isSpeedAltering(t) || update) {
					double acc = 0;
					double d = nspd-ospd;
					if (d>0) acc = self.getSpeedThrottle();
					else acc = -self.getSpeedBrake();
					if (Math.abs(d)>Math.abs(acc)) {
						ospd = ospd + acc;
						self.setSpeed(t,ospd);
					} else {
						self.setSpeed(t,nspd);
						_spd = nspd;
					}	
				}
						
				if (isAltitudeAltering(t)) {
					double roc = 0;
					double d = nalt-oalt;
					if (d>0) roc = self.getSpeedPitchPositive();
					else roc = -self.getSpeedPitchNegative();
					if (Math.abs(d)>Math.abs(roc)) {
						oalt = oalt + roc;
						self.setAltitude(t,oalt);
					} else {
						self.setAltitude(t,nalt);
						_alt = nalt;
					}
				}
						
				t++; 
						
			}
					
			this.thdg = last;
			this.tspd = last;
			this.talt = last;
				
		}
		
		private boolean updateAutopilot(long time) {
			
			if (self.getRoute().isFinished() || self.getEnvironment().isStopped()) return false;
			
			Waypoint wp = self.getRoute().next();		
			
			if (wp!=null) {
		
				Position own = self.getPosition(time);
				double ownSpeed = self.getSpeed(time);

				Position.RhumbLine bdwp = own.getRhumbLine(wp.getPosition());

				Double brgWP = bdwp.getBearing();
				
				if (brgWP!=null) {
					
					double cset = self.isWatercraft() ? self.getEnvironment().getCurrentSet() : 0;
					double cdrift = self.isWatercraft() ? self.getEnvironment().getCurrentDrift() : 0;

					double hdg = NAUTIC.getHeading(brgWP, ownSpeed, cset, cdrift);
					
					if (MATH.compare(self.getHeadingFinal(),hdg,2)!=0) {
						this.thdg = time;
						this.hdg = (hdg+360)%360;
						return true;
					}
					
					double d2wp = bdwp.getDistance();
					double x = 0;
					double d = 10;
						
					Waypoint afterWP = wp.next();
						
					if (!self.getRoute().isAcross()) {
						d = Math.max(d,self.getLength());
						if (afterWP!=null) {
							Double brgAfterNext = wp.getPosition().getRhumbLine(afterWP.getPosition()).getBearing();
							if (brgAfterNext!=null) {
								x = Math.abs(NAUTIC.getAngleDifference(self.getHeading(time),brgAfterNext,0));
								d = Math.sqrt(ownSpeed*x)*10;
							}					
						}
					}

					if (d2wp<d) {
						wp.setAchieved(true);
					}
					
				}
				
			}

			return false;
		}
		
		private boolean updateSpeed(long time) {
			
			if ((self==null) || (self.getEnvironment().isStopped())) return false;

			double spd = self.getSpeed(time);
			
			double max = self.getSpeedMaximum();
			double min = self.getSpeedMinimum();

			if (spd>max) {
				this.tspd = time;
				this.spd = max;	
				return true;
			} else if (spd<min) {
				this.tspd = time;
				this.spd = min;	
				return true;
			}			

			return false;
		
		}
		
	}
	
	/*
	 * info
	 */
	
	public String toString() {
		return (this.getID()!=null ? this.getID().toString() : "");
	}

}