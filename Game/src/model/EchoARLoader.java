package model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import engine.Model;

public class EchoARLoader {

	public static Model loadModel() {
		HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).build();
		
		HttpRequest request = HttpRequest.newBuilder(URI.create("https://console.echoar.xyz/query?key=young-field-0597"))
		.header("Content-Type", "application/json").GET().build();
		
		CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
		
		HttpResponse<String> resp;
		try {
			resp = response.get();
			resp.body();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(resp.body());
			
			var hologram = get(get(get(obj, "db"), "571df155-e7d5-4dd3-8c82-7c5d17ef4af5"), "hologram");
			
			var url = "https://console.echoar.xyz/query?key=young-field-0597&file=" + hologram.get("storageID");
			System.out.println(url);
			
			BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
			
			return OBJLoader.loadOBJ2(in);
		} catch (InterruptedException | ExecutionException | ParseException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static JSONObject get(JSONObject o, String property) {
		return (JSONObject) o.get(property);
	}
	
}
