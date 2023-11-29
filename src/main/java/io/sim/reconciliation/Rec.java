package io.sim.reconciliation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.sim.reconciliation.excel.LeitorRelatorioCarros;
import io.sim.reconciliation.excel.ReconciliationReport;

public class Rec extends Thread {

	private static int numeroRepeticoes = 10;

	public Rec() {

	}

	// @Override
    // public void run() {

	public static void main(String args[]) {
		try {
			System.out.println("\n\n");
			System.out.println("======== Preparando os dados para a Realização da Reconciliação de Dados ========");
			
			// Configurando as classes necessárias para a análise do documento XML
            File xmlFile = new File("data/dadosAV2.xml");
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

			int numeroParticoes = rota.size()/2;

			System.out.println("A rota analisada foi dividida em " + numeroParticoes + " partiçoes");
			
			// Caminho do arquivo Excel
            String filePath = "RelatorioCarros.xlsx";
            ArrayList<Double> timeStamps = new ArrayList<>();
            ArrayList<Double> distancias = new ArrayList<>();

			// Chamar o método para ler e extrair informações do Excel
            LeitorRelatorioCarros.lerExcel(filePath, timeStamps, distancias);

            // Imprimir os vetores
			System.out.println("\nLendo os valores de RelatorioCarros.xlsx");
            System.out.println("TimeStamps: " + timeStamps);
            System.out.println("Distances: " + distancias);


			System.out.println("\n\nCriando planilha ReconciliationReport.xlsx");
			// Criando planilha
			ReconciliationReport.criaRecociliationReport(numeroParticoes);

			for (int i = 1; i <= numeroRepeticoes; i++) {
				ArrayList<Double> temposParciais = new ArrayList<>();
				ArrayList<Double> distanciasParciais = new ArrayList<>();
			
				for (int j = 1; j < numeroParticoes; j++) {
					double parcialTempo = (timeStamps.get(j * i) - timeStamps.get(j * i - 1)) * Math.pow(10, 9);
					double parcialDistancia = (distancias.get(j * i) - distancias.get(j * i - 1));
			
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

				ReconciliationReport.adicionaValoresALinha(i, temposParciais, distanciasParciais);
			
				temposParciais.clear();
				distanciasParciais.clear();
			}

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

    // @Override
    // public void run() {

	// 	//   F1    F3     F5     F6
	// 	// =====>O=====>O=====>O=====>
	// 	//     v |             | ^
	// 	//       |  F2     F4  |
	// 	//       ======>O======>

	// 	double[] y = new double[] { 110.5, 60.8, 35.0, 68.9, 38.6, 101.4 };

	// 	double[] v = new double[] { 0.6724, 0.2809, 0.2116, 0.5041, 0.2025, 1.44 };

	// 	double[][] A = new double[][] { { 1, -1, -1, 0, 0, 0 }, { 0, 1, 0, -1, 0, 0 }, { 0, 0, 1, 0, -1, 0 },
	// 			{ 0, 0, 0, 1, 1, -1 } };

	// 	Reconciliation rec = new Reconciliation(y, v, A);
	// 	System.out.println("Y_hat:");
	// 	rec.printMatrix(rec.getReconciledFlow());
	// }

}
