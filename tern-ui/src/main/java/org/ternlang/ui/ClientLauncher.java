package org.ternlang.ui;

import java.io.File;
import java.net.URI;

public class ClientLauncher {

	public static void main(String[] list) throws Exception {
		String temp = System.getProperty("java.io.tmpdir");
		File log = new File(temp, "/cef.log");
		File cache = new File(temp);

      ClientContext context = new ClientContext()
         .setDebug(true)
         .setTitle("Browser")
         .setHost(URI.create(list[0]).getHost())
         .setPort(URI.create(list[0]).getPort())
		 .setLogFile(log)
		 .setCachePath(cache)
         .setArguments(list);

		ClientProvider.provide().show(context);
	}
}
