package engine;

import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

public class Model {

	protected int vaoID;
	protected int vertexCount;
	
	protected int[] vbos;
	
	public Model(int vaoID, int vertexCount, int...vbos){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vbos = vbos;
	}
	
	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public void unbindDraw() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
	}
	
	public void bind() {
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}
	
	public void unbind() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public void draw(){
		bind();
		unbindDraw();
		unbind();
	}
	
	public void draw(Texture t){
		t.bind();
		draw();
		t.unbind();
	}
	
	public static Model loadModel2D(float[] positions, float[] textureCoords, int[] indices){
		int vaoID = createVertexArray();
		
		Model model = createModel(vaoID, indices.length);
		
		GL30.glBindVertexArray(vaoID);
		Texture.error("BIND VERTEX ARRAY 1");
		model.bindIndicesBuffer(indices);
		Texture.error("STORE INDICES");
		model.storeDataInAttribute(positions, 0, 2);
		Texture.error("STORE POSITIONS");
		model.storeDataInAttribute(textureCoords, 1, 2);
		Texture.error("STORE TEXTURE COORDS");
		
		GL30.glBindVertexArray(0);
		Texture.error("BIND VERTEX ARRAY");
		return model;
	}
	
	public static Model loadModel3D(float[] positions, float[] textureCoords, int[] indices, float[] normals){
		int vaoID = createVertexArray();
		
		Model model = createModel(vaoID, indices.length);
		GL30.glBindVertexArray(vaoID);
		
		model.bindIndicesBuffer(indices);
		model.storeDataInAttribute(positions, 0, 3);
		model.storeDataInAttribute(textureCoords, 1, 2);
		model.storeDataInAttribute(normals, 2, 3);
		
		GL30.glBindVertexArray(0);
		
		return model;
	}
	
	protected int bindIndicesBuffer(int[] indices) {
		int ibo = GL15.glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, MathUtils.toIntBuffer(indices), GL_STATIC_DRAW);
		return ibo;
	}
	
	protected int storeDataInAttribute(float[] data, int attribNum, int values) {
		int vbo = GL15.glGenBuffers();
		glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		
		glBufferData(GL15.GL_ARRAY_BUFFER, MathUtils.toFloatBuffer(data), GL_STATIC_DRAW);
		
		GL20.glVertexAttribPointer(attribNum, values, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(attribNum);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		return vbo;
	}
	
	private static Model createModel(int vaoID, int indicesLength, int... vbos) {
		return new Model(vaoID, indicesLength, vbos);
	}
	
	private static int createVertexArray() {
		if(GL.getCapabilities().OpenGL45) {
			return GL45.glCreateVertexArrays();
		}else{
			return GL30.glGenVertexArrays();
		}
	}
	
	public void destroy(){
		for(int v: vbos)
			GL15.glDeleteBuffers(v);
		GL30.glDeleteVertexArrays(vaoID);
	}
	
	@Override
	public int hashCode() {
		return vaoID;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Model)) return false;
		Model m = (Model) other;
		return m.vaoID == vaoID;
	}
	
}
