/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.gui.dialog;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;

public class FileChooser extends JFileChooser {

	private ResourceBundle rb = null;
    private String textFileExists = null;
	private Object[] buttonText = null;
    
	public static DIALOG SAVE = DIALOG.SAVE;
	public static DIALOG OPEN = DIALOG.OPEN;
	
	public static OPTION APPROVE = OPTION.APPROVE;
	public static OPTION CANCEL = OPTION.CANCEL;
	public static OPTION NONE = OPTION.NONE;
	
	public static SELECTION FILES_ONLY = SELECTION.FILES_ONLY;
	public static SELECTION DIRECTORIES_ONLY = SELECTION.DIRECTORIES_ONLY;
	public static SELECTION FILES_AND_DIRECTORIES = SELECTION.FILES_AND_DIRECTORIES;
       
	private JDialog dialog = null;
	private OPTION dialogOption = null;
    
    private boolean fileNameEditable = true;
    private String fileNameDefault = null;
    
    public FileChooser(File current) {
    	super(current);
    	this.init();
    }
    
    private void init() {
 	   	rb = ResourceBundle.getBundle("resources."+
				//FileChooser.class.getPackage().getName()+"."+
				FileChooser.class.getSimpleName(),Locale.getDefault());
 	   	textFileExists = rb.getString("TEXT_FILE_EXISTS");
 	   	buttonText = new Object[] {rb.getString("BUTTON_SAVE_YES"),rb.getString("BUTTON_SAVE_NO")};
    }
    
    public void setDialogType(DIALOG type) {
    	super.setDialogType(type.value);
    }
    
    public void setFileSelectionMode(SELECTION selection) {
    	super.setFileSelectionMode(selection.value);
    }
    
    public FileFilter addChoosableFileFilter(String extension, String description) {
    	ChooseableFileFilter filter = new ChooseableFileFilter(extension,description);
    	super.addChoosableFileFilter(filter);
    	return filter;
    }
    
    @Override
    public int showDialog(Component parent, String approveButtonText) {
    	if (parent instanceof Container) {
    		return this.showDialog((Container)parent,approveButtonText).value;
    	}
		return NONE.value;
    }
    
    public OPTION showOpenDialog(Container parent) {
    	int option = super.showOpenDialog(parent);
    	switch (option) {
    		case JFileChooser.APPROVE_OPTION: return APPROVE;
    		case JFileChooser.CANCEL_OPTION: return CANCEL;
    	}
    	return NONE;
    }
    
    public OPTION showSaveDialog(Container parent) {
    	int option = super.showSaveDialog(parent);
    	switch (option) {
    		case JFileChooser.APPROVE_OPTION: return APPROVE;
    		case JFileChooser.CANCEL_OPTION: return CANCEL;
    	}
    	return NONE;
    }

    public OPTION showDialog(Container parent) {
    	return this.showDialog(parent,null);
    }
    
