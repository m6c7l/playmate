/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import de.m6c7l.lib.util.NAUTIC;
import de.m6c7l.lib.util.geo.Position;

public class Entity {
	
	private static int nextSerial = 0;
	private int serial = 0;
	
	private Environment environment = null;
	private Track track = null;
	private Movement orientation = null;
	
	private Object id = null;

	protected double minAltitude = 0;
	protected double maxAltitude = 0;
	
	protected double minSpeed = 0;
	protected double maxSpeed = 0;

	public Entity(
			Environment environment,
			Object id,
			Position position,
			double minSpeed,
			double maxSpeed,
			double minAltitude,
			double maxAltitude) {
		
		super();
		
		this.serial = nextSerial++;
		
		this.environment = environment;
		this.id = id;
		
		if (position.getAltitude()>maxAltitude) position.setAltitude(maxAltitude);
		if (position.getAltitude()<minAltitude) position.setAltitude(minAltitude);
		
		this.minAltitude = minAltitude;
		this.maxAltitude = maxAltitude;
		
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		
		double heading = 0;
		double speed = getSpeedMinimum();
		
		this.orientation = new Movement(
				environment.getTimeLast(),
				heading,
				speed);

		double cset = isWatercraft() ? environment.getCurrentSet() : 0;
		double cdrift = isWatercraft() ? environment.getCurrentDrift() : 0;
		
		this.track = new Track(
				environment.getTimeLast(),
				position,
				NAUTIC.getCourseOverGround(heading,speed,cset,cdrift),
				NAUTIC.getSpeedOverGround(heading,speed,cset,cdrift));

	}
	
	public void free() {
		environment = null;
		track = null;
		orientation = null;
	}
	public Environment getEnvironment() {
		return this.environment;
	}
		
	protected boolean setHeading(long time, double heading) {
		double cset = isWatercraft() ? this.environment.getCurrentSet() : 0;
		double cdrift = isWatercraft() ? this.environment.getCurrentDrift() : 0;
		return 
			this.orientation.setHeading(time,heading) &&
			this.track.setPosition(
				time,
				NAUTIC.getCourseOverGround(
						(heading+360)%360,
						this.getSpeed(time),
						cset,
						cdrift),
				NAUTIC.getSpeedOverGround(
						this.getHeading(time),
						this.getSpeed(time),
						cset,
						cdrift),
				getAltitude(time));	
	}
	
	protected boolean setSpeed(long time, double speed) {
		double cset = isWatercraft() ? this.environment.getCurrentSet() : 0;
		double cdrift = isWatercraft() ? this.environment.getCurrentDrift() : 0;
		//double spd = Math.min(Math.max(speed,this.getSpeedMinimum()),this.getSpeedMaximum());
		return
			this.orientation.setSpeed(time,speed) &&
			this.track.setPosition(
				time,
				NAUTIC.getCourseOverGround(
						this.getHeading(time),
						this.getSpeed(time),
						cset,
						cdrift),
				NAUTIC.getSpeedOverGround(
						this.getHeading(time),
						speed,
						cset,
						cdrift),
				this.getAltitude(time));
	}

	protected boolean setAltitude(long time, double altitude) {
		//double alt = Math.min(Math.max(altitude,this.getAltitudeMinimum()),this.getAltitudeMaximum());
		return this.track.setPosition(
				time,
				this.getCourseOverGround(time),
				this.getSpeedOverGround(time),
				altitude);
	}
	
	public Track getTrack() {
		return this.track;
	}
	
	public Movement getOrientation() {
		return this.orientation;
	}
	
	/*
	 * get/is
	 */
	
	public boolean isExisting(long time) {
		return (this.track.firstTime()<=time) && (time<=this.environment.getTimeLast());
	}
	
	public boolean isExisting() {
		return (this.track.firstTime()<=this.environment.getTime());
	}
	
	public boolean isNew() {
		return this.track.firstTime()==this.environment.getTimeLast();
	}
	
	public Object getID() {
		return this.id;
	}
	
	public double getSpeedMinimum() {
		return this.minSpeed; 
	}
	
	public double getSpeedMaximum() {
		return this.maxSpeed; 
	}
	
	public double getAltitudeMinimum() {
		return this.minAltitude; 
	}
	
	public double getAltitudeMaximum() {
		return this.maxAltitude; 
	}
	
	public boolean isVessel() {
		return this.getAltitudeMaximum()==this.getAltitudeMinimum();
	}
	
	public boolean isSubmarine() {
		return Math.abs(this.getAltitudeMinimum())>Math.abs(this.getAltitudeMaximum());
	}
	
	public boolean isPlane() {
		return Math.abs(this.getAltitudeMaximum())>Math.abs(this.getAltitudeMinimum()) &&
				(this.getSpeedMinimum()>0);
	}

