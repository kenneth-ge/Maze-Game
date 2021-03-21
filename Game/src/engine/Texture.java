package engine;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import engine.MathUtils;

/* Modified from https://github.com/TheCherno/Flappy */

public class Texture {
	
	protected int width, height;
	protected int textureID;
	
	public Texture(String filePath){
		load(filePath);
	}
	
	private void load(String path) {
		int[] pixels = null;
		try {
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/" + path));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[] data = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}
		
		create(data);
	}
	
	public void create(int[] data) {
		int result = GL11.glGenTextures();
		
		textureID = result;
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, result);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, 
				GL11.GL_UNSIGNED_BYTE, MathUtils.toIntBuffer(data));
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public static void error(String action){
		int error = GL11.glGetError();
		if(error != 0){
			System.out.println("ERROR CODE: " + error + " ACTION: " + action);
		}
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public boolean isDeleted(){
		return GL11.glIsTexture(textureID);
	}

	public void destroy() {
		GL11.glDeleteTextures(textureID);
	}
	
	@Override
	public int hashCode() {
		return textureID;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Texture)) return false;
		Texture t = (Texture) other;
		return t.textureID == textureID;
	}
	
}
