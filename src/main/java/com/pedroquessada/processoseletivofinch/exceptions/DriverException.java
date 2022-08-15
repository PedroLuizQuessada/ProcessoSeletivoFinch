package com.pedroquessada.processoseletivofinch.exceptions;

public class DriverException extends Exception {
    public DriverException(String navegador) {
        super(String.format("Falha ao iniciar o driver do navegador %s", navegador));
    }
}
