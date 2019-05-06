package org.ternlang.ui;

import org.ternlang.ui.chrome.ChromeClient;

public class ClientProvider {

	public static Client provide(){
		return new ChromeClient();
	}
}
