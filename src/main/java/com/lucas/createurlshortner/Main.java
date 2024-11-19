package com.lucas.createurlshortner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

        //body passado na requisição
        String body = input.get("body").toString();

        //Desserializando o body para obter os valores passado na requisição
        Map<String, String> bodyMap;

        //inicializando a classe ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        //Fazendo try catch pois os campos do body podem vir corretos,errados ou nulos
        try{
            //transformando a String body em um Map, usando a classe ObjectMapper
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch (JsonProcessingException exception){
            throw new RuntimeException("Error parsing JSON body" + exception.getMessage(), exception);
        }

        //Extraindo a URL para uma String
        String originalUrl = bodyMap.get("originalUrl");

        //Extraindo o tempo de expiração para uma String
        String expirationTime = bodyMap.get("expirationTime");

        //Criando o UUID aleatorio e cortar a String pra conter 8 caracteres, começa no indice 0 e vai até 8
        String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

        //Criando objeto de resposta
        Map<String, Object> response = new HashMap<>();
        response.put("code", shortUrlCode);
        return response;
    }
}