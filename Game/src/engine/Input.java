package engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

/* Modified from 
 * https://github.com/TheCherno/Flappy
 * https://www.youtube.com/watch?v=527bR2JHSR0
 * */

public class Input extends GLFWKeyCallback {

	public static boolean[] keys = new boolean[65536];

	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action != GLFW.GLFW_RELEASE;
	}

	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}

}