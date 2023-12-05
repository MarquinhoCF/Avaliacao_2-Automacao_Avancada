package io.sim.processing.scheduling.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import io.sim.App;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe responsável por gerenciar o relatório de escalonamento em formato Excel.
 */
public class RelatorioEscalonamento {
    private static String fileName = "RelatorioEscalonamento.xlsx";

    // Construtor padrão da classe RelatorioEscalonamento.
    public RelatorioEscalonamento() {

    }

    // Cria uma tabela de tarefas vazia no arquivo Excel.
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

    // Cria o cabeçalho da tabela de tarefas.
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

    // Registra o tempo de execução de uma tarefa no relatório.
    public synchronized static void registrarTempo(String nome, int numeroTarefa, long startTime, long inicio, long fim, long estimativaManualDi) {
        synchronized (RelatorioEscalonamento.class) {
            String nomeTarefa = "T" + numeroTarefa;

            long Ji = startTime - App.TEMPOINICIOGERAL; // Tempo de chegada, mantido como está
            long Ci = fim - inicio; // Tempo de conclusão em milissegundos
            long tempoProcessamento = fim - startTime; // Tempo de processamento em milissegundos
            double Di = estimativaManualDi; // Mantenha Di como está, assumindo que já está em milissegundos

            try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
                Workbook workbook = WorkbookFactory.create(fileInputStream);
                Sheet sheet = workbook.getSheet("Tabela de Tarefas");

                Row row = sheet.getRow(numeroTarefa);
                if (row == null) {
                    row = sheet.createRow(numeroTarefa);
                    row.createCell(0).setCellValue(nomeTarefa);
                }

                Cell cell = row.createCell(1);
                cell.setCellValue(nome);

                cell = row.createCell(2);
                cell.setCellValue(Ji);
                System.out.println(nomeTarefa + " - Tempo de Chegada (Ji): " + Ji + " milisegundos");

                cell = row.createCell(3);
                cell.setCellValue(Ci);
                System.out.println(nomeTarefa + " - Tempo de Conclusão (Ci): " + Ci + " milissegundos");

                cell = row.createCell(4);
                cell.setCellValue(tempoProcessamento);
                System.out.println(nomeTarefa + " - Tempo de Processamento (Pi): " + tempoProcessamento + " milissegundos");

                cell = row.createCell(5);
                cell.setCellValue(Di);
                System.out.println(nomeTarefa + " - Tempo de Atraso (Di): " + Di + " milissegundos");

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

    // Obtém os dados de uma coluna específica da tabela de tarefas.
    public static ArrayList<Double> obterDadosColuna(int coluna) {
        ArrayList<Double> valores = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheet("Tabela de Tarefas");

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

    // Cria a planilha "Análise de Escalonamento" no arquivo Excel, caso não exista.
    public static void criarAnaliseEscalonamento() {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheet("Análise de Escalonamento");

            if (sheet == null) {
                sheet = workbook.createSheet("Análise de Escalonamento");

                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("Tempo de Resposta Máximo");

                row = sheet.createRow(1);
                row.createCell(0).setCellValue("Utilização do Processador");

                row = sheet.createRow(2);
                row.createCell(0).setCellValue("Limite de Utilização do Processador");

                row = sheet.createRow(3);
                row.createCell(0).setCellValue("Escalonável");

                try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                    workbook.write(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adiciona os valores da análise de escalonamento na planilha correspondente.
    public static void adicionarValoresAnaliseEscalonamento(double tempoRespostaMaximo, double utilizacao, double limiteUtilizacao, String escalonavel) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheet("Análise de Escalonamento");

            Row row = sheet.getRow(0);
            Cell cell = row.createCell(1);
            cell.setCellValue(tempoRespostaMaximo);

            row = sheet.getRow(1);
            cell = row.createCell(1);
            cell.setCellValue(utilizacao);

            row = sheet.getRow(2);
            cell = row.createCell(1);
            cell.setCellValue(limiteUtilizacao);

            row = sheet.getRow(3);
            cell = row.createCell(1);
            cell.setCellValue(escalonavel);

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
