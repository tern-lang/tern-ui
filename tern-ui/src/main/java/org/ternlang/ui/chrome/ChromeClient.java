package org.ternlang.ui.chrome;

import org.ternlang.ui.Client;
import org.ternlang.ui.ClientCloseListener;
import org.ternlang.ui.ClientContext;
import org.ternlang.ui.ClientControl;
import org.ternlang.ui.WindowIcon;
import org.ternlang.ui.WindowIconLoader;
import org.ternlang.ui.chrome.load.LibraryLoader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChromeClient implements Client {

	private final ScheduledThreadPoolExecutor executor;

	public ChromeClient() {
		this.executor = new ScheduledThreadPoolExecutor(2);
	}

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
		} catch (Throwable e) {
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		reload(frame);

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

	public void reload(ChromeFrame frame) {
		executor.schedule(() -> {
			try {
				SwingUtilities.invokeLater(() -> {
					frame.getBrowser().reloadIgnoreCache();
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 5, TimeUnit.SECONDS);
	}

}
