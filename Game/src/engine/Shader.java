package engine;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/* Modified from 
 * https://github.com/TheCherno/Flappy
 * https://www.youtube.com/watch?v=527bR2JHSR0*/

public class Shader {
	
	public static final int VERTEX_ATTRIB = 0;
	public static final int TCOORD_ATTRIB = 1;
	public static final int NORMAL_ATTRIB = 2;
	
	private boolean enabled = false;
	
	protected final int ID;
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	public Shader(int id){
		ID = id;
	}
	
	public Shader(String vertex, String fragment) {
		ID = load(vertex, fragment);
	}
	
	public int getUniform(String name) {
		if (locationCache.containsKey(name))
			return locationCache.get(name);
		
		int result = glGetUniformLocation(ID, name);
		if (result == -1){
			//System.err.println("Could not find uniform variable '" + name + "'!");
		}else
			locationCache.put(name, result);
		return result;
	}
	
	public void setUniformBool(String name, boolean value){
		if (!enabled) enable();
		float toLoad = 0;
		if(value)
			toLoad = 1;
		glUniform1f(getUniform(name), toLoad);
	}
	
	public void setUniform1i(String name, int value) {
		if (!enabled) enable();
		glUniform1i(getUniform(name), value);
	}
	
	public void setUniform1f(String name, float value) {
		if (!enabled) enable();
		glUniform1f(getUniform(name), value);
	}
	
	public void setUniform2f(String name, Vector2f vector) {
		setUniform2f(name, vector.x, vector.y);
	}
	
	public void setUniform2f(String name, float x, float y) {
		if (!enabled) enable();
		glUniform2f(getUniform(name), x, y);
	}
	
	public void setUniform3f(String name, Vector3f vector) {
		if (!enabled) enable();
		glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
	}
	
	public void setUniform3f(String name, float x, float y, float z) {
		if (!enabled) enable();
		glUniform3f(getUniform(name), x, y, z);
	}
	
	public void setUniform4f(String name, Vector4f vector) {
		if (!enabled) enable();
		glUniform4f(getUniform(name), vector.x, vector.y, vector.z, vector.w);
	}
	
	public void setUniform4f(String name, float x, float y, float z, float w) {
		if (!enabled) enable();
		glUniform4f(getUniform(name), x, y, z, w);
	}
	
	public void setUniformMat4f(String name, Matrix4f matrix) {
		if (!enabled) enable();
		glUniformMatrix4fv(getUniform(name), false, toFloatBuffer(matrix));
	}
	
	public static FloatBuffer toFloatBuffer(Matrix4f matrix){
		float[] array = new float[16];
		matrix.get(array);
		
		FloatBuffer fb = BufferUtils.createFloatBuffer(array.length);
		fb.put(array);
		fb.flip();
		
		return fb;
	}
	
	public void enable() {
		glUseProgram(ID);
		enabled = true;
	}
	
	public void disable() {
		glUseProgram(0);
		enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public static int load(String vertPath, String fragPath) {
		String vert = loadAsString(vertPath); 
		String frag = loadAsString(fragPath);
		return create(vert, frag);
	}
	
	public static int create(String vert, String frag) {
		int program = glCreateProgram(); 
		int vertID = glCreateShader(GL_VERTEX_SHADER);
		int fragID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(vertID, vert);
		glShaderSource(fragID, frag);
		
		glCompileShader(vertID);
		if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile vertex shader!");
			System.err.println(glGetShaderInfoLog(vertID));
			return -1;
		}
		
		glCompileShader(fragID);
		if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile fragment shader!");
			System.err.println(glGetShaderInfoLog(fragID));
			return -1;
		}
		
		glAttachShader(program, vertID);
		glAttachShader(program, fragID);
		glLinkProgram(program);
		glValidateProgram(program);
		
		glDetachShader(program, vertID);
		glDetachShader(program, fragID);
		
		glDeleteShader(vertID);
		glDeleteShader(fragID);
		
		return program;
	}

    public static String loadAsString(String path) {
        StringBuffer result = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Shader.class.getResourceAsStream("/" + path)));
            String buffer = new String();
            while ((buffer = br.readLine()) != null) {
                result.append(String.valueOf(buffer) + "\n");
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    
    public void destroy(){
    	GL20.glDeleteProgram(ID);
    }
}