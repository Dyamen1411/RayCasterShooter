package ca.dyamen.world.entities;

import org.lwjgl.input.Keyboard;

public class Player {
	public static final float MOVE_SPEED = 0.01f;
	public static final float CAMERA_MOVE_SPEED = 0.01f;
	
    private float x;
	private float y;
	private float r;

    public Player() {
		this(0, 0);
	}

	public Player(float x, float y) {
		this(x, y, 0);
	}

	public Player(float x, float y, float r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}

    public void update() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			x += (float) Math.cos(r) * MOVE_SPEED;
			y += (float) Math.sin(r) * MOVE_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			x -= (float) Math.cos(r) * MOVE_SPEED;
			y -= (float) Math.sin(r) * MOVE_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			x += (float) Math.cos(r + Math.PI / 2.) * MOVE_SPEED;
			y += (float) Math.sin(r + Math.PI / 2.) * MOVE_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			x -= (float) Math.cos(r + Math.PI / 2.) * MOVE_SPEED;
			y -= (float) Math.sin(r + Math.PI / 2.) * MOVE_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			r += CAMERA_MOVE_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			r -= CAMERA_MOVE_SPEED;
		}

		while (r > 2 * Math.PI) r -= 2.f * (float) Math.PI;
		while (r < 0) r += 2.f * (float) Math.PI;
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
