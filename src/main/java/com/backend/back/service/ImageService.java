package com.backend.back.service;

import com.backend.back.dto.ImageDTO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import java.io.IOException;


@Service
public class ImageService {

    public ImageDTO generateImage(String prompt){
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUserInput(prompt);
        String jsonInput = String.format("{\"message\" : \"%s\"}", prompt);

        JSONParser parser = new JSONParser();
        JSONObject output;

        String url = "https://mygeminiapiservicetemp.onrender.com/generate";

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(url);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setEntity(new StringEntity(jsonInput, "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    output = (JSONObject) parser.parse(result);
                    imageDTO.setPrompt(output.get("prompt").toString());
                    imageDTO.setImage(output.get("image").toString());
                }
            } catch (org.json.simple.parser.ParseException e) {
                System.err.println("HTTP request failed: " + e.getMessage());
            }
        } catch (IOException ex) {
            System.err.println("HTTP request failed: " + ex.getMessage());
        }

        return imageDTO;
    }
}
