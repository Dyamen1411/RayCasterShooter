package ca.dyamen;

import ca.dyamen.graphics.Renderer;
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

    public Main() {
        try {
            initialize();
            loop();
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
            Display.destroy();
        }
    }

    private void initialize() throws Exception {
        initializeWindow();
        initializeWorld();
        initializeRenderer();
        initializePlayer();
    }

    private void initializeWindow() throws Exception {
        DisplayMode displayMode = new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        Display.setDisplayMode(displayMode);
        Display.create();
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
        player = new Player(8, 8);
    }
    
    private void update() throws Exception {
        player.update();
    	Display.setTitle(String.format("pos: %8.8f %8.8f | rot: %8.8f", player.getX(), player.getY(), (float) (player.getRotation() / (2 * Math.PI))));
    }
    
    private void render() {
    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        renderer.render(player, (float) Math.PI / 2.f);

        Display.update();
        Display.sync(144);
    }
    
    private void loop() throws Exception {
        while (!Display.isCloseRequested()) {
        	update();
        	render();
        }
    }

    private void cleanup() {
        if (renderer != null) {
            renderer.cleanup();
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
