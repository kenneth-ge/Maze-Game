package model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Model;

public class OBJLoader {

	public static Model loadOBJ2(BufferedInputStream stream) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]) * 0.2f,
							(float) Float.valueOf(currentLine[2]) * 0.2f,
							(float) Float.valueOf(currentLine[3]) * 0.2f);
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices);
				processVertex(vertex2, vertices, indices);
				processVertex(vertex3, vertices, indices);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
				texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		Model data = Model.loadModel3D(verticesArray, texturesArray, indicesArray, normalsArray);
		return data;
	}

	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
					vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}
	
	private static void removeUnusedVertices(List<Vertex> vertices){
		for(Vertex vertex:vertices){
			if(!vertex.isSet()){
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
	
	
	
	
	
	public static Model loadOBJ(BufferedInputStream stream) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		String ln;
		while ((ln = br.readLine()) != null) {
            if (ln == null || ln.equals("") || ln.startsWith("#")) {
            	
            } else {
                String[] split = ln.split(" ");
                switch (split[0]) {
                    case "v":
                        vertices.add(new Vector3f(
                                Float.parseFloat(split[1]) * 0.1f,
                                Float.parseFloat(split[2]) * 0.1f,
                                Float.parseFloat(split[3]) * 0.1f
                        ));
                        break;
                    case "vn":
                        normals.add(new Vector3f(
                                Float.parseFloat(split[1]),
                                Float.parseFloat(split[2]),
                                Float.parseFloat(split[3])
                        ));
                        break;
                    case "vt":
                        textures.add(new Vector2f(
                                Float.parseFloat(split[1]),
                                Float.parseFloat(split[2])
                        ));
                        break;
                    case "f":
                        addAll(indices,
                                    Integer.parseInt(split[1].split("/")[0]) - 1,
                                    Integer.parseInt(split[2].split("/")[0]) - 1,
                                    Integer.parseInt(split[3].split("/")[0]) - 1,
                                
                                    Integer.parseInt(split[1].split("/")[1]) - 1,
                                    Integer.parseInt(split[2].split("/")[1]) - 1,
                                    Integer.parseInt(split[3].split("/")[1]) - 1,
                                
                                    Integer.parseInt(split[1].split("/")[2]) - 1,
                                    Integer.parseInt(split[2].split("/")[2]) - 1,
                                    Integer.parseInt(split[3].split("/")[2]) - 1
                        );
                        break;
                    default:
                        System.err.println("[OBJ] Unknown Line: " + ln);
                }
            }
		}
		
		Model m = Model.loadModel3D(toArray3f(vertices), toArray2f(textures), toArray(indices), toArray3f(normals));
		
		return m;
	}
	
	public static float[] toArray3f(List<Vector3f> l) {
		float[] ret = new float[l.size() * 3];
		
		for(int i = 0; i < l.size(); i++) {
			ret[3 * i] = l.get(i).x;
			ret[3 * i + 1] = l.get(i).y;
			ret[3 * i + 2] = l.get(i).z;
		}
		
		return ret;
	}
	
	public static float[] toArray2f(List<Vector2f> l) {
		float[] ret = new float[l.size() * 2];
		
		for(int i = 0; i < l.size(); i++) {
			ret[2 * i] = l.get(i).x;
			ret[2 * i + 1] = l.get(i).y;
		}
		
		return ret;
	}
	
	public static int[] toArray(List<Integer> i) {
		int[] x = new int[i.size()];
		
		for(int j = 0; j < i.size(); j++) {
			x[j] = i.get(j);
		}
		
		return x;
	}
		
		private static void addAll(List<Integer> indices, int...is ) {
			for(int i: is) {
				indices.add(i);
			}
		}
}
