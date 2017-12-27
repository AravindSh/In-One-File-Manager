package application;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Finder
        extends SimpleFileVisitor<Path> {

    //private final PathMatcher matcher;
    public int numMatches = 0;
    // DbModel demodbModel;

    public static WatchService watcher;
    public static Map<WatchKey, Path> keys;
    
    ArrayList<String> roots = new ArrayList<>();

    /*@SuppressWarnings("unchecked")
     static <T> WatchEvent<T> cast(WatchEvent<?> event) {
     return (WatchEvent<T>) event;
     }*/
    Finder() {
        for(File f:File.listRoots()){
            roots.add(f.toString());
        }

    }

    // Compares the glob pattern against
    // the file or directory name.
    void find(Path file) {

        Path name = file.getFileName();
        // System.out.println("before if Filename: "+file);
        if (name != null) {
            numMatches++;
            // System.out.println("after if Filename: "+file);
            //send path to DbModel class insert method
            StartApp.demodbModel.insert(file);

        }
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attrs) {

        DosFileAttributes attr = null;
        try {
            attr = Files.readAttributes(file, DosFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, ex);
        }

        if ( !attr.isHidden() ) {
            find(file);
        }

        return CONTINUE;
    }

    private void register(Path dir) {
        // System.out.println("register dir  :  "+dir);

        WatchKey key = null;
        try {
            key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException ex) {
            Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*      Path prev = keys.get(key);
         if (prev == null) {
         //           System.out.format("register: %s\n", dir);
         } else {
         if (!dir.equals(prev)) {
         //               System.out.format("update: %s -> %s\n", prev, dir);
         }
         }
         */
        keys.put(key, dir);
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attrs) {

        DosFileAttributes attr = null;
        try {
            attr = Files.readAttributes(dir, DosFileAttributes.class);
        } catch (IOException ex) {
            Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, ex);
        }
       // System.out.println(dir);
        if ( attr.isHidden() && !roots.contains(dir.toString()) ) {
          //  System.out.println("ishidden");
            return SKIP_SUBTREE;
        } 
        
        register(dir);
        StartApp.demodbModel.insertPath(dir);       
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException exc) {
        //       System.err.println(exc);
        return CONTINUE;
    }

}
