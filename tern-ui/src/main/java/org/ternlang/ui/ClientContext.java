package org.ternlang.ui;

import java.awt.*;
import java.io.File;
import java.net.URI;

public class ClientContext {

	public static final String ICON_PATH = "/icon/icon-large.png";
	public static final String HOME_FOLDER = ".cef";

	private File cachePath;
	private File logFile;
	private String folder; // home folder
	private String title;
	private String address;
	private String icon;
	private int width;
	private int height;
	private boolean debug;
	private String[] arguments;

	public File getCachePath() {
		return cachePath;
	}

	public ClientContext setCachePath(File cachePath) {
		this.cachePath = cachePath;
		return this;
	}

	public File getLogFile() {
		return logFile;
	}

	public ClientContext setLogFile(File logFile) {
		this.logFile = logFile;
		return this;
	}

	public ClientContext setFolder(String folder) {
		this.folder = folder;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ClientContext setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public ClientContext setAddress(String address) {
		this.address = address;
		return this;
	}

	public ClientContext setWidth(int width) {
		this.width = width;
		return this;
	}

	public ClientContext setHeight(int height) {
		this.height = height;
		return this;
	}

	public ClientContext setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public boolean isDebug() {
		return debug;
	}

	public ClientContext setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

	public ClientContext setArguments(String[] arguments) {
		this.arguments = arguments;
		return this;
	}

	public int getWidth() {
		if(width <= 0) {
			try {
				return Toolkit.getDefaultToolkit().getScreenSize().width / 2;
			} catch(Exception e) {
				return 800;
			}
		}
		return width;
	}

	public int getHeight() {
		if(height <= 0) {
			try {
				return Toolkit.getDefaultToolkit().getScreenSize().height / 2;
			} catch(Exception e) {
				return 600;
			}
		}
		return height;
	}
	
	public String[] getArguments(){
		return arguments != null ? arguments : new String[]{};
	}

	public String getIcon() {
		return icon != null ? icon : ICON_PATH;
	}

	public String getFolder() {
		return folder != null ? folder : HOME_FOLDER;
	}

	public void validate() {
		String address = getAddress();
		String title = getTitle();
		File logFile = getLogFile();
		File cachePath = getCachePath();

		if(title == null) {
			throw new IllegalStateException("Title is required");
		}
		if(address == null) {
			throw new IllegalStateException("Address is required");
		}
		try {
			URI.create(address);
		} catch(Exception e) {
			throw new IllegalStateException("Address " + address + " is invalid");
		}
		if(cachePath != null) {
			cachePath.mkdirs();
		}
		if(logFile != null) {
			logFile.getParentFile().mkdirs();
		}
	}
}
