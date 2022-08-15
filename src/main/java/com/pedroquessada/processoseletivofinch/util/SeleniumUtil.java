package com.pedroquessada.processoseletivofinch.util;

import com.pedroquessada.processoseletivofinch.exceptions.ElementoNaoEncontradoException;
import com.pedroquessada.processoseletivofinch.exceptions.UrlInvalidaException;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class SeleniumUtil {
    public void navegar(WebDriver webDriver, String url) throws UrlInvalidaException {
        try {
            webDriver.navigate().to(url);
        }
        catch (InvalidArgumentException e) {
            throw new UrlInvalidaException(url);
        }
    }

    public WebElement aguardarElementoVisivel(WebDriver webDriver,
                                              int tempoMaximoEspera,
                                              By identificadorElemento) throws ElementoNaoEncontradoException {
        try {
            return new WebDriverWait(webDriver, Duration.ofSeconds(tempoMaximoEspera))
                    .until(ExpectedConditions.visibilityOfElementLocated(identificadorElemento));

        } catch (Exception ex) {
            throw new ElementoNaoEncontradoException("aguardarElementoVisivel", identificadorElemento);
        }
    }

    public WebElement aguardarElementoClicavel(WebDriver webDriver,
                                               int tempoMaximoEspera,
                                               By identificadorElemento) throws ElementoNaoEncontradoException {
        try {
            return new WebDriverWait(webDriver, Duration.ofSeconds(tempoMaximoEspera))
                    .until(ExpectedConditions.elementToBeClickable(identificadorElemento));

        } catch (Exception ex) {
            throw new ElementoNaoEncontradoException("aguardarElementoClicavel", identificadorElemento);
        }
    }

    public List<WebElement> aguardarElementosVisiveis(WebDriver webDriver,
                                                      int tempoMaximoEspera,
                                                      By identificadorElemento) throws ElementoNaoEncontradoException {
        try {
            return new WebDriverWait(webDriver, Duration.ofSeconds(tempoMaximoEspera))
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(identificadorElemento));

        } catch (Exception ex) {
            throw new ElementoNaoEncontradoException("aguardarElementosVisiveis", identificadorElemento);
        }
    }

    public void moverParaElemento(WebDriver webDriver, int tempoMaximoEspera, By identificadorElemento) throws ElementoNaoEncontradoException {
        Actions action = new Actions(webDriver);

        WebElement webElement = aguardarElementoVisivel(webDriver, tempoMaximoEspera, identificadorElemento);
        action.moveToElement(webElement).perform();
    }
}
