/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Locale;
import java.util.ResourceBundle;

public class TabbedDialog extends BasicDialog {

	private static ResourceBundle rb = null;
	
	private JPanel content = null;
	private JPanel bottom = null;
	private JPanel buttons = null;
	private JPanel additional = null;
	
	private JButton buttonClose = null;
	private JButton buttonUpdate = null;
	
	private TabbedPane comp = null;
	
	public TabbedDialog(Container parent, String title) {
		super(parent, title, true, false);
		Dimension d = super.getPreferredSize();
		super.setPreferredSize(new Dimension((int)(d.width*1.25),(int)(d.height*1.25)));
		comp = new TabbedPane(this,null);
		initialize();
	}
	
	private void initialize() {
		rb = ResourceBundle.getBundle("resources."+
				//TabbedPane.class.getPackage().getName()+"."+
				TabbedDialog.class.getSimpleName(),Locale.getDefault());
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setContentPane(getContent());
		this.setAlwaysOnTop(false);
	}
	
	public void dispose() {
		comp.free();
		comp = null;
		super.dispose();
	}
	
	public void clear() {
		TabbedPane tabs = comp;
		for (int i=0; i<tabs.getComponentCount(); i++) {
			tabs.free();
		}
	}
	
	public void addTab(String[] titles, String title, Object object) {
		TabbedPane tabs = comp;
		for (int i=0; i<titles.length; i++) {
			TabbedPane t = tabs.getTab(titles[i]);
			tabs.addTab(t);
			tabs = t;
		}
		TabPane tab = new TabPane(title,object);
		tabs.addTab(tab);
	}
	
	private JPanel getContent() {
		if (content == null) {
			content = new JPanel();
			content.setLayout(new BorderLayout());
			content.add(getBottom(), BorderLayout.SOUTH);
			content.add(comp, BorderLayout.CENTER);
		}
		return content;
	}
	
	private JPanel getBottom() {
		if (bottom == null) {
			bottom = new JPanel();
			bottom.setLayout(new BorderLayout());
			bottom.add(getButtons(), BorderLayout.EAST);
			bottom.add(getAdditional(), BorderLayout.CENTER);
		}
		return bottom;
	}
	
	private JPanel getAdditional() {
		if (additional == null) {
			additional = new JPanel();
			additional.setLayout(new GridBagLayout());
		}
		return additional;
	}

	private JPanel getButtons() {
		if (buttons == null) {
			
			buttons = new JPanel();
			buttons.setLayout(new GridBagLayout());
			
			GridBagConstraints gridBagConstraintsButton1 = new GridBagConstraints();
			gridBagConstraintsButton1.gridx = 2;
			gridBagConstraintsButton1.insets = new Insets(10,5,10,10);
			gridBagConstraintsButton1.fill = GridBagConstraints.BOTH;
			gridBagConstraintsButton1.anchor = GridBagConstraints.WEST;
			gridBagConstraintsButton1.weighty = 0;
			gridBagConstraintsButton1.gridy = 0;

			buttons.add(getButtonClose(),gridBagConstraintsButton1);
			
			GridBagConstraints gridBagConstraintsButton2 = new GridBagConstraints();
			gridBagConstraintsButton2.gridx = 1;
			gridBagConstraintsButton2.insets = new Insets(10,5,10,5);
			gridBagConstraintsButton2.fill = GridBagConstraints.BOTH;
			gridBagConstraintsButton2.anchor = GridBagConstraints.WEST;
			gridBagConstraintsButton2.weighty = 0;
			gridBagConstraintsButton2.gridy = 0;

			buttons.add(getButtonRefresh(),gridBagConstraintsButton2);
			
		}
		return buttons;
	}
	
