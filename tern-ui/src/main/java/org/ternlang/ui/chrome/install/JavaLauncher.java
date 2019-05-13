package org.ternlang.ui.chrome.install;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class JavaLauncher implements Launcher {
   
   private static final String UNIX_LIBRARY_PATH = "LD_LIBRARY_PATH";
   private static final String WINDOWS_LIBRARY_PATH = "Path";
   private static final String IGNORE_UNKNOWN_OPTIONS = "-XX:+IgnoreUnrecognizedVMOptions";
   private static final String INCLUDE_JAVA_MODULE = "--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED";
   
   private final String[] libraryPaths;
   
   public JavaLauncher(String[] libraryPaths) {
      this.libraryPaths = libraryPaths;
   }

   @Override
   public void launch(String mainClass, String[] arguments, String... properties) {
      String javaHome = System.getProperty("java.home");
      String classPath = System.getProperty("java.class.path");
      String libraryPath = Arrays.asList(libraryPaths)
            .stream()
            .collect(Collectors.joining(File.pathSeparator));
            
      try {
         File directory = new File(".");
         List<String> command = new ArrayList<String>();
         
         command.add(javaHome + File.separator + "bin" + File.separator + "java");
         command.add(IGNORE_UNKNOWN_OPTIONS);
         command.add(INCLUDE_JAVA_MODULE);
         command.add("-cp");
         command.add(classPath);

         for(String property : properties) {
             if(property.startsWith("-D")) {
                 command.add(property);
             } else {
                 command.add("-D" + property);
             }
         }
         command.add(mainClass);
         
         for(String argument : arguments) {
            command.add(argument);
         }
         ProcessBuilder builder = new ProcessBuilder(command);
         
         System.err.println("Launching: "+ command);
         
         Map<String, String> environment = builder.environment();
         environment.put(UNIX_LIBRARY_PATH, libraryPath);
         environment.put(WINDOWS_LIBRARY_PATH, libraryPath);
         builder.directory(directory)
            .redirectErrorStream(true)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor();
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}
