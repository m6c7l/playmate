/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.playmate.main;

import java.io.File;
import java.io.IOException;

public interface Saveable {

	public boolean save(File file) throws IOException;
	
}
