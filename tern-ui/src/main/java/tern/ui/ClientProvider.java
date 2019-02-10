package tern.ui;

import lombok.extern.slf4j.Slf4j;
import tern.ui.chrome.ChromeClient;
import tern.ui.javafx.JavaFXClient;

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
