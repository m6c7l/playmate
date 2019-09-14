/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util;

import java.io.Serializable;

import de.m6c7l.lib.util.geo.Position;

public final class NAUTIC {

	public static double getCourseOverGround(double heading, double speed, double set, double drift) {
		if (drift==0) return (heading+360)%360;
		double g = getAngleOnBow(heading,set);
		if (g!=0) {
			double radg = Math.abs(g)/180.0*Math.PI;
			double rada = Math.atan((drift*Math.sin(radg))/(speed-drift*Math.cos(radg)));
			double a = rada*180.0/Math.PI;
			if (a>0) {
				return (heading+a*(g/Math.abs(g))+360)%360;				
			} else {
				return (heading+a*(g/Math.abs(g))+180)%360;
			}
		} else {
			if (drift>speed) {
				return (set+360)%360;
			}
		}
		return (heading+360)%360;
	}

	public static double getSpeedOverGround(double heading, double speed, double set, double drift) {
		if (drift==0) return speed;
		double x = speed*Math.sin(heading/180.0*Math.PI)+drift*Math.sin(set/180.0*Math.PI);
		double y = speed*Math.cos(heading/180.0*Math.PI)+drift*Math.cos(set/180.0*Math.PI);
		return Math.sqrt(x*x+y*y);
	}
	
	
	
	public static double getHeading(double cog, double sog, double set, double drift) {
		if (drift==0) return cog;
		double x = sog*Math.sin(cog/180.0*Math.PI)-drift*Math.sin(set/180.0*Math.PI);
		double y = sog*Math.cos(cog/180.0*Math.PI)-drift*Math.cos(set/180.0*Math.PI);
		double z = (Math.atan(x/y)*180/Math.PI);
		if ((y<0)) z = z+180; 
		return (z+360)%360;
	}
	
	public static double getSpeed(double cog, double sog, double set, double drift) {
		if (drift==0) return sog;
		double x = sog*Math.sin(cog/180.0*Math.PI)-drift*Math.sin(set/180.0*Math.PI);
		double y = sog*Math.cos(cog/180.0*Math.PI)-drift*Math.cos(set/180.0*Math.PI);
		return Math.sqrt(x*x+y*y);
	}
	
	
	
	/*
	 * Kurs
	 */
	public static double getCourse(double trueBearing, double angleOnBow) {
		double x = getBearingOpposite(trueBearing)-angleOnBow;
		return (x+360) % 360;
	}

	/*
	 * Lagewinkel
	 */
	public static double getAngleOnBow(double trueBearing, double targetHeading) {
		double x = getBearingOpposite(trueBearing)-targetHeading;
		if (x>+180) x = x - 360;
		if (x<-180) x = x + 360;
		return x;
	}
	
	/*
	 * Schiffsseitenpeilung
	 */
	public static double getBearingRelative(double trueBearing, double heading) {
		double x = trueBearing-heading;
		if (x>+180) x = x - 360;
		if (x<-180) x = x + 360;
		return x;
	}

	
	/*
	 * Entfernung (genähert)
	 */
	public static double getApproxDistance(double distance) {
		int f = 50;
		if (distance>10000) {
			f = 500;
		} else if (distance>7000) {
			f = 200;
		} else if (distance>4000) {
			f = 100;
		} else if (distance>1000) {
			f = 50;
		}
		return (((int)distance)/f*f);
	}
	
	/*
	 * Geschwindigkeit (genähert)
	 */
	public static double getApproxSpeed(double speed) {
		int f = ((int)Math.abs(speed))/10+1;
		return (((int)Math.round(speed))/f*f);
	}
	
	/*
	 * Winkel (genähert)
	 */
	public static double getApproxAngle(double angle) {
		double x = angle/Math.abs(angle);
		return ((((int)(angle+2.5*x))/5)*5)%360;
	}
	
