package com.emailreplier.EmailReplier.service;


import com.emailreplier.EmailReplier.entity.EmailEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class emailService {

    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private  String geminiUrl;

    @Value("${gemini.api.key}")
    private  String geminiApiKey;

    public emailService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String generateEmailReply(EmailEntity emailEntity){

        String prompt=buildPrompt(emailEntity);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );
        String apiUrl = geminiUrl + "?key=" + geminiApiKey;

        String response = webClient.post()
                .uri(apiUrl)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException("Gemini API error: " + errorBody))
                )
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .block();

        return extractReponseContent(response);
    }

    private String extractReponseContent(String response){
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode= mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }
        catch (Exception e){
            return "Error"+e.getMessage();
        }
    }


    private String buildPrompt(EmailEntity emailEntity){
        StringBuilder prompt= new StringBuilder();
        prompt.append("Generate a ");
        if (emailEntity.getTone()!=null&&!emailEntity.getTone().isEmpty()){
            prompt.append(emailEntity.getTone());
        }
        else {
            prompt.append(" professional ");
        }
         prompt.append("email reply for the following email content:\n").append(emailEntity.getEmailResponse())
                 .append(" .Please dont generate a subject line . Only reply you have to give");
        return prompt.toString();
    }
}
