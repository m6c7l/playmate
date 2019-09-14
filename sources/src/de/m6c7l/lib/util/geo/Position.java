/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.geo;

import java.io.Serializable;

/**
 * Loxodrome (Kursgleiche, Rhumb Lines)
 * Orthodrome (Grosskreis, Great Circle)
 */
public class Position {

	public static final double EARTH_CIRCUMFERENCE_KM = 40000.0;
	public static final double EARTH_RADIUS_M = EARTH_CIRCUMFERENCE_KM/(2*Math.PI)*1000;
	public static final double SQUARED_DOUBLED_EARTH_RADIUS_M = Math.sqrt(2*EARTH_RADIUS_M);

	private static final double PI_DIV_2 = Math.PI/2.0;
	private static final double PI_DIV_4 = Math.PI/4.0;
	private static final double PI_MUL_2 = 2.0*Math.PI;

	private static final double MIN_DISTANCE = 0.1;

	public static Position lerp(Position from, Position to, float value) {
		double fl = from.lambda.getValue(); 
		double tl = to.lambda.getValue(); 
		if (tl<fl) {
			fl = (fl - 360) % 360;
			tl = (tl + 360) % 360;
		}		
		double dl = (fl - tl) * value;
		return new Position(from.phi.getValue() + ((to.phi.getValue() - from.phi.getValue()) * value), fl - dl);
	}
	
	private Angle phi = null;
	private Angle lambda = null;
	private double altitude = 0;

	public Position(Angle phi, Angle lambda, double altitude) {
		this.phi = phi;
		this.lambda = lambda;
		this.altitude = altitude;
	}

	public Position(Angle phi, Angle lambda) {
		this(phi,lambda,0);
	}
	
	public Position(double phi, double lambda, double altitude) {
		this(	new Angle(phi>=0 ? Angle.NORTH : Angle.SOUTH, phi),
				new Angle(lambda>=0 ? Angle.EAST : Angle.WEST, lambda),
				altitude);
	}
	
	public Position(double phi, double lambda) {
		this(phi,lambda,0.0);
	}

	public void setPosition(Position position) {
		this.phi = new Angle(position.phi.getOrientation(),position.phi.getValue());
		this.lambda = new Angle(position.lambda.getOrientation(),position.lambda.getValue());
		this.altitude = position.altitude;
	}

	public double getLatitude() {
		return this.phi.getValue();
	}
	
	public void setLatitude(Angle phi) {
		this.phi = phi;
	}
	
	public void setLatitude(double phi) {
		this.phi = new Angle(phi>=0 ? Angle.NORTH : Angle.SOUTH, phi);
	}
	
	public double getLongitude() {
		return this.lambda.getValue();
	}
	
	public void setLongitude(Angle lambda) {
		this.lambda = lambda;
	}
	
	public void setLongitude(double lambda) {
		this.lambda = new Angle(lambda>=0 ? Angle.EAST : Angle.WEST, lambda);
	}
	
	public double getAltitude() {
		return this.altitude;
	}
	
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public boolean isNorthern(Position position) {
		Position.RhumbLine r = this.getRhumbLine(position);
		Double d = r.getBearing();
		if (d==null) return false;
		return (d>90) && (d<270);
	}
	
	public boolean isSouthern(Position position) {
		return !isNorthern(position);
	}
	
	public static double getVisibility(double altitude1, double altitude2) {
		return SQUARED_DOUBLED_EARTH_RADIUS_M*(Math.sqrt(altitude1)+Math.sqrt(altitude2));
	}
	
	public boolean isVisible(Position position) {
		if ((this.getAltitude()<0) || (position.getAltitude()<0)) return false;
		double max = getVisibility(this.getAltitude(),position.getAltitude());
		if (this.getDistanceGreatCircle(position)>max) return false;
		return true;
	}
	
	/*
	 * Kursgleiche, Position nach Peilung und Abstand
	 */
	public Position getPositionRhumbLine(double loxodromicBearing, double loxodromicDistance, double altitude) {
		double brg = loxodromicBearing;
		double dst = loxodromicDistance;
		if (dst<0) {
			brg = (brg+180)%360;
			dst = Math.abs(dst);
		}
		if (dst<MIN_DISTANCE) {
			return new Position(this.getLatitude(),this.getLongitude(),altitude);
		}
		double b = toRAD(brg);
        double d = dst/(EARTH_RADIUS_M);
		double lat1 = toRAD(this.getLatitude());
        double lon1 = toRAD(this.getLongitude());
        double dlat = d*Math.cos(b);
        double lat2 = lat1 + dlat;
		if (lat2>+PI_DIV_2) lat2 = +PI_DIV_2;
		if (lat2<-PI_DIV_2) lat2 = -PI_DIV_2;
        double dphi = Math.log(Math.tan(lat2/2.0+PI_DIV_4)/Math.tan(lat1/2.0+PI_DIV_4));
        double q = Math.abs(dphi) > 10e-12 ? dlat/dphi : Math.cos(lat1);
        double dlon = d*Math.sin(b)/q;
        if (Math.abs(lat2)> PI_DIV_2) lat2 = lat2>0 ? +Math.PI-lat2 : -Math.PI-lat2;
        double lon2 = (lon1+dlon+Math.PI)%(PI_MUL_2)-Math.PI;
        return new Position(Math.toDegrees(lat2),Math.toDegrees(lon2),altitude);
	}
	
	public Position getPositionRhumbLine(double loxodromicBearing, double loxodromicDistance) {
		return this.getPositionRhumbLine(loxodromicBearing, loxodromicDistance, this.altitude);
	}