	public boolean isHelicopter() {
		return Math.abs(this.getAltitudeMaximum())>Math.abs(this.getAltitudeMinimum()) &&
				(this.getSpeedMinimum()==0);
	}

	public boolean isWatercraft() {
		return isSubmarine() || isVessel();
	}
	
	public boolean isAircraft() {
		return isPlane() || isHelicopter();
	}
	
	/*
	 * get/is by time
	 */
	
	public Position getPosition() {
		return this.getPosition(this.getTime());
	}
	
	public Double getCourseOverGround() {
		return this.getCourseOverGround(this.getTime());
	}
	
	public double getSpeedOverGround() {
		return this.getSpeedOverGround(this.getTime());
	}

	public double getAltitude() {
		return this.getAltitude(this.getTime());
	}
	
	public double getDistance() {
		return this.getDistance(this.getTime());
	}

	public double getHeading() {
		return this.getHeading(this.getTime());
	}
	
	public double getSpeed() {
		return this.getSpeed(this.getTime());
	}
	
	public double getAcceleration() {
		return this.getAcceleration(this.getTime());
	}
	
	public double getRateOfTurn() {
		return this.getRateOfTurn(this.getTime());
	}
	
	public boolean isSurfaced() {
		return this.isSurfaced(this.getTime());
	}
	
	public boolean isDived() {
		return this.isDived(this.getTime());
	}
	
	public boolean isFlying() {
		return this.isFlying(this.getTime());
	}
	
	protected Position getPosition(long time) {
		return this.track.getPosition(time);
	}
	
	protected Double getCourseOverGround(long time) {
		return this.track.getCourseOverGround(time);
	}
	
	protected double getSpeedOverGround(long time) {
		return this.track.getSpeedOverGround(time);
	}

	protected double getAltitude(long time) {
		return this.track.getPosition(time).getAltitude();
	}
	
	protected double getDistance(long time) {
		return this.track.getDistanceTravelled(time);
	}
	
	public double getHeading(long time) {
		return this.orientation.getHeading(time);
	}
	
	public double getSpeed(long time) {
		return this.orientation.getSpeed(time);
	}
	
	protected double getAcceleration(long time) {
		return this.orientation.getAcceleration(time);
	}
	
	public double getRateOfTurn(long time) {
		return this.orientation.getRateOfTurn(time);
	}
	
	protected boolean isSurfaced(long time) {
		return Math.abs(this.getAltitude(time))==Math.min(Math.abs(this.getAltitudeMinimum()),Math.abs(this.getAltitudeMaximum()));
	}
	
	protected boolean isDived(long time) {
		return Math.abs(this.getAltitude(time))>Math.abs(this.getAltitudeMaximum());
	}
	
	protected boolean isFlying(long time) {
		return Math.abs(this.getAltitude(time))>Math.abs(this.getAltitudeMinimum());
	}
	
	/*
	 * to other entities by time
	 */
	
	public Double getBearing(Entity entity) {
		return this.getBearing(this.getTime(),entity);
	}
	
	public Double getBearingRelative(Entity entity) {
		return this.getBearingRelative(this.getTime(),entity);
	}
	
	public Double getSpeedOwnAcross(Entity entity) {
		return this.getSpeedOwnAcross(this.getTime(),entity);
	}
	
	public Double getSpeedOwnAlong(Entity entity) {
		return this.getSpeedOwnAlong(this.getTime(),entity);
	}
	
	public Double getAngleOnBow(Entity entity) {
		return this.getAngleOnBow(this.getTime(),entity);
	}
	
	public Double getSpeedTargetAcross(Entity entity) {
		return this.getSpeedTargetAcross(this.getTime(),entity);
	}
	
	public Double getSpeedTargetAlong(Entity entity) {
		return this.getSpeedTargetAlong(this.getTime(),entity);
	}

	public Double getRange(Entity entity) {
		return this.getRange(this.getTime(),entity);
	}

	public Double getRangeEstimated(Entity entity) {
		return this.getRangeEstimated(this.getTime(),entity);
	}

	public Double getDistanceOffTrack(Entity entity) {
		return this.getDistanceOffTrack(this.getTime(),entity);
	}
	
	public Double getSpeedResultantAcross(Entity entity) {
		return this.getSpeedResultantAcross(this.getTime(),entity);
	}

	public Double getSpeedResultantAlong(Entity entity) {
		return this.getSpeedResultantAlong(this.getTime(),entity);
	}
	
	public Double getBearingRate(Entity entity) {
		return this.getBearingRate(this.getTime(),entity);
	}
	
	public NAUTIC.Motion getMotionOverGroundRelative(Entity entity) {
		return this.getMotionOverGroundRelative(this.getTime(),entity);
	}
	
