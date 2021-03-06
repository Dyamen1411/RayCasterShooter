package ca.dyamen.graphics.shader;

import ca.dyamen.world.entities.Player;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ComputeShaderProgram {
    private final int programId;
    private int computeShaderId;

    private int texId;
    private int tex_width;
    private int tex_height;

    private FloatBuffer data = null;

    private final Map<String, Integer> uniforms;

    public ComputeShaderProgram() throws Exception {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader!");
        }

        uniforms = new HashMap<>();
    }

    public void createTexture(int width, int height) {
        tex_width = width;
        tex_height = height;

        data = BufferUtils.createFloatBuffer(tex_width * tex_height * 4);
        data.clear();

        texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, tex_width, tex_height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, data);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void createComputeShader(String shaderPath) throws Exception {
        computeShaderId = createShader(shaderPath);
    }

    protected int createShader(String source) throws Exception {
        int shaderId = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + GL43.GL_COMPUTE_SHADER);
        }

        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }

        if (computeShaderId != 0) {
            GL20.glDetachShader(programId, computeShaderId);
        }

        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform3(String uniformName, float value1, float value2, float value3) {
        GL20.glUniform3f(uniforms.get(uniformName), value1, value2, value3);
    }

    public void setUniform(String uniformName, Player player) {
        setUniform(uniformName + ".x", player.getX());
        setUniform(uniformName + ".y", player.getY());
        setUniform(uniformName + ".r", player.getRotation());
    }

    public void setUniform(String uniformName, int[] data) {
        for (int i = 0; i < data.length; ++i) {
            setUniform(uniformName + "[" + i + "]", data[i]);
        }
    }

    public void setUniform(String uniformName, float[] data) {
        for (int i = 0; i < data.length; ++i) {
            setUniform(uniformName + "[" + i + "]", data[i]);
        }
    }

    public void setUniform(String uniformName, byte[] data) {
        for (int i = 0; i < data.length; ++i) {
            setUniform(uniformName + "[" + i + "]", data[i]);
        }
    }

    public void setUniform3(String uniformName, float[] data) {
        for (int i = 0; i < data.length; i += 3) {
            setUniform3(uniformName + "[" + i/3 + "]", data[i+0], data[i+1], data[i+2]);
        }
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void use() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL42.glBindImageTexture(0, texId, 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F);
        GL43.glDispatchCompute(tex_width, 1, 1);
        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL30.GL_RGBA32F, data);
        GL42.glBindImageTexture(0, 0, 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public int getTexId() {
        return texId;
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glDeleteTextures(texId);
    }
}
