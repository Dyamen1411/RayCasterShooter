package ca.dyamen;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {
    public static final String GAME_DIRECTORY;
    public static final String ASSETS_DIRECTORY;
    public static final String SEPARATOR;

    static {
        String os_name = System.getProperty("os.name").toLowerCase();
        
        String game_directory;
        if (os_name.contains("win")) {
        	SEPARATOR = "\\";
        	game_directory = System.getenv("APPDATA");
        } else {
        	SEPARATOR = "/";
        	game_directory = System.getProperty("user.home");
        }
        
        GAME_DIRECTORY = game_directory + SEPARATOR + ".RayCaster" + SEPARATOR;
        ASSETS_DIRECTORY = GAME_DIRECTORY + "assets" + SEPARATOR;
    }

    public static String fileToString(String path) throws Exception {
        String result;
        try (InputStream in = new FileInputStream(path);
             Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }
}