	public Position getPositionRhumbLine(Position.RhumbLine rhumbline) {
		return this.getPositionRhumbLine(rhumbline.getBearing(), rhumbline.getDistance(), this.altitude);
	}

	/*
	 * Kursgleiche, feste Peilung
	 */
	public Position.RhumbLine getRhumbLine(Position position) {
		return getRhumbLine(position, (byte)0);
    }
	
	/*
	 * Kursgleiche, feste Peilung
	 */
	public Position.RhumbLine getRhumbLine(Position position, byte direction) {
		if ((this.equals(position)) || (position == null)) return new Position.RhumbLine();
        double lat1 = toRAD(this.getLatitude());
        double lat2 = toRAD(position.getLatitude());
		double dlat = lat2-lat1;
        double lon1 = toRAD(this.getLongitude());
        double lon2 = toRAD(position.getLongitude());        
        double dlon = lon2-lon1;
		double dphi = Math.log(Math.tan(lat2/2.0+PI_DIV_4)/Math.tan(lat1/2.0+PI_DIV_4));
		double q = Math.abs(dphi) > 10e-12 ? dlat/dphi : Math.cos(lat1);
    	if (Math.abs(dlon)>Math.PI) {
        	dlon = dlon>0 ? -(PI_MUL_2-dlon) : +(PI_MUL_2+dlon);
    	}
        if ((direction>0) && (dlon<0)) { // only east
       		dlon = dlon+PI_MUL_2;
       	} else if ((direction<0) && (dlon>0)) { // only west
       		dlon = dlon-PI_MUL_2;
       	}
		return new Position.RhumbLine(
				toDEG(Math.atan2(dlon,dphi)),
				Math.sqrt(dlat*dlat+q*q*dlon*dlon)*(EARTH_RADIUS_M));
    }
	
	/*
	 * Grosskreis, Anfangs-Peilung
	 */
	public Double getBearingInitialGreatCircle(Position position) {
		if (this.equals(position)) return null;
        double lat1 = toRAD(this.getLatitude());
        double lat2 = toRAD(position.getLatitude());
        double lon1 = toRAD(this.getLongitude());
        double lon2 = toRAD(position.getLongitude());
        double y = Math.sin(lon2-lon1)*Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1);
        double b = toDEG(Math.atan2(y,x));
        return (b+360)%360;
    }
	
	/*
	 * Grosskreis, End-Peilung
	 */
	public Double getBearingFinalGreatCircle(Position position) {
		if (this.equals(position)) return null;
		Double b = position.getBearingInitialGreatCircle(this);
		return (b+180)%360;
	}
	
	/*
	 * Grosskreis, Entfernung (kürzeste)
	 */
	public double getDistanceGreatCircle(Position position) {
        double lat1 = toRAD(this.getLatitude());
        double lat2 = toRAD(position.getLatitude());
        double lon1 = toRAD(this.getLongitude());
        double lon2 = toRAD(position.getLongitude()); 
		return Math.acos(
				Math.sin(lat1)*Math.sin(lat2)+
				Math.cos(lat1)*Math.cos(lat2)*
				Math.cos(lon2-lon1))*(EARTH_RADIUS_M);
	}

	/*
	 * Grosskreis, Position nach (Anfangs-)Peilung und Abstand
	 */
	public Position getPositionGreatCircle(double orthodromicInitialBearing, double orthodromicDistance, double altitude) {
		if (orthodromicDistance<1) return new Position(this.getLatitude(),this.getLongitude(),altitude);
		double brg = orthodromicInitialBearing;
		double dst = orthodromicDistance;
		if (dst<0) {
			brg = (brg+180)%360;
			dst = Math.abs(dst);
		}
        double d = dst/(EARTH_RADIUS_M);
        double b = toRAD(brg);   
        double lat1 = toRAD(this.getLatitude());
        double lon1 = toRAD(this.getLongitude());
        double lat2 = Math.asin(Math.sin(lat1)*Math.cos(d)+Math.cos(lat1)*Math.sin(d)*Math.cos(b));
        double lon2 = lon1+	Math.atan2(Math.sin(b)*Math.sin(d)*Math.cos(lat1),
        								Math.cos(d)-Math.sin(lat1)*Math.sin(lat2));
        return new Position(toDEG(lat2),toDEG(lon2),altitude);
	}
	
	public Position getPositionGreatCircle(double orthodromicInitialBearing, double orthodromicDistance) {
		return this.getPositionGreatCircle(orthodromicInitialBearing, orthodromicDistance, this.altitude);
	}
	
    private double toRAD(double deg) {
		return deg/180.0*Math.PI;
    }

    private double toDEG(double rad) {
        return (rad*180.0/Math.PI + 360) % 360;
    }
	
	public boolean equals(Object object) {
		if (object instanceof Position) {
			if (
				(((Position)object).getLatitude()==this.getLatitude())
					&& (((Position)object).getLongitude()==this.getLongitude())
				)
				return true;
		}
		return false;
	}

	public String toStringLatitude() {
		return this.phi.toString();
	}

	public String toStringLongitude() {
		return this.lambda.toString();
	}
	
	public String toString() {
		return toStringLatitude() + " " + toStringLongitude();
	}

	public static final class RhumbLine implements Serializable {

		protected Double brg = null;
		protected double dst = 0;

		protected RhumbLine() {}

		public RhumbLine(double brg, double dst) {
			this();
			this.brg = brg;
			this.dst = dst;
		}

		public Double getBearing() {
			return this.brg;
		}

		public double getDistance() {
			return this.dst;
		}
	}

}
