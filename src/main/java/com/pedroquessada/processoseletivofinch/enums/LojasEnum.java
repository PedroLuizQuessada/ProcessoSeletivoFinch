package com.pedroquessada.processoseletivofinch.enums;

public enum LojasEnum {
    AMERICANAS("https://americanas.com.br/");

    private final String url;

    LojasEnum(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