	private JButton getButtonClose() {
		if (buttonClose == null) {
			buttonClose = new JButton();
			buttonClose.setText(rb.getString("BUTTON_CLOSE"));
			buttonClose.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return buttonClose;
	}
	
	private JButton getButtonRefresh() {
		if (buttonUpdate == null) {
			buttonUpdate = new JButton();
			buttonUpdate.setText(rb.getString("BUTTON_UPDATE"));
			buttonUpdate.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					buttonUpdate.setEnabled(false);
					TabPane p = getSelectedTab();
					if (p!=null) {
						p.refresh();
					}
					buttonUpdate.setEnabled(true);
				}
			});
		}
		return buttonUpdate;
	}
	
	private TabPane getSelectedTab() {
		Component c = comp.getSelectedComponent();
		while ((c!=null) && (c.isVisible()) && (!(c instanceof TabPane)))
			c = ((TabbedPane)c).getSelectedComponent();
		if ((c!=null) && (c instanceof TabPane))
			return (TabPane)c;
		return null;
	}
	
	private static class TabbedPane extends JTabbedPane {

		private TabbedPane parent = null;
		private String title = null;
		private TabbedDialog dialog = null;
		
		public TabbedPane(TabbedDialog dd, String title) {
			super(JTabbedPane.TOP);
			this.dialog = dd;
			this.title = title;
			this.addComponentListener(new ComponentListener() {
				@Override
				public void componentShown(ComponentEvent arg0) {
//					if (resources!=null) {
//						TabPane pane = resources.getSelectedTab();
//						if (pane!=null) {
//							pane.refresh();					
//						}
//					}
				}
				@Override
				public void componentResized(ComponentEvent arg0) {}
				@Override
				public void componentMoved(ComponentEvent arg0) {}
				@Override
				public void componentHidden(ComponentEvent arg0) {}
			});
		}
		
		public TabbedPane getTab(String title) {
			TabbedPane temp = null;
			int index = this.indexOfTab(title);	
			if ((index>-1) && (this.getComponentAt(index) instanceof TabbedPane)) {
				temp = (TabbedPane)this.getComponentAt(index);
			} else {
				temp = new TabbedPane(dialog,title);
			}
			return temp;
		}
		
		public void addTab(TabPane component) {
			if (super.indexOfTab(component.title)==-1) {
				super.add(component.title, component);
				component.parent = this;				
			}
		}
		
		public void addTab(TabbedPane component) {
			if (super.indexOfTab(component.title)==-1) {
				super.add(component.title, component);
				component.parent = this;				
			}
		}

		public void free() {
			this.dialog = null;
			this.parent = null;
			while (this.getTabCount()>0) {
				if (this.getComponentAt(0) instanceof TabbedPane) {
					((TabbedPane)this.getComponentAt(0)).free();
				} else if (this.getComponentAt(0) instanceof TabPane) {
					((TabPane)this.getComponentAt(0)).free();
				}
				this.remove(0);
			}
		}
		
		public boolean equals(Object o) {
			if ((o!=null) && (o instanceof TabbedPane) && ((TabbedPane)o).toString()!=null) {
				return ((TabbedPane)o).toString().equals(this.toString());
			}
			return false;
		}
		
		public String toString() {
			String s = this.getTitle();
			TabbedPane tabbed = getAncestor();
			while ((tabbed!=null) && (tabbed.getTitle()!=null)) {
				s = tabbed.getTitle() + "." + s;
				tabbed = tabbed.getAncestor();
			}
			return s;
		}
		
		public String getTitle() {
			return this.title;
		}
		
		public TabbedPane getAncestor() {
			return parent;
		}
		
	}
	
	private static class TabPane extends JPanel {
		
		private static final String SEP = System.getProperty("line.separator");
		
		private TabbedPane parent = null;
		private String title = null;
		
		private JTextArea textPane = null;
		private Object object = null;
		
		private JScrollPane scrollPane = null;
		
		private int vPos = 0;
		private int hPos = 0;

		public TabPane(String title, Object object) {
			
			this.title = title;
			this.object = object;
			
			setLayout(new BorderLayout());
			scrollPane = new JScrollPane();
			add(scrollPane, BorderLayout.CENTER);
			
			textPane = new JTextArea();
			textPane.setFont(new Font(Font.MONOSPACED,textPane.getFont().getStyle(),textPane.getFont().getSize()));
			textPane.setLineWrap(false);
			
			textPane.setEditable(false);
			textPane.setFocusable(false);
			textPane.setDoubleBuffered(true);
			
			scrollPane.setViewportView(textPane);
			textPane.setBackground(SystemColor.text);
			
		    AdjustmentListener adjListener = new AdjustmentListener() {  
		        public void adjustmentValueChanged(AdjustmentEvent e) {
		        	vPos = scrollPane.getVerticalScrollBar().getValue();
		        	hPos = scrollPane.getHorizontalScrollBar().getValue();
		        }  
		    };  
		    
		    scrollPane.getVerticalScrollBar().addAdjustmentListener(adjListener);
		    scrollPane.getHorizontalScrollBar().addAdjustmentListener(adjListener);
	
		    this.addComponentListener(new ComponentListener() {
				@Override
				public void componentShown(ComponentEvent arg0) {
//					refresh();
				}
				@Override
				public void componentResized(ComponentEvent arg0) {}
				@Override
				public void componentMoved(ComponentEvent arg0) {}
				@Override
				public void componentHidden(ComponentEvent arg0) {}
			});

		    refresh();
		    
		}
		
		public void free() {
			this.parent = null;
			this.object = null;
		}
		
		public void refresh() {
			if (object==null) return;
			new Runnable() {
				public void run() {
					int v = vPos;
					int h = hPos;
					textPane.setText(SEP + object.toString() + SEP);
					scroll(v,h);
				}
			}.run();
		}
		
		public boolean equals(Object o) {
			if ((o!=null) && (o instanceof TabPane) && ((TabPane)o).toString()!=null) {
				return ((TabPane)o).toString().equals(this.toString());
			}
			return false;
		}
		
		public String toString() {
			String s = this.getTitle();
			TabbedPane tabbed = getAncestor();
			while ((tabbed!=null) && (tabbed.getTitle()!=null)) {
				s = tabbed.getTitle() + "." + s;
				tabbed = tabbed.getAncestor();
			}
			return s;
		}

		public String getTitle() {
			return this.title;
		}
		
		public TabbedPane getAncestor() {
			return parent;
		}
		
	    private void scroll(final int vp, final int hp) {
	    	scrollPane.setVisible(false);
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                scrollPane.getVerticalScrollBar().setValue(vp);
	                scrollPane.getHorizontalScrollBar().setValue(hp);
	    	    	scrollPane.setVisible(true);
	            }
	        });
	    }
	    
	}
    
