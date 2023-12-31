package com.example.telegrambotjesderrr;

import com.example.telegrambotjesderrr.config.BotConfig;
import com.example.telegrambotjesderrr.model.ModelMessage;
import com.example.telegrambotjesderrr.model.ModelPhoto;
import com.example.telegrambotjesderrr.service.MessageService;
import com.example.telegrambotjesderrr.service.PhotoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private Map<Long, String> userState;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                case "read" -> {
                    userState.put(chatId, "read");
                    sendMessage(chatId, "You've chosen to read, now enter the text number.");
                }
                case "see" -> {
                    userState.put(chatId, "see");
                    sendMessage(chatId, "You've chosen to see, now enter the photo number.");
                }
                default -> {
                    String state = userState.get(chatId);
                    try {
                        int number = Integer.parseInt(messageText);
                        if (isValidNumber(number)) {
                            if ("see".equals(state)) {
                                ModelPhoto modelPhoto = new ModelPhoto();
                                PhotoService photoService = new PhotoService(this);
                                String url = photoService.getPhoto("photos", modelPhoto, number);
                                photoService.sendPhoto(chatId, url);
                            } else if ("read".equals(state)) {
                                ModelMessage modelMessage = new ModelMessage();
                                MessageService messageService = new MessageService();
                                String resMessage = messageService.getMessage("posts", modelMessage, number);
                                sendMessage(chatId, resMessage);
                            }
                            userState.remove(chatId);
                        } else {
                            sendMessage(chatId, "Please enter a valid number (1-100).");
                        }
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "Please enter a number.");
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
//            switch (messageText) {
//                case "/start" -> {
//                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
//                }
//                case "read" -> {
//                    userState.put(chatId, "read");
//                    sendMessage(chatId, "You've chosen to read, now enter the text number.");
//                }
//                case "see" -> {
//                    userState.put(chatId, "see");
//                    sendMessage(chatId, "You've chosen to see, now enter the photo number.");
//                }
//                default -> {
//                    String state = userState.get(chatId);
//                    if ("see".equals(state)) {
//                        try {
//                            int photoNumber = Integer.parseInt(messageText);
//                            if (isValidNumber(photoNumber)) {
//                                ModelPhoto modelPhoto = new ModelPhoto();
//                                PhotoService photoService = new PhotoService(this);
//                                String url = photoService.getPhoto("photos", modelPhoto, photoNumber);
//                                photoService.sendPhoto(chatId, url);
//                                userState.remove(chatId);
//                            } else {
//                                sendMessage(chatId, "Please enter correct number.(0-100)");
//                            }
//                        } catch (NumberFormatException e) {
//                            sendMessage(chatId, "Please enter a number to get a photo.");
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        } catch (ParseException e) {
//                            throw new RuntimeException("Unable to parse message");
//                        }
//                    } else if ("read".equals(state)) {
//                        try {
//                            int textNumber = Integer.parseInt(messageText);
//                            if (isValidNumber(textNumber)) {
//                                ModelMessage modelMessage = new ModelMessage();
//                                MessageService messageService = new MessageService();
//                                String resMessage = messageService.getMessage("posts", modelMessage, textNumber);
//                                sendMessage(chatId, resMessage);
//                                userState.remove(chatId);
//                            } else {
//                                sendMessage(chatId, "Please enter correct number.(0-100)");
//                            }
//                        } catch (NumberFormatException e) {
//                            sendMessage(chatId, "Please enter a number to get a photo.");
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        } catch (ParseException e) {
//                            throw new RuntimeException("Unable to parse message");
//                        }
//                    } else {
//                        sendMessage(chatId, "Please select the 'see' or 'read' option.");
//                    }
//                }
//            }

    private boolean isValidNumber(int number) {
        return number >= 1 && number <= 100;
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Write down what you want to see or read?" + "\n" +
                "Write 'see' or 'read'!";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}