package org.ternlang.ui.chrome.load;

import java.io.File;
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
         
         return path;
      } catch (Exception e) {
         throw new IllegalStateException("Could not load library from " + directory, e);
      }
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

      System.setProperty("java.library.path", expanded);
      String[] parts = expanded.split(File.pathSeparator);

      return Arrays.asList(parts)
              .stream()
              .filter(Objects::nonNull)
              .filter(entry -> !entry.isEmpty())
              .toArray(String[]::new);
   }
}
