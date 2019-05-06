package org.ternlang.ui.chrome.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.compress.utils.IOUtils;
import org.ternlang.ui.OperatingSystem;

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
      
      String[] libraryPaths = path.stream()
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
      
      if(isDirectoryEmpty(root)) {
         extractToPath(root, libResources);
      } else {
         System.err.println("Already extracted to " + root);
      }
      return libraryPaths;
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

   private static void extractToPath(File location, Set<File> libraryPaths) throws Exception {
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
      for(File libResource : libraryPaths) {
         String name = libResource.getName();
         File file = new File(location, name);
         
         try(InputStream libStream = new FileInputStream(libResource)) {
            try(OutputStream outputStream = new FileOutputStream(file)) {
         
               try {
                  IOUtils.copy(libStream, outputStream);
               } catch(Exception e) {
                  System.err.println("Could not copy "+ libResource + " to " + location);
                  e.printStackTrace();
               }
            }
         }
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