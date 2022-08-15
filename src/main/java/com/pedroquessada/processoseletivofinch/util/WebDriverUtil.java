package com.pedroquessada.processoseletivofinch.util;

import com.pedroquessada.processoseletivofinch.enums.NavegadoresEnum;
import com.pedroquessada.processoseletivofinch.exceptions.DriverException;
import com.pedroquessada.processoseletivofinch.exceptions.FecharNavegadorException;
import com.pedroquessada.processoseletivofinch.exceptions.NavegadorNaoIdentificadoException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebDriverUtil {

    @Value("${driver.navegador}")
    private String navegador;

    @Value("${driver.chrome-driver-path}")
    private String pathChromeDriver;

    @Value("${driver.firefox-driver-path}")
    private String pathFirefoxDriver;

    @Autowired
    private TimerUtil timerUtil;

    public WebDriver getWebDriver() throws NavegadorNaoIdentificadoException, DriverException {
        WebDriver webDriver;
        NavegadoresEnum navegadorEnum;
        try {
            navegadorEnum = NavegadoresEnum.valueOf(navegador.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new NavegadorNaoIdentificadoException(navegador);
        }

        switch (navegadorEnum) {
            case CHROME:
                webDriver = getChromeWebDriver();
                break;

            case FIREFOX:
                webDriver = getFirefoxWebDriver();
                break;

            default:
                throw new NavegadorNaoIdentificadoException(navegador);
        }

        webDriver.manage().window().maximize();
        return webDriver;
    }

    private WebDriver getChromeWebDriver() throws DriverException {
        try {
            System.setProperty("webdriver.chrome.driver", pathChromeDriver);
            return new ChromeDriver();
        }
        catch (Exception e) {
            throw new DriverException("chrome");
        }
    }

    private WebDriver getFirefoxWebDriver() throws DriverException {
        try {
            System.setProperty("webdriver.gecko.driver", pathFirefoxDriver);
            return new FirefoxDriver();
        }
        catch (Exception e) {
            throw new DriverException("firefox");
        }
    }

    public void fecharNavegador(WebDriver webDriver) throws FecharNavegadorException {
        try {
            timerUtil.aguardarSegundos(2);
            webDriver.quit();
        }
        catch (Exception e) {
            throw new FecharNavegadorException();
        }
    }
}
