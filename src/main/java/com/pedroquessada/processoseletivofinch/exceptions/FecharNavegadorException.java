package com.pedroquessada.processoseletivofinch.exceptions;

public class FecharNavegadorException extends Exception {
    public FecharNavegadorException() {
        super("Falha ao fechar o navegador");
    }
}
