package org.ternlang.ui.chrome.load;

import org.ternlang.ui.OperatingSystem;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LibraryExtractor {

   public static final String CEF_VERSION = "3.3538.1852.gcb937fc";
   public static final String CEF_REQUIRED_LIST = "required.list";
   public static final String CEF_PATH = "cef/" + CEF_VERSION;
   private static final String[] LINUX_AWT_LIBS = {
           "libawt.so",
           "libawt_xawt.so"
   };

   public static File extractTo(File location) throws Exception {
      File root = new File(location, CEF_PATH);

      if(!root.exists()) {
         root.mkdirs();
      }
      extractToPath(root);
      return root;
   }

   private static void extractToPath(File location) throws Exception {
      OperatingSystem os = OperatingSystem.resolveSystem();

      listFiles(os).forEach(dependency -> {
         File file = dependency.getLocation(location);
         File parent = file.getParentFile();
         URL resource = dependency.getResource();

         try {
            if (dependency.isJar()) {
               String path = file.toString();
               LibraryClassPathExtender.updateClassPath(path);
            }
            if (parent.isFile()) {
               parent.delete();
            }
            System.err.println("Writing to " + file);
            parent.mkdirs();

            if(file.isDirectory()) {
               System.err.println("Ignoring directory " + file);
            } else {
               if(!file.exists()) {
                  writeTo(resource, file);
               } else {
                  System.err.println("File " + file + " already exists");
               }
               file.setExecutable(true);
               file.setReadable(true);
            }
         } catch (Exception e) {
            System.err.println("Error writing to " + file);
            e.printStackTrace();
         }
      });
   }

   private static void writeTo(URL resource, File location) throws Exception {
      OutputStream out = new FileOutputStream(location);

      try {
         InputStream source = resource.openStream();
         byte[] buffer = new byte[1024 * 8];
         int count = 0;

         while ((count = source.read(buffer)) != -1) {
            out.write(buffer, 0, count);
         }
         source.close();
      } finally {
         out.close();
      }
   }

   private static List<LibraryDependency> listFiles(OperatingSystem os) throws Exception {
      String path = "/" + os.name().toLowerCase();
      List<String> resources = locateRequiredResources(path);
      List<LibraryDependency> libraryDependencies = resources.stream()
              .map(resource -> loadDependency(path, resource))
              .filter(dependency -> dependency.isValid())
              .collect(Collectors.toList());

      if(os.isLinux()) {
         URL[] libResources = LibraryLocator.findLibraries(LINUX_AWT_LIBS);

         for(URL libResource : libResources) {
            String libPath = libResource.getPath().replaceAll(".*/", "");
            LibraryDependency dependency = new LibraryDependency(libResource, libPath);

            if(dependency.isValid()) {
               libraryDependencies.add(dependency);
            }
         }
      }
      return libraryDependencies;
   }

   private static List<String> locateRequiredResources(String path) {
      URL list = locateResource(path + "/" + CEF_REQUIRED_LIST);

      if (list == null) {
         throw new IllegalArgumentException("No library found at " + path);
      }
      try {
         InputStream source = list.openStream();

         try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int count = 0;

            while ((count = source.read(chunk)) != -1) {
               buffer.write(chunk);
            }
            String[] lines = buffer.toString().split("\\r?\\n");
            return Arrays.asList(lines)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(text -> text.trim())
                    .filter(text -> !text.isEmpty() && !text.startsWith("#"))
                    .collect(Collectors.toList());
         } finally {
            source.close();
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not locate " + path, e);
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


   private static LibraryDependency loadDependency(String prefix, String path) {
      String normal = path.startsWith("/") ? path.substring(1) : path;
      URL resource = locateResource(prefix + "/" + normal);
      return new LibraryDependency(resource, normal);
   }

   private static class LibraryDependency {
      private final URL resource;
      private final String path;

      public LibraryDependency(URL resource, String path) {
         this.resource = resource;
         this.path = path;
      }

      public URL getResource() {
         return resource;
      }

      public boolean isJar() {
         return path.endsWith(".jar");
      }

      public boolean isValid(){
         return resource != null;
      }

      public File getLocation(File root) {
         try {
            return new File(root, path).getCanonicalFile();
         } catch (IOException e) {
            throw new IllegalStateException("Could not get location for " + path + " in " + root, e);
         }
      }
   }


}