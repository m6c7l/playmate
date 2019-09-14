/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.layout;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBC extends GridBagConstraints  {
	
	public static final FILL 	BOTH 				= FILL.BOTH;
	public static final FILL 	NONE				= FILL.NONE;
	public static final FILL 	HORIZONTAL 			= FILL.HORIZONTAL;
	public static final FILL 	VERTICAL 			= FILL.VERTICAL;
	
	public static final ANCHOR 	FIRST_LINE_START 	= ANCHOR.FIRST_LINE_START;
	public static final ANCHOR 	PAGE_START			= ANCHOR.PAGE_START;
	public static final ANCHOR 	FIRST_LINE_END 		= ANCHOR.FIRST_LINE_END;
	public static final ANCHOR 	LINE_START 			= ANCHOR.LINE_START;
	public static final ANCHOR 	CENTER 				= ANCHOR.CENTER;
	public static final ANCHOR 	LINE_END 			= ANCHOR.LINE_END;
	public static final ANCHOR 	LAST_LINE_START 	= ANCHOR.LAST_LINE_START;
	public static final ANCHOR 	PAGE_END 			= ANCHOR.PAGE_END;
	public static final ANCHOR 	LAST_LINE_END 		= ANCHOR.LAST_LINE_END;

	/*
	 *
	 * gridx, gridy				Position im Grid
	 * gridwidth, gridheight	Erstreckung �ber Spalten und Zeilen
	 * fill						Aufbl�hung im Anzeigebereich (wenn Anzeigebereich gr��er ist als prefSzie/minSize)
	 * weightx, weighty			Verh�ltniszahl zur Verteilung des freien Raums in horizonaler und vertikaler Richtung (0..1)
	 * anchor					Positionierung im Anzeigebereich (wenn dieser Anzeigebereich gr��er ist als prefSzie/minSize)
	 * insets					Polsterung, d. h. Abstand zwischen Komponente und Rand des Anzeigebereichs
	 * 
	 * --------------------------------------------------------------
     *
	 * ANCHOR
	 * 
	 * abs. 	CENTER, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, und NORTHWEST.
	 * orient.	PAGE_START, PAGE_END, LINE_START, LINE_END, FIRST_LINE_START, FIRST_LINE_END, LAST_LINE_START und LAST_LINE_END.
	 * basel.	BASELINE, BASELINE_LEADING, BASELINE_TRAILING, ABOVE_BASELINE, ABOVE_BASELINE_LEADING, ABOVE_BASELINE_TRAILING, BELOW_BASELINE, BELOW_BASELINE_LEADING und BELOW_BASELINE_TRAILING. 
	 *
	 * --------------------------------------------------------------
	 *
	 * FILL
	 * 
	 * NONE: 		die Komponente nicht skalieren
     * HORIZONTAL: 	die Komponente breit genug machen, um ihren Anzeigebereich horizontal zu f�llen
     * VERTICAL: 	die Komponente gro� genug machen, um ihren Anzeigebereich vertikal zu f�llen
     * BOTH: 		die Komponente soll den ganzen Anzeigebereich f�llen
	 * 
	 * -------------------------------------------------
     * |FIRST_LINE_START   PAGE_START     FIRST_LINE_END|
     * |                                                |
     * |                                                |
     * |LINE_START           CENTER             LINE_END|
     * |                                                |
     * |                                                |
     * |LAST_LINE_START     PAGE_END       LAST_LINE_END|
     * -------------------------------------------------
	 * 
 	 * --------------------------------------------------------------
 	 * 
 	 * 
	 */
	
	public GBC(int gridx) {
		this(gridx,0);
	}
	
	public GBC(int gridx, int gridy) {
		this.gridx = gridx;
		this.gridy = gridy;
	}
	
	public GBC clone() {
		return clone(this.gridx);
	}
	
	public GBC clone(int gridx) {
		return clone(gridx,this.gridy);
	}
	
	public GBC clone(int gridx, int gridy) {
		GBC gbc = new GBC(gridx, gridy);
		gbc.gridwidth = this.gridwidth;
		gbc.gridheight = this.gridheight;
		gbc.fill = this.fill;
		gbc.weightx = this.weightx;
		gbc.weighty = this.weighty;
		gbc.anchor = this.anchor;
		gbc.insets = new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right);
		gbc.ipadx = this.ipadx;
		gbc.ipady = this.ipady;
		return gbc;
	}
	
	public GBC setSpan(int gridwidth) {
		this.gridwidth = gridwidth;
		return this;
	}
	
	public GBC setSpan(int gridwidth, int gridheight) {
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		return this;
	}

	public GBC setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	public GBC setAnchor(ANCHOR anchor) {
		return this.setAnchor(anchor.value);
	}
	
	public GBC setFill(int fill) {
		this.fill = fill;
		return this;
	}

	public GBC setFill(FILL fill) {
		return this.setFill(fill.value);
	}
	
	public GBC setWeight(double weightx, double weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}

	public GBC setWeight(double weightx) {
		return this.setWeight(weightx,0.0);
	}
	
	public GBC setInsets(int top, int left, int bottom, int right) {
		this.insets = new java.awt.Insets(top, left, bottom, right);
		return this;
	}
	
	public GBC setInsets(int distance) {
		return this.setInsets(distance, distance, distance, distance);
	}
	
	public GBC setInsets(int horizontal, int vertical) {
		return this.setInsets(vertical, horizontal, vertical, horizontal);
	}
	
	public GBC setInsets(Insets insets) {
		return this.setInsets(insets.top, insets.left, insets.bottom, insets.right);
	}
	
	public GBC setPadding(int ipadx, int ipady) {
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}

	public GBC setPadding(int padding) {
		return this.setPadding(padding, padding);
	}
	
	public static enum FILL {
		 
		NONE				(GridBagConstraints.NONE),
		BOTH				(GridBagConstraints.BOTH),
		HORIZONTAL			(GridBagConstraints.HORIZONTAL),
		VERTICAL			(GridBagConstraints.VERTICAL);

		public int value = 0;
		
		private FILL(int value) {
			this.value = value;
		}
		
	}
	
	public static enum ANCHOR {
		 
		FIRST_LINE_START	(GridBagConstraints.FIRST_LINE_START),
		PAGE_START			(GridBagConstraints.PAGE_START),
		FIRST_LINE_END		(GridBagConstraints.FIRST_LINE_END),
		LINE_START			(GridBagConstraints.LINE_START),
		CENTER				(GridBagConstraints.CENTER),
		LINE_END			(GridBagConstraints.LINE_END),
		LAST_LINE_START		(GridBagConstraints.LAST_LINE_START),
		PAGE_END			(GridBagConstraints.PAGE_END),
		LAST_LINE_END		(GridBagConstraints.LAST_LINE_END);
		
		public int value = 0;
		
		private ANCHOR(int value) {
			this.value = value;
		}
		
	}

}
