package com.pedroquessada.processoseletivofinch.services;

import com.pedroquessada.processoseletivofinch.exceptions.GeracaoPlanilhaResultadoException;
import com.pedroquessada.processoseletivofinch.exceptions.MoverArquivoException;
import com.pedroquessada.processoseletivofinch.objetos.Pendencia;
import com.pedroquessada.processoseletivofinch.objetos.Produto;
import com.pedroquessada.processoseletivofinch.util.ConversorUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class GoogleDriveService {

    @Value("${rpa.google-drive-path}")
    private String googleDrivePath;

    @Autowired
    private ConversorUtil conversorUtil;

    private final static String PATH_ROOT = "/AutomaçãoFinch";
    private final static String PATH_PENDENTES = PATH_ROOT + "/Pendentes/";
    private final static String PATH_PROCESSADOS = PATH_ROOT + "/Processados/";
    private final static String PATH_RESULTADOS = PATH_ROOT + "/Resultados/";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<Pendencia> identificarPendencias() {
        logger.info("Identificando pendências");
        List<Pendencia> pendencias = new ArrayList<>();

        File folder = new File(googleDrivePath + PATH_PENDENTES);
        File[] files = folder.listFiles();
        if (files == null) {
            files = new File[0];
        }

        for (File file : files) {
            Pendencia pendencia = new Pendencia();
            pendencia.setNomeArquivo(file.getName());

            if (file.isFile() && (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx"))) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Workbook workbook = new XSSFWorkbook(fileInputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    for (Row row : sheet) {
                        Cell cell = row.getCell(0);
                        if (cell != null) {
                            String nomeProduto = cell.getStringCellValue();
                            if (nomeProduto.length() > 0) {
                                Produto produto = new Produto();
                                produto.setNome(nomeProduto);
                                pendencia.addProduto(produto);
                            }
                        }
                    }

                    pendencia.setFormatoInvalido(false);
                }
                catch (IOException e) {
                    pendencia.setFormatoInvalido(true);
                }
            }
            else {
                pendencia.setFormatoInvalido(true);
            }

            pendencias.add(pendencia);
        }

        return pendencias;
    }

    public void retirarPendenciaFila(Pendencia pendencia) throws MoverArquivoException {
        String novoPath = adicionarHoraExecucao(pendencia.getNomeArquivo(), googleDrivePath + PATH_PROCESSADOS, false);

        File pendente = new File(googleDrivePath + PATH_PENDENTES + pendencia.getNomeArquivo());
        if (!pendente.renameTo(new File(novoPath))) {
            throw new MoverArquivoException(novoPath);
        }
    }

    public Map<String, Object> gerarPlanilhaResultado(Pendencia pendencia, Integer numeroResultados, Workbook workbook, boolean fecharArquivo, boolean subirDrive) throws GeracaoPlanilhaResultadoException {
        if (workbook == null) {
            workbook = new XSSFWorkbook();
        }

        Sheet sheet = workbook.createSheet(pendencia.getNomeArquivo());
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        if (pendencia.isFormatoInvalido()) {
            headerCell.setCellValue(String.format("Formato do arquivo %s inválido", pendencia.getNomeArquivo()));
            headerCell.setCellStyle(headerStyle);
        }
        else {
            headerCell.setCellValue("Nome");
            headerCell.setCellStyle(headerStyle);

            for (int i = 1; i <= numeroResultados; i++) {
                headerCell = header.createCell(i);
                headerCell.setCellValue(String.format("%dº preço mais barato", i));
                headerCell.setCellStyle(headerStyle);
            }

            for (Produto produto : pendencia.getProdutos()) {
                Row row = sheet.createRow(pendencia.getProdutos().indexOf(produto) + 1);

                Cell cell = row.createCell(0);
                cell.setCellValue(produto.getNome());

                for (Double preco : produto.getPrecos()) {
                    cell = row.createCell(produto.getPrecos().indexOf(preco) + 1);
                    cell.setCellValue(preco);
                }

                if (produto.getPrecos().isEmpty()) {
                    cell = row.createCell(1);
                    cell.setCellValue("Não foram encontrados produtos");
                }
            }
        }

        try {
            File outputFile = null;
            FileOutputStream outputStream;
            if (subirDrive) {
                String path = adicionarHoraExecucao(pendencia.getNomeArquivo(), googleDrivePath + PATH_RESULTADOS, true);
                outputStream = new FileOutputStream(path);
            }
            else {
                outputFile = File.createTempFile(conversorUtil.getDateToString(Calendar.getInstance(), "dd_MM_yyyy_HH_mm_sss"), ".xlsx");
                outputStream = new FileOutputStream(outputFile);
            }
            workbook.write(outputStream);
            if (fecharArquivo) {
                workbook.close();
            }

            Map<String, Object> retorno = new HashMap<>();
            retorno.put("workbook", workbook);
            retorno.put("file", outputFile);
            return retorno;
        }
        catch (IOException e) {
            throw new GeracaoPlanilhaResultadoException();
        }
    }

    private String adicionarHoraExecucao(String nomeArquivo, String path, boolean converterParaExcel) {
        int inicioExtensao = nomeArquivo.indexOf(".");
        String horaExecucao = conversorUtil.getDateToString(Calendar.getInstance(), "dd_MM_yyyy_HH_mm_sss");

        String novoPath = path + nomeArquivo;
        if (inicioExtensao > -1) {
            inicioExtensao = inicioExtensao + path.length();
            if (converterParaExcel) {
                novoPath = novoPath.substring(0, inicioExtensao) + horaExecucao + ".xlsx";
            }
            else {
                novoPath = novoPath.substring(0, inicioExtensao) + horaExecucao + novoPath.substring(inicioExtensao);
            }
        }
        else {
            if (converterParaExcel) {
                novoPath = novoPath + horaExecucao + ".xlsx";
            }
            else {
                novoPath = novoPath + horaExecucao;
            }
        }

        return novoPath;
    }

    public File gerarPlanilhaResultadoCompilada(List<Pendencia> pendencias, Integer numeroResultados) throws GeracaoPlanilhaResultadoException {
        Workbook workbook = null;
        File file = null;
        for (Pendencia pendencia : pendencias) {
            Map<String, Object> map = gerarPlanilhaResultado(pendencia, numeroResultados, workbook, pendencias.indexOf(pendencia) == pendencias.size() - 1, false);
            workbook = (Workbook) map.get("workbook");
            file = (File) map.get("file");
        }

        return file;
    }
}
