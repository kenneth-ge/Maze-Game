package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.StringTokenizer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBEasyFont;

import engine.Camera;
import engine.Input;
import engine.MathUtils;
import engine.Model;
import engine.Mouse;
import engine.Shader;
import engine.Texture;
import model.EchoARLoader;

import static org.lwjgl.opengl.GL11.*;

public class Game {

	public Model cube, fox;
	public Texture texture, target, floor;
	public Shader shader;

	public int size;
	public int[][] walls;
	
	public float[][] randomR, randomG, randomB;
	public float[][] floorR, floorG, floorB;

	public Random random = new Random();

	public InetAddress addr;
	public int number;
	public BufferedReader br;
	public BufferedWriter bw;
	
	public float otherX = -1, otherZ = -1;
	
	public Game(InetAddress addr) {
		if(addr != null) {
			this.addr = addr;
			
			try {
				Socket s = new Socket(addr, 5555);
				br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
				String line = br.readLine();
				StringTokenizer tok = new StringTokenizer(line);
				
				this.number = Integer.parseInt(tok.nextToken());
				long seed = Long.parseLong(tok.nextToken());
				
				System.out.println("Seed: " + seed);
				random.setSeed(seed);
				
				new Thread(this::net).start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void init() {
		GL11.glEnable(GL11.GL_DEPTH_TEST); // Make sure you see the closest pixel/object
		GL11.glCullFace(GL11.GL_BACK); //Don't draw things you can't see (behind objects)
		
		Camera.create(this);
		Camera.setPos(new Vector3f(1, 1, 1));
		
		cube = Model.loadModel3D(vertices, textureCoords, indices, normals);
		texture = new Texture("res/wall.png"); // images are my creation
		target = new Texture("res/target.png");
		floor = new Texture("res/floor.png");
		shader = new Shader("shaders/shader.vert", "shaders/shader.frag");

		shader.enable();
		shader.setUniformMat4f("projectionMatrix",
				MathUtils.createProjectionMatrix(Initializer.width, Initializer.height, 70, 0.01f, 100f));
		
		size = random.nextInt(15) + 5;
		walls = new int[size][size]; // 0 means there is a wall; a nonzero number represents an empty space
		//Integer arrays fill with 0 by default
		
		randomR = new float[size][size];
		randomG = new float[size][size];
		randomB = new float[size][size];
		
		for (int i = 0; i<walls.length; i++) {
			for(int j = 0; j < walls[i].length; j++) {
				randomR[i][j] = (float) random.nextFloat();
				randomG[i][j] = (float) random.nextFloat();
				randomB[i][j] = (float) random.nextFloat();
			}
		}
		floorR = new float[size][size];
		floorG = new float[size][size];
		floorB = new float[size][size];
		
		for (int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				floorR[i][j] = (float) random.nextFloat();
				floorG[i][j] = (float) random.nextFloat();
				floorB[i][j] = (float) random.nextFloat();
			}
		}
		
		shader.disable();

		// Note that the rows and columns are the starting position and the ending
		// position, meaning we free a path to both tiles
		recurse(0, 0);
		recurse2(size - 1, size - 1);
		
		System.out.println("Loading echoAR");
		fox = EchoARLoader.loadModel();
	}
	
	private int[] dRow = new int[] {1, 0, -1, 0},
				  dCol = new int[] {0, 1, 0, -1}; //used to simplify the calculations through iteration rather than hard-coded means
	
	public boolean adjacentEmptyWithLoops(int row, int col) {
		int adjacent = adjacentEmpty(row, col);
		
		if(adjacent <= 1) {
			return false;
		}
		
		if(adjacent == 2) {
			return random.nextFloat() < 0.80;
		}
		
		if(adjacent == 3) {
			return random.nextFloat() < 0.95;
		}
		
		return true;
	}
	
	//Called from starting position (0, 0)
	private void recurse(int r, int c) { //Generates a random path such that no two adjacent tiles are free
		walls[r][c] = 1; //Mark this path as 1 to distinguish from recurse2
		
		int start = random.nextInt(4); //Choose random direction
		
		for(int i = 0; i < 4; i++) { //Cycle through until no more paths can be created
			int index = (start + i) % 4;
			int newRow = r + dRow[index];
			int newCol = c + dCol[index];
			
			//Out of bounds, open up area, visited
			if(outOfBounds(newRow, newCol) || adjacentEmptyWithLoops(newRow, newCol) || walls[newRow][newCol] == 1) {
				continue; //Do NOT recurse on this position if any above conditions are satisfied
			}
			
			recurse(newRow, newCol);
		}
	}
	
	/* Allows adjacent tiles to touch; recurses until a tile on this path intersects with a tile on the beginning path, thus
	 * guaranteeing a valid route. */
	private boolean recurse2(int r, int c) {
		walls[r][c] = 2;
		
		if(adjacentContains1(r, c)) { //If this path (the path from the end) connects with the path from the start
			return true;
		}
		
		int start = random.nextInt(4);
		
		
		for(int i = 0; i < 4; i++) {
			int index = (start + i) % 4;
			int newRow = r + dRow[index];
			int newCol = c + dCol[index];
			
			//Out of bounds, visited, clear area that does not intersect with the other path
			if(outOfBounds(newRow, newCol) || walls[newRow][newCol] == 2 || 
					(!adjacentContains1(newRow, newCol) && adjacentEmpty(newRow, newCol) > 1)) {
				continue;
			}
			
			if(recurse2(newRow, newCol)) { //Calls the method and terminates recursive calls if an intersection is detected
				return true;
			}
		}
		return false;
	}
	
	public boolean adjacentContains1(int row, int col) {
		for(int i = 0; i < 4; i++) {
			int newRow = row + dRow[i];
			int newCol = col + dCol[i];
			if(outOfBounds(newRow, newCol))
				continue;
			if(walls[newRow][newCol] == 1) {
				return true;
			}
		}
		return false;
	}
	
	public int adjacentEmpty(int row, int col) {
		int num = 0;
		for(int i = 0; i < 4; i++) {
			int newRow = row + dRow[i];
			int newCol = col + dCol[i];
			if(outOfBounds(newRow, newCol))
				continue;
			if(walls[newRow][newCol] != 0) {
				num++;
			}
		}
		return num;
	}
	
	public boolean outOfBounds(int row, int col) {
		return row < 0 || col < 0 || row >= size || col >= size;
	}

	public void renderText() {
		String text = "Test text";
		float scale = 10;
		
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);

        int quads = STBEasyFont.stb_easy_font_print(0, 0, text, null, charBuffer);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
        glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

		glClear(GL_COLOR_BUFFER_BIT);

		float scaleFactor = 1.0f + scale * 0.25f;

		glPushMatrix();
		// Zoom
		glScalef(scaleFactor, scaleFactor, 1f);
		// Scroll
		glTranslatef(4.0f, 4.0f - scale * scale, 0f);

		glDrawArrays(GL_QUADS, 0, quads * 4);

		glPopMatrix();

        glDisableClientState(GL_VERTEX_ARRAY);
	}
	
	public void render() {
		GL11.glClearColor(135f / 255f, 206f / 255f, 235f / 255f, 1);
		
		shader.enable();
		shader.setUniformMat4f("viewMatrix", Camera.viewMatrix);
		shader.setUniform3f("camPos", Camera.getPos());
		
		//Render target
		shader.setUniformMat4f("transformationMatrix", new Matrix4f().translate(size, 0, size));
		shader.setUniform1f("addR", 10000);
		shader.setUniform1f("addG", 10000);
		shader.setUniform1f("addB", 10000);
		fox.draw();
		shader.setUniform1f("addR", 0);
		shader.setUniform1f("addG", 0);
		shader.setUniform1f("addB", 0);
		
		Matrix4f transformation = new Matrix4f();
		//Render floor
		floor.bind();
		for(int x = 1; x <= size; x++) {
			for(int z = 1; z <= size; z++) {
				transformation = new Matrix4f();
				transformation.translate(x, -1, z);
				shader.setUniformMat4f("transformationMatrix", transformation);
				shader.setUniform1f("randomR", randomR[x - 1][z - 1]);
				shader.setUniform1f("randomG", randomG[x - 1][z - 1]);
				shader.setUniform1f("randomB", randomB[x - 1][z - 1]);
				cube.draw();
			}
		}
		floor.unbind();
		
		texture.bind();
		//Render outer boundary
		for(int i = 0; i < size + 2; i++) {
			for(int y = 0; y < 3; y++){
				{
					float posX = i;
					float posZ = 0;
					transformation = new Matrix4f();
					transformation.translate(posX, y, posZ);
					shader.setUniformMat4f("transformationMatrix", transformation);
					cube.draw();
				}
				{
					float posX = 0;
					float posZ = i;
					transformation = new Matrix4f();
					transformation.translate(posX, y, posZ);
					shader.setUniformMat4f("transformationMatrix", transformation);
					cube.draw();
				}
				{
					float posX = i;
					float posZ = size + 1;
					transformation = new Matrix4f();
					transformation.translate(posX, y, posZ);
					shader.setUniformMat4f("transformationMatrix", transformation);
					cube.draw();
				}
				{
					float posX = size + 1;
					float posZ = i;
					transformation = new Matrix4f();
					transformation.translate(posX, y, posZ);
					shader.setUniformMat4f("transformationMatrix", transformation);
					cube.draw();
				}
			}
		}
		//Render inner walls
		for(int x = 1; x <= size; x++) {
			for(int z = 1; z <= size; z++) {
				for(int y = 0; y < 3; y++) {
					if(walls[x - 1][z - 1] == 0) {
						transformation = new Matrix4f();
						
						//set the position
						transformation.translate(x, y, z);
						shader.setUniformMat4f("transformationMatrix", transformation);
						
						//set the color using shader.setUniform....
						shader.setUniform1f("randomR", randomR[x - 1][z - 1]);
						shader.setUniform1f("randomG", randomG[x - 1][z - 1]);
						shader.setUniform1f("randomB", randomB[x - 1][z - 1]);
						
						//draw the cube
						cube.draw();
					}
				}
			}
		}
		
		texture.unbind();
		
		//render other player
		shader.setUniform1f("randomR", 1);
		shader.setUniform1f("randomG", 1);
		shader.setUniform1f("randomB", 1);
		shader.setUniform1f("addR", 1);
		shader.setUniform1f("addG", 0);
		shader.setUniform1f("addB", 0);
		//System.out.println("Other position: " + otherX + " " + otherZ);
		shader.setUniformMat4f("transformationMatrix", new Matrix4f().scale(1).translate(otherX, 1, otherZ));
		
		cube.draw();
		
		shader.disable();
	}
	
	public boolean valid(Vector3f position) {
		float epsilon = 0.03f;
		for(int i = 0; i < 4; i++) {
			float offsetX = dRow[i] * epsilon;
			float offsetZ = dCol[i] * epsilon;
			if(!validSinglePoint(position.x + offsetX, position.z + offsetZ)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validSinglePoint(float x, float z) {
		int row = Math.round(x) - 1;
		int col = Math.round(z) - 1;
		
		if(outOfBounds(row, col))
			return false;
		
		return walls[row][col] != 0;
	}
	
	public void update() {
		Mouse.input(); //Updates mouse position in the code
		Camera.acceptInput(); //Updates camera
		Camera.apply(); //Updates view matrix used to position the object
		
		if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			running = false;
			GLFW.glfwSetWindowShouldClose(Initializer.window, true); //Tell the main game loop to exit
		}
		
		/*if(Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			Camera.addToY(0.1f);
		}
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			Camera.addToY(-0.1f);
		}*/
		
		int row = Math.round(Camera.getX()) - 1;
		int col = Math.round(Camera.getZ()) - 1;
		
		if(row == size - 1 && col == size - 1) { //If the player is touching the red target cube
			GLFW.glfwSetWindowShouldClose(Initializer.window, true); //Tell the main game loop to exit
		}
	}
	
	public boolean running = true;
	public void net() {
		try {
			while(running) {
				if(Camera.getPos() == null) {
					Thread.sleep(100);
				}
				
				float x = Camera.getX();
				float z = Camera.getZ();
				
				bw.append(number + ":" + x + " " + z);
				bw.flush();
				
				String reply = br.readLine();
				
				StringTokenizer tok = new StringTokenizer(reply);
				
				this.otherX = Float.parseFloat(tok.nextToken());
				this.otherZ = Float.parseFloat(tok.nextToken());
				
				Thread.sleep(50);
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() { //Destroy native objects to avoid wasting RAM
		cube.destroy();
		texture.destroy();
		shader.destroy();
		floor.destroy();
		target.destroy();
	}
	
	float[] vertices = {	//Vertices are scaled by 1/2 so they fit in a 1x1x1 area		
			-0.5f,0.5f,-0.5f,	
			-0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,-0.5f,	
			0.5f,0.5f,-0.5f,		
			
			-0.5f,0.5f,0.5f,	
			-0.5f,-0.5f,0.5f,	
			0.5f,-0.5f,0.5f,	
			0.5f,0.5f,0.5f,
			
			0.5f,0.5f,-0.5f,	
			0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,0.5f,	
			0.5f,0.5f,0.5f,
			
			-0.5f,0.5f,-0.5f,	
			-0.5f,-0.5f,-0.5f,	
			-0.5f,-0.5f,0.5f,	
			-0.5f,0.5f,0.5f,
			
			-0.5f,0.5f,0.5f,
			-0.5f,0.5f,-0.5f,
			0.5f,0.5f,-0.5f,
			0.5f,0.5f,0.5f,
			
			-0.5f,-0.5f,0.5f,
			-0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,0.5f
			
	};
	
	float[] normals = {			
			-1f,1f,-1f,	
			-1f,-1f,-1f,	
			1f,-1f,-1f,	
			1f,1f,-1f,		
			
			-1f,1f,1f,	
			-1f,-1f,1f,	
			1f,-1f,1f,	
			1f,1f,1f,
			
			1f,1f,-1f,	
			1f,-1f,-1f,	
			1f,-1f,1f,	
			1f,1f,1f,
			
			-1f,1f,-1f,	
			-1f,-1f,-1f,	
			-1f,-1f,1f,	
			-1f,1f,1f,
			
			-1f,1f,1f,
			-1f,1f,-1f,
			1f,1f,-1f,
			1f,1f,1f,
			
			-1f,-1f,1f,
			-1f,-1f,-1f,
			1f,-1f,-1f,
			1f,-1f,1f
			
	};
	
	float[] textureCoords = {
			
			0,0,
			0,1,
			1,1,
			1,0,			
			0,0,
			0,1,
			1,1,
			1,0,			
			0,0,
			0,1,
			1,1,
			1,0,
			0,0,
			0,1,
			1,1,
			1,0,
			0,0,
			0,1,
			1,1,
			1,0,
			0,0,
			0,1,
			1,1,
			1,0

			
	};
	
	int[] indices = {
			0,1,3,	
			3,1,2,	
			4,5,7,
			7,5,6,
			8,9,11,
			11,9,10,
			12,13,15,
			15,13,14,	
			16,17,19,
			19,17,18,
			20,21,23,
			23,21,22

	};

}
