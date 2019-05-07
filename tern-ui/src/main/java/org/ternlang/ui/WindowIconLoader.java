package org.ternlang.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class WindowIconLoader {

    public static WindowIcon loadIcon(String path) {
        URL resource = findResource(path);

        if (resource != null) {
            try {
                InputStream input = resource.openStream();

                try {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count = 0;

                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                    byte[] data = output.toByteArray();
                    return new WindowIcon(resource, data);
                } finally {
                    input.close();
                }
            } catch(Exception e) {
                throw new IllegalArgumentException("Could not load " + path, e);
            }
        }
        return null;
    }

    private static URL findResource(String path) {
        OperatingSystem system = OperatingSystem.resolveSystem();

        if(system.isWindows() || system.isLinux()) {
            try {
                ClassLoader loader = WindowIconLoader.class.getClassLoader();
                URL source = loader.getResource(path);

                if (source == null) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    } else {
                        path = "/" + path;
                    }
                    return loader.getResource(path);
                }
                return source;
            } catch(Exception e) {
                throw new IllegalArgumentException("Could not find " + path, e);
            }
        }
        return null;
    }
}
