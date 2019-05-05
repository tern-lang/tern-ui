package org.ternlang.ui.chrome.load;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class LibraryLoader {

   public static boolean isLibraryLoaded(String folder) {
      File path = libraryPath(folder);

      if(path.exists() && path.isDirectory()) {
         File cefFolder = new File(path, LibraryExtractor.CEF_PATH);

         if(cefFolder.exists() && cefFolder.isDirectory()) {
            return true;
         }
      }
      return false;
   }

   public static File libraryPath(String folder) {
      int slash = folder.indexOf(File.separatorChar);
      boolean exists = new File(folder).exists();

      if(slash == -1 && !exists) {
         String home = System.getProperty("user.home");
         return new File(home, folder);
      }
      return new File(folder);
   }

   public static void loadFrom(String folder) {
      int slash = folder.indexOf(File.separatorChar);
      boolean exists = new File(folder).exists();

      if(slash != -1 || exists) {
         File directory = new File(folder);
         
         System.err.println("Loading library from " + directory);
         loadFromPath(directory);
      } else {
         String home = System.getProperty("user.home");
         File directory = new File(home, folder);

         System.err.println("Loading library from " + directory);
         loadFromPath(directory);
      }
   }

   public static void loadFromPath(File directory) {
      try {
         File location = LibraryExtractor.extractTo(directory);
         String[] path = expandPath(location);
         Field field = findField(ClassLoader.class, "usr_paths");

         System.err.println(Arrays.asList(path));
         field.setAccessible(true);
         field.set(null, path);
      } catch (Exception e) {
         throw new IllegalStateException("Could not load library from " + directory, e);
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

   private static String[] expandPath(File location) throws Exception {
      String path = location.getCanonicalPath();
      String current = System.getProperty("java.library.path");
      String expanded = current + File.pathSeparator + path;

      System.setProperty("java.library.path", expanded);
      String[] parts = expanded.split(File.pathSeparator);

      return Arrays.asList(parts)
              .stream()
              .filter(Objects::nonNull)
              .filter(entry -> !entry.isEmpty())
              .toArray(String[]::new);
   }
}
