package org.ternlang.ui;

import org.ternlang.ui.chrome.install.Installer;
import org.ternlang.ui.chrome.install.LinuxInstaller;
import org.ternlang.ui.chrome.install.MacInstaller;
import org.ternlang.ui.chrome.install.WindowsInstaller;

public enum OperatingSystem {
   WINDOWS("win64"),
   MAC("mac"),
   LINUX("linux64");

   private final String code;

   private OperatingSystem(String code) {
      this.code = code;
   }
   
   public String getCode() {
      return code;
   }
   
   public Installer getInstaller(String folder) {
      if(isWindows()) {
         return new WindowsInstaller(folder);
      }
      if(isLinux()) {
         return new LinuxInstaller(folder);
      }
      if(isMac()) {
         return new MacInstaller(folder);
      }
      throw new IllegalStateException("No installer for " + this);
   }
   
   public boolean isWindows() {
      return this == WINDOWS;
   }
   
   public boolean isLinux() {
      return this == LINUX;
   }
   
   public boolean isMac() {
      return this == MAC;
   }
   
   public static OperatingSystem resolveSystem() {
      OperatingSystem[] values = OperatingSystem.values();
      String system = System.getProperty("os.name");
      String token = system.toLowerCase();
      
      for(OperatingSystem os : values) {
         String name = os.name().toLowerCase();

         if(token.startsWith(name)) {
            return os;
         }
      }
      return WINDOWS;
   }
}
