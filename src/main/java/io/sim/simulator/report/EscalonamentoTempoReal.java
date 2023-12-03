package io.sim.simulator.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EscalonamentoTempoReal {
    private static String fileName = "EscalonamentoTempoReal.xlsx";

    public EscalonamentoTempoReal() {

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
        headerRow.createCell(1).setCellValue("Ji");
        headerRow.createCell(2).setCellValue("Ci");
        headerRow.createCell(3).setCellValue("Pi");
        headerRow.createCell(4).setCellValue("Di");
        headerRow.createCell(5).setCellValue("Threads");

        for (int i = 1; i <= 10; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("T" + i);
        }
    }

    public synchronized static void registrarTempo(String nome, int numeroTarefa, long startTime, long inicio, long fim) {
        synchronized (EscalonamentoTempoReal.class) {
            String nomeTarefa = "T" + numeroTarefa;
            long tempoProcessamento = fim - inicio;
            long tempoDecorrido = fim - startTime;
        
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
                long Ji = TimeUnit.MILLISECONDS.toSeconds(inicio);
                cell.setCellValue(Ji);
                System.out.println(nomeTarefa + " - Tempo de Chegada (Ji): " + Ji);
        
                cell = row.createCell(2);
                long Ci = TimeUnit.MILLISECONDS.toSeconds(fim);
                cell.setCellValue(Ci);
                System.out.println(nomeTarefa + " - Tempo de Conclusão (Ci): " + Ci);
        
                cell = row.createCell(3);
                cell.setCellValue(tempoProcessamento);
                System.out.println(nomeTarefa + " - Tempo de Processamento (Pi): " + tempoProcessamento + " milisegundos");
        
                cell = row.createCell(4);
                double Di = tempoDecorrido - tempoProcessamento;
                cell.setCellValue(Di);
                System.out.println(nomeTarefa + " - Tempo de Atraso (Di): " + Di + " milisegundos");
        
                cell = row.createCell(5);
                cell.setCellValue(nome);
        
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
    
}
