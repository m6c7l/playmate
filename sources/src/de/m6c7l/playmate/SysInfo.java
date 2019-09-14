/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate;

import java.io.File;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.m6c7l.lib.util.SIPREFIX;
import de.m6c7l.lib.util.text.Plain;

public class SysInfo {
	
    public static class Memory {
    	
    	public String toString() {
    		
        	Runtime runtime = Runtime.getRuntime();

            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory-freeMemory;
            long unallocatedMemory = maxMemory-usedMemory;
            
        	String[][] tab = new String[5][3];
        	String suffix = "Byte";
        	
            tab[0][0] = "maximum";		tab[0][1] = SIPREFIX.format(maxMemory,suffix);
            tab[1][0] = "total";		tab[1][1] = SIPREFIX.format(totalMemory,suffix);
            tab[2][0] = "free";			tab[2][1] = SIPREFIX.format(freeMemory,suffix);			tab[2][2] = ((int)((freeMemory/(totalMemory*1.0))*100*10))/10.0 + " %";  
            tab[3][0] = "used";			tab[3][1] = SIPREFIX.format(usedMemory,suffix);			tab[3][2] = ((int)((usedMemory/(totalMemory*1.0))*100*10))/10.0 + " %";
            tab[4][0] = "unallocated";	tab[4][1] = SIPREFIX.format(unallocatedMemory,suffix);

            return Plain.toTable(tab,new int[] {-1,+1,+1});

    	}

    }

    public static class Properties {
    	
    	public String toString() {

    		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    		
    		Map<String, String> systemProperties = runtimeBean.getSystemProperties();
    		Collection<String> keys = systemProperties.keySet();
    		keys.remove("line.separator");
    		
    		List<String> list = new ArrayList<String>(keys);
    		Collections.sort(list);
    		keys = list;
    		
    		String[][] tab = new String[keys.size()][2]; 
    		
        	int j = 0;
    		for (String key : keys) {
       			String value = systemProperties.get(key);
       			tab[j][0] = key;
       			tab[j][1] = value;
       			j++;    				
    		}

            return Plain.toTable(tab,new int[] {-1,-1});
            
    	}

    }
    
    public static class Filesystem {
    	
    	private static final String SEP = System.getProperty("line.separator");
    	
    	public String toString() {
    		
            File[] roots = File.listRoots();

        	String[][][] tab = new String[roots.length][3][2];
        	String suffix = "Byte";
        	
        	int j = 0;
    		String s = "";
    		
            for (File root : roots) {
            	
            	s = s + root.toURI() + SEP + SEP;

    			tab[j][0][0] = "total"; 	tab[j][0][1] = SIPREFIX.format(root.getTotalSpace(),suffix);
    			tab[j][1][0] = "free"; 		tab[j][1][1] = SIPREFIX.format(root.getFreeSpace(),suffix);
   				tab[j][2][0] = "usable"; 	tab[j][2][1] = SIPREFIX.format(root.getUsableSpace(),suffix);

                s = s + Plain.toTable(tab[j],new int[] {-1,+1}) + SEP;

    			j++;
    			
    		}

            return s;
            
    	}

    }
    
}
