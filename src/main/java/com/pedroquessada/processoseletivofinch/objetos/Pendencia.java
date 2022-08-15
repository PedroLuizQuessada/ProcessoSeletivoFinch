package com.pedroquessada.processoseletivofinch.objetos;

import java.util.ArrayList;
import java.util.List;

public class Pendencia {
    private String nomeArquivo;
    private final List<Produto> produtos = new ArrayList<>();
    private boolean formatoInvalido;

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void addProduto(Produto produto) {
        produtos.add(produto);
    }

    public boolean isFormatoInvalido() {
        return formatoInvalido;
    }

    public void setFormatoInvalido(boolean formatoInvalido) {
        this.formatoInvalido = formatoInvalido;
    }
}
