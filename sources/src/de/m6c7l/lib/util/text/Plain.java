/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.text;

public class Plain {
	
	private static final String SEP = System.getProperty("line.separator");
	
	private Plain() {}
	
	public static String toTable(String[][] table, int[] align) {
		return toTable(table, align, false);
	}

	public static String toTable(String[][] table, int[] align, boolean headline) {
        String s = "";
        int[] c = columns(table);
        if (table.length>0) {
            int x = 0;
        	if (headline) {
            	s  = s + format(table[0],c,new int[] {-1}) + SEP;
                if (0<table.length-1) s = s + line(c) + SEP;
            	x = 1;
            }
            for (int i=x; i<table.length; i++) {
               s  = s + format(table[i],c,align) + SEP;
               if (i<table.length-1) s = s + line(c) + SEP;
            }
        }
        return s;
	}
	
	private static String space(int count) {
		String s = "";
		for (int j=0; j<count; j++) {
			s = s + " ";			
		}
		return s;
	}
	
	private static String format(String[] row, int[] cols, int[] align) {
		String s = "";
		int a = 0;
		for (int i=0; i<cols.length; i++) {
			s = s + " ";
			String t = (row[i] != null ? row[i] : "");
			if (i<align.length) a = align[i];
			if (a>0) s = s + space(cols[i]-t.length());
			else if (a==0) s = s + space((cols[i]-t.length())/2); 							
			s = s + t;
			if (a<0) s = s + space(cols[i]-t.length());
			else if (a==0) s = s + space((cols[i]-t.length())/2+((cols[i]-t.length())%2)); 							
			s = s + " ";
			if (i<cols.length-1) s = s + "|";
		}
		return s;
	}
	
	private static String line(int[] cols) {
		String s = "";
		for (int i=0; i<cols.length; i++) {
			s = s + "-";
			for (int j=0; j<cols[i]; j++) {
				s = s + "-";
			}
			s = s + "-";
			if (i<cols.length-1) s = s + "+";
		}
		return s;
	}

	private static int[] columns(String[][] rowsAndCols) {
		int[] max = new int[rowsAndCols[0].length];
		if (rowsAndCols.length>0) {
			for (int j=0; j<rowsAndCols.length; j++) { // zeilen
				for (int i=0; i<rowsAndCols[j].length; i++) { // spalten
					if (rowsAndCols[j][i]!=null) {
						max[i] = Math.max(max[i],rowsAndCols[j][i].length());
					}
				}
			}
		}
		return max;
	}

}
