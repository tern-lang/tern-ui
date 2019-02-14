package org.ternlang.ui;

import lombok.extern.slf4j.Slf4j;
import org.ternlang.ui.chrome.ChromeClient;
import org.ternlang.ui.javafx.JavaFXClient;

@Slf4j
public class ClientProvider {

	public static Client provide(ClientEngine engine){
		OperatingSystem os = OperatingSystem.resolveSystem();

		log.info("Engine is " + engine);

		if(os.isWindows() && engine.isChromium()) {
			return new ChromeClient();
		}
		return new JavaFXClient();
	}
}
