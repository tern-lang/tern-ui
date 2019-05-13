package org.ternlang.ui.chrome.install;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.ui.chrome.install.extract.LibraryExtractor;

abstract class UserHomeInstaller implements Installer {
   
   public static final String CEF_VERSION = "3.3538.1852.gcb937fc";
   public static final String CEF_PATH = "cef/" + CEF_VERSION;
   
   private final String folder;
   
   protected UserHomeInstaller(String folder) {
      this.folder = folder;
   }
   
   @Override
   public Launcher install() {
      return install(false);
   }
   
   protected String[] extractAndInstall() {
      try {
         File directory = getCefInstallPath();
         String[] locations = LibraryExtractor.extractTo(directory);
         String[] path = expandPath(locations);

         return path;
      } catch (Exception e) {
         throw new IllegalStateException("Could not install library to " + folder, e);
      }
   }
   
   protected File getCefInstallPath() {
      File libraryPath = getHomeInstallPath();
      return new File(libraryPath, CEF_PATH);
   }

   protected File getHomeInstallPath() {
      int slash = folder.indexOf(File.separatorChar);
      boolean exists = new File(folder).exists();

      if(slash == -1 && !exists) {
         String home = System.getProperty("user.home");
         return new File(home, folder);
      }
      return new File(folder);
   }

   protected String[] expandPath(String[] location) throws Exception {
      String combined = Arrays.asList(location)
            .stream()
            .collect(Collectors.joining(File.pathSeparator));
      
      String current = System.getProperty("java.library.path");
      String expanded = current + File.pathSeparator + combined;
      String[] parts = expanded.split(File.pathSeparator);

      return Arrays.asList(parts)
              .stream()
              .filter(Objects::nonNull)
              .filter(entry -> !entry.isEmpty())
              .toArray(String[]::new);
   }
   
   protected void copyFile(URL source, File destination) {
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
   
   protected void copyFile(File source, File destination) {
      try {
         URL resource = source.toURI().toURL();
         copyFile(resource, destination);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }

   protected URL locateResource(String path) {
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
   
   protected void useReflectionToUpdatePath(String[] path) {
      try {
          Field field = findField(ClassLoader.class, "usr_paths");

          System.err.println(Arrays.asList(path));
          field.setAccessible(true);
          field.set(null, path);
      } catch(Throwable e) {
         System.err.println("Could not update USR paths");
      }
   }
   
   private static Field findField(Class type, String name) {
      try {
         Method method = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
        
         if(!method.isAccessible()) {
            method.setAccessible(true);
         }
         Field[] list = (Field[])method.invoke(type, false);
         
         for(Field entry : list) {
            String declaration = entry.getName();
            
            if(declaration.equals(name)) {
               entry.setAccessible(true);
               return entry;
            }
         }
         return type.getDeclaredField("usr_paths");
      } catch (Exception e) {
         throw new IllegalStateException("Could not find library path field", e);
      }
   }
}
