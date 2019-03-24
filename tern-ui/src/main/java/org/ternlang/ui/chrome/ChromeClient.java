package org.ternlang.ui.chrome;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.ternlang.ui.Client;
import org.ternlang.ui.ClientCloseListener;
import org.ternlang.ui.ClientContext;
import org.ternlang.ui.ClientControl;
import org.ternlang.ui.WindowIcon;
import org.ternlang.ui.WindowIconLoader;
import org.ternlang.ui.chrome.load.LibraryLoader;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChromeClient implements Client {

	@Override
	@SneakyThrows
	public ClientControl show(ClientContext context) {
		int width = context.getWidth();
		int height = context.getHeight();
		String folder = context.getFolder();
		String address = context.getTarget();
		String title = context.getTitle();
		String path = context.getIcon();
		File logiFle = context.getLogFile();
		File cachePath = context.getCachePath();
		URI target = URI.create(address);

		try {
		   LibraryLoader.loadFrom(folder);
		} catch(Throwable e) {
		   log.info("Error loading library from {}", folder, e);
		}
		WindowIcon icon = WindowIconLoader.loadIcon(path);
		String[] arguments = context.getArguments();
		final ChromeFrameListener listener = new ChromeLogListener();
		final ChromeFrame frame = new ChromeFrame(listener, target, logiFle, cachePath, false, false, null, arguments);
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
