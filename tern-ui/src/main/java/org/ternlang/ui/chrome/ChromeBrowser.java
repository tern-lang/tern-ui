package org.ternlang.ui.chrome;

import org.ternlang.ui.chrome.ui.BrowserFrame;

import java.io.File;
import java.net.URI;

public class ChromeBrowser {

    public static final String LIBRARY_PATH = ".cef";

    public static void main(String[] args) throws Exception {
        // OSR mode is enabled by default on Linux.
        // and disabled by default on Windows and Mac OS X.
        boolean osrEnabledArg = false;
        boolean transparentPaintingEnabledArg = false;
        boolean createImmediately = false;
        String cookiePath = null;
        for (String arg : args) {
            arg = arg.toLowerCase();
            if (arg.equals("--off-screen-rendering-enabled")) {
                osrEnabledArg = true;
            } else if (arg.equals("--transparent-painting-enabled")) {
                transparentPaintingEnabledArg = true;
            } else if (arg.equals("--create-immediately")) {
                createImmediately = true;
            } else if (arg.startsWith("--cookie-path=")) {
                cookiePath = arg.substring("--cookie-path=".length());
                File testPath = new File(cookiePath);
                if (!testPath.isDirectory() || !testPath.canWrite()) {
                    System.out.println("Can't use " + cookiePath
                            + " as cookie directory. Check if it exists and if it is writable");
                    cookiePath = null;
                } else {
                    System.out.println("Storing cookies in " + cookiePath);
                }
            }
        }
        System.out.println("Offscreen rendering " + (osrEnabledArg ? "enabled" : "disabled"));

        URI target = URI.create("http://www.google.com");
        // MainFrame keeps all the knowledge to display the embedded browser
        // frame.
        BrowserFrame frame = ChromeFrame.createChromeFrame(
                target,
                null,
                null,
                LIBRARY_PATH,
                cookiePath,
                osrEnabledArg,
                transparentPaintingEnabledArg,
                createImmediately,
                args);

        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.invalidate();
        frame.setSize(1000, 800);
        frame.invalidate();
    }
}
