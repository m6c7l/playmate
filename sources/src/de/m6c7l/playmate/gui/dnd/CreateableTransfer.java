/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui.dnd;

import java.awt.datatransfer.*;

public class CreateableTransfer implements Transferable {
	
	public static DataFlavor createable = new DataFlavor(Createable.class,Createable.class.getName());  
	
	private Createable cr = null;
	
	public CreateableTransfer(Createable createable) {
		this.cr = createable;
	}
	
	// Returns an object which represents the data to be transferred.
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.equals(createable)) return cr;
		throw new UnsupportedFlavorException(flavor);
	}

	// Returns an array of DataFlavor objects indicating the flavors the data can be provided in.
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { createable };
	}

	// Returns whether or not the specified data flavor is supported for this object.
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(createable);
	}

}