	public NAUTIC.CPA getCPA(Entity entity) {
		return this.getCPA(this.getTime(),entity);
	}
	
	// +++++
	
	public Double getBearing(long time, Entity entity) {
		Position p1 = this.getPosition(time);
		Position p2 = entity.getPosition(time);
		if (p1==null || p2==null) return null;
		return NAUTIC.getBearing(p1,p2);
	}
	
	public Double getBearingRelative(long time, Entity entity) {
		Double x = this.getBearing(time,entity);
		if (x==null) return null;
		return NAUTIC.getBearingRelative(x,this.getHeading(time));		
	}
	
	public Double getSpeedOwnAcross(long time, Entity entity) {
		Double x = this.getBearingRelative(time,entity);
		if (x==null) return null;
		return NAUTIC.getSpeedAcross(x,this.getSpeed(time));
	}
	
	public Double getSpeedOwnAlong(long time, Entity entity) {
		Double x = this.getBearingRelative(time,entity);
		if (x==null) return null;
		return NAUTIC.getSpeedAlong(x,this.getSpeed(time));
	}
	
	public Double getAngleOnBow(long time, Entity entity) {
		Double x = this.getBearing(time,entity);
		if (x==null) return null;
		return NAUTIC.getAngleOnBow(x,entity.getHeading(time));
	}
	
	public Double getSpeedTargetAcross(long time, Entity entity) {
		Double x = this.getAngleOnBow(time,entity);
		if (x==null) return null;
		return NAUTIC.getSpeedAcross(x,entity.getSpeed(time));
	}

	public Double getSpeedTargetAlong(long time, Entity entity) {
		Double x = this.getAngleOnBow(time,entity);
		if (x==null) return null;
		return NAUTIC.getSpeedAlong(x,entity.getSpeed(time));
	}
	
	public double getRange(long time, Entity entity) {
		return NAUTIC.getDistance(this.getPosition(time),entity.getPosition(time));
	}

	public double getRangeEstimated(long time, Entity entity) {
		return NAUTIC.getApproxDistance(NAUTIC.getDistance(this.getPosition(time),entity.getPosition(time)));
	}
	
	public Double getDistanceOffTrack(long time, Entity entity) {
		Double x = this.getAngleOnBow(time,entity);
		if (x==null) return null;
		return NAUTIC.getDistanceOffTrack(x,this.getRange(time,entity));
	}
	
	public Double getSpeedResultantAcross(long time, Entity entity) {
		Double x = this.getSpeedOwnAcross(time,entity);
		Double y = this.getSpeedTargetAcross(time,entity);
		if ((x==null) || (y==null)) return null;
		return NAUTIC.getSpeedResultant(x,y);
	}

	public Double getSpeedResultantAlong(long time, Entity entity) {
		Double x = this.getSpeedOwnAlong(time,entity);
		Double y = this.getSpeedTargetAlong(time,entity);
		if ((x==null) || (y==null)) return null;
		return NAUTIC.getSpeedResultant(x,y);
	}
	
	public Double getBearingRate(long time, Entity entity) {
		Double b1 = this.getBearing(time,entity);
		Double b2 = this.getBearing(time+1,entity);
		if (b1==null || b2==null) return null;
		return NAUTIC.getBearingRate(b1,b2,1);
	}
	
	public NAUTIC.Motion getMotionOverGroundRelative(long time, Entity entity) {
		Double x1 = entity.getCourseOverGround(time);
		Double y1 = entity.getSpeedOverGround(time);
		Double x2 = this.getCourseOverGround(time);
		Double y2 = this.getSpeedOverGround(time);
		if ((x1==null) || (y1==null) || (x2==null) || (y2==null)) return new NAUTIC.Motion();
		return NAUTIC.getMotionRelative(x1,y1,x2,y2);
	}

	public NAUTIC.CPA getCPA(long time, Entity entity) {
		Double x = this.getBearing(time,entity);
		NAUTIC.Motion mot = this.getMotionOverGroundRelative(time,entity);
		Double y = mot.getHeading();
		Double z = mot.getSpeed();
		if ((x==null) || (y==null) || (z==null)) return new NAUTIC.CPA();
		return NAUTIC.getCPA(x,y,z,this.getRange(time,entity));
	}
	
	/*
	 * helper
	 */
	
	public long getTime() {
		return this.environment.getTime();
	}

	public long getTimeElapsed() {
		return this.environment.getTimeLast();
	}
	
	public boolean equals(Object o) {
		if ((o!=null) && (o instanceof Entity)) {
			Entity entity = (Entity)o;
			return entity.serial==this.serial;
		}
		return false;
	}
	
//	public int hashCode() {
//	    int hc = 17;
//	    int hashMultiplier = 31;
//	    hc = hc * hashMultiplier + this.serial;
//	    return hc; 
//	}
	
}