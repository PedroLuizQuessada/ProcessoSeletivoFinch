package com.pedroquessada.processoseletivofinch.services.lojas;

import com.pedroquessada.processoseletivofinch.exceptions.*;
import com.pedroquessada.processoseletivofinch.objetos.Pendencia;
import com.pedroquessada.processoseletivofinch.objetos.Produto;

public interface LojaService {
    void iniciarRotina(Pendencia pendencia) throws NavegadorNaoIdentificadoException, SiteIndisponivelException, UrlInvalidaException, FecharNavegadorException, ElementoNaoEncontradoException, TimerUtilException, DriverException;
    void acessarSite() throws SiteIndisponivelException, UrlInvalidaException;
    String getUrl();
    void pesquisarProduto(Produto produto) throws ElementoNaoEncontradoException, TimerUtilException;
    void fecharNavegador() throws FecharNavegadorException;
}
