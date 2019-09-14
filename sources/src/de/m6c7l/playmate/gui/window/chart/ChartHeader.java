/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.window.chart;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.m6c7l.lib.gui.graphics.CanvasPane.IPoint;
import de.m6c7l.lib.gui.layout.GBC;
import de.m6c7l.lib.util.geo.Position;
import de.m6c7l.playmate.main.VALUE;

public class ChartHeader extends JPanel implements MouseMotionListener, ChangeListener {

	private ChartHeaderMagnifier chartValuePane = null;
	
	private Chart chartPane = null;

	private JLabel lblCursor = null;
	private Position position = null;
	
	private JLabel lblArea = null;
	private Position ul = null;
	
	public ChartHeader(Chart chart) {
		
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		this.chartPane = chart;
		this.position = chart.getPositionOrigin();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.25, 1.0, 0.25};
		setLayout(gridBagLayout);

		// ***
		
		GBC gbc_position = new GBC(0).setInsets(5,5).setFill(GBC.FILL.VERTICAL).setAnchor(GBC.ANCHOR.CENTER);
		lblCursor = new Label(position.toStringLatitude() + " \u00B7 " + position.toStringLongitude());
		lblCursor.setHorizontalAlignment(SwingConstants.CENTER);
		//lblCursor.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(lblCursor, gbc_position);

		// ***
		
		GBC gbc_panel = new GBC(1).setInsets(5,0,5,5).setFill(GBC.FILL.HORIZONTAL);
		chartValuePane = new ChartHeaderMagnifier(chart);
		add(chartValuePane, gbc_panel);

		// ***

		GBC gbc_area = new GBC(2).setInsets(5,5).setFill(GBC.FILL.VERTICAL).setAnchor(GBC.ANCHOR.CENTER);
		lblArea = new Label();
		lblArea.setHorizontalAlignment(SwingConstants.CENTER);
		//lblArea.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(lblArea, gbc_area);
		
		// ***
		
		this.position = null;
		
		apply();

		this.chartPane.addMouseMotionListener(this);
		this.chartPane.addChangeListener(this);
		
	}
	
	public void free() {
		
		this.chartPane.removeMouseMotionListener(this);
		this.chartPane.removeChangeListener(this);
		
		this.chartPane = null;
		
		remove(chartValuePane);
		chartValuePane.free();
		chartValuePane = null;
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		IPoint onSource = chartPane.convertImage(e.getPoint());
		if ((onSource!=null) && (chartPane.isCanvas(e.getPoint()))) {
			this.position = chartPane.getPosition(onSource);
		} else {
			this.position = null;
		}
		apply();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		IPoint onSource = chartPane.convertImage(e.getPoint());
		if ((onSource!=null) && (chartPane.isCanvas(e.getPoint()))) {
			this.position = chartPane.getPosition(onSource);
		} else {
			this.position = null;
		}
		apply();
	}
	
	private void apply() {
		if (this.position!=null) {
			lblCursor.setEnabled(true);
			lblCursor.setText(position.toStringLatitude() + " \u00B7 " + position.toStringLongitude());
		} else {
			lblCursor.setEnabled(false);
			//lblString.setText(" ");	
		}
		applyArea();
	}

	private void applyArea() {
		
		Position ul = chartPane.getPositionCanvas(Chart.UL);
		if (this.ul!=ul) {
			
			Position ur = chartPane.getPositionCanvas(Chart.UR);
			Position lr = chartPane.getPositionCanvas(Chart.LR);
			
			Position x0 = new Position(lr.getLatitude()+((ur.getLatitude()-lr.getLatitude())/2.0),ul.getLongitude()); 
			Position xn = new Position(x0.getLatitude(),lr.getLongitude()); 

//			lblArea.setText(width + " Nm" + " \u00B7 " + height + " Nm");	
			
			String[] width = VALUE.RANGE(x0.getRhumbLine(xn,(byte)+1).getDistance());
			String[] height = VALUE.RANGE(ur.getRhumbLine(lr,(byte)-1).getDistance());
			
			lblArea.setText(width[0] + " " + width[1]+ " \u00B7 " + height[0] + " " + height[1]);	

			this.ul = ul;
			
		}

		lblArea.setEnabled(this.position!=null);

//		ul = chartPane.getPosition(Chart.UL);
//		if (this.ul!=ul) {
//			
//			Position ur = chartPane.getPosition(Chart.UR);
//			Position lr = chartPane.getPosition(Chart.LR);
//			
//			Position x0 = new Position(lr.getLatitude()+((ur.getLatitude()-lr.getLatitude())/2.0),ul.getLongitude()); 
//			Position xn = new Position(x0.getLatitude(),lr.getLongitude()); 
//
//			System.out.println(x0.getDistanceRhumbLine(xn,+1) + " " + ur.getDistanceRhumbLine(lr,-1));
//			
//		}
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		applyArea();
	}
	
	protected class Label extends JLabel {
		
		private Dimension dim = null;

		public Label() {
			super();
			this.init();
		}
		
		public Label(String text) {
			super(text);
			this.init();
		}
		
		private void init() {
			this.dim = super.getMinimumSize();
		}
		
		private void update() {
			if (super.getPreferredSize().height>dim.height) {
				dim.height = super.getPreferredSize().height;
			}
			if (super.getPreferredSize().width>dim.width) {
				dim.width = super.getPreferredSize().width;
			}
		}
		
		public void setText(String text) {
			super.setText(text);
			if (dim!=null) update();
		}

		public Dimension getMinimumSize() {
			if (dim!=null) return dim;
			return super.getMinimumSize();
		}

		public Dimension getPreferredSize() {
			if (dim!=null) return dim;
			return super.getPreferredSize();
		}

	}
}
