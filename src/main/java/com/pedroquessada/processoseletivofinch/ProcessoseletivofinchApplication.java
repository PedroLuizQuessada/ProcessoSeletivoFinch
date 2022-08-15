package com.pedroquessada.processoseletivofinch;

import com.pedroquessada.processoseletivofinch.enums.LojasEnum;
import com.pedroquessada.processoseletivofinch.exceptions.*;
import com.pedroquessada.processoseletivofinch.objetos.Pendencia;
import com.pedroquessada.processoseletivofinch.services.GoogleDriveService;
import com.pedroquessada.processoseletivofinch.services.lojas.AmericanasService;
import com.pedroquessada.processoseletivofinch.services.lojas.LojaService;
import com.pedroquessada.processoseletivofinch.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.File;
import java.util.List;

@SpringBootApplication
public class ProcessoseletivofinchApplication {

    @Value("${rpa.loja}")
    private String loja;

    @Value("${rpa.minutos-intervalo-execucao}")
    private int intervaloExecucao;

    @Value("${rpa.numero-resultados}")
    private Integer numeroResultados;

    @Value("${email}")
    private String destinatario;

    @Value("${spring.mail.username}")
    private String emailAdm;

    @Autowired
    private GoogleDriveService googleDriveService;

    @Autowired
    private TimerUtil timerUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private WebDriverUtil webDriverUtil;

    @Autowired
    private SeleniumUtil seleniumUtil;

    @Autowired
    private ConversorUtil conversorUtil;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ProcessoseletivofinchApplication.class);
        builder.headless(false);
        builder.run(args);
    }

    @PostConstruct
    public void init() {
        try {
            while (true) {
                LojaService lojaService = identificarLoja();
                List<Pendencia> pendencias = googleDriveService.identificarPendencias();

                for (Pendencia pendencia : pendencias) {
                    if (!pendencia.isFormatoInvalido()) {
                        lojaService.iniciarRotina(pendencia);
                    }

                    googleDriveService.retirarPendenciaFila(pendencia);
                    googleDriveService.gerarPlanilhaResultado(pendencia, numeroResultados, null, true, true);
                }

                if (pendencias.isEmpty()) {
                    logger.info("Não existem novas pendências");
                }
                else {
                    File planilhaResultadoCompilado = googleDriveService.gerarPlanilhaResultadoCompilada(pendencias, numeroResultados);
                    emailUtil.enviarEmail("Resultado processamento automação", "Segue em anexo a planilha com o resultado do processamento da automação", destinatario, planilhaResultadoCompilado);
                }

                logger.info(String.format("Execução finalizada, aguardando %d minuto(s)", intervaloExecucao));
                timerUtil.aguardarMinutos(intervaloExecucao);
            }
        }
        catch (TimerUtilException | NavegadorNaoIdentificadoException | SiteIndisponivelException | UrlInvalidaException
               | FecharNavegadorException | EmailException | LojaNaoIdentificadaException | ElementoNaoEncontradoException
               | GeracaoPlanilhaResultadoException | MoverArquivoException | DriverException e) {
            e.printStackTrace();
            try {
                emailUtil.enviarEmail("Problema na automação", String.format("Falha na automação: %s", e.getMessage()), emailAdm, null);
            }
            catch (EmailException e1) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LojaService identificarLoja() throws LojaNaoIdentificadaException {
        LojasEnum lojaEnum;
        try {
            lojaEnum = LojasEnum.valueOf(loja.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new LojaNaoIdentificadaException(loja);
        }

        switch (lojaEnum) {
            case AMERICANAS:
                return new AmericanasService(webDriverUtil, seleniumUtil, conversorUtil, timerUtil, numeroResultados);

            default:
                throw new LojaNaoIdentificadaException(loja);
        }
    }

}
