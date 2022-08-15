package com.pedroquessada.processoseletivofinch.exceptions;

public class LojaNaoIdentificadaException extends Exception {
    public LojaNaoIdentificadaException(String loja) {
        super(String.format("Loja %s não é uma loja válida. Favor conferir o atributo \"rpa.loja\" no arquivo application.properties para mais informações.", loja));
    }
}
