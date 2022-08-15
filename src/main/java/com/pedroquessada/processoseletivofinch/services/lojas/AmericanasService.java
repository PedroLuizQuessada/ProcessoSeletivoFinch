package com.pedroquessada.processoseletivofinch.services.lojas;

import com.pedroquessada.processoseletivofinch.enums.LojasEnum;
import com.pedroquessada.processoseletivofinch.exceptions.*;
import com.pedroquessada.processoseletivofinch.objetos.Pendencia;
import com.pedroquessada.processoseletivofinch.objetos.Produto;
import com.pedroquessada.processoseletivofinch.util.ConversorUtil;
import com.pedroquessada.processoseletivofinch.util.SeleniumUtil;
import com.pedroquessada.processoseletivofinch.util.TimerUtil;
import com.pedroquessada.processoseletivofinch.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class AmericanasService implements LojaService {

    private final WebDriverUtil webDriverUtil;
    private final SeleniumUtil seleniumUtil;
    private final ConversorUtil conversorUtil;
    private final TimerUtil timerUtil;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private WebDriver webDriver;
    private final Integer numeroResultados;

    @Autowired
    public AmericanasService(WebDriverUtil webDriverUtil, SeleniumUtil seleniumUtil, ConversorUtil conversorUtil, TimerUtil timerUtil, Integer numeroResultados) {
        this.webDriverUtil = webDriverUtil;
        this.seleniumUtil = seleniumUtil;
        this.conversorUtil = conversorUtil;
        this.timerUtil = timerUtil;
        this.numeroResultados = numeroResultados;
    }

    public void iniciarRotina(Pendencia pendencia) throws NavegadorNaoIdentificadoException, SiteIndisponivelException, UrlInvalidaException, FecharNavegadorException, ElementoNaoEncontradoException, TimerUtilException, DriverException {
        logger.info("Rotina iniciada");

        try {
            webDriver = webDriverUtil.getWebDriver();
            acessarSite();

            for (Produto produto : pendencia.getProdutos()) {
                pesquisarProduto(produto);
            }

            fecharNavegador();
        }
        catch (NavegadorNaoIdentificadoException | SiteIndisponivelException | UrlInvalidaException |
               ElementoNaoEncontradoException | TimerUtilException e) {
            fecharNavegador();

            throw e;
        }
    }

    @Override
    public void acessarSite() throws SiteIndisponivelException, UrlInvalidaException {
        logger.info(String.format("Acessando o site %s", getUrl()));

        seleniumUtil.navegar(webDriver, getUrl());
        try {
            seleniumUtil.aguardarElementoVisivel(webDriver, 15, By.xpath("//input[@placeholder='busque aqui seu produto']"));
        }
        catch (ElementoNaoEncontradoException e) {
            throw new SiteIndisponivelException(getUrl(), conversorUtil.getDateToString(Calendar.getInstance(), "dd/MM/yyyy HH:mm"));
        }
    }

    @Override
    public String getUrl() {
        return LojasEnum.AMERICANAS.getUrl();
    }

    @Override
    public void pesquisarProduto(Produto produto) throws ElementoNaoEncontradoException, TimerUtilException {
        seleniumUtil.aguardarElementoVisivel(webDriver, 10, By.xpath("//input[@placeholder='busque aqui seu produto']")).clear();
        seleniumUtil.aguardarElementoVisivel(webDriver, 10, By.xpath("//input[@placeholder='busque aqui seu produto']")).sendKeys(produto.getNome() + Keys.ENTER);

        try {
            timerUtil.aguardarSegundos(2);
            seleniumUtil.aguardarElementoClicavel(webDriver, 10, By.xpath("//button[contains(text(), 'continuar e fechar')]")).click();
        }
        catch (ElementoNaoEncontradoException e){
            //mensagem sobre cookies não apareceu
        }

        try {
            seleniumUtil.aguardarElementoVisivel(webDriver, 10, By.xpath("//select[@id='sort-by']"));
        }
        catch (ElementoNaoEncontradoException e) {
            //produto não encontrado
            return;
        }

        ordenarResultados();

        timerUtil.aguardarSegundos(5);
        List<WebElement> webElements = seleniumUtil.aguardarElementosVisiveis(webDriver, 10, By.xpath("//a[@aria-current='page']/div[3]/span[1]"));
        for (WebElement webElement : webElements) {
            produto.addPreco(conversorUtil.getStringToDouble(webElement.getText().replace("R$ ", "").replace(".", "").replace(",", ".")));

            if (webElements.indexOf(webElement) == numeroResultados - 1) {
                break;
            }
        }
    }

    @Override
    public void ordenarResultados() throws ElementoNaoEncontradoException, TimerUtilException {
        seleniumUtil.moverParaElemento(webDriver, 10, By.xpath("//select[@id='sort-by']"));
        timerUtil.aguardarSegundos(2);
        seleniumUtil.aguardarElementoClicavel(webDriver, 10, By.xpath("//select[@id='sort-by']")).click();
        seleniumUtil.aguardarElementoClicavel(webDriver, 10, By.xpath("//option[@value='lowerPriceRelevance']")).click();
    }

    @Override
    public void fecharNavegador() throws FecharNavegadorException {
        if (webDriver != null) {
            webDriverUtil.fecharNavegador(webDriver);
        }
    }
}
