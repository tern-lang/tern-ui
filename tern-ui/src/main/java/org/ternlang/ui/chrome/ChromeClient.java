package org.ternlang.ui.chrome;

import org.ternlang.ui.*;
import org.ternlang.ui.chrome.load.LibraryLoader;
import org.ternlang.ui.chrome.ui.BrowserFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class ChromeClient implements Client {

	public ClientControl show(ClientContext context) {
		int width = context.getWidth();
		int height = context.getHeight();
		String folder = context.getFolder();
		String address = context.getTarget();
		String title = context.getTitle();
		String path = context.getIcon();
		File logFile = context.getLogFile();
		File cachePath = context.getCachePath();
		URI target = URI.create(address);

		try {
		   LibraryLoader.loadFrom(folder);
		} catch(Throwable e) {
		   System.err.println("Error loading library from " + folder);
		   e.printStackTrace();
		}
		WindowIcon icon = WindowIconLoader.loadIcon(path);
		String[] arguments = context.getArguments();
		ChromeFrame frame = ChromeFrame.createChromeFrame(
				target,
				logFile,
				cachePath,
				folder,
				null,
				false,
				false,
				false,
				arguments);

		frame.setTitle(title);
		frame.setSize(width, height);
		frame.setVisible(true);

		if (icon != null) {
			URL resource = icon.getResource();
			Image image = Toolkit.getDefaultToolkit().getImage(resource);

			frame.setIconImage(image);
		}
		if(context.isDebug()) {
			SwingUtilities.invokeLater(() -> frame.showDevTools());
		}
		return new ClientControl() {
			@Override
			public void registerListener(ClientCloseListener listener) {
				frame.addCloseListener(listener);
			}
			@Override
			public void showDebugger() {
				SwingUtilities.invokeLater(() -> frame.showDevTools());
			}
		};
	}

}
