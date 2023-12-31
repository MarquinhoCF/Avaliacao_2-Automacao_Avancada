package io.sim.processing.reconciliation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.sim.processing.reconciliation.chart.GraficoDispersao;
import io.sim.processing.reconciliation.chart.GraficoLinha;
import io.sim.processing.reconciliation.excel.LeitorRelatorioCarros;
import io.sim.processing.reconciliation.excel.ReconciliationReport;

/**
 * A classe CalcularEstatisticas é responsável por realizar cálculos e geração de relatórios
 * relacionados à reconciliação de dados, estatísticas e gráficos.
 */
public class CalcularEstatisticas extends Thread {

    private long taxaAquisicao = 40;
    private int numeroDeAmostras = 100;

    /**
     * Construtor da classe.
     *
     * @param _taxaAquisicao Taxa de aquisição de dados.
     * @param _numeroDeAmostras Número de amostras.
     */
    public CalcularEstatisticas(long _taxaAquisicao, int _numeroDeAmostras) {
        this.taxaAquisicao = _taxaAquisicao;
        this.numeroDeAmostras = _numeroDeAmostras;
    }

    @Override
    public void run() {
        try {
            System.out.println("\n\n======== Preparando os dados para a Realização da Reconciliação de Dados ========");
            
            // Calcula o número de partições da rota
            int numeroParticoes = calculaNumeroParticoes("data/dadosAV2.xml");

            System.out.println("A rota analisada foi dividida em " + numeroParticoes + " partições");
            
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

            System.out.println("\nPlotando os gráficos de dispersão...");
            GraficoDispersao.plotarGraficosDispersoes(todosOsT, todosOsD);

            System.out.println("\nCalculando a média e desvio padrão...");
            ReconciliationReport.adicionaSheetEstatisticas(numeroParticoes);
            calcularEstatisticas1(numeroParticoes, todosOsT, todosOsD);

            System.out.println("\nFazendo a Reconciliação de Dados para os tempos e distâncias...");
            preparaReconciliacao(numeroParticoes);

            System.out.println("\nCalculando as estatísticas...");
            calcularEstatisticas2(numeroParticoes);

            System.out.println("\nCalculando as velocidades e suas incertezas para cada parcial...");
            ReconciliationReport.adicionaSheetVelocidade(numeroParticoes);
            calculaVelocidade(numeroParticoes);
            
            System.out.println("\nCalculo das velocidades finalizado!!");
            System.out.println("\nPlotando os gráficos das velocidades sugeridas:");
            ArrayList<Double> velocidadesMpS = ReconciliationReport.lerColunaReconciliation(2, 1);
            GraficoLinha.plotarGraficoLinha("Velocidade Sugerida para cada fluxo: m/s", velocidadesMpS, "m/s");
            ArrayList<Double> velocidadesKMpH = ReconciliationReport.lerColunaReconciliation(2, 7);
            GraficoLinha.plotarGraficoLinha("Velocidade Sugerida para cada fluxo: Km/h", velocidadesKMpH, "Km/h");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	// Calcula o número de partições com base em informações de um arquivo XML.
	private int calculaNumeroParticoes(String xmlPath) {
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

			// Converte as arestas em uma lista de strings e retorna a metade do tamanho
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

	// Calcula estatísticas parciais para tempos e distâncias.
	private void calcularParciais(int numeroParticoes, ArrayList<Double> timeStamps, ArrayList<Double> distancias) {
		for (int i = 1; i <= numeroDeAmostras; i++) {
			// Lista para armazenar tempos e distâncias parciais
			ArrayList<Double> temposParciais = new ArrayList<>();
			ArrayList<Double> distanciasParciais = new ArrayList<>();
		
			for (int j = 1; j < numeroParticoes; j++) {
				// Calcula tempos e distâncias parciais entre amostras consecutivas
				double parcialTempo = (timeStamps.get(((i - 1) * numeroParticoes) + j) - timeStamps.get(((i - 1) * numeroParticoes) + j - 1)) * Math.pow(10,-7)/taxaAquisicao;
				double parcialDistancia = (distancias.get(((i - 1) * numeroParticoes) + j) - distancias.get(((i - 1) * numeroParticoes) + j - 1));
				
				// Adiciona os resultados às listas
				temposParciais.add(parcialTempo);
				distanciasParciais.add(parcialDistancia);
			}

			// Calcula e adiciona o tempo e a distância totais à lista
			double tTotal = (timeStamps.get(((i - 1) * numeroParticoes) + numeroParticoes - 1) - timeStamps.get(((i - 1) * numeroParticoes))) * Math.pow(10,-7)/taxaAquisicao;
			temposParciais.add(tTotal);
			double dTotal = distancias.get(((i - 1) * numeroParticoes) + numeroParticoes - 1) - distancias.get(((i - 1) * numeroParticoes));
			distanciasParciais.add(dTotal);

			// Adiciona os resultados ao relatório
			ReconciliationReport.adicionaValoresALinhaTimeDistance(i, temposParciais, distanciasParciais);
			
			// Limpa as listas para a próxima iteração
			temposParciais.clear();
			distanciasParciais.clear();
		}
	}

	// Calcula médias, desvios padrão e escreve esses dados para tempos e distâncias.
	private void calcularEstatisticas1(int numeroParticoes, ArrayList<ArrayList<Double>> todosOsT, ArrayList<ArrayList<Double>> todosOsD) {
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
		ReconciliationReport.escreverDadosColunaReconciliation(1, 1, mediaT);
		ReconciliationReport.escreverDadosColunaReconciliation(1, 10, mediaD);

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
	
		ReconciliationReport.escreverDadosColunaReconciliation(1, 2, desvioPadraoT);
		ReconciliationReport.escreverDadosColunaReconciliation(1, 11, desvioPadraoD);
	}

	// Prepara dados para a reconciliação e escreve os resultados para tempos e distâncias.
    private void preparaReconciliacao(int numeroParticoes) {
		// Obtendo os dados de uma medição randomica
        // Gerando um número aleatório entre 1 e 100
		Random random = new Random();
        int amostraAleatoria = random.nextInt(100) + 1;
		ArrayList<Double> dadosLinha = ReconciliationReport.extraiValoresDaLinhaTimeDistance(amostraAleatoria + 1, numeroParticoes);

		// Fazendo a Reconciliação para os tempos:
		ArrayList<Double> medidaBrutaT = new ArrayList<Double>();
		for (int i = 0; i < dadosLinha.size(); i += 2) {
			medidaBrutaT.add(dadosLinha.get(i));
		}
		ArrayList<Double> desvioPadraoT = ReconciliationReport.lerColunaReconciliation(1, 2);
		Reconciliation ReconciliacaoT = reconciliacao(medidaBrutaT, desvioPadraoT);
		double[] Treconciliado = ReconciliacaoT.getReconciledFlow();
		ArrayList<Double> Trec = new ArrayList<>();
		for (double t : Treconciliado) {
			Trec.add(t);
		}
		Trec.remove(Trec.size() - 1); // Remove o lixo que ficou acumulado no vetor para que ele não seja escrito no Excel
		ReconciliationReport.escreverDadosColunaReconciliation(1, 3, Trec);
		
		// Fazendo a Reconciliação para as distâncias:
		ArrayList<Double> medidaBrutaD = new ArrayList<Double>();
		for (int i = 1; i < dadosLinha.size(); i += 2) {
			medidaBrutaD.add(dadosLinha.get(i));
		}
		ArrayList<Double> desvioPadraoD = ReconciliationReport.lerColunaReconciliation(1, 11);
		Reconciliation ReconciliacaoD = reconciliacao(medidaBrutaD, desvioPadraoD);
		double[] Dreconciliado = ReconciliacaoD.getReconciledFlow();
		ArrayList<Double> Drec = new ArrayList<>();
		for (double t : Dreconciliado) {
			Drec.add(t);
		}
		Drec.remove(Drec.size() - 1); // Remove o lixo que ficou acumulado no vetor para que ele não seja escrito no Excel
		ReconciliationReport.escreverDadosColunaReconciliation(1, 12, Drec);
	}

	// Realiza a reconciliação estatística usando o método dos mínimos quadrados ponderados.
	private Reconciliation reconciliacao(ArrayList<Double> medias, ArrayList<Double> desvioPadrao) {
		// Obtém o tamanho da lista de médias
		int tam = medias.size();

		// Converte as listas de médias e desvios padrão para arrays de double
		double[] y = new double[tam];
		for (int i = 0; i < tam; i++) {
			y[i] = medias.get(i);
		}

		double[] v = new double[tam];
		for (int i = 0; i < tam; i++) {
			v[i] = Math.pow(desvioPadrao.get(i), 2);
		}

		// Cria um vetor A, preenchido com 1, exceto o último elemento que é definido como -1
		double[] A = new double[tam];
		Arrays.fill(A, 1);
		A[tam - 1] = -1;

		// Cria um objeto Reconciliation com os dados preparados
		Reconciliation rec = new Reconciliation(y, v, A);

		// Retorna o objeto Reconciliation contendo os resultados da reconciliação
		return rec;
	}

	// Calcula diversas estatísticas como Polarização, Precisão e Incerteza e escreve os resultados.
	private void calcularEstatisticas2(int numeroParticoes) {
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
		ReconciliationReport.escreverDadosColunaReconciliation(1, 4, biasT);
		ReconciliationReport.escreverDadosColunaReconciliation(1, 13, biasD);
	
		// Calculando a Precisão
		ArrayList<Double> precisaoT = new ArrayList<>();
		ArrayList<Double> precisaoD = new ArrayList<>();
		for (int i = 0; i < numeroParticoes; i++) {
			precisaoT.add(1 - (desvioPadraoT.get(i) / mediaT.get(i)));
			precisaoD.add(1 - (desvioPadraoD.get(i) / mediaD.get(i)));
		}
		ReconciliationReport.escreverDadosColunaReconciliation(1, 5, precisaoT);
		ReconciliationReport.escreverDadosColunaReconciliation(1, 14, precisaoD);
	
		// Calculando a Incerteza
		ArrayList<Double> incertezaT = new ArrayList<>();
		ArrayList<Double> incertezaD = new ArrayList<>();
		for (int i = 0; i < numeroParticoes; i++) {
			incertezaT.add(desvioPadraoT.get(i) / Math.sqrt(numeroDeAmostras));
			incertezaD.add(desvioPadraoD.get(i) / Math.sqrt(numeroDeAmostras));
		}
		ReconciliationReport.escreverDadosColunaReconciliation(1, 6, incertezaT);
		ReconciliationReport.escreverDadosColunaReconciliation(1, 15, incertezaD);
	}

	// Calcula velocidades e suas incertezas propagadas, convertendo para km/h e escreve os resultados.
	private static void calculaVelocidade(int numeroParticoes) {
		ArrayList<Double> Treconciliado = ReconciliationReport.lerColunaReconciliation(1, 3);
		ArrayList<Double> Dreconciliado = ReconciliationReport.lerColunaReconciliation(1, 12);
		ArrayList<Double> incertezaT = ReconciliationReport.lerColunaReconciliation(1, 6);
		ArrayList<Double> incertezaD = ReconciliationReport.lerColunaReconciliation(1, 15);
	
		// Lista para armazenar as velocidades calculadas
		ArrayList<Double> velocidades = new ArrayList<>();
		// Lista para armazenar as incertezas propagadas da velocidade
		ArrayList<Double> incertezas = new ArrayList<>();
	
		// Calcula velocidades e incertezas propagadas em m/s
		for (int i = 0; i < numeroParticoes; i++) {
			double tempo = Treconciliado.get(i);
			double distancia = Dreconciliado.get(i);
			double incertezaTempo = incertezaT.get(i);
			double incertezaDistancia = incertezaD.get(i);
	
			// Calcula velocidade
			double velocidade = distancia / tempo;
			velocidades.add(velocidade);
	
			// Calcula a incerteza propagada da velocidade
			double termo1 = (1 / tempo) * incertezaDistancia;
			double termo2 = (-distancia / (tempo * tempo)) * incertezaTempo;
			double incertezaVelocidade = Math.sqrt(termo1 * termo1 + termo2 * termo2);
			incertezas.add(incertezaVelocidade);
		}
		ReconciliationReport.escreverDadosColunaReconciliation(2, 1, velocidades);
		ReconciliationReport.escreverDadosColunaReconciliation(2, 2, incertezas);

		// Convertendo as velocidades calculadas para Km/h
		for (int i = 0; i < velocidades.size(); i++) {
			velocidades.set(i, velocidades.get(i)*3.6);
		}
		for (int i = 0; i < incertezas.size(); i++) {
			incertezas.set(i, incertezas.get(i)*3.6);
		}
		ReconciliationReport.escreverDadosColunaReconciliation(2, 7, velocidades);
		ReconciliationReport.escreverDadosColunaReconciliation(2, 8, incertezas);
	}
	
}
