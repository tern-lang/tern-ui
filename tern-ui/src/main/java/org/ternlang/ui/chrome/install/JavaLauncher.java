package org.ternlang.ui.chrome.install;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaLauncher implements Launcher {
   
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
         
         command.add(javaHome + "/bin/java");
         command.add("-XX:+IgnoreUnrecognizedVMOptions");
         command.add("--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED");
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
         
         Map<String, String> environment = builder.environment();
         environment.put("LD_LIBRARY_PATH", libraryPath);
         environment.put("Path", libraryPath);
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
