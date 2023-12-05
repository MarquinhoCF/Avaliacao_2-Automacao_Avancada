package io.sim.processing.reconciliation.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * A classe LeitorRelatorioCarros é responsável por ler dados de um arquivo Excel contendo informações de timestamps e distâncias.
 * Ela utiliza a biblioteca Apache POI para manipulação de arquivos Excel no formato XLSX.
 */
public class LeitorRelatorioCarros {

    // Construtor padrão da classe LeitorRelatorioCarros.
    public LeitorRelatorioCarros() {
        // O construtor está vazio, pois esta classe contém apenas métodos estáticos.
    }

    // Método estático para ler dados de um arquivo Excel.
    public static void lerExcel(String filePath, List<Double> timestamps, List<Double> distances) throws IOException {
        FileInputStream excelFile = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(excelFile);

        // Considerando que os dados estão na primeira planilha (Sheet)
        Sheet sheet = workbook.getSheetAt(0);

        // Iterar sobre as linhas da planilha, começando da segunda linha (índice 1)
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);

            // Obter o valor da coluna de Timestamp (assumindo que é a primeira coluna)
            Cell timestampCell = row.getCell(0);
            if (timestampCell != null) {
                timestamps.add(Double.parseDouble(timestampCell.toString()));
            }

            // Obter o valor da coluna de Distance (assumindo que é a última coluna)
            Cell distanceCell = row.getCell(row.getLastCellNum() - 1);
            if (distanceCell != null) {
                distances.add(Double.parseDouble(distanceCell.toString()));
            }
        }

        // Fechar o workbook para liberar recursos
        workbook.close();
    }
}
