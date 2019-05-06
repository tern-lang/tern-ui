package org.ternlang.ui.chrome.load;

import org.ternlang.ui.OperatingSystem;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationLauncher {

   public static void launch(String mainClass, String[] arguments, File installFolder, String[] libraryPaths, String... properties) {
      OperatingSystem os = OperatingSystem.resolveSystem();
      
      if(os.isLinux() || os.isWindows()) {
         launchWithJava(mainClass, arguments, libraryPaths, properties);
      } else {
         launchWithDesktop(installFolder);
      }
   }
   
   private static void launchWithDesktop(File installFolder) {
      File[] installedFiles = installFolder.listFiles();
      File installApp = Arrays.asList(installedFiles)
            .stream()
            .filter(file -> file.getName().endsWith(".app") && file.isDirectory())
            .findFirst()
            .get();

      if(installApp != null) {
         try {
            String app = installApp.getCanonicalPath();
            System.err.println("Launching " + app);
            Desktop.getDesktop().open(installApp); // should open mac application
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
   
   private static void launchWithJava(String mainClass, String[] arguments, String[] libraryPaths, String... properties) {
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