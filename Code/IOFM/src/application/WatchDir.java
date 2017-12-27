package application;

import static application.DbModel.audio;
import static application.DbModel.document;
import static application.DbModel.executables;
import static application.DbModel.image;
import static application.DbModel.others;
import static application.DbModel.video;
import static application.StartApp.backgCreatDel;
import static application.StartApp.finder;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
/**
 * Example to watch a directory (or tree) for changes to files.
 */
public class WatchDir implements Runnable {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    //Stage backgCreatDel;

    Thread t;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir() {

        //this.finder = finder;
        // System.out.println("Inside WatchDir Constructor");
        this.watcher = Finder.watcher;
        this.keys = Finder.keys;

        t = new Thread(this, "WatchDir");
        t.setDaemon(true);

        t.start();
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) {
        try {
            // register directory and sub-directories
            Files.walkFileTree(start, finder);
        } catch (IOException ex) {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {

        /**
         * Process all events for keys queued to the watcher
         */
        //System.out.println("Entered thread");
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                System.err.println(x);
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                //   System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {

                //Start: opEN pROGRESS iNDICATOR
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                 
                        backgCreatDel.show();
                       
                    }
                });
                
                //Process Events
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                //System.out.format("%s: %s\n", event.kind().name(), child);
                if (kind == ENTRY_DELETE) {

                    //System.out.println;
                    System.out.format("deletion happening:%s\n", child);
                    StartApp.demodbModel.removeEntry(child);

                }

                // print out event
                //  System.out.format("%s: %s\n", event.kind().name(), child);
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {

                    System.out.format("creation happening:%s\n", child);

                    if (Files.isDirectory(child, NOFOLLOW_LINKS)) {

                        registerAll(child);

                    } else {

                        StartApp.demodbModel.insert(child);
                    }

                }

                //to ensure that above and below Platrorm.runlater do not clash
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Update  List Property and close progress indicator
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        CategoriesController.listPropertyAud.set(FXCollections.observableArrayList(audio));
                        CategoriesController.listPropertyDoc.set(FXCollections.observableArrayList(document));
                        CategoriesController.listPropertyVid.set(FXCollections.observableArrayList(video));
                        CategoriesController.listPropertyExec.set(FXCollections.observableArrayList(executables));
                        CategoriesController.listPropertyImg.set(FXCollections.observableArrayList(image));
                        CategoriesController.listPropertyOthers.set(FXCollections.observableArrayList(others));

                        //close Progressindicator
                        backgCreatDel.close();
                    }
                });

            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }

    }

}
