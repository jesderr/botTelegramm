package com.example.telegrambotjesderrr.model;

import lombok.Data;

@Data
public class ModelMessage {
    Long userId;
    Long id;
    String title;
    String body;
}

