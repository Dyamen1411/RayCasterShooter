package ca.dyamen.graphics.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class Window {
    private final long handle;
    private final KeyboardCallback keyboardCallback;

    public Window(int width, int height, String title) {
        if (!GLFW.glfwInit())
            throw new RuntimeException("Could not initialize GLFW");
        handle = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (handle == 0)
            throw new RuntimeException("Could not initialize GLFW window.");
        keyboardCallback = new KeyboardCallback();
        GLFW.glfwSetKeyCallback(handle, keyboardCallback);
        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();
    }

    public boolean shouldClose() {
        return (GLFW.glfwWindowShouldClose(handle));
    }

    public void update() {
        GLFW.glfwSwapBuffers(handle);
        GLFW.glfwPollEvents();
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(handle, title);
    }

    public boolean isKeyDown(int key) {
        return (keyboardCallback.isKeyDown(key));
    }

    public void close() {
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
    }
}
