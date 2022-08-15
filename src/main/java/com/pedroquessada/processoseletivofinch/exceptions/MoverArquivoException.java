package com.pedroquessada.processoseletivofinch.exceptions;

public class MoverArquivoException extends Exception {
    public MoverArquivoException(String path) {
        super(String.format("Falha ao mover o arquivo %s", path));
    }
}
