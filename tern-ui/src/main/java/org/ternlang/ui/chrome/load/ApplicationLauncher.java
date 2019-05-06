package org.ternlang.ui.chrome.load;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationLauncher {

   public static void launch(Class mainClass, String[] arguments, String[] libraryPaths) {
       String javaHome = System.getProperty("java.home");
       String classPath = System.getProperty("java.class.path");
       String libraryPath = Arrays.asList(libraryPaths)
             .stream()
             .collect(Collectors.joining(File.pathSeparator));
             
       try {
          File directory = new File(".");
          List<String> command = new ArrayList<String>();
          
          command.add(javaHome + "/bin/java");
          command.add("-cp");
          command.add(classPath);
          command.add(mainClass.getName());
          
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