	/*
	 * Sinus (genähert)
	 */
	public static double getApproxSin(double angle) {
		double x = 1;
		if (angle!=0) x = angle/Math.abs(angle);
		int a = Math.abs((int)getApproxAngle(angle));
		if (a>90) a = 90-(a-90);
		int b = (a/10)*10;
		double c = b/100.0;
		if (a>=10) c = c + 0.1;
		if (a>=30) c = c + 0.1;
		//if (a>=50) c = c + 0.1;
		if (a>b) c = c + 0.1;
		if (c>1) c = 1;
		return c*x;
	}
	
	/*
	 * Cosinus (genähert)
	 */
	public static double getApproxCos(double angle) {
		return getApproxSin(90-angle);
	}



	/*
	 * Resultant Speed
	 */
	public static double getSpeedResultant(double os, double ts) {
		return os+ts;
	}

	/*
	 * Speed Across
	 */
	public static double getSpeedAcross(double angle, double speed) {
		double alpha = angle/180.0*Math.PI;
		return Math.sin(alpha)*speed;
	}
	
	/*
	 * Speed Along
	 */
	public static double getSpeedAlong(double angle, double speed) {
		double alpha = angle/180.0*Math.PI;
		return Math.cos(alpha)*speed;
	}

		
	
	/*
	 * Distance of Track
	 */
	public static Double getDistanceOffTrack(double angleOnBow, double range) {
		if (Math.abs(angleOnBow)<=90) {
			double alpha = angleOnBow/180.0*Math.PI;
			return Math.abs(Math.sin(alpha)*range);
		}
		return null;
	}

	
	
	/*
	 * Abfang-Winkel (Iut)
	 */
	public static Double getAngleIntercept(double angleOnBow, double targetSpeed, double speed) {
		double tsa = getSpeedAcross(angleOnBow,targetSpeed);
		Double rbrg = Math.asin(tsa/speed)*180/Math.PI;
		if (rbrg.isNaN()) return null;
		return rbrg;
	}
	
	/*
	 * Abfang-Kurs (Ku)
	 */
	public static Double getCourseIntercept(double trueBearing, double speed, double targetCourse, double targetSpeed) {
		Double rbrg = getAngleIntercept(getAngleOnBow(trueBearing,targetCourse),targetSpeed,speed);
		if (rbrg!=null) return ((trueBearing+rbrg)+360)%360;
		return null;
	}
	
	/*
	 * Abfang-Entfernung (Rt)
	 */
	public static Double getRangeIntercept(double trueBearing, double speed, double targetCourse, double targetSpeed, double range) {
		double im = Math.abs(getAngleOnBow(trueBearing,targetCourse));
		Double iut = Math.abs(getAngleIntercept(getAngleOnBow(trueBearing,targetCourse),targetSpeed,speed));
		if (iut!=null) {
			double it = 180-iut-im;
			if (speed>0) {
				Double x = 0.0;
				if (Math.abs(im)==0) {
					x = speed/(speed+targetSpeed);
				} else if (Math.abs(im)==180) {
					x = speed/(speed-targetSpeed);
				} else {
					x = Math.sin(im/180*Math.PI)/Math.sin(it/180*Math.PI);
				}
				if ((x.isNaN()) || (x.isInfinite())) return null;
				return x*range;								
			}
		}
		return null;
	}
	
	
	
	/*
	 * Gegenpeilung
	 */
	public static double getBearingOpposite(double bearing) {
		return (bearing + 180) % 360;
	}

	/*
	 * Peilung
	 */
	public static Double getBearing(Position p1, Position p2) {
		return p1.getRhumbLine(p2).getBearing();
	}

	/*
	 * Entfernung
	 */
	public static double getDistance(Position p1, Position p2) {
		return p1.getRhumbLine(p2).getDistance();
	}

	/*
	 * Bearing Rate (mit RSA und Entfernung)
	 */
	public static double getBearingRate(double osa, double tsa, double range) {
		return (1935.78*((((osa*1.944)+(tsa*1.944)))/(range*1.0936)));
	}

	/*
	 * Bearing Rate
	 */
	public static double getBearingRate(double fromBearing, double toBearing, int timespan) {
		return (getAngleDifference(fromBearing,toBearing,0)/(timespan/60.0));
	}
	
