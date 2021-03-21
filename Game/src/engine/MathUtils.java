package engine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

public class MathUtils {

	public static IntBuffer toIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public static FloatBuffer toFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public static float pythag(float a, float b){
		return (float)Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}
	
	public static Matrix4f createProjectionMatrix(float WIDTH, float HEIGHT, float FOV, float NEAR_PLANE, float FAR_PLANE){
		Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(FOV),
            WIDTH/HEIGHT, NEAR_PLANE, FAR_PLANE);
			return projectionMatrix;
		}
	
	public static int roundEven(float num) {
		    return Math.round(num / 2) * 2;
	}
	
	public static float toRadians(float input) {
		return (float) Math.toRadians(input);
	}

	public static Vector2f setLength(Vector2f vector, float scalar) {
		return vector.normalize().mul(scalar);
	}
	
}
