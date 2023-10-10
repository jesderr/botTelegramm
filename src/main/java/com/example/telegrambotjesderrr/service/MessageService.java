package com.example.telegrambotjesderrr.service;

import com.example.telegrambotjesderrr.TelegramBot;
import com.example.telegrambotjesderrr.model.ModelMessage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Scanner;

public class MessageService {
    public String getMessage(String message, ModelMessage model, int numberMessage) throws IOException, ParseException {
        URL url = new URL("https://jsonplaceholder.typicode.com/" + message + "/" + numberMessage);
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }
        JSONObject object = new JSONObject(result);

        model.setUserId(object.getLong("userId"));
        model.setId(object.getLong("id"));
        model.setTitle(object.getString("title"));
        model.setBody(object.getString("body"));


        return "Id: " + model.getId() + "\n" +
                "Title: " + model.getTitle() + "\n" +
                "Body: " + model.getBody();
    }
}
