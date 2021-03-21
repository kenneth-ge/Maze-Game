package engine;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

import java.nio.DoubleBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

public class Mouse {
	static boolean mouseLocked = false;
	static double newX;
	static double newY;

	static double prevX = 0;
	static double prevY = 0;

	static boolean rotX = false;
	static boolean rotY = false;

	static float scroll;

	static long window;

	static int width, height;

	static private double deltaY;
	static private double deltaX;

	static private DoubleBuffer buffer;
	static private DoubleBuffer buffer2;

	public static void create(long window, int width, int height) {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		Mouse.width = width;
		Mouse.height = height;

		newX = width / 2;
		newY = height / 2;

		Mouse.window = window;

		mouseLocked = true;
	}

	public static void create(long window, int width, int height, boolean disabled) {
		if (disabled) {
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			mouseLocked = true;
		} else {
			mouseLocked = false;
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
		Mouse.width = width;
		Mouse.height = height;

		newX = width / 2;
		newY = height / 2;

		Mouse.window = window;
	}

	public static void input() {
		buffer = BufferUtils.createDoubleBuffer(1);
		buffer2 = BufferUtils.createDoubleBuffer(1);

		glfwGetCursorPos(window, buffer, buffer2);
		buffer.rewind();
		buffer2.rewind();

		newX = buffer.get();
		newY = buffer2.get();

		if (mouseLocked) {
			deltaX = newX - width / 2;
			deltaY = newY - height / 2;
		} else {
			deltaX = newX - prevX;
			deltaY = newY - prevY;
		}

		rotX = newX != prevX;
		rotY = newY != prevY;

		if (rotY) {
			// System.out.println("ROTATE Y AXIS: " + deltaY);

		}
		if (rotX) {
			// System.out.println("ROTATE X AXIS: " + deltaX);
		}

		prevX = newX;
		prevY = newY;

		if (mouseLocked)
			glfwSetCursorPos(window, width / 2, height / 2);
	}

	public static double getDeltaY() {
		return deltaX;
	}

	public static float getDeltaYf() {
		return (float) deltaX;
	}

	public static double getDeltaX() {
		return deltaY;
	}

	public static float getDeltaXf() {
		return (float) deltaY;
	}

	public static boolean getRotX() {
		return rotX;
	}

	public static boolean getRotY() {
		return rotY;
	}

	public static void setMouseLocked(boolean value) {
		mouseLocked = value;
	}

	public static boolean isMouseLocked() {
		return mouseLocked;
	}

	public static double getCursorPosX(long windowID) {
		buffer = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(windowID, buffer, null);
		return buffer.get(0);
	}

	public static void setScrollValue(float value) {
		Mouse.scroll = value;
	}

	public static float getScrollValue() {
		return scroll;
	}

	public static boolean isScrollUp() {
		return scroll > 0.5f;
	}

	public static boolean isScrollDown() {
		return scroll < 0.5f;
	}

	public static Vector2f getPosition() {
		buffer = BufferUtils.createDoubleBuffer(1);
		buffer2 = BufferUtils.createDoubleBuffer(1);

		glfwGetCursorPos(window, buffer, buffer2);
		buffer.rewind();
		buffer2.rewind();
		return new Vector2f((float) buffer.get(0), (float) buffer2.get(0));
	}

	public static double getCursorPosY(long windowID) {
		buffer = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(windowID, buffer, null);
		return buffer.get(0);
	}

}
