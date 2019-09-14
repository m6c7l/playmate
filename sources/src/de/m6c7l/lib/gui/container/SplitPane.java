/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.container;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class SplitPane extends JSplitPane{
	
	public SplitPane(int orientation) {
		super(orientation);
		init();
	}

	public SplitPane() {
		super();
		init();
	}

	public void setLeftComponent(JComponent c) {
		super.setLeftComponent(new SplitContainer(this,c));
	}

	private void init() { }
	
	private static class SplitContainer extends JComponent {

		private SplitPane split = null;		
		private JComponent comp = null;		

		private boolean hidden = false;
		private boolean fadein = false;

		public SplitContainer(SplitPane sp, JComponent c) {
			
			this.split = sp;
			this.comp = c;
			
			super.setLayout(new BorderLayout());
			super.add(comp,BorderLayout.CENTER);			
				
			this.addComponentListener(new ComponentListener() {
				public void componentResized(ComponentEvent e) {
					if (hidden) {
						e.getComponent().setVisible(false);
					}
				}
				public void componentShown(ComponentEvent e) {}
				public void componentMoved(ComponentEvent e) {}
				public void componentHidden(ComponentEvent e) {
					e.getComponent().setVisible(true);
				}
			});
			
		}
		
		public void setBounds(Rectangle r) {
			this.setBounds(r.x,r.y,r.width,r.height);
		}
		
		public void setBounds(int x, int y, int width, int height) {
			if ((!hidden) && (width<this.getMinimumSize().width)) {
				fadein = true;
			} else if ((!hidden) && (width==this.getMinimumSize().width)) {
				hidden = !fadein;
				fadein = false;
			} else if ((hidden) && (width>this.getMinimumSize().width)) {
				hidden = false;
				fadein = true;
			}
			super.setBounds(x,y,width,height);
		}
		
		public Dimension getPreferredSize() {
			if (fadein) return mod(super.getPreferredSize(),1);
			if (hidden) return mod(super.getPreferredSize(),0.0);
			return super.getPreferredSize();
		}

		public Dimension getMinimumSize() {
			if ((!hidden) && (!fadein)) return mod(super.getPreferredSize(),0.618);
			return this.getPreferredSize();
		}
		
		private Dimension mod(Dimension d, double f) {
			if (split.getOrientation()==SplitPane.HORIZONTAL_SPLIT) {
				return new Dimension((int)(d.width*f),d.height);
			} else if (split.getOrientation()==SplitPane.VERTICAL_SPLIT) {
				return new Dimension(d.width,(int)(d.height*f));					
			}
			return new Dimension(0,0);
		}
		
		private Dimension mod(Dimension d, int x) {
			if (split.getOrientation()==SplitPane.HORIZONTAL_SPLIT) {
				return new Dimension(d.width+x,d.height);
			} else if (split.getOrientation()==SplitPane.VERTICAL_SPLIT) {
				return new Dimension(d.width,d.height+x);					
			}
			return new Dimension(0,0);
		}

//		private short compare(Dimension a, Dimension b) {
//			if (split.getOrientation()==SplitPane.HORIZONTAL_SPLIT) {
//				if (a.width<b.width) return -1; else if (a.width>b.width) return +1; 
//			} else if (split.getOrientation()==SplitPane.VERTICAL_SPLIT) {
//				if (a.height<b.height) return -1; else if (a.height>b.height) return +1; 
//			}
//			return 0;
//		}
		
	}
	
}