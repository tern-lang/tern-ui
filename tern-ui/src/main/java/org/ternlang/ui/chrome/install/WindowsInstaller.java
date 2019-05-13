package org.ternlang.ui.chrome.install;

import java.io.File;

public class WindowsInstaller extends UserHomeInstaller {

   public WindowsInstaller(String folder) {
      super(folder);
   }
   
   @Override
   public Launcher install(boolean dynamicPathUpdate) {
      String[] libraryPaths = extractAndInstall();
      
      if(dynamicPathUpdate) {
         useReflectionToUpdatePath(libraryPaths);
      }
      return new JavaLauncher(libraryPaths);
   }
   
   @Override
   public boolean isInstalled() {
      File path = getCefInstallPath();

      if(path.exists() && path.isDirectory()) {
         File[] files = path.listFiles();

         if(files != null) {
            for(File file : files) {
               String name = file.getName();

               if(!name.equals(".") && !name.equals("..")) {
                  return true;
               }
            }
         }
      }
      return false;
   }
}