//	private static class TabbedDialogStyleContext extends StyleContext {
//
//		/*****************************************************************************
//		 * Constants
//		 *****************************************************************************/
//		/**
//		 * Attribute key to reference the default font according to Sun source code.
//		 * Used in javax.swing.text.StyleContext.getFont() to resolve the default
//		 * font.
//		 * <p>
//		 * This turns out not to be the case. This constant is no longer used by
//		 * getFont() if it ever was. We maintain it anyway.
//		 */
//		protected static final String FONT_ATTRIBUTE_KEY = "FONT_ATTRIBUTE_KEY";
//
//		/*****************************************************************************
//		 * Properties
//		 *****************************************************************************/
//		/** Default Style Attributes */
//		protected SimpleAttributeSet m_Default;
//		/** Restoring Defaults Flag */
//		protected boolean m_Restoring;
//
//		/*****************************************************************************
//		 * Constructor
//		 */
//		/**
//		 * Instantiates the StyleContext. Build the Default Style Attributes and
//		 * adds the ChangeListener to the default style.
//		 *****************************************************************************/
//		public TabbedDialogStyleContext() {
//
//			super();
//
//			Style s = getStyle(StyleContext.DEFAULT_STYLE);
//
//			/*--------------------------------------*/
//			/* Create the default style attributes. */
//			/*--------------------------------------*/
//			m_Default = new SimpleAttributeSet(s);
//
//			StyleConstants.setFontFamily(m_Default, "Courier");
//			StyleConstants.setFontSize(m_Default, 14);
//			StyleConstants.setBold(m_Default, true);
//			StyleConstants.setItalic(m_Default, false);
//			StyleConstants.setForeground(m_Default, Color.black);
//			m_Default.addAttribute(FONT_ATTRIBUTE_KEY, new FontUIResource(
//					getFont(m_Default)));
//
//			/*-----------------------------------------------------*/
//			/* Add the default attributes and the change listener. */
//			/*-----------------------------------------------------*/
//			s.addAttributes(m_Default);
//			s.addChangeListener(new ChangeListener() {
//				public void stateChanged(ChangeEvent e) {
//					if (m_Restoring)
//						return;
//
//					Style s = (Style) e.getSource();
//
//					synchronized (s) {
//						m_Restoring = true;
//						s.removeAttributes(s);
//						s.addAttributes(m_Default);
//						m_Restoring = false;
//					}
//				}
//			});
//		}
//
////		/*****************************************************************************
////		 * updateDefault
////		 */
////		/**
////		 * Updates the default style with an attribute set.
////		 * 
////		 * @param as
////		 *            Attribute Set
////		 *****************************************************************************/
////		public void updateDefault(AttributeSet as) {
////			m_Default.addAttributes(as);
////			getStyle(StyleContext.DEFAULT_STYLE).addAttributes(m_Default);
////		}
//
//	}
	
}
