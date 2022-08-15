package com.pedroquessada.processoseletivofinch.exceptions;

public class UrlInvalidaException extends Exception {
    public UrlInvalidaException(String url) {
        super(String.format("A url \"%s\" é inválida", url));
    }
}
