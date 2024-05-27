package ca.dyamen.graphics;

import ca.dyamen.graphics.shader.ComputeShaderProgram;
import ca.dyamen.graphics.shader.ShaderProgram;
import ca.dyamen.util.Utils;
import ca.dyamen.world.entities.Player;
import ca.dyamen.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class Renderer {
    private static final String COMPUTE_SHADER_PATH;

    private World world;

    //TODO: Find better name
    private ShaderProgram shader;
    private ComputeShaderProgram world_shader;

    private int window_width;
    private int window_height;

    private int vao = 0;
    private int vbo = 0;

    private static final int buffer_data = 0x34B;

    static {
        COMPUTE_SHADER_PATH = Utils.ASSETS_DIRECTORY + "computeShaders" + Utils.SEPARATOR + "compute.glsl";
    }

    public Renderer() {

    }

    public void init(World world, int width, int height) throws Exception {
        shader = new ShaderProgram();
        world_shader = new ComputeShaderProgram();

        updateShaders(world, width, height);

        initializeBuffers();
    }

    private void initializeBuffers() {
        float[] data = new float[12];
        for (int i = 0; i < 12; ++i) {
            data[i] = (((buffer_data >> i) & 1) << 1) - 1.f;
        }

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        FloatBuffer buffer;

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        buffer = BufferUtils.createFloatBuffer(12);
        buffer.put(data).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void updateShaders(World world, final int window_width, final int window_height) throws Exception {
        this.window_width = window_width;
        this.window_height = window_height;

        if (world != null) {
            this.world = world;
        }

        updateShader();
        updateComputeShader();
    }

    private void updateShader() throws Exception {
        if (shader != null) {
            shader.cleanup();
        }

        shader = new ShaderProgram();
        shader.createVertexShader("vert.glsl");
        shader.createFragmentShader("frag.glsl");
        shader.link();
    }

    private void updateComputeShader() throws Exception {
        if (world_shader != null) {
            world_shader.cleanup();
        }

        world_shader = new ComputeShaderProgram();

        {
            final String defines = "#define WORLD_X_SIZE " + world.getWidth() + "\n" +
                    "#define WORLD_Y_SIZE " + world.getHeight() + "\n" +
                    "#define WORLD_NUMBER_COLOR " + world.getColorCount();

            final String source = Utils.fileToString(COMPUTE_SHADER_PATH).replaceFirst("#include#", defines);

            world_shader.createComputeShader(source);
        }

        world_shader.createTexture(window_width, window_height);
        world_shader.link();

        world_shader.createUniform("width");
        world_shader.createUniform("height");

        world_shader.createUniform("fov");
        world_shader.createUniform("player.x");
        world_shader.createUniform("player.y");
        world_shader.createUniform("player.r");

        for(int i = 0; i < world.getWidth() * world.getHeight(); ++i) {
            world_shader.createUniform("world_positions[" + i + "]");
        }

        for(int i = 0; i < world.getColorCount(); ++i) {
            world_shader.createUniform("world_colors[" + i + "]");
        }
    }

    public final void render(final Player player, final float fov) {
        world_shader.bind();

        world_shader.setUniform("width", window_width);
        world_shader.setUniform("height", window_height);
        world_shader.setUniform("fov", fov);
        world_shader.setUniform("player", player);
        world_shader.setUniform("world_positions", world.getData());
        world_shader.setUniform3("world_colors", world.getColors());

        /*System.out.println("width : " + window_width);
        System.out.println("height: " + window_height);
        System.out.println("fov   : " + fov);
        System.out.println("player");
        System.out.println("|.x   : " + player.x);
        System.out.println("|.y   : " + player.y);
        System.out.println("|.r   : " + player.r);
        System.out.println();*/

        world_shader.use();
        world_shader.unbind();

        shader.bind();

        GL30.glBindVertexArray(vao);
        GL20.glEnableVertexAttribArray(0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, world_shader.getTexId());

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, window_width / 2);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.unbind();
    }

    private void cleanupBuffers() {
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
    }

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }

        if (world_shader != null) {
            world_shader.cleanup();
        }

        cleanupBuffers();
    }
}
