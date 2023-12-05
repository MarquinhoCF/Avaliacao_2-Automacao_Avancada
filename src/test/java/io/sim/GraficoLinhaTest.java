package io.sim;

import static org.junit.Assert.*;
import org.jfree.chart.JFreeChart;
import org.junit.Test;

import io.sim.processing.reconciliation.chart.GraficoLinha;

import java.util.ArrayList;

public class GraficoLinhaTest {

    @Test
    public void testGraficoLinha() {
        // Criar instância de GraficoLinha
        GraficoLinha lineChart = new GraficoLinha("Teste", "m/s");

        // Adicionar dados fictícios
        ArrayList<Double> dados = new ArrayList<>();
        dados.add(10.0);
        dados.add(15.0);
        dados.add(20.0);

        // Adicionar dados ao gráfico
        for (int i = 0; i < dados.size(); i++) {
            lineChart.addData(i, dados.get(i));
        }

        // Verificar se os dados foram adicionados corretamente
        assertEquals(dados.size(), lineChart.getChart().getXYPlot().getDataset().getItemCount(0));

        // Obtém o gráfico
        JFreeChart chart = lineChart.getChart();

        // Verificar se o título do gráfico é o esperado
        assertEquals("Teste", chart.getTitle().getText());

        // Verificar se o gráfico tem um XYPlot
        assertNotNull(chart.getPlot());

        // Verificar se o gráfico é visível
        assertTrue(lineChart.isVisible());
    }

    @Test
    public void testPlotarGraficoLinha() {
        // Criar instância de GraficoLinha
        GraficoLinha lineChart = new GraficoLinha("Teste", "m/s");

        // Adicionar dados fictícios
        ArrayList<Double> dados = new ArrayList<>();
        dados.add(10.0);
        dados.add(15.0);
        dados.add(20.0);

        // Chamar o método para plotar o gráfico
        lineChart.plotarGraficoLinha("Teste", dados, "m/s");

        // Verificar se os dados foram adicionados corretamente
        assertEquals(dados.size(), lineChart.getChart().getXYPlot().getDataset().getItemCount(0));

        // Verificar se o gráfico é visível
        assertTrue(lineChart.isVisible());
    }
}
