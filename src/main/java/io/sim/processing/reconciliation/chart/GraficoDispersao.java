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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * A classe GraficoDispersao representa um gráfico de dispersão utilizando a biblioteca JFreeChart.
 * Permite a criação de gráficos com dados de tempo e distância.
 */
public class GraficoDispersao extends ApplicationFrame {
    private JFreeChart chart;
    private XYSeries series;
    private XYSeriesCollection dataset;

    // Construtor da classe GraficoDispersao.
    public GraficoDispersao(String title) {
        super(title);
        series = new XYSeries("Data");
        dataset = new XYSeriesCollection(series);
        chart = ChartFactory.createScatterPlot(
                title,
                "Tempo",
                "Distância",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

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
        series.add(x, y);
    }

    // Método estático para plotar gráficos de dispersão para diferentes conjuntos de dados.
    public static void plotarGraficosDispersoes(ArrayList<ArrayList<Double>> todosOsT, ArrayList<ArrayList<Double>> todosOsD) {
        if (todosOsD.size() == todosOsT.size()) {
            for (int i = 0; i < todosOsT.size(); i++) {
                String title;
                if (i < (todosOsT.size() - 1)) {
                    title = "Dispersão: t" + (i + 1) + " x d" + (i + 1);
                } else {
                    title = "Dispersão: tTOTAL x dTOTAL";
                }

                auxPlotarGraficos(title, todosOsT.get(i), todosOsD.get(i));
            }
        } else {
            System.out.println("Houve erro na quisição de dados, por favor realize a simulação novamente!");
        }
    }

    // Método auxiliar para plotar gráficos de dispersão individualmente.
    private static void auxPlotarGraficos(String title, ArrayList<Double> xData, ArrayList<Double> yData) {
        double minX = xData.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double maxX = xData.stream().mapToDouble(Double::doubleValue).max().orElse(10);
        double minY = yData.stream().mapToDouble(Double::doubleValue).min().orElse(0) - 25;
        double maxY = yData.stream().mapToDouble(Double::doubleValue).max().orElse(10) + 25;

        GraficoDispersao scatterPlot = new GraficoDispersao(title);
        for (int j = 0; j < xData.size(); j++) {
            scatterPlot.addData(xData.get(j), yData.get(j));
        }

        XYPlot plot = (XYPlot) scatterPlot.getChart().getPlot();
        plot.getDomainAxis().setRange(minX, maxX);
        plot.getRangeAxis().setRange(minY, maxY);

        // Obtendo o renderizador
        XYItemRenderer renderer = plot.getRenderer();

        // Definindo a cor dos pontos
        ChartColor color = new ChartColor(14, 18, 77);
        renderer.setSeriesPaint(0, color);

        scatterPlot.pack();
        scatterPlot.setVisible(true);
    }
}
