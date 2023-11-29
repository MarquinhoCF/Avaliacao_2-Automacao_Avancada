package io.sim.reconciliation.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ReconciliationReport {

    private static String fileName = "ReconciliationReport.xlsx";

    public ReconciliationReport() {

    }

    // Método para criar a planilha com os cabeçalhos "t0, d0, t1, d1, t2, d2"
    public static void criaRecociliationReport(int numeroParticoes) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TimeDistanceReport");

            // Cria o cabeçalho da planilha
            criaCabecalhoTimeDistance(sheet, numeroParticoes);

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

    // Método para criar o cabeçalho da planilha "t0, d0, t1, d1, t2, d2"
    private static void criaCabecalhoTimeDistance(Sheet sheet, int numeroParticoes) {
        Row headerRow = sheet.createRow(0);

        // Define os cabeçalhos das colunas
        for (int i = 0; i < (numeroParticoes - 1); i++) {
            headerRow.createCell(i * 2).setCellValue("t" + (i + 1));
            headerRow.createCell(i * 2 + 1).setCellValue("d" + (i + 1));
        }
        headerRow.createCell(36).setCellValue("tTOTAL");
        headerRow.createCell(37).setCellValue("dTOTAL");
    }


    // Adiciona os pares de valores de dois ArrayLists a uma linha específica
public static void adicionaValoresALinha(int linha, ArrayList<Double> temposParciais, ArrayList<Double> distanciasParciais) {
    try (Workbook workbook = WorkbookFactory.create(new FileInputStream(fileName))) {
        // Adiciona os pares de valores à linha especificada na primeira sheet
        Sheet sheet = workbook.getSheetAt(0); // Obtém a primeira sheet
        Row row = sheet.getRow(linha); // Obtém a linha específica

        if (row == null) {
            row = sheet.createRow(linha);
        }

        int colNum = 0; // Começar da primeira coluna

        // Itera sobre os pares de valores de temposParciais e distanciasParciais
        for (int i = 0; i < temposParciais.size(); i++) {
            double tempoParcial = temposParciais.get(i);
            double distanciaParcial = distanciasParciais.get(i);

            // Adiciona o valor de temposParciais
            Cell cellTempo = row.createCell(colNum);
            cellTempo.setCellValue(tempoParcial);
            colNum++;

            // Adiciona o valor de distanciasParciais
            Cell cellDistancia = row.createCell(colNum);
            cellDistancia.setCellValue(distanciaParcial);
            colNum++;
        }

        // Salva as alterações na planilha
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

