package org.ternlang.ui;

public interface ClientControl {
   ClientContext getContext();
   ClientControl registerListener(ClientCloseListener listener);
   ClientControl closeOnExit(boolean close);
   ClientControl showDebugger();
   ClientControl show();
   ClientControl dispose();
}
