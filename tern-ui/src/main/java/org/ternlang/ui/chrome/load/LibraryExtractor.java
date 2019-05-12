package org.ternlang.ui.chrome.load;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ternlang.ui.OperatingSystem;

public class LibraryExtractor {

   public static final String CEF_VERSION = "3.3538.1852.gcb937fc";
   public static final String CEF_PATH = "cef/" + CEF_VERSION;
   public static final String CEF_ARCHIVE = "cef.tar";
   public static final String CEF_ICON_PNG = "CefDockIcon.png";
   public static final String CEF_ICON_ICNS = "CefIcon.icns";
   public static final String TERN_ICON_PNG = "icon/icon-large.png";
   public static final String TERN_ICON_ICNS = TERN_ICON_PNG + ".icns";
   public static final String MAC_JAR_PATH = "Contents/Java";
   public static final String MAC_RESOURCE_PATH = "Contents/Resources";
   public static final List<String> LINUX_AWT_LIBS = Collections.unmodifiableList(
         Arrays.asList(
               "libawt.so", 
               "libawt_xawt.so"
         )
   );

   public static String[] extractTo(File location) throws Exception {
      File root = new File(location, CEF_PATH);
      Set<File> path = new HashSet<File>();
      
      if(!root.exists()) {
         root.mkdirs();
      }
      Set<File> libResources = SystemLibraryLocator.findLibraries(LINUX_AWT_LIBS);
      
      path.add(root);
      path.addAll(libResources);
      
      if(isDirectoryEmpty(root)) {
         extractToPath(root);
      } else {
         System.err.println("Already extracted to " + root);
      }
      return path.stream()
           .map(file -> file.isDirectory() ? file : file.getParentFile())
           .filter(file -> file.isDirectory() && file.exists())
           .map(file -> {
              try {
                 return file.getCanonicalPath();
              }catch(Exception e) {
                 throw new IllegalStateException("Could not resolve path " + file, e);
              }
           })
           .toArray(String[]::new);
   }
   
   private static boolean isDirectoryEmpty(File root) {
      if(root.exists() && root.isDirectory()) {
         File[] files = root.listFiles();
         
         if(files != null) {
            for(File file : files) {
               String name = file.getName();
               
               if(!name.equals(".") && !name.equals("..")) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private static void extractToPath(File location) throws Exception {
      OperatingSystem os = OperatingSystem.resolveSystem();
      String prefix = os.name().toLowerCase();
      URL resource = locateResource(prefix + "/" + CEF_ARCHIVE);
      InputStream archive = resource.openStream();
      
      try {
         TarExtractor.extract(archive, location);
      } catch(Exception e) {
         System.err.println("Could not extract to " + location);
         e.printStackTrace();
      } finally {
         archive.close();
      }
      if(os.isMac()) {
         installMacClassPath(location);
         installMacIcons(location);
      }
   }

   public static boolean isMacLibraryInstalled(File location) {
      File root = new File(location, CEF_PATH);
      File installFolder = resolveMacInstallPath(root, MAC_JAR_PATH);

      if (installFolder != null) {
         File[] jarFiles = installFolder.listFiles();
         return Arrays.asList(jarFiles)
               .stream()
               .anyMatch(entry -> entry.getName().endsWith(".jar"));
      }
      return false;
   }
   
   private static File resolveMacInstallPath(File location, String path) {
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

   private static void installMacClassPath(File location) {
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
   
   private static void installMacIcons(File location) {
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

   private static void copyFile(URL source, File destination) {
      try {
         try(InputStream inputStream = source.openStream()) {
            try (OutputStream outputStream = new FileOutputStream(destination)) {
               byte[] chunk = new byte[8192];
               int count = 0;

               while((count = inputStream.read(chunk)) != -1) {
                  outputStream.write(chunk, 0, count);
               }
            }
         }
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   private static void copyFile(File source, File destination) {
      try {
         URL resource = source.toURI().toURL();
         copyFile(resource, destination);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }

   private static URL locateResource(String path) {
      String normal = path.replace("//", "/");
      URL resource = LibraryExtractor.class.getResource(normal);

      if (resource == null) {
         if (normal.startsWith("/")) {
            normal = normal.substring(1);
         } else {
            normal = "/" + normal;
         }
         return LibraryExtractor.class.getResource(normal);
      }
      return resource;
   }
}