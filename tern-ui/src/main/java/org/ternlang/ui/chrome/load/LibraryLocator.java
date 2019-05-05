package org.ternlang.ui.chrome.load;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class LibraryLocator {

    public static URL[] findLibraries(String... names) {
        String javaHome = System.getProperty("java.home");
        File homeDir = new File(javaHome);

        if (homeDir.exists() && homeDir.isDirectory()) {
            try {
                File javaLibPath = new File(homeDir, "lib");
                Set<String> requiredLibs = new HashSet<String>();

                for(String name : names) {
                    requiredLibs.add(name);
                }
                if (javaLibPath.exists() && javaLibPath.isDirectory()) {
                    URL[] resources = findLibraries(javaLibPath, requiredLibs);

                    for (URL resource : resources) {
                        System.err.println("Found library: " + resource);
                    }
                    return resources;
                }
            } catch (Exception e) {
                throw new IllegalStateException("Could not determine java library path", e);
            }
        }
        return new URL[]{};
    }

    private static URL[] findLibraries(File root, Set<String> names) {
        Set<URL> paths = new HashSet<URL>();

        if(root != null && root.isDirectory()) {
            File[] files = root.listFiles();

            if (files != null) {
                for (File file : files) {
                    if(file.isDirectory()) {
                        URL[] libResources = findLibraries(file, names);

                        for(URL libResource: libResources) {
                            paths.add(libResource);
                        }
                    } else {
                        String name = file.getName();

                        if(names.contains(name)) {
                            try {
                                URL libResource = file.toURI().toURL();
                                paths.add(libResource);
                            } catch(Exception e) {
                                throw new IllegalStateException("Could not determine path for " + root, e);
                            }
                        }
                    }
                }
            }
        }
        return paths.stream().toArray(URL[]::new);
    }
}
