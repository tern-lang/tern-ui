package org.ternlang.ui;

import java.net.URL;

public class WindowIcon {

   private final URL resource;
   private final byte[] data;

   public WindowIcon(URL resource, byte[] data) {
      this.resource = resource;
      this.data = data;
   }

   public URL getResource() {
      return resource;
   }

   public byte[] getData() {
      return data;
   }
}
