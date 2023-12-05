package io.sim;

import static org.junit.Assert.*;
import org.jfree.chart.JFreeChart;
import org.junit.Test;

import io.sim.processing.reconciliation.chart.GraficoDispersao;

import java.util.ArrayList;

public class GraficoDispersaoTest {

    @Test
    public void testGraficoDispersao() {
        // Criar instância de GraficoDispersao
        GraficoDispersao scatterPlot = new GraficoDispersao("Teste");

        // Adicionar dados fictícios
        ArrayList<Double> xData = new ArrayList<>();
        ArrayList<Double> yData = new ArrayList<>();
        xData.add(1.0);
        yData.add(2.0);
        xData.add(3.0);
        yData.add(4.0);

        // Adicionar dados ao gráfico
        for (int j = 0; j < xData.size(); j++) {
            scatterPlot.addData(xData.get(j), yData.get(j));
        }

        // Verificar se os dados foram adicionados corretamente
        assertEquals(xData.size(), scatterPlot.getChart().getXYPlot().getDataset().getItemCount(0));

        // Obtém o gráfico
        JFreeChart chart = scatterPlot.getChart();

        // Verificar se o título do gráfico é o esperado
        assertEquals("Teste", chart.getTitle().getText());

        // Verificar se o gráfico tem um XYPlot
        assertNotNull(chart.getPlot());

        // Verificar se o gráfico é visível
        assertTrue(scatterPlot.isVisible());
    }

    @Test
    public void testPlotarGraficosDispersoes() {
        // Criar instância de GraficoDispersao
        GraficoDispersao scatterPlot = new GraficoDispersao("Teste");

        // Criar dados fictícios
        ArrayList<ArrayList<Double>> todosOsT = new ArrayList<>();
        ArrayList<ArrayList<Double>> todosOsD = new ArrayList<>();
        ArrayList<Double> xData1 = new ArrayList<>();
        ArrayList<Double> yData1 = new ArrayList<>();
        xData1.add(1.0);
        yData1.add(3.0);
        todosOsT.add(xData1);
        todosOsD.add(yData1);

        // Adicionar dados ao gráfico
        scatterPlot.plotarGraficosDispersoes(todosOsT, todosOsD);

        // Verificar se os dados foram adicionados corretamente
        assertEquals(xData1.size(), scatterPlot.getChart().getXYPlot().getDataset().getItemCount(0));

        // Verificar se o gráfico é visível
        assertTrue(scatterPlot.isVisible());
    }
}
