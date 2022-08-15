package com.pedroquessada.processoseletivofinch.exceptions;

public class TimerUtilException extends Exception {
    public TimerUtilException(long tempo, String unidadeTempo) {
        super(String.format("Falha ao aguardar %d %s", tempo, unidadeTempo));
    }
}
