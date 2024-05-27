package ca.dyamen.world.entities;

import ca.dyamen.graphics.window.Window;
import ca.dyamen.world.World;

import org.lwjgl.glfw.GLFW;

public class Player {
	public static final float MOVE_SPEED = 0.01f;
	public static final float CAMERA_MOVE_SPEED = 0.01f;

	private World world;

    private float x;
	private float y;
	private float r;

    public Player(World world) {
		this(0, 0, world);
	}

	public Player(float x, float y, World world) {
		this(x, y, 0, world);
	}

	public Player(float x, float y, float r, World world) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.world = world;
	}

    public void update(Window window) {
		if (window.isKeyDown(GLFW.GLFW_KEY_C)) {
			r += CAMERA_MOVE_SPEED;
		}

		if (window.isKeyDown(GLFW.GLFW_KEY_Z)) {
			r -= CAMERA_MOVE_SPEED;
		}

		while (r > 2 * Math.PI) r -= 2.f * (float) Math.PI;
		while (r < 0) r += 2.f * (float) Math.PI;

		float lambda_x = 0;
		float lambda_y = 0;

		if (window.isKeyDown(GLFW.GLFW_KEY_W)) {
			lambda_x += (float) Math.cos(r) * MOVE_SPEED;
			lambda_y += (float) Math.sin(r) * MOVE_SPEED;
		}

		if (window.isKeyDown(GLFW.GLFW_KEY_S)) {
			lambda_x -= (float) Math.cos(r) * MOVE_SPEED;
			lambda_y -= (float) Math.sin(r) * MOVE_SPEED;
		}

		if (window.isKeyDown(GLFW.GLFW_KEY_D)) {
			lambda_x += (float) Math.cos(r + Math.PI / 2.) * MOVE_SPEED;
			lambda_y += (float) Math.sin(r + Math.PI / 2.) * MOVE_SPEED;
		}

		if (window.isKeyDown(GLFW.GLFW_KEY_A)) {
			lambda_x -= (float) Math.cos(r + Math.PI / 2.) * MOVE_SPEED;
			lambda_y -= (float) Math.sin(r + Math.PI / 2.) * MOVE_SPEED;
		}

		/*final float old_x;
		if (lambda_x > 0) {
			old_x = (float) Math.floor(x);
		} else {
			old_x = (float) Math.ceil(x);
		}

		final float old_y;
		if (lambda_y > 0) {
			old_y = (float) Math.floor(y);
		} else {
			old_y = (float) Math.ceil(y);
		}*/

		final float old_x = x;
		final float old_y = y;

		x += lambda_x;
		y += lambda_y;

		if (world.isSolid(x, old_y)) {
			x = (float) (lambda_x > 0 ? Math.floor(x) - .000001f : Math.ceil(x) + .000001f);
		}

		if (world.isSolid(old_x, y)) {
			y = (float) (lambda_y > 0 ? Math.floor(y) - .000001f : Math.ceil(y) + .000001f);
		}
	}

	public float getX() {
    	return x;
	}

	public float getY() {
    	return y;
	}

	public float getRotation() {
    	return r;
	}
}
