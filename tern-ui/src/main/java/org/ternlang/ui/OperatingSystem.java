package org.ternlang.ui;

import org.ternlang.ui.chrome.install.Installer;
import org.ternlang.ui.chrome.install.LinuxInstaller;
import org.ternlang.ui.chrome.install.MacInstaller;
import org.ternlang.ui.chrome.install.WindowsInstaller;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

public enum OperatingSystem {
   WINDOWS("win64", "c:/Program Files/Tern/app", "${install.home}", "${user.home}"),
   MAC("mac", "${install.home}", "${user.home}"),
   LINUX("linux64", "${install.home}", "${user.home}");

   private final String code;
   private final String[] install;

   private OperatingSystem(String code, String... install) {
      this.install = install;
      this.code = code;
   }

   public String getCode() {
      return code;
   }

   public Installer getInstaller(String folder) {
      if (isWindows()) {
         return new WindowsInstaller(folder);
      }
      if (isLinux()) {
         return new LinuxInstaller(folder);
      }
      if (isMac()) {
         return new MacInstaller(folder);
      }
      throw new IllegalStateException("No installer for " + this);
   }

   public File getInstallDirectory() {
      for (String path : install) {
         Properties properties = System.getProperties();
         Enumeration<?> keys = properties.propertyNames();

         while (keys.hasMoreElements()) {
            String name = String.valueOf(keys.nextElement());
            String value = properties.getProperty(name);

            if (value != null) {
               path = path.replace("${" + name + "}", value);
               path = path.replace("$" + name, value);
            }
         }
         File file = new File(path.trim());

         if (file.exists()) {
            return file;
         }
      }
      throw new IllegalStateException("Could not resolve install directory");
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

      for (OperatingSystem os : values) {
         String name = os.name().toLowerCase();

         if (token.startsWith(name)) {
            return os;
         }
      }
      return WINDOWS;
   }
}
