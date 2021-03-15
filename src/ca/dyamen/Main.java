package ca.dyamen;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;

public class Main {
	private static final int WINDOW_WIDTH = 1920;
	private static final int WINDOW_HEIGHT = 1080;
	
	private boolean reload = false, reset = false;
	
    private final float[] screen = {
            -1.f, -1.f,
             1.f,  1.f,
            -1.f,  1.f,
            -1.f, -1.f,
             1.f, -1.f,
             1.f,  1.f,
    };

    private final float[] tex_coords = {
            0.f, 0.f,
            1.f, 1.f,
            0.f, 1.f,
            0.f, 0.f,
            1.f, 0.f,
            1.f, 1.f,
    };

    private final float[] world_colors = {
            1, 0, 0,
            0, 0, 1,
            0, 1, 0,
            1, 1, 1,
    };

    private int[] world = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 2, 2, 2, 2, 2, 0, 0, 3, 0, 0, 0, 3, 0, 1,
            1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 1,
            1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 2, 2, 0, 2, 2, 0, 0, 3, 0, 0, 0, 3, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 4, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1,
            1, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    };

    private int vao = 0;
    private int vbo1 = 0;
    private int vbo2 = 0;

    private ShaderProgram shaderProgram = null;
    private ComputeShaderProgram computeShaderProgram = null;
    
    private Player player;

    public Main() {
        try {
            init();
            loop();

        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
            Display.destroy();
        }
    }

    private void init() throws Exception {
        initializeWindow();
        initializeBuffers();
        initializeShaders();
        
        player = new Player();
        player.x = 8;
        player.y = 8;
    }

    private void initializeWindow() throws Exception {
        DisplayMode displayMode = new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        Display.setDisplayMode(displayMode);
        Display.create();
    }

    private void initializeBuffers() {
        FloatBuffer buffer;

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo1 = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo1);
        buffer = BufferUtils.createFloatBuffer(screen.length);
        buffer.put(screen).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

        vbo2 = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo2);
        buffer = BufferUtils.createFloatBuffer(tex_coords.length);
        buffer.put(tex_coords).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void initializeShaders() throws Exception{
        initializeShader();
        initializeComputeShader();
    }

    private void initializeShader() throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader("vert.glsl");
        shaderProgram.createFragmentShader("frag.glsl");
        shaderProgram.link();
    }

    private void initializeComputeShader() throws Exception {
        computeShaderProgram = new ComputeShaderProgram();
        computeShaderProgram.createComputeShader("compute.glsl");
        computeShaderProgram.createTexture(WINDOW_WIDTH, WINDOW_HEIGHT);
        computeShaderProgram.link();

        computeShaderProgram.createUniform("width");
        computeShaderProgram.createUniform("height");

        computeShaderProgram.createUniform("fov");
        computeShaderProgram.createUniform("player.x");
        computeShaderProgram.createUniform("player.y");
        computeShaderProgram.createUniform("player.r");

        for(int i = 0; i < 16*16; ++i) {
            computeShaderProgram.createUniform("world_positions[" + i + "]");
        }

        for(int i = 0; i < 4; ++i) {
            computeShaderProgram.createUniform("world_colors[" + i + "]");
        }
    }
    
    private void update() throws Exception {
    	if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
    		player.x += (float) Math.cos(player.r) * Player.MOVE_SPEED;
    		player.y += (float) Math.sin(player.r) * Player.MOVE_SPEED;
    	}
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
    		player.x -= (float) Math.cos(player.r) * Player.MOVE_SPEED;
    		player.y -= (float) Math.sin(player.r) * Player.MOVE_SPEED;
    	}
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
    		player.x += (float) Math.cos(player.r + Math.PI / 2.) * Player.MOVE_SPEED;
    		player.y += (float) Math.sin(player.r + Math.PI / 2.) * Player.MOVE_SPEED;
    	}
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
    		player.x -= (float) Math.cos(player.r + Math.PI / 2.) * Player.MOVE_SPEED;
    		player.y -= (float) Math.sin(player.r + Math.PI / 2.) * Player.MOVE_SPEED;
    	}
    	
    	/*if (player.vx != 0) {
    		
    		player.vx += (player.vx > 0 ? -1 : 1) * Player.MOVE_SPEED / 8.;
    	}
    	if (player.vy != 0) player.vy -= (player.vy > 0 ? -1 : 1) * Player.MOVE_SPEED / 8.;
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
    		player.vx += Math.cos(player.r) * Player.MOVE_SPEED;
    		player.vy += Math.sin(player.r) * Player.MOVE_SPEED;
    	}
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
    		player.vx -= Math.cos(player.r) * Player.MOVE_SPEED;
    		player.vy -= Math.sin(player.r) * Player.MOVE_SPEED;
    	}
    	
    	if (player.vx < -Player.MOVE_SPEED) player.vx = -Player.MOVE_SPEED;
    	if (player.vy < -Player.MOVE_SPEED) player.vy = -Player.MOVE_SPEED;
    	if (player.vx >  Player.MOVE_SPEED) player.vx =  Player.MOVE_SPEED;
    	if (player.vx >  Player.MOVE_SPEED) player.vx =  Player.MOVE_SPEED;
    	
    	player.x += player.vx;
    	player.y += player.vy;*/
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
    		player.r += Player.CAMERA_MOVE_SPEED;
    	}
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
    		player.r -= Player.CAMERA_MOVE_SPEED;
    	}
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
    		if (!reload) {
    			reloadShaders();
    			reload = true;
    		}
    	} else {
    		reload = false;
    	}
    	
    	if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
    		if (!reset) {
    			player.x = 8;
    			player.y = 8;
    			player.r = (float) Math.PI;
    			reset = true;
    		}
    	} else {
    		reset = false;
    	}
    	
    	Display.setTitle(String.format("pos: %8.8f %8.8f | rot: %8.8f | reload=%s", player.x, player.y, (float) (player.r / Math.PI), reload ? "1" : "0"));
    }
    
    private void render() {
    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        computeShaderProgram.bind();

        computeShaderProgram.setUniform("width", WINDOW_WIDTH);
        computeShaderProgram.setUniform("height", WINDOW_HEIGHT);
        computeShaderProgram.setUniform("fov", (float) Math.PI/2.f - 0.01f);
        computeShaderProgram.setUniform("player", player);
        computeShaderProgram.setUniform("world_positions", world);
        computeShaderProgram.setUniform3("world_colors", world_colors);

        computeShaderProgram.use();
        computeShaderProgram.unbind();

        shaderProgram.bind();

        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, computeShaderProgram.getTexId());

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, screen.length / 2);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shaderProgram.unbind();

        Display.update();
        Display.sync(144);
    }
    
    private void loop() throws Exception {
        while (!Display.isCloseRequested()) {
        	update();
        	render();
        }
    }
    
    private void reloadShaders() throws Exception {
    	cleanupShaders();
    	initializeShaders();
    }

    private void cleanup() {
        cleanupBuffers();
        cleanupShaders();
    }

    private void cleanupBuffers() {
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo1);
        GL15.glDeleteBuffers(vbo2);
    }

    private void cleanupShaders() {
        shaderProgram.cleanup();
        computeShaderProgram.cleanup();
    }

    public static void main(String[] args) {
        new Main();
    }
}
