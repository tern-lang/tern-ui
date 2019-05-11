package org.ternlang.ui.chrome;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;

import org.ternlang.ui.Client;
import org.ternlang.ui.ClientCloseListener;
import org.ternlang.ui.ClientContext;
import org.ternlang.ui.ClientControl;
import org.ternlang.ui.WindowIcon;
import org.ternlang.ui.WindowIconLoader;
import org.ternlang.ui.chrome.load.LibraryLoader;

public class ChromeClient implements Client {

	public ChromeClient() {
		super();
	}

	public ClientControl create(ClientContext context) {
		String folder = context.getFolder();
		String address = context.getAddress();
		File logFile = context.getLogFile();
		File cachePath = context.getCachePath();
		URI target = URI.create(address);

		if(!LibraryLoader.isLibraryDeployed(folder)) {
			throw new IllegalStateException("Client library not deployed to " + folder);
		}
		String[] arguments = context.getArguments();
		ChromeFrame frame = ChromeFrame.createChromeFrame(
				target,
				logFile,
				cachePath,
				null,
				false,
				false,
				false,
				arguments);

		return create(context, frame);
	}

	private ClientControl create(ClientContext context, ChromeFrame frame) {
		int width = context.getWidth();
		int height = context.getHeight();
		String path = context.getIcon();
		String title = context.getTitle();
		WindowIcon icon = WindowIconLoader.loadIcon(path);

		frame.setTitle(title);
		frame.setSize(width, height);

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
			public void closeOnExit(boolean close) {
				frame.setDefaultCloseOperation(close ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
			}

			@Override
			public void showDebugger() {
				SwingUtilities.invokeLater(() -> frame.showDevTools());
			}

			@Override
			public void show() {
				frame.setVisible(true);
			}

			@Override
			public void dispose() {
				SwingUtilities.invokeLater(() -> {
					frame.setVisible(false);
					frame.dispose();
				});
			}
		};
	}
}
