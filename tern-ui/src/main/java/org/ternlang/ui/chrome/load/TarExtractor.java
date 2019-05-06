package org.ternlang.ui.chrome.load;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarExtractor {

   public static void extract(InputStream in, File out) throws IOException {
      try (TarArchiveInputStream fin = new TarArchiveInputStream(in)){
          TarArchiveEntry entry;
          while ((entry = fin.getNextTarEntry()) != null) {
              if (entry.isDirectory()) {
                  continue;
              }
              File curfile = new File(out, entry.getName());
              File parent = curfile.getParentFile();
              if (!parent.exists()) {
                  parent.mkdirs();
              }
              try(FileOutputStream newFile = new FileOutputStream(curfile)) {
                 IOUtils.copy(fin, newFile);
              }
          }
      }
  }
}
