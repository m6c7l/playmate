/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.container;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.m6c7l.lib.gui.layout.GBC;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.SystemColor;

import javax.swing.JLabel;

public class LabelPanel extends JPanel {

	private JLabel[][] labels = null;

	private int rows = 0;

	private int[] rowsPerGroup = null;
	private int cols = 0;
	private int[] align = null;
	private int cellSpacing = 0;
	private int groupSpacing = 0;
	private int tablePadding = 0 ;

	private String[] brackets = new String[] {"",""};
	
	private Dimension prefDim = null;
	
	public LabelPanel(int[] rowsPerGroup, int cols) {
		this(rowsPerGroup, cols, new int[]{});
	}
	
	public LabelPanel(int[] rowsPerGroup, int cols, int[] align) {
		this(rowsPerGroup, cols, align, 2, 4, 2);
	}
	
	public LabelPanel(int[] rowsPerGroup, int cols, int cellSpacing, int groupSpacing, int tablePadding) {
		this(rowsPerGroup, cols, new int[]{}, cellSpacing, groupSpacing, tablePadding);
	}
	
	public LabelPanel(int[] rowsPerGroup, int cols, int[] align, int cellSpacing, int groupSpacing, int tablePadding) {
		this(rowsPerGroup, cols, align, cellSpacing, groupSpacing, tablePadding, false);
	}

	public LabelPanel(int[] rowsPerGroup, int cols, int[] align, int cellSpacing, int groupSpacing, int tablePadding, boolean hideable) {

		this.rowsPerGroup = rowsPerGroup;
		this.cols = cols;
		this.align = align;
		this.cellSpacing = cellSpacing;
		this.groupSpacing = groupSpacing;
		this.tablePadding = tablePadding;
		
		for (int i=0; i<rowsPerGroup.length; i++) {
			rows = rows + rowsPerGroup[i];
		}
	
		this.labels = new JLabel[rows][cols];

		double[] cweights = new double[cols];
		for (int i=0; i<cweights.length; i++) {
			cweights[i] = 1.0;
		}

		double[] rweights = new double[rows];
		for (int i=0; i<rweights.length; i++) {
			rweights[i] = 0.0;
		}

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = cweights;
		gridBagLayout.rowWeights = rweights;
		
		setLayout(gridBagLayout);
		setBackground(SystemColor.text);
		
		init();
		prefDim = super.getPreferredSize();
	}
	
	public Dimension getPreferredSize() {
		return prefDim;
	}
	
	public void setBrackets(String[] brackets) {
		if (brackets!=null) {
			for (int i=0; i<Math.min(brackets.length,this.brackets.length); i++) {
				this.brackets[i] = brackets[i];				
			}
		}
	}
	
	private void init() {
				
		removeAll();
		
		int top = 0;
		int left = 0;
		int bottom = 0;
		int right = 0;
		
		int row = 0;
		
		for (int k=0; k<cols; k++) {
			
			row = 0;
			
			int anchor = 0; if (k<align.length) anchor = align[k];

			left = 0;
			right = 0;			
			if (k>0) { left = cellSpacing; } 
			if (k==0) { left = tablePadding; } else
				if (k==cols-1) { right = tablePadding; } 			
			
			for (int j=0; j<rowsPerGroup.length; j++) {

				top = 0;
				if (j>0) { top = groupSpacing; } 
				
				for (int i=0; i<rowsPerGroup[j]; i++) {
					
					if (i>0) { top = top + cellSpacing; }
					if ((i==0) && (j==0)) { top = top + tablePadding; } else
						if ((i==rowsPerGroup[j]-1) && (j==rowsPerGroup.length-1)) { bottom = tablePadding; }
						
					GBC gbc = new GBC(k,row).setAnchor(GBC.PAGE_START).setFill(GBC.BOTH).setInsets(top, left, bottom, right);
					this.labels[row][k] = new JLabel(" ");

					top = 0;
					bottom = 0;
					
					if (anchor<0) {
						gbc.setAnchor(GBC.FIRST_LINE_START);					
						this.labels[row][k].setHorizontalAlignment(SwingConstants.LEFT);
					} else if (anchor>0) {
						gbc.setAnchor(GBC.FIRST_LINE_END);
						this.labels[row][k].setHorizontalAlignment(SwingConstants.RIGHT);
					}
					
					add(this.labels[row][k], gbc);
					row++;
					
				}
				
			}
			
		}
		
	}
	
	public boolean clearColumn(int col) {
		return setColumn(col,(Object)null);
	}

	private boolean setColumn(int col, Object value) {
		if ((labels.length>0) && (col<labels[0].length)) {
			Object[] values = new Object[labels.length];
			for (int i=0; i<values.length; i++) {
				values[i] = value;
			}
			return setColumn(col,values);
		}
		return false;
	}
	
	public boolean setColumn(int col, Object[] values) {
		if ((labels.length>0) && (col<labels[0].length)) {
			for (int i=0; i<labels.length; i++) {
				if ((values[i]==null) || (i>=values.length)) {
					this.labels[i][col].setText("");
				} else  {					
					this.labels[i][col].setText(brackets[0] + values[i].toString() + brackets[1]);										
				}
			}	
			return true;
		}
		return false;
	}

	public void clear() {
		for (int i=0; i<labels.length; i++) {
			clearRow(i);
		}
	}
	
	public boolean clearRow(int row) {
		return setRow(row,(Object)null);
	}

	private boolean setRow(int row, Object value) {
		if (row<labels.length) {
			Object[] values = new Object[labels[row].length];
			for (int i=0; i<values.length; i++) values[i] = value;
			return setRow(row,values);
		}
		return false;
	}

	public boolean setRow(int row, Object[] values) {
		if (row<labels.length) {
			for (int j=0; j<labels[row].length; j++) {
				if ((values[j]==null) || (j>=values.length)) {
					this.labels[row][j].setText("");				
				} else {
					this.labels[row][j].setText(brackets[0] + values[j].toString() + brackets[1]);					
				}
			}
			return true;
		}
		return false;
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (labels.length>0) {
			for (int i=0; i<labels.length; i++) {
				for (int j=0; j<labels[i].length; j++) {
					this.labels[i][j].setEnabled(enabled);
				}
			}
		}
	}
	
}
