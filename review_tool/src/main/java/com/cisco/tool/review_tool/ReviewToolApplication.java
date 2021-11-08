package com.cisco.tool.review_tool;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
public class ReviewToolApplication {

	private static RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		//SpringApplication.run(ReviewToolApplication.class, args);
		List<String> lines = new ArrayList<String>();
		HttpURLConnection connection;
		String authString = "aypandia:Aug78@1493";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		try {
//		connection = (HttpURLConnection) new URL("https://bitbucket-eng-sjc1.cisco.com/bitbucket/projects/APICACI/repos/aci-telemetry/pull-requests/12588/commits").openConnection();
//		//connection.setRequestProperty("Authorization", "Basic " + new String(authStringEnc));
//		connection.setRequestProperty("headers", getHeaders().toString());
//		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//		String line;
//		while ((line = reader.readLine()) != null) {
//		    lines.add(line);
//		}
//	//	JSONObject lastestCommit = new JSONObject(new JSONObject(reader.readLine()).getJSONArray("values").get(0).toString());
//		//System.out.println("Pull request "+lastestCommit.toString());
//		System.out.println(lines.toString());
		
		
		String urlToAccess = "https://bitbucket-eng-sjc1.cisco.com/bitbucket/rest/api/latest/projects/ATOM/repos/agent/pull-requests/31/diff/";
		   URL repositoryUrl = new URL (urlToAccess);
		    connection = (HttpURLConnection) repositoryUrl.openConnection();
		   //For authentication
		   connection.addRequestProperty("Authorization", "Basic "+authStringEnc); 
		   connection.setRequestMethod("GET");
		   connection.connect();
		  // System.out.println(connection.getResponseCode()+" "+connection.getContent().toString());
		   //The InputStream is required to read in the data of the GET request
		   InputStream connectionDataStream = connection.getInputStream();
		   String connectionStreamData = IOUtils.toString(connectionDataStream, StandardCharsets.UTF_8);
		   //System.out.println(connectionStreamData); 
		   
		   try {
			      FileWriter myWriter = new FileWriter("C://users/aypandia/filename.json");
			      myWriter.write(connectionStreamData);
			      myWriter.close();
			      System.out.println("Successfully wrote to the file.");
			    } catch (IOException e) {
			      System.out.println("An error occurred.");
			      e.printStackTrace();
			    }
		   JSONObject obj=new JSONObject(connectionStreamData);
		   JSONArray jsonarray = (JSONArray) obj.get("diffs");

		}catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e);
		}
	}
	
	public static HttpHeaders getHeaders() {
		String authString = "aypandia:Aug78@1493";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();

		map.add("grant_type", "client_credentials");

		HttpHeaders header = new HttpHeaders();

		header.setBasicAuth(HttpHeaders.AUTHORIZATION,authStringEnc);

		header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, header);

//		ResponseEntity<Object> responseEntity = restTemplate.postForEntity("https://bitbucket.org/site/oauth2/access_token", request, Object.class);
//
//		LinkedHashMap<String, Object> body = (LinkedHashMap)responseEntity.getBody();

		header = new HttpHeaders();

		header.setContentType(MediaType.APPLICATION_JSON);

		header.set(HttpHeaders.AUTHORIZATION, "Bearer " +"OTU1Nzg4MjQ5MjY1Oqe1hwlionVUDFcJ33pUAIb7QC5C");

		//header.set(HttpHeaders.AUTHORIZATION, "Bearer " + requireNonNull(body).get("access_token").toString());

		return header;
	}

//	private HttpHeaders requireNonNull(LinkedHashMap<String, Object> body) {
//		// TODO Auto-generated method stub
//		return body;
//	}

}
