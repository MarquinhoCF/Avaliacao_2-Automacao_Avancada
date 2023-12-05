package io.sim.processing.reconciliation.chart;

import java.awt.Dimension;
import java.util.ArrayList;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.ValueMarker;
import java.awt.BasicStroke;
import java.awt.Color;

/**
 * A classe GraficoLinha representa um gráfico de linha utilizando a biblioteca JFreeChart.
 * Permite a criação de gráficos com dados de fluxos e velocidade.
 */
public class GraficoLinha extends ApplicationFrame {
    private JFreeChart chart;
    private XYSeries series;
    private XYSeriesCollection dataset;

    // Construtor da classe GraficoLinha.
    public GraficoLinha(String title, String unidadeMedida) {
        super(title);
        series = new XYSeries("Data");
        dataset = new XYSeriesCollection(series);
        
        // Criando o gráfico de linha
        chart = ChartFactory.createXYLineChart(
                title,
                "Fluxos",
                "Velocidade [" + unidadeMedida + "]",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Configurando o painel do gráfico
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 500));
        setContentPane(chartPanel);
    }

    // Obtém o gráfico gerado.
    public JFreeChart getChart() {
        return chart;
    }

    // Adiciona dados ao gráfico.
    public void addData(double x, double y) {
        series.add(x + 1, y); // Ajuste para começar a partir de 1
    }

    // Método estático para plotar gráfico de linha com média.
    public static void plotarGraficoLinha(String titulo, ArrayList<Double> dados, String unidadeMedida) {
        GraficoLinha lineChart = new GraficoLinha(titulo, unidadeMedida);

        // Adicionando os dados ao gráfico
        for (int i = 0; i < dados.size() - 1; i++) {
            lineChart.addData(i, dados.get(i));
        }

        // Obtendo a média dos dados
        double media = dados.get(dados.size() - 1);

        // Adicionando uma linha horizontal para representar a média
        lineChart.addMediaLine(media);

        // Obtendo o plot do gráfico
        XYPlot plot = (XYPlot) lineChart.getChart().getPlot();
        plot.getDomainAxis().setRange(1, dados.size());
        plot.getRangeAxis().setAutoRange(true);

        // Obtendo o renderizador do gráfico
        XYItemRenderer renderer = plot.getRenderer();

        // Configurando a espessura da linha dos pontos
        ChartColor color = new ChartColor(14, 18, 77);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, color);

        // Exibindo o gráfico
        lineChart.pack();
        lineChart.setVisible(true);
    }   

    // Adiciona uma linha horizontal representando a média ao gráfico.
    private void addMediaLine(double media) {
        // Obtendo o plot do gráfico
        XYPlot plot = (XYPlot) chart.getPlot();
    
        // Criando um marcador de valor com a posição da média
        ValueMarker mediaMarker = new ValueMarker(media);
    
        // Configurando a cor da linha e a largura da linha
        mediaMarker.setPaint(Color.RED);
        mediaMarker.setStroke(new BasicStroke(2.0f)); // Pode ajustar o valor conforme necessário
    
        // Adicionando o marcador de valor ao plot
        plot.addRangeMarker(mediaMarker);
    }
}
