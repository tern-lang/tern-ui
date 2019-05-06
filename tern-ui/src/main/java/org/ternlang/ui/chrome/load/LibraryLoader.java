package org.ternlang.ui.chrome.load;

import org.ternlang.ui.OperatingSystem;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class LibraryLoader {

   public static String[] loadFrom(String folder) {
      File directory = libraryPath(folder);

      System.err.println("Loading library from " + directory);
      return loadFromPath(directory);
   }

   public static String[] loadFromPath(File directory) {
      try {
         String[] locations = LibraryExtractor.extractTo(directory);
         String[] path = expandPath(locations);
         
         try {
//            Field field = findField(ClassLoader.class, "usr_paths");
//
//            System.err.println(Arrays.asList(path));
//            field.setAccessible(true);
//            field.set(null, path);
         } catch(Throwable e) {
           System.err.println("Could not update USR paths");
         }
         return path;
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
   public static boolean isLibraryDeployed(String folder) {
      OperatingSystem os = OperatingSystem.resolveSystem();
      File path = libraryPath(folder);

      if(os.isMac()) {
         return LibraryExtractor.isMacLibraryInstalled(path);
      }
      if(path.exists() && path.isDirectory()) {
         File cefFolder = new File(path, LibraryExtractor.CEF_PATH);

         if(cefFolder.exists() && cefFolder.isDirectory()) {
            File[] files = cefFolder.listFiles();

            if(files != null) {
               for(File file : files) {
                  String name = file.getName();

                  if(!name.equals(".") && !name.equals("..")) {
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }

   public static File installPath(String folder) {
      File libraryPath = libraryPath(folder);
      return new File(libraryPath, LibraryExtractor.CEF_PATH);
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

   private static String[] expandPath(String[] location) throws Exception {
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
}
