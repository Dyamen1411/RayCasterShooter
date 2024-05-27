package ca.dyamen.graphics.window;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public class KeyboardCallback implements GLFWKeyCallbackI {

    private Map<Integer, Integer>   keymap;

    public KeyboardCallback() {
        keymap = new HashMap<>();
    }

    @Override
    synchronized public void invoke(long window, int key, int scancode, int action, int mods) {
        keymap.put(key, action);
    }

    synchronized public boolean isKeyDown(int key) {
        Integer action = keymap.get(key);
        return (action == null ? false : action != GLFW.GLFW_RELEASE);
    }
}
