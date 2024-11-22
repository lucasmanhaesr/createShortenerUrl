package com.lucas.generateshortenerurl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucas.generateshortenerurl.model.UrlData;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

        //Injetando as dependencias ObjectMapper e S3Client
        ObjectMapper objectMapper = new ObjectMapper();
        S3Client s3Client = S3Client.builder().build();

        //body passado na requisição
        String body = input.get("body").toString();

        //Criando um map que receberá o body
        Map<String, String> bodyMap;

        //Fazendo try catch pois os campos do body podem vir corretos,errados ou nulos
        try{

            //transformando a String body em um Map, usando a classe ObjectMapper.
            bodyMap = objectMapper.readValue(body, Map.class);

        } catch (JsonProcessingException exception){
            throw new RuntimeException("Error parsing JSON body " + exception.getMessage(), exception);
        }

        //Extraindo a URL para uma String
        String originalUrl = bodyMap.get("originalUrl");

        //Extraindo o tempo de expiração para uma String
        String expirationTime = bodyMap.get("expirationTime");

        //Transformando o tempo de expiração em segundos
        Long expirationTimeInSeconds = Long.parseLong(expirationTime);

        //Criando o UUID aleatorio e cortar a String pra conter 8 caracteres, começa no indice 0 e vai até 8
        String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

        //Criando objeto que representará um JSON no S3
        UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

        try{

            //Transformando nosso objeto model em uma String
            String urlDataJson = objectMapper.writeValueAsString(urlData);

            //Criando request com a String do nosso objeto para o bucket S3 e definindo a extesão do arquivo .json
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket("app-url-shortener-storage") //Nome do bucket
                .key(shortUrlCode + (".json")) //Nome do arquivo que será o UUID.json
                .build();

            //Enviando JSON para o bucket S3 contendo as informações do request e o objeto Json
            s3Client.putObject(request, RequestBody.fromString(urlDataJson));

        }catch (Exception exception){
            throw new RuntimeException("Error saving data to Amazon S3 " + exception.getMessage(), exception);
        }

        //Criando objeto de resposta
        Map<String, Object> response = new HashMap<>();
        response.put("code", shortUrlCode);
        return response;
    }
}