package io.sim.reconciliation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.sim.reconciliation.chart.Grafico;
import io.sim.reconciliation.excel.LeitorRelatorioCarros;
import io.sim.reconciliation.excel.ReconciliationReport;

public class Rec extends Thread {

	private static int numeroRepeticoes = 100;
	private static long taxaAquisicao = 20;

	public Rec() {

	}

	// @Override
    // public void run() {

	public static void main(String args[]) {
		try {
			System.out.println("\n\n======== Preparando os dados para a Realização da Reconciliação de Dados ========");
			
			int numeroParticoes = calculaNumeroParticoes("data/dadosAV2.xml");

			System.out.println("A rota analisada foi dividida em " + numeroParticoes + " partiçoes");
			
			// Caminho do arquivo Excel
            String filePath = "RelatorioCarros.xlsx";
            ArrayList<Double> timeStamps = new ArrayList<>();
            ArrayList<Double> distancias = new ArrayList<>();

			// Chamar o método para ler e extrair informações do Excel
            LeitorRelatorioCarros.lerExcel(filePath, timeStamps, distancias);

            // Imprimir os vetores
			System.out.println("\nLendo os valores de RelatorioCarros.xlsx\n");
            System.out.println("TimeStamps: " + timeStamps + "\n");
            System.out.println("Distances: " + distancias + "\n");

			System.out.println("\n\nLeitura realizada!!");

			System.out.println("\n\nCriando planilha ReconciliationReport.xlsx");
			ReconciliationReport.criaReconciliationReport(numeroParticoes);
			System.out.println("\nCalculando as parciais...");
			calcularParciais(numeroParticoes, timeStamps, distancias);
			
			// Obtendo os dados de TimeDistanceReport
			ArrayList<ArrayList<Double>> todosOsT = new ArrayList<>();
			ArrayList<ArrayList<Double>> todosOsD = new ArrayList<>();
			for (int i = 0; i < numeroParticoes; i++) {
				todosOsT.add(ReconciliationReport.lerColunaReconciliation(0, i*2));
				todosOsD.add(ReconciliationReport.lerColunaReconciliation(0, (i*2) + 1));
			}

			System.out.println("\nCalculando a média e desvio padrão...");
			ReconciliationReport.adicionaSheetEstatisticas(numeroParticoes);
			calcularEstatisticas1(numeroParticoes, todosOsT, todosOsD);

			System.out.println("\nFazendo a Reconciliação de Dados para os tempos e distâncias...");
			preparaReconciliacao();

			System.out.println("\nCalculando as estatísticas...");
			calcularEstatisticas2(numeroParticoes);
			
			System.out.println("\n\nPlotando os gráficos de dispersão...");
			Grafico.plotarGraficosDispersoes(todosOsT, todosOsD);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	private static int calculaNumeroParticoes(String xmlPath) {
		try {
			// Configurando as classes necessárias para a análise do documento XML
			File xmlFile = new File(xmlPath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);

			// Obtendo a lista de elementos com a tag "vehicle" do documento
			NodeList vehicleList = doc.getElementsByTagName("vehicle");
			
			String edges = "";
			// Verifica se há pelo menos um veículo
			if (vehicleList.getLength() > 0) {
				Element firstVehicleElement = (Element) vehicleList.item(0);
				NodeList routeList = firstVehicleElement.getElementsByTagName("route");

				// Verifica se há pelo menos uma rota
				if (routeList.getLength() > 0) {
					Element firstRouteElement = (Element) routeList.item(0);
					edges = firstRouteElement.getAttribute("edges");
				}
			}

			ArrayList<String> rota = new ArrayList<String>();
			for(String e : edges.split(" ")) {
				rota.add(e);
			}

			return rota.size()/2;
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	private static void calcularParciais(int numeroParticoes, ArrayList<Double> timeStamps, ArrayList<Double> distancias) {
		for (int i = 1; i <= numeroRepeticoes; i++) {
			ArrayList<Double> temposParciais = new ArrayList<>();
			ArrayList<Double> distanciasParciais = new ArrayList<>();
		
			for (int j = 1; j < numeroParticoes; j++) {
				double parcialTempo = (timeStamps.get(((i - 1) *numeroParticoes) + j) - timeStamps.get(((i - 1) * numeroParticoes) + j - 1)) * Math.pow(10,-7)/taxaAquisicao;
				double parcialDistancia = (distancias.get(((i - 1) *numeroParticoes) + j) - distancias.get(((i - 1) *numeroParticoes) + j - 1));
		
				temposParciais.add(parcialTempo);
				distanciasParciais.add(parcialDistancia);
			}

			double soma = 0;
			for (double t : temposParciais) {
				soma += t;
			}
			temposParciais.add(soma);
			soma = 0;
			for (double d : distanciasParciais) {
				soma += d;
			}
			distanciasParciais.add(soma);

			ReconciliationReport.adicionaValoresALinhaTimeDistance(i, temposParciais, distanciasParciais);
		
			temposParciais.clear();
			distanciasParciais.clear();
		}
	}

	private static void calcularEstatisticas1(int numeroParticoes, ArrayList<ArrayList<Double>> todosOsT, ArrayList<ArrayList<Double>> todosOsD) {
		// Calculando as médias
		ArrayList<Double> mediaT = new ArrayList<>();
		ArrayList<Double> mediaD = new ArrayList<>();
		for (int i = 0; i < numeroParticoes; i++) {
			double soma = 0;
			ArrayList<Double> Tatual = todosOsT.get(i);
			for (int j = 0; j < Tatual.size(); j++) {
				soma += Tatual.get(j);
			}
			mediaT.add(soma/Tatual.size());

			soma = 0;
			ArrayList<Double> Datual = todosOsD.get(i);
			for (int j = 0; j < Datual.size(); j++) {
				soma += Datual.get(j);
			}
			mediaD.add(soma/Datual.size());
		}
		ReconciliationReport.escreverDadosColunaEstatisticas(1, mediaT);
		ReconciliationReport.escreverDadosColunaEstatisticas(10, mediaD);

		// Calculando o desvio padrão
		ArrayList<Double> desvioPadraoT = new ArrayList<>();
		ArrayList<Double> desvioPadraoD = new ArrayList<>();
		for (int i = 0; i < numeroParticoes; i++) {
			ArrayList<Double> Tatual = todosOsT.get(i);
			double mediaAtualT = mediaT.get(i);
			double somaQuadradosT = 0;
	
			ArrayList<Double> Datual = todosOsD.get(i);
			double mediaAtualD = mediaD.get(i);
			double somaQuadradosD = 0;
	
			for (double valor : Tatual) {
				somaQuadradosT += Math.pow((valor - mediaAtualT), 2);
			}
			desvioPadraoT.add(Math.sqrt(somaQuadradosT / Tatual.size()));
	
			for (double valor : Datual) {
				somaQuadradosD += Math.pow((valor - mediaAtualD), 2);
			}
			desvioPadraoD.add(Math.sqrt(somaQuadradosD / Datual.size()));
		}
	
		ReconciliationReport.escreverDadosColunaEstatisticas(2, desvioPadraoT);
		ReconciliationReport.escreverDadosColunaEstatisticas(11, desvioPadraoD);
	}

    private static void preparaReconciliacao() {
		// Fazendo a Reconciliação para os tempos:
		ArrayList<Double> mediasT = ReconciliationReport.lerColunaReconciliation(1, 1);
		ArrayList<Double> desvioPadraoT = ReconciliationReport.lerColunaReconciliation(1, 2);
		double[] Treconciliado = reconciliacao(mediasT, desvioPadraoT);
		ArrayList<Double> Trec = new ArrayList<>();
		for (double t : Treconciliado) {
			Trec.add(t);
		}
		Trec.remove(Trec.size() - 1); // Remove o lixo que ficou acumulado no vetor para que ele não seja escrito no Excel
		ReconciliationReport.escreverDadosColunaEstatisticas(3, Trec);
		
		ArrayList<Double> mediasD = ReconciliationReport.lerColunaReconciliation(1, 10);
		ArrayList<Double> desvioPadraoD = ReconciliationReport.lerColunaReconciliation(1, 11);
		double[] Dreconciliado = reconciliacao(mediasD, desvioPadraoD);
		ArrayList<Double> Drec = new ArrayList<>();
		for (double d : Dreconciliado) {
			Drec.add(d);
		}
		Drec.remove(Drec.size() - 1); // Remove o lixo que ficou acumulado no vetor para que ele não seja escrito no Excel
		ReconciliationReport.escreverDadosColunaEstatisticas(12, Drec);
	}

	private static double[] reconciliacao(ArrayList<Double> medias, ArrayList<Double> desvioPadrao) {
		int tam = medias.size();
		double[] y = new double[tam];
		for (int i = 0; i < tam; i++) {
			y[i] = medias.get(i);
		}

		double[] v = new double[tam];
		for (int i = 0; i < tam; i++) {
			v[i] = Math.pow(desvioPadrao.get(i),2);
		}

		double[] A = new double[tam];

        // Preenche o vetor com 1
        Arrays.fill(A, 1);

        // Define o último elemento como -1
        A[tam - 1] = -1;

		Reconciliation rec = new Reconciliation(y, v, A);
		return rec.getReconciledFlow();
	}

	private static void calcularEstatisticas2(int numeroParticoes) {
		// Obtendo os dados de Statistics
		ArrayList<Double> mediaT = ReconciliationReport.lerColunaReconciliation(1, 1);
		ArrayList<Double> mediaD = ReconciliationReport.lerColunaReconciliation(1, 10);
		ArrayList<Double> desvioPadraoT = ReconciliationReport.lerColunaReconciliation(1, 2);
		ArrayList<Double> desvioPadraoD = ReconciliationReport.lerColunaReconciliation(1, 11);
		ArrayList<Double> Treconciliado = ReconciliationReport.lerColunaReconciliation(1, 3);
		ArrayList<Double> Dreconciliado = ReconciliationReport.lerColunaReconciliation(1, 12);

		// Calculando a Polarização (bias);
		ArrayList<Double> biasT = new ArrayList<>();
		ArrayList<Double> biasD = new ArrayList<>();
		for (int i = 0; i < numeroParticoes; i++) {
			biasT.add(Treconciliado.get(i) - mediaT.get(i));
			biasD.add(Dreconciliado.get(i) - mediaD.get(i));
		}
		ReconciliationReport.escreverDadosColunaEstatisticas(4, biasT);
		ReconciliationReport.escreverDadosColunaEstatisticas(13, biasD);
	}

}
