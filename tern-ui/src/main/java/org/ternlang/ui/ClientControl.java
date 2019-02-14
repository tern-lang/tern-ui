package org.ternlang.ui;

public interface ClientControl {
   void registerListener(ClientCloseListener listener);
   void showDebugger();
}
