package org.ternlang.ui;

import org.ternlang.ui.chrome.ChromeClient;

public class ClientProvider {

	public static Client provide(ClientEngine engine){
		OperatingSystem os = OperatingSystem.resolveSystem();

		System.err.println("Engine is " + engine);

		return new ChromeClient();
	}
}
