/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;

public class ExceptionDialog extends BasicDialog {

	public static final TYPE INFORMATION 	= TYPE.INFORMATION;
	public static final TYPE WARNING 		= TYPE.WARNING;
	public static final TYPE ERROR 			= TYPE.ERROR;
	
    private BasicDialog self = null;
    
    private TYPE type = null;
	private Exception exception = null;
	private StackTraceElement[] stackTrace = null;

	private JPanel jPanelMain = null;
	private JTextArea jTextAreaMessage = null;
	private JScrollPane jScrollPaneMessage = null;
	
	private JPanel jPanelButtons = null;
	private JButton jButtonClose = null;
	private JButton jButtonSave = null;
	private JButton jButtonPrint = null;
	
	private static ResourceBundle rb = null;
	private Class<?> originator = null;
	
	public ExceptionDialog(
				TYPE type,
				Class<?> originator,
				Exception exception) {
		super(""+type,false,true);
		init(type,originator,exception);
	}
    
	public ExceptionDialog(
			Dialog owner,
			TYPE type,
			Class<?> originator,
			Exception exception) {
		super(owner,""+type,false,true);
		init(type,originator,exception);
	}
	
	public ExceptionDialog(
			Frame owner,
			TYPE type,
			Class<?> originator,
			Exception exception) {
		super(owner,""+type,false,true);
		init(type,originator,exception);
	}
	
	public ExceptionDialog(
			Window owner,
			TYPE type,
			Class<?> originator,
			Exception exception) {
		super(owner,""+type,false,true);
		init(type,originator,exception);
	}
	
	public ExceptionDialog(
			Container owner,
			TYPE type,
			Class<?> originator,
			Exception exception) {
		super(owner,""+type,false,true);
		init(type,originator,exception);
	}
	
	private void init(
			TYPE type,
			Class<?> originator,
			Exception exception) {
		this.self = this;
		this.type = type;
		this.exception = exception;
		Package[] p = new Package[]{};
		if (originator!=null) {
			p = new Package[]{originator.getPackage()};
			this.originator = originator;
		}
		this.stackTrace = this.findStackTrace(exception,p);
	  	rb = ResourceBundle.getBundle("resources."+
				//ExceptionDialog.class.getPackage().getName()+"."+
				ExceptionDialog.class.getSimpleName(),Locale.getDefault());
	  	switch (type) {
	  		case INFORMATION:super.setTitle(rb.getString("TITLE_INFORMATION")); break;
	  		case WARNING:super.setTitle(rb.getString("TITLE_WARNING")); break;
	  		case ERROR:super.setTitle(rb.getString("TITLE_ERROR")); break;
	  	}
	  	super.setContentPane(getJPanelMain());
	}
	
	public void addButtonSave(ActionListener listener) {
		if (listener!=null) {
			this.getJButtonSave().addActionListener(listener);
		}
	}

	public void addButtonPrint(ActionListener listener) {
		if (listener!=null) {
			this.getJButtonPrint().addActionListener(listener);
		}
	}

	public String getText() {
		return this.getJTextAreaMessage().getText();
	}
    
	private JPanel getJPanelMain() {
		if (jPanelMain == null) {
			jPanelMain = new JPanel();
			jPanelMain.setLayout(new java.awt.BorderLayout());
			jPanelMain.add(getJPanelButtons(), java.awt.BorderLayout.SOUTH);
			jPanelMain.add(getJScrollPaneMessage(), java.awt.BorderLayout.CENTER);
		}
		return jPanelMain;
	}

	private JTextArea getJTextAreaMessage() {
		if (jTextAreaMessage == null) {
			jTextAreaMessage = new JTextArea();
            jTextAreaMessage.setText(this.generateMessage(System.getProperty("line.separator"),"\t"));
            jTextAreaMessage.setBackground(this.type.getColor());
            jTextAreaMessage.setEditable(false);
            jTextAreaMessage.setCaretPosition(0);
		}
		return jTextAreaMessage;
	}

	private JScrollPane getJScrollPaneMessage() {
		if (jScrollPaneMessage == null) {
			jScrollPaneMessage = new JScrollPane();
			jScrollPaneMessage.setViewportView(getJTextAreaMessage());
		}
		return jScrollPaneMessage;
	}
	
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
 			jPanelButtons.add(getJButtonClose());
		}
		return jPanelButtons;
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(rb.getString("BUTTON_CLOSE"));
			jButtonClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					self.setVisible(false);
					self.dispose(); // Ressourcen freigeben
				}
			});
		}
		return jButtonClose;
	}

	private JButton getJButtonSave() {
	    if (jButtonSave == null) {
			jButtonSave = new JButton();
			jButtonSave.setText(rb.getString("BUTTON_SAVE"));
			getJPanelButtons().add(jButtonSave,0);
		}
		return jButtonSave;
	}

	private JButton getJButtonPrint() {
		if (jButtonPrint == null) {
			jButtonPrint = new JButton();
			jButtonPrint.setText(rb.getString("BUTTON_PRINT"));
			getJPanelButtons().add(jButtonPrint,0);
		}
		return jButtonPrint;
	}
	
    private String generateMessage(String sep, String pre) {
    	
    	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,Locale.getDefault());
    	StringBuffer sb = new StringBuffer();

		sb.append(	"[ EXCEPTION ]" + sep);
		sb.append(	sep);

    	sb.append(	"time:" + pre + df.format(new Date()).toString() + sep +
    				"originator:" + pre + this.originator.getName() + sep);		
    	sb.append(	"exception:" + pre + this.exception.getClass().getName() + sep +
					"message:" + pre + this.exception.getLocalizedMessage() + sep);

		sb.append(	(this.exception.getCause()!=null) ?
					sep +
					"[ CAUSE ]" + sep +
					sep +
					"class:" + pre + this.exception.getCause().getClass() + sep +
					"message:" + pre + this.exception.getCause().getLocalizedMessage() + sep : "");
    	
		sb.append(	sep);
		sb.append(	"[ STACK TRACE ]" + sep);
		
    	for (int i=stackTrace.length-1; i>=0; i--) {
        	sb.append(
        			sep +
    				"file:" + pre + ((stackTrace[i]!=null) ? stackTrace[i].getFileName() : "-") + sep + 
    				"class:" + pre + ((stackTrace[i]!=null) ? stackTrace[i].getClassName() : "-") + sep +
    				"method:" + pre + ((stackTrace[i]!=null) ? stackTrace[i].getMethodName() : "-") + sep +
    				"line:" + pre + ((stackTrace[i]!=null) ? stackTrace[i].getLineNumber() : "-") + sep);
    	}
    	return sb.toString();
    }
    
    private StackTraceElement[] findStackTrace(Exception throwable, Package[] packages) {
    	ArrayList<StackTraceElement> temp = new ArrayList<StackTraceElement>();
    	StackTraceElement[] elems = throwable.getStackTrace();
    	boolean stop = false;
    	for (int i=0; i<elems.length; i++) {
    		temp.add(elems[i]);
            for (int j=0; j<packages.length; j++) {
            	 if (elems[i].getClassName().startsWith(packages[j].getName())) {
            		 stop = true;
            		 break;
            	 }
            }    	
    		if (stop) break;
        }
        return (StackTraceElement[])temp.toArray(new StackTraceElement[temp.size()]);
    }

	private enum TYPE {
		
		INFORMATION	(new Color(255,255,255)),
		WARNING		(new Color(255,255,210)),
		ERROR		(new Color(255,210,210));
		
		private Color color = null;
		
		TYPE(Color color) {
			this.color = color;
		}
		
		public Color getColor() {
			return this.color;
		}
				
	}

}