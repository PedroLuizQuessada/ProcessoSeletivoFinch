package com.pedroquessada.processoseletivofinch.exceptions;

public class GeracaoPlanilhaResultadoException extends Exception {
    public GeracaoPlanilhaResultadoException() {
        super("Falha ao tentar gerar planilha com resultado");
    }
}
