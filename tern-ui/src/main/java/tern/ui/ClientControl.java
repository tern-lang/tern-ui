package tern.ui;

public interface ClientControl {
   void registerListener(ClientCloseListener listener);
   void showDebugger();
}
