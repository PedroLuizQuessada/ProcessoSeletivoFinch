package com.pedroquessada.processoseletivofinch.exceptions;

public class EmailException extends Exception {
    public EmailException() {
        super("Falha ao enviar e-mail");
    }
}
