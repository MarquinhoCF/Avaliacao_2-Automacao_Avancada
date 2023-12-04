package io.sim.processing.scheduling.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RelatorioEscalonamento {
    private static String fileName = "RelatorioEscalonamento.xlsx";

    public RelatorioEscalonamento() {

    }

    public static void criarTabelaTarefas() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tabela de Tarefas");

            // Cria o cabeçalho da planilha
            criaCabecalhoTabelaTarefas(sheet);

            // Salva o arquivo Excel
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void criaCabecalhoTabelaTarefas(Sheet sheet) {
        Row headerRow = sheet.createRow(0);

        // Define os cabeçalhos das colunas
        headerRow.createCell(0).setCellValue("Tarefas");
        headerRow.createCell(1).setCellValue("Classe");
        headerRow.createCell(2).setCellValue("Ji");
        headerRow.createCell(3).setCellValue("Ci");
        headerRow.createCell(4).setCellValue("Pi");
        headerRow.createCell(5).setCellValue("Di");

        for (int i = 1; i <= 10; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("T" + i);
        }
    }

    public synchronized static void registrarTempo(String nome, int numeroTarefa, long startTime, long inicio, long fim, long estimativaManualDi) {
        synchronized (RelatorioEscalonamento.class) {
            String nomeTarefa = "T" + numeroTarefa;
            
            long Ji = TimeUnit.MILLISECONDS.toSeconds(startTime);
            long Ci = TimeUnit.MILLISECONDS.toSeconds(fim);
            long tempoProcessamento = fim - inicio;
            double Di = estimativaManualDi;
        
            // Escrever os valores na planilha
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(fileName))) {
                Sheet sheet = workbook.getSheet("Tabela de Tarefas");
        
                // Verifica se a linha já existe, se não, cria uma nova
                Row row = sheet.getRow(numeroTarefa);
                if (row == null) {
                    row = sheet.createRow(numeroTarefa);
                    row.createCell(0).setCellValue(nomeTarefa);
                }
        
                Cell cell = row.createCell(1);
                cell.setCellValue(nome);

                cell = row.createCell(2);
                cell.setCellValue(Ji);
                System.out.println(nomeTarefa + " - Tempo de Chegada (Ji): " + Ji);
        
                cell = row.createCell(3);
                cell.setCellValue(Ci);
                System.out.println(nomeTarefa + " - Tempo de Conclusão (Ci): " + Ci);
        
                cell = row.createCell(4);
                cell.setCellValue(tempoProcessamento);
                System.out.println(nomeTarefa + " - Tempo de Processamento (Pi): " + tempoProcessamento + " milisegundos");
        
                cell = row.createCell(5);
                cell.setCellValue(Di);
                System.out.println(nomeTarefa + " - Tempo de Atraso (Di): " + Di + " milisegundos");
        
                // Escrever no arquivo
                try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                    workbook.write(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Double> obterDadosColuna(int coluna) {
        ArrayList<Double> valores = new ArrayList<Double>();

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(fileName))) {
            Sheet sheet = workbook.getSheet("Tabela de Tarefas");

            // Itera sobre as linhas da sheet, começando da segunda linha (índice 1)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(coluna);
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        double valor = cell.getNumericCellValue();
                        valores.add(valor);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return valores;
    }
    
}
