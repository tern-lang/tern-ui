package org.ternlang.ui;

import java.io.File;

public class ClientLauncher {

	public static void main(String[] list) throws Exception {
		String temp = System.getProperty("java.io.tmpdir");
		File log = new File(temp, "/cef.log");
		File cache = new File(temp);

      ClientContext context = new ClientContext()
         .setDebug(true)
         .setTitle("Browser")
         .setAddress("http://www.authrus.com:8180/")
		   .setLogFile(log)
		   .setCachePath(cache)
         .setArguments(list);

		OperatingSystem.resolveSystem().getInstaller(".cef").install(true);
		ClientControl control = ClientProvider.provide().create(context);

		//control.setCookie("SESSID", "foo");
		control.show();
	}
}
