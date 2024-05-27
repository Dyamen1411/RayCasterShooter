package ca.dyamen;

import ca.dyamen.graphics.Renderer;
import ca.dyamen.graphics.window.Window;
import ca.dyamen.util.Utils;
import ca.dyamen.world.entities.Player;
import ca.dyamen.world.World;
import org.lwjgl.opengl.*;

public class Main {
	private static final int WINDOW_WIDTH = 1920;
	private static final int WINDOW_HEIGHT = 1080;

    private World world;
    private Renderer renderer;

    private Player player;

    private Window window;

    public Main() {
        try {
            initialize();
            loop();
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
            window.close();
        }
    }

    private void initialize() throws Exception {
        initializeWindow();
        initializeWorld();
        initializeRenderer();
        initializePlayer();
    }

    private void initializeWindow() throws Exception {
        window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "RCS");
    }

    private void initializeWorld() throws Exception {
        world = new World();
        world.loadWorld(Utils.ASSETS_DIRECTORY + "maps" + Utils.SEPARATOR + "level1.map");
    }

    private void initializeRenderer() throws Exception {
        renderer = new Renderer();
        renderer.init(world, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void initializePlayer() {
        player = new Player(8, 8, world);
    }
    
    private void update() {
        player.update(window);
        window.setTitle(String.format("pos: %8.8f %8.8f | rot: %8.8f", player.getX(), player.getY(), (float) (player.getRotation() / (2 * Math.PI))));
    }
    
    private void render() {
    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        renderer.render(player, (float) Math.PI / 2.f);

        // Cap FPS to 60 or 144 ?
        window.update();
    }
    
    private void loop() throws Exception {
        while (!window.shouldClose()) {
        	update();
        	render();
        }
    }

    private void cleanup() {
        if (renderer != null) {
            renderer.cleanup();
        }
        if (window != null) {
            window.close();
        }
    }

    public static void main(String[] args) {
        /*try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(Utils.ASSETS_DIRECTORY + "maps" + Utils.SEPARATOR + "level1.map"));
            for (int i = 0; i < 4; ++i) {
                dos.write((16 >> (i*8)) & 0xFF);
            }

            for (int i = 0; i < 4; ++i) {
                dos.write((16 >> (i*8)) & 0xFF);
            }

            for (int i = 0; i < 4; ++i) {
                dos.write((4 >> (i*8)) & 0xFF);
            }

            dos.write(world_pos);
            dos.write(world_colors);
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        new Main();
    }
}
