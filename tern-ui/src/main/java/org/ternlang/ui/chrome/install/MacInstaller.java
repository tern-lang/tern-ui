package org.ternlang.ui.chrome.install;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class MacInstaller extends UserHomeInstaller {
   
   public static final String CEF_ICON_PNG = "CefDockIcon.png";
   public static final String CEF_ICON_ICNS = "CefIcon.icns";
   public static final String TERN_ICON_PNG = "icon/icon-large.png";
   public static final String TERN_ICON_ICNS = TERN_ICON_PNG + ".icns";
   public static final String MAC_JAR_PATH = "Contents/Java";
   public static final String MAC_RESOURCE_PATH = "Contents/Resources";
   
   public MacInstaller(String folder) {
      super(folder);
   }
   
   @Override
   public Launcher install(boolean dynamicPathUpdate) {
      File homeFolder = getHomeInstallPath();
      File cefFolder = getCefInstallPath();
      String[] libraryPaths = extractAndInstall();
      
      if(dynamicPathUpdate) {
         useReflectionToUpdatePath(libraryPaths);
      }
      installMacClassPath(cefFolder);
      installMacIcons(cefFolder);

      return (mainClass, arguments, properties) -> {
         File[] installedFiles = homeFolder.listFiles();
         File installApp = Arrays.asList(installedFiles)
               .stream()
               .filter(file -> file.getName().endsWith(".app") && file.isDirectory())
               .findFirst()
               .get();
         
         if(installApp != null) {
            try {
               String app = installApp.getCanonicalPath();
               System.err.println("Launching " + app);
               Desktop.getDesktop().open(installApp); // should open mac application
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
      };
   }
   
   @Override
   public boolean isInstalled() {
      File path = getCefInstallPath();
      File installFolder = resolveMacInstallPath(path, MAC_JAR_PATH);

      if (installFolder != null) {
         File[] jarFiles = installFolder.listFiles();
         return Arrays.asList(jarFiles)
               .stream()
               .anyMatch(entry -> entry.getName().endsWith(".jar"));
      }
      return false;
   }
   
   private File resolveMacInstallPath(File location, String path) {
      File[] installedFiles = location.listFiles();
      
      if(installedFiles != null) {
         return Arrays.asList(installedFiles)
               .stream()
               .filter(file -> file.getName().endsWith(".app") && file.isDirectory())
               .map(file -> new File(file, path))
               .filter(file -> file.exists() && file.isDirectory())
               .findFirst()
               .get();
      }
      return null;
   }
   
   private void installMacClassPath(File location) {
      File installFolder = resolveMacInstallPath(location, MAC_JAR_PATH);

      if(installFolder != null) {
         try {
            String classPath = System.getProperty("java.class.path", "");
            String[] classPathEntries = classPath.split(File.pathSeparator);

            System.err.println("Installing JAR files to " + installFolder);

            Arrays.asList(classPathEntries)
                  .stream()
                  .filter(entry -> entry.endsWith(".jar"))
                  .map(File::new)
                  .filter(file -> file.exists() && file.isFile())
                  .forEach(file -> {
                     String fileName = file.getName();
                     File destination = new File(installFolder, fileName);

                     try {
                        System.err.println("Installing " + destination);
                        copyFile(file, destination);
                     } catch (Exception e) {
                        System.err.println("Could not extract to " + destination);
                        e.printStackTrace();
                     }
                  });
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
   
   private  void installMacIcons(File location) {
      File installFolder = resolveMacInstallPath(location, MAC_RESOURCE_PATH);

      if(installFolder != null) {
         try {
            URL pngIcon = locateResource(TERN_ICON_PNG);
            URL icnsIcon = locateResource(TERN_ICON_ICNS);
            
            System.err.println("Installing icon files to " + installFolder);
            
            copyFile(pngIcon, new File(installFolder, CEF_ICON_PNG));
            copyFile(icnsIcon, new File(installFolder, CEF_ICON_ICNS));
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }

}
