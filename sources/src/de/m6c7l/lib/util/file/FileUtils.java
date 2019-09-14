/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class FileUtils {
	
	private FileUtils() {}
	
	public static URI toURI(URL url) throws MalformedURLException, URISyntaxException {
		URI uri = url.toURI();
        if (uri.getAuthority()!=null && uri.getAuthority().length()>0) { // Hack for Windows UNC Path
            uri = (new URL("file://" + url.toString().substring("file:".length()))).toURI();
        }
		return uri;
	}
	
	public static ArrayList<File> getDirectories(File directory, boolean includeSubdirectories) {
		File[] files = directory.listFiles();
		ArrayList<File> matches = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					matches.add(files[i]);
					if (includeSubdirectories) {
						matches.addAll(getDirectories(files[i],includeSubdirectories));
					}
				}
			}
		}
		return matches;
	}
	
	public static ArrayList<File> getFiles(File directory, boolean includeSubdirectories) {
		File[] files = directory.listFiles();
		ArrayList<File> matches = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (includeSubdirectories) {
						matches.addAll(getFiles(files[i],includeSubdirectories));						
					}
				} else {
					matches.add(files[i]);
				}
			}
		}
		return matches;
	}
	
	public static ArrayList<File> searchDirectory(File directory, String regex, boolean includeSubdirectories) {
		File[] files = directory.listFiles();
		ArrayList<File> matches = new ArrayList<File> ();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (files[i].getName().matches(regex)) {
						matches.add(files[i]);
					}
					if (includeSubdirectories) {
						matches.addAll(searchDirectory(files[i], regex, includeSubdirectories));
					}
				}
			}
		}
		return matches;
	}

	public static ArrayList<File> searchFile(File directory, String regex, boolean includeSubdirectories) {
		File[] files = directory.listFiles();
		ArrayList<File> matches = new ArrayList<File> ();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (includeSubdirectories) {
						matches.addAll(searchFile(files[i], regex, includeSubdirectories));
					}
				} else {
					if (files[i].getName().matches(regex)) {
						matches.add(files[i]);
					}
				}
			}
		}
		return matches;
	}
	
	public static boolean deleteDirectory(File directory) {
		File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]); 
				} else {
					if (!files[i].delete()) return false;
				}
			}
			if (!directory.delete()) return false;
		}
		return true;
	}
	
	public static void copyDirectory(File source, File target) throws IOException {
		File[] files = source.listFiles();
		File newFile = null; 
		target.mkdirs();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
					newFile = new File(target.getAbsolutePath() +
							System.getProperty("file.separator") +
							files[i].getName());
				if (files[i].isDirectory()) {
					copyDirectory(files[i], newFile);
				}
				else {
					copyFile(files[i], newFile);
				}
			}
		}
	}
	
	public static void copyFile(File source, File target) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target,false));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes);
		}
		in.close();
		out.close();
	}
	
	public static long getDirectorySize(File directory, boolean includeSubdirectories) {
		long size = 0;
		File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (includeSubdirectories) {
						size += getDirectorySize(files[i],includeSubdirectories);
					}
				}
				else {
					size += files[i].length();
				}
			}
		}
		return size;
	}
	
	public static long getDirectoryCount(File directory, boolean includeSubdirectories) {
		long count = 0;
		File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					count++;
					if (includeSubdirectories) {
						count += getDirectoryCount(files[i],includeSubdirectories);
					}
				}
			}
		}
		return count;
	}
	
	public static long getFileCount(File directory, boolean includeSubdirectories) {
		long count = 0;
		File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (includeSubdirectories) {
						count += getFileCount(files[i],includeSubdirectories);
					}
				} else {
					count++;
				}
			}
		}
		return count;
	}

	public static String toString(File f) throws IOException {
		byte[] buffer = new byte[ (int) f.length() ];
		InputStream is = new FileInputStream( f );
		is.read(buffer);
		is.close();
		byte[] str = new byte[buffer.length];
		int c = 0;
		for (int i=0; i<buffer.length; i++) {
			if (buffer[i]>0) {
				str[c] = buffer[i];
				c++;
			}
		}
		return new String(str);
	}
	
}
