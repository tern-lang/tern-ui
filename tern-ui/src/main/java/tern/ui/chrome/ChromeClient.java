package tern.ui.chrome;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;

import lombok.SneakyThrows;
import tern.ui.Client;
import tern.ui.ClientCloseListener;
import tern.ui.ClientContext;
import tern.ui.ClientControl;
import tern.ui.WindowIcon;
import tern.ui.WindowIconLoader;
import tern.ui.chrome.load.LibraryLoader;

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

		LibraryLoader.loadFrom(folder);
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
