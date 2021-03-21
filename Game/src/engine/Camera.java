package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import game.Game;

/* Modified from https://stackoverflow.com/questions/23727493/creating-a-camera-class-with-lwjgl */

public class Camera {
	
    public static float moveSpeed = 0.1f;

    private static float maxLook = 85;

    private static float mouseSensitivity = 0.10f;

    private static Vector3f pos;
    private static Vector3f rotation;
    
    public static Matrix4f viewMatrix = new Matrix4f();
    
    private static Game game;

    public static void create(Game game) {
        pos = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        Camera.game = game;
        rotation.y = 180;
        rotation.x = -90;
    }

    public static void apply() {
        while(rotation.y > 360) {
            rotation.y -= 360;
        }
        while(rotation.y < -360) {
            rotation.y += 360;
        }
        
        viewMatrix.setLookAt(0.0F, 0.0F, 0.01F, 
        	      0.0F, 0.0F, 0.0F, 
        	      0.0F, 1.0F, 0.0F)
        	      .rotateX((float)Math.toRadians(rotation.x)) //pitch
        	      .rotateY((float)Math.toRadians(rotation.y)) //yaw
        	      .translate(-pos.x, -pos.y, -pos.z);
    }

    public static void acceptInput() {
        acceptInputRotate();
        acceptInputMove();
    }

    public static void acceptInputRotate() {
        if(Mouse.isMouseLocked()) {
            float mouseDX = Mouse.getDeltaYf();
            float mouseDY = Mouse.getDeltaXf();
            rotation.y += mouseDX * mouseSensitivity;
            rotation.x += mouseDY * mouseSensitivity;
            rotation.x = Math.max(-maxLook, Math.min(maxLook, rotation.x));
        }
    }
    
    private static Vector3f delta;

    public static void acceptInputMove() {
        boolean keyUp = Input.isKeyDown(GLFW.GLFW_KEY_W);
        boolean keyDown = Input.isKeyDown(GLFW.GLFW_KEY_S);
        boolean keyRight = Input.isKeyDown(GLFW.GLFW_KEY_D);
        boolean keyLeft = Input.isKeyDown(GLFW.GLFW_KEY_A);
        boolean keyFast = Input.isKeyDown(GLFW.GLFW_KEY_Q);
        boolean keySlow = Input.isKeyDown(GLFW.GLFW_KEY_E);

        float speed;

        if(keyFast) {
            speed = moveSpeed * 5;
        }
        else if(keySlow) {
            speed = moveSpeed / 2;
        }else{
            speed = moveSpeed;
        }
        
        delta = new Vector3f();

        if(keyDown) {
        	delta.x -= Math.sin(Math.toRadians(rotation.y)) * speed;
        	delta.z += Math.cos(Math.toRadians(rotation.y)) * speed;
        }
        if(keyUp) {
        	delta.x += Math.sin(Math.toRadians(rotation.y)) * speed;
        	delta.z -= Math.cos(Math.toRadians(rotation.y)) * speed;
        }
        if(keyLeft) {
        	delta.x += Math.sin(Math.toRadians(rotation.y - 90)) * speed;
        	delta.z -= Math.cos(Math.toRadians(rotation.y - 90)) * speed;
        }
        if(keyRight) {
        	delta.x += Math.sin(Math.toRadians(rotation.y + 90)) * speed;
        	delta.z -= Math.cos(Math.toRadians(rotation.y + 90)) * speed;
        }
        
        //Separate collision axes so you can slide along a wall
        if(game.valid(new Vector3f(pos).add(delta.x, 0, 0))) {
        	pos.add(delta.x, 0, 0);
        }
        
        if(game.valid(new Vector3f(pos).add(0, 0, delta.z))) {
        	pos.add(0, 0, delta.z);
        }
    }

    public static void setSpeed(float speed) {
        moveSpeed = speed;
    }

    public static void setPos(Vector3f pos) {
        Camera.pos = pos;
    }

    public static Vector3f getPos() {
        return pos;
    }

    public static void setX(float x) {
        pos.x = x;
    }

    public static float getX() {
        return pos.x;
    }

    public static void addToX(float x) {
        pos.x += x;
    }

    public static void setY(float y) {
        pos.y = y;
    }

    public static float getY() {
        return pos.y;
    }

    public static void addToY(float y) {
        pos.y += y;
    }

    public static void setZ(float z) {
        pos.z = z;
    }

    public static float getZ() {
        return pos.z;
    }

    public static void addToZ(float z) {
        pos.z += z;
    }

    public static void setRotation(Vector3f rotation) {
        Camera.rotation = rotation;
    }

    public static Vector3f getRotation() {
        return rotation;
    }

    public static void setRotationX(float x) {
        rotation.x = x;
    }

    public static float getRotationX() {
        return rotation.x;
    }

    public static void addToRotationX(float x) {
        rotation.x += x;
    }

    public static void setRotationY(float y) {
        rotation.y = y;
    }

    public static float getRotationY() {
        return rotation.y;
    }

    public static void addToRotationY(float y) {
        rotation.y += y;
    }

    public static void setRotationZ(float z) {
        rotation.z = z;
    }

    public static float getRotationZ() {
        return rotation.z;
    }

    public static void addToRotationZ(float z) {
        rotation.z += z;
    }

    public static void setMaxLook(float maxLook) {
        Camera.maxLook = maxLook;
    }

    public static float getMaxLook() {
        return maxLook;
    }

    public static void setMouseSensitivity(float mouseSensitivity) {
        Camera.mouseSensitivity = mouseSensitivity;
    }

    public static float getMouseSensitivity() {
        return mouseSensitivity;
    }
}
