package org.ternlang.ui.javafx;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.ternlang.ui.Client;
import org.ternlang.ui.ClientCloseListener;
import org.ternlang.ui.ClientContext;
import org.ternlang.ui.ClientControl;
import org.ternlang.ui.WindowIcon;
import org.ternlang.ui.WindowIconLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@SuppressWarnings("restriction")
public class JavaFXClient implements Client {

   @Override
   public ClientControl show(ClientContext context) {
      try {
         CompletableFuture.runAsync(() -> {
            JavaFXApplication.launch(context);
         });
      }catch(Exception e) {
         log.info("Could not create client", e);
         throw new IllegalStateException("Could not create browser", e);
      }
      return new ClientControl() {
         @Override
         public void registerListener(ClientCloseListener listener) {
            JavaFXApplication.registerListener(listener);
         }
         @Override
         public void showDebugger() {
            JavaFXApplication.showDebugger();
         }
      };
   }

   public static class JavaFXApplication extends Application {

      private static final Set<ClientCloseListener> closeListeners = new CopyOnWriteArraySet<>();
      private static ClientContext context;
      private static JavaFXRegion browser;
      private static Scene scene;

      public static void launch(ClientContext context) {
         String[] arguments = context.getArguments();
         JavaFXApplication.context = context;
         launch(arguments);
      }

      public static void registerListener(ClientCloseListener listener) {
         closeListeners.add(listener);
      }

      public static void showDebugger() {
         if(browser != null) {
            browser.showDebugger();;
         }
      }

      public void stop() {
         for(ClientCloseListener closeListener : closeListeners) {
            try {
               closeListener.onClose();
            } catch(Exception e) {
               log.info("Could not notify of close", e);
            }
         }
         System.exit(0);
      }

      @Override
      public void start(Stage stage) {
         String title = context.getTitle();
         String path = context.getIcon();
         String target = context.getTarget();
         WindowIcon icon = WindowIconLoader.loadIcon(path);

         try {
            MenuBar menuBar = new MenuBar();
            Menu menu = new Menu("File");
            MenuItem quit = new MenuItem("Quit");

            menuBar.setUseSystemMenuBar(true);
            menuBar.useSystemMenuBarProperty().set(true);
            quit.setOnAction(new EventHandler() {
               @Override
               public void handle(Event event) {
                  Platform.exit();
               }
            });
            menu.getItems().add(quit);
            menuBar.getMenus().add(menu);
            stage.setTitle(title);

            if (icon != null) {
               byte[] data = icon.getData();
               InputStream stream = new ByteArrayInputStream(data);
               Image image = new Image(stream);

               stage.getIcons().add(image);
            }
         } catch (Exception e) {
            log.info("Could not load image", e);
         }
         browser = new JavaFXRegion(context);
         scene = new Scene(browser, 1200, 800, Color.web("#666970"));
         stage.setScene(scene);
         stage.show();
         browser.show(target);
      }
   }
}