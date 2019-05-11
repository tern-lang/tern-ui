package org.ternlang.ui;

import java.io.File;

import org.ternlang.ui.chrome.load.LibraryLoader;

public class ClientLauncher {

	public static void main(String[] list) throws Exception {
		String temp = System.getProperty("java.io.tmpdir");
		File log = new File(temp, "/cef.log");
		File cache = new File(temp);

      ClientContext context = new ClientContext()
         .setDebug(true)
         .setTitle("Browser")
         .setAddress(list[0])
		   .setLogFile(log)
		   .setCachePath(cache)
         .setArguments(list);

		LibraryLoader.loadAndUpdateLibraryPathFrom(".cef");
		ClientProvider.provide().create(context).show();
	}
}