    public OPTION showDialog(Container parent, String approveButtonText) {

    	if (isAcceptAllFileFilterUsed()) {
        	this.setFileFilter(this.getAcceptAllFileFilter());    		
    	} else {
    		if (super.getChoosableFileFilters().length>0) {
            	this.setFileFilter(super.getChoosableFileFilters()[0]);
    		}
    	}
    	
    	if (approveButtonText!=null) {
    		this.setApproveButtonText(approveButtonText);
    		this.setDialogType(JFileChooser.CUSTOM_DIALOG);
    	}
    	
    	dialog = super.createDialog(parent);
    	dialog.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			dialogOption = CANCEL;
    		}
    	});	
    	
    	applyFileNameDefault();
    	applyFileNameEditable();
    	
		dialogOption = NONE;
		
    	rescanCurrentDirectory();
    	
    	dialog.setVisible(true);
    	dialog.dispose();
    	dialog = null;

    	return dialogOption;
    	
    }
    
    @Override
    public void cancelSelection() {
    	this.setSelectedFile(null);
    	this.setSelectedFiles(new File[] {});
    	dialogOption = CANCEL;
    	if (dialog!=null) dialog.setVisible(false);
    	fireActionPerformed(CANCEL_SELECTION);
    }
    
    @Override
    public void approveSelection() {
    	if (getDialogType()==DIALOG.SAVE.value) {
    		if (this.approveSelectionSave()) {
            	this.approveSelectionFinal();
    		}
    	} else {
    		this.approveSelectionFinal();
    	}
    }

    private void approveSelectionFinal() {
    	dialogOption = APPROVE;
        if (dialog!=null) dialog.setVisible(false);
    	fireActionPerformed(APPROVE_SELECTION);    	
    }
    
    private boolean approveSelectionSave() {
        File file = this.getSelectedFile();                      
        if (file!=null) {
            if (file.exists()) {                                   
                int option = JOptionPane.showOptionDialog(
                		this,
                		textFileExists,
                		null,
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        buttonText,
                        buttonText[1]);                          
                if (option == JOptionPane.YES_OPTION) { 
                	return true;
                }                
                return false;
            }
            return true;
        }
        return false;
    }

    public String getFileNameDefault() {
        return this.fileNameDefault;
    }

    public void setFileNameDefault(String fileNameDefault) {
        this.fileNameDefault = fileNameDefault;
    }
    
    private void applyFileNameDefault() {
    	if (dialog!=null) {
            Object[] comp  = dialog.getContentPane().getComponents();
            JTextField txtFileName = getFirstTextField(comp);
            if (txtFileName != null) {
                txtFileName.setText(this.fileNameDefault);
            }
    	}
    }

    public boolean isFileNameEditable() {
    	return this.fileNameEditable;
    }
    
    public void setFileNameEditable(boolean fileNameEditable) {
    	this.fileNameEditable = fileNameEditable;
    }
    
    private void applyFileNameEditable() {
    	if (dialog!=null) {
            Object[] comp  = dialog.getContentPane().getComponents(); 
            JTextField txtFileName = getFirstTextField(comp);         
            if (txtFileName != null) {
                txtFileName.setEditable(this.fileNameEditable);         
            }
    	}
    }
     
    private JTextField getFirstTextField(Object[] elements) {
        int i=0;
        boolean found = false;                          
        JTextField first = null;                         
        while ((i < elements.length) && !found) {                             
            Object comp = elements[i];                 
            if (comp instanceof JTextField) { 
                found = true;
                first = (JTextField) comp;
            }
            if ((comp instanceof JPanel || comp instanceof JFileChooser) && !found) {
                first = getFirstTextField(((Container)elements[i]).getComponents());
                if (first!=null) found = true;
            }
            i++;
        }
        return first;
	}

    private static class ChooseableFileFilter extends FileFilter {

    	private String extension = null;
    	private String description = null;
    	
    	public ChooseableFileFilter(String extension, String description) {
    		this.extension = extension;
    		this.description = description;
    	}
    	
        public boolean accept(File f) {
        	return (f.isDirectory()) || ((f.isFile()) && (f.getName().toLowerCase().endsWith(this.extension.toLowerCase())));
        }

        public String getDescription() {
            return this.description;
        }

    }
    
    private enum OPTION {

    	APPROVE					(JFileChooser.APPROVE_OPTION),
        CANCEL					(JFileChooser.CANCEL_OPTION),
        NONE					(JFileChooser.ERROR_OPTION);        
    	
    	private int value = 0;
    	
    	private OPTION(int value) {
    		this.value = value;
    	}

    }
    
    private enum SELECTION {

    	FILES_ONLY				(JFileChooser.FILES_ONLY),
    	DIRECTORIES_ONLY		(JFileChooser.DIRECTORIES_ONLY),
    	FILES_AND_DIRECTORIES	(JFileChooser.FILES_AND_DIRECTORIES);
    	
    	private int value = 0;
    	
    	private SELECTION(int value) {
    		this.value = value;
    	}

    }
    
    private enum DIALOG {

    	SAVE					(JFileChooser.SAVE_DIALOG),
    	OPEN					(JFileChooser.OPEN_DIALOG);
    	
    	private int value = 0;
    	
    	private DIALOG(int value) {
    		this.value = value;
    	}

    }
    

}