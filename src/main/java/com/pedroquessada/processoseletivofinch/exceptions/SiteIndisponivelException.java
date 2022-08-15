package com.pedroquessada.processoseletivofinch.exceptions;

public class SiteIndisponivelException extends Exception {
    public SiteIndisponivelException(String url, String tempo) {
        super(String.format("O site \"%s\" estava indisponível durante a tentativa de acesso em %s", url, tempo));
    }
}
