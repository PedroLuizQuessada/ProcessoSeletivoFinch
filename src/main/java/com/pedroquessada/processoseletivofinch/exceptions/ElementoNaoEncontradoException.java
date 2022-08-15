package com.pedroquessada.processoseletivofinch.exceptions;

import org.openqa.selenium.By;

public class ElementoNaoEncontradoException extends Exception {
    public ElementoNaoEncontradoException(String metodo, By xpath) {
        super(String.format("Falha ao tentar encontrar o elemento \"%s\" via o método \"%s\"", xpath.toString(), metodo));
    }
}
