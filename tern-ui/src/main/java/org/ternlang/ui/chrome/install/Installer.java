package org.ternlang.ui.chrome.install;

public interface Installer {
   Launcher install();
   Launcher install(boolean dynamicPathUpdate); // uses reflection
   boolean isInstalled();
}
