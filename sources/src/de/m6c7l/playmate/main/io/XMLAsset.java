/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main.io;

import java.util.Locale;

import de.m6c7l.lib.util.xml.XMLElement;

public class XMLAsset {

	private static final String ID			= "id";
	
	private static final String CLASSNAME	= "class";
	private static final String TYPE 		= "type";

	private static final String NAME 		= "name";
	private static final String FLAG 		= "flag";
	
	private static final String BOA 		= "boa";
	private static final String LOA 		= "loa";
	private static final String DRAFT 		= "draft";
	private static final String MAXSPEED 	= "maxspeed";
	
	private static final String WINGSPAN 	= "wingspan";
	private static final String ROTOR 		= "rotor";
	
	private static final String SURFACED 	= "surfaced";
	private static final String DIVED 		= "dived";
	
	private static final String MOD 		= "mod";
	private static final String ROLE 		= "role";
	
	private static final String LENGTH 		= "length";
	private static final String CEILING 	= "ceiling";
	
	private static final String SPEED 		= "speed";
	private static final String CRUISE 		= "cruise";
	private static final String MAX	 		= "max";
	
	private static final String DIAMETER 	= "diameter";
	
	private static final String MILITARY 	= "military";
	private static final String SONAR 		= "sonar";
	
	/*
	 * build
	 */
	
	private XMLElement data = null;

	private static int nextSerial = 0;
	private int serial = 0;
	
	protected XMLAsset(XMLElement data) {
		this.data = data;
		this.serial = nextSerial++;
	}

	public String getID() {
		return CONVERT.getString(data,ID,false);
	}
	
	public String getName() {
		return CONVERT.getString(data,NAME,false);
	}
	
	public Locale getFlag() {
		if (data.hasElement(FLAG)) {
			return CONVERT.getLocale(data.firstElement(FLAG));
		}
		return null;
	}
	
	public String getClassName() {
		return CONVERT.getString(data,CLASSNAME,false);
	}
	
	public String getType() {
		return CONVERT.getString(data,TYPE,false);
	}
	
	public Double getLOA() {
		return CONVERT.getDouble(data,LOA,false);
	}
	
	public Double getBOA() {
		return CONVERT.getDouble(data,BOA,false);
	}
	
	public Double getDraft() {
		return CONVERT.getDouble(data,DRAFT,false);
	}
	
	public Double getCruiseSpeed() {
		if (data.hasElement(SPEED)) {
			return CONVERT.getDouble(data.firstElement(SPEED),CRUISE,false);
		}
		return null;
	}
	
	public Double getMaxSpeed() {
		if (isPlane()) {
			if (data.hasElement(SPEED)) {
				return CONVERT.getDouble(data.firstElement(SPEED),MAX,false);
			}
		}
		return CONVERT.getDouble(data,MAXSPEED,false);
	}
	
	public Double getMaxSpeedDived() {
		if (data.hasElement(MAXSPEED)) {
			return CONVERT.getDouble(data.firstElement(MAXSPEED),DIVED,false);
		}
		return null;
	}
	
	public Double getMaxSpeedSurfaced() {
		if (data.hasElement(MAXSPEED)) {
			return CONVERT.getDouble(data.firstElement(MAXSPEED),SURFACED,false);
		}
		return null;
	}
	
	public Double getMOD() {
		return CONVERT.getDouble(data,MOD,false);
	}
	
	public String getRole() {
		return CONVERT.getString(data,ROLE,false);
	}
	
	public Double getLength() {
		return CONVERT.getDouble(data,LENGTH,false);
	}
	
	public Double getWingspan() {
		return CONVERT.getDouble(data,WINGSPAN,false);
	}
	
	public Double getCeiling() {
		return CONVERT.getDouble(data,CEILING,false);
	}
	
	public String getRotorType() {
		if (data.hasElement(ROTOR)) {
			return CONVERT.getString(data.firstElement(ROTOR),TYPE,false);
		}
		return null;
	}
	
	public Double getRotorDiameter() {
		if (data.hasElement(ROTOR)) {
			return CONVERT.getDouble(data.firstElement(ROTOR),DIAMETER,false);
		}
		return null;
	}
	
	public Boolean isMilitary() {
		Boolean b =CONVERT.getBoolean(data,MILITARY,false);
		return b!=null ? b : false;
	}
	
	public Boolean hasSonar() {
		Boolean b = CONVERT.getBoolean(data,SONAR,false); 
		return b!=null ? b : false;
	}
	
	public boolean isVessel() {
		return (data.hasElement(DRAFT) && data.hasElement(MAXSPEED) &&
				!data.getElement(MAXSPEED)[0].hasElement(DIVED));
	}
	
	public boolean isSubmarine() {
		return (data.hasElement(DRAFT) && data.hasElement(MAXSPEED) &&
				data.getElement(MAXSPEED)[0].hasElement(DIVED));
	}
	
	public boolean isPlane() {
		return data.hasElement(WINGSPAN);
	}
	
	public boolean isHelicopter() {
		return data.hasElement(ROTOR);
	}
	
	public boolean equals(Object object) {
		if ((object!=null) && (object instanceof XMLAsset)) {
			return (((XMLAsset)object).serial==this.serial);
		}
		return false;
	}
	
	public int hashCode() {
	    int hc = 7;
	    int hashMultiplier = 31;
	    hc = hc * hashMultiplier + this.serial;
	    return hc; 
	}
	
	public String toString() {
		return getName();
	}
	
}