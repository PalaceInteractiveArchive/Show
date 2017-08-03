package network.palace.show.utils;

import java.io.File;

/**
 * @author Marc
 * @since 8/2/17
 */
public class FileUtil {

    public static void setupFiles() {
        File main = new File("plugins/Show/");
        if (!main.exists()) {
            main.mkdir();
        }
        File shows = new File("plugins/Show/shows/");
        if (!shows.exists()) {
            shows.mkdir();
        }
        File sequences = new File("plugins/Show/sequences/");
        if (!sequences.exists()) {
            sequences.mkdir();
        }
        File fountains = new File("plugins/Show/sequences/fountains/");
        if (!fountains.exists()) {
            fountains.mkdir();
        }
        File armorstands = new File("plugins/Show/sequences/armorstands/");
        if (!armorstands.exists()) {
            armorstands.mkdir();
        }
    }
}
