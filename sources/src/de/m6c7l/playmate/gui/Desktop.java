/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;

public class Desktop extends JDesktopPane {

	private BufferedImage img = null;
	
	public Desktop() {
		super();
		this.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		this.setBackground(new JPanel().getBackground());
	}
	
	public Desktop(URL url) throws IOException {
		this();
        img = ImageIO.read(url);
	}

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        if (img==null) return;
        grphcs.drawImage(
        		img,
        		getWidth()/2-img.getWidth()/2,
        		getHeight()/2-img.getHeight()/2,
        		null);
    }

    @Override
    public Dimension getPreferredSize() {
    	if (img==null) return super.getPreferredSize();
        return new Dimension(
        		img.getWidth(),
        		img.getHeight());
    }
    
}
