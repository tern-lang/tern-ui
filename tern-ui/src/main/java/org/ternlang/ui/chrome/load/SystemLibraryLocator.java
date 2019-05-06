package org.ternlang.ui.chrome.load;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SystemLibraryLocator {

    public static Set<File> findLibraries(List<String> names) {
        String javaHome = System.getProperty("java.home");
        File homeDir = new File(javaHome);

        if (homeDir.exists() && homeDir.isDirectory()) {
            try {
                File javaLibPath = new File(homeDir, "lib");
                Set<String> requiredLibs = new HashSet<String>(names);

                if (javaLibPath.exists() && javaLibPath.isDirectory()) {
                    return findLibraries(javaLibPath, requiredLibs);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Could not determine java library path", e);
            }
        }
        return Collections.emptySet();
    }

    private static Set<File> findLibraries(File root, Set<String> names) {
        Set<File> paths = new HashSet<File>();

        if(root != null && root.isDirectory()) {
            File[] files = root.listFiles();

            if (files != null) {
                for (File file : files) {
                    if(file.isDirectory()) {
                        Set<File> libResources = findLibraries(file, names);
                        paths.addAll(libResources);
                    } else {
                        String name = file.getName();

                        if(names.contains(name)) {
                           paths.add(file);
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableSet(paths);
    }
}
