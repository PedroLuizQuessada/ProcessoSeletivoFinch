package com.pedroquessada.processoseletivofinch.exceptions;

public class NavegadorNaoIdentificadoException extends Exception {
    public NavegadorNaoIdentificadoException(String navegador) {
        super(String.format("Navegador \"%s\" não é um navegador válido. Favor conferir o atributo \"driver.navegador\" no arquivo application.properties para mais informações.", navegador));
    }
}
