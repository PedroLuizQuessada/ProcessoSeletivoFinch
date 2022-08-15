package com.pedroquessada.processoseletivofinch.objetos;

import java.util.ArrayList;
import java.util.List;

public class Produto {
    private String nome;
    private final List<Double> precos = new ArrayList<>();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Double> getPrecos() {
        return precos;
    }

    public void addPreco(Double preco) {
        precos.add(preco);
    }
}
