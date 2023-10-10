package com.example.telegrambotjesderrr.service;

import com.example.telegrambotjesderrr.model.ModelPhoto;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Scanner;

public class PhotoService {

    private final AbsSender bot;

    public PhotoService(AbsSender bot) {
        this.bot = bot;
    }

    public String getPhoto(String message, ModelPhoto model, int photoNumber) throws IOException, ParseException {
        URL url = new URL("https://jsonplaceholder.typicode.com/" + message + "/" + photoNumber);
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }
        JSONObject object = new JSONObject(result);

        model.setAlbumId(object.getInt("albumId"));
        model.setId(object.getLong("id"));
        model.setTitle(object.getString("title"));
        model.setUrl(object.getString("url"));
        model.setThumbnailUrl(object.getString("thumbnailUrl"));


        return model.getUrl();
    }


    public void sendPhoto(Long chatId, String url) {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile(url));
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
