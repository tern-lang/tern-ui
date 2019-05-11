package org.ternlang.ui;

public interface ClientControl {
   ClientContext getContext();
   void registerListener(ClientCloseListener listener);
   void closeOnExit(boolean close);
   void showDebugger();
   void show();
   void dispose();
}
