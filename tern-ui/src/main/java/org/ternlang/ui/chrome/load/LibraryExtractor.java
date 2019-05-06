package org.ternlang.ui.chrome.load;

import org.ternlang.ui.OperatingSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class LibraryExtractor {

   public static final String CEF_VERSION = "3.3538.1852.gcb937fc";
   public static final String CEF_PATH = "cef/" + CEF_VERSION;
   public static final String CEF_ARCHIVE = "cef.tar";
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
      }
   }

   public static boolean isMacLibraryInstalled(File location) {
      File root = new File(location, CEF_PATH);
      File[] installedFiles = root.listFiles();

      if(installedFiles != null) {
         File installFolder = Arrays.asList(installedFiles)
               .stream()
               .filter(file -> file.getName().endsWith(".app") && file.isDirectory())
               .map(file -> new File(file, "Contents/Java/"))
               .filter(file -> file.exists() && file.isDirectory())
               .findFirst()
               .get();

         if (installFolder != null) {
            File[] jarFiles = installFolder.listFiles();
            return Arrays.asList(jarFiles)
                  .stream()
                  .anyMatch(entry -> entry.getName().endsWith(".jar"));
         }
      }
      return false;
   }

   private static void installMacClassPath(File location) {
      File[] installedFiles = location.listFiles();
      File installFolder = Arrays.asList(installedFiles)
            .stream()
            .filter(file -> file.getName().endsWith(".app") && file.isDirectory())
            .map(file -> new File(file, "Contents/Java"))
            .filter(file -> file.exists() && file.isDirectory())
            .findFirst()
            .get();

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

   private static void copyFile(File source, File destination) {
      try {
         try(FileInputStream inputStream = new FileInputStream(source)) {
            try (FileOutputStream outputStream = new FileOutputStream(destination)) {
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