	/*
	 * Entfernung mit RSA und BR
	 */
	public static double getRange(double rsa, double bearingRate) {
		return (1935.78*((rsa*1.944)/bearingRate))*0.9144;
	}

//	/*
//	 * Entfernung mit dOSA und dBR (nach Methode 1936)
//	 */
//	public static double getRange1936(double osa1, double bearingRate1, double osa2, double bearingRate2) {
//		double dOSA = Math.abs((osa1*1.944)-(osa2*1.944));
//		double dBR = Math.abs(bearingRate1-bearingRate2);
//		return (1935.78*(dOSA/dBR))*0.9144;
//	}

	/*
	 * Winkelunterschied zwischen zwei Peilungen
	 */
	public static double getAngleDifference(double fromBearing, double toBearing, int direction) {
		if (fromBearing==toBearing) return 0;
		double x = toBearing-fromBearing;
		if (x>+180) x = x - 360;
		if (x<-180) x = x + 360;
		if ((direction>0) && (x<0)) {
			return 360+x;
		} else if ((direction<0) && (x>0)) {
			return -(360-x);
		}
		return x;
	}

	/*
	 * Relative motion of a target
	 */
	public static Motion getMotionRelative(double targetCourse, double targetSpeed, double course, double speed) {
		double alpha = getAngleDifference(course,targetCourse,0);
		double spd = Math.sqrt(speed*speed+targetSpeed*targetSpeed-2*speed*targetSpeed*Math.cos(alpha/180.0*Math.PI));
		if (alpha==0) {
			if (targetSpeed>=speed) {
				return new Motion(targetCourse, spd);
			} else {
				return new Motion(getBearingOpposite(targetCourse), spd);
			}
		} else if ((Math.abs(alpha)==180) || (targetSpeed==0)) {
			return new Motion(getBearingOpposite(course), spd);
		}
		double beta = Math.acos((speed*speed-targetSpeed*targetSpeed-spd*spd)/(-2*targetSpeed*spd))/Math.PI*180.0;
		double gamma = 180-(Math.abs(alpha)+Math.abs(beta));
		return new Motion(getCourse(course,gamma*(alpha/Math.abs(alpha))), spd);
	}

	/*
	 * Closest Point of Approach
	 */
	public static NAUTIC.CPA getCPA(double trueBearing, double relativeCourse, double relativeSpeed, double distance) {
		double relativeAOB = getAngleOnBow(trueBearing,relativeCourse);
		Double d = getDistanceOffTrack(relativeAOB,distance);
		if ((d!=null) && (Math.abs(relativeSpeed)>0)) {
			Double b = null;
			if (relativeAOB < 0) {
				b = 90 + relativeAOB;
				b = (trueBearing-b+360)%360;
			} else if (relativeAOB > 0) {
				b = 90 - relativeAOB;
				b = (trueBearing+b+360)%360;
			}
			double alpha = (180-(90+Math.abs(relativeAOB)))/180.0*Math.PI;
			int t = (int)((Math.sqrt(distance*distance+d*d-2*distance*d*Math.cos(alpha)))/relativeSpeed);
			return new NAUTIC.CPA(b, d, t);
		}
		return new NAUTIC.CPA();
	}

	public static final class Motion implements Serializable {

		protected Double hdg = null;
		protected double spd = 0;

		public Motion() {}

		public Motion(double hdg, double spd) {
			this();
			this.hdg = hdg;
			this.spd = spd;
		}

		public Double getHeading() {
			return hdg;
		}
		public double getSpeed() {
			return spd;
		}

	}

	public static final class CPA implements Serializable {

		protected Double brg = null;
		protected double dst = 0;
		protected long tim = 0;

		public CPA() {}

		public CPA(Double brg, double dst, long tim) {
			this();
			this.brg = brg;
			this.dst = dst;
			this.tim = tim;
		}

		public boolean isApproaching() {
			return tim > 0;
		}

		public Double getBearing() {
			return brg;
		}
		public double getDistance() {
			return dst;
		}
		public long getTime() {
			return tim;
		}

	}

}
