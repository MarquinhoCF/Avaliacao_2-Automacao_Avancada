package io.sim.processing.reconciliation.auxiliar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A classe VerificaIdsDepartments é responsável por verificar se há IDs e departamentos idênticos
 * nos elementos "vehicle" de um arquivo XML de rotas de veículos (map.rou.xml).
 */
public class VerificaIdsDepartments {

    //      Método principal que carrega o arquivo XML, extrai IDs e departamentos dos elementos "vehicle" 
    // e verifica se há IDs e departamentos idênticos.
    public static void main(String[] args) {
        try {
            // Carrega o arquivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("map/map.rou.xml");

            // Obtém a lista de elementos "vehicle"
            NodeList vehicleList = document.getElementsByTagName("vehicle");

            // Cria um array para armazenar IDs e departamentos
            int[] ids = new int[vehicleList.getLength()];
            double[] departures = new double[vehicleList.getLength()];

            // Itera sobre os elementos "vehicle" para extrair IDs e departamentos
            for (int i = 0; i < vehicleList.getLength(); i++) {
                Node vehicleNode = vehicleList.item(i);

                if (vehicleNode.getNodeType() == Node.ELEMENT_NODE) {
                    String id = vehicleNode.getAttributes().getNamedItem("id").getNodeValue();
                    String depart = vehicleNode.getAttributes().getNamedItem("depart").getNodeValue();

                    ids[i] = Integer.parseInt(id);
                    departures[i] = Double.parseDouble(depart);
                }
            }

            // Verifica se há IDs e departamentos idênticos
            boolean hasDuplicates = false;
            for (int i = 0; i < ids.length - 1; i++) {
                for (int j = i + 1; j < ids.length; j++) {
                    if (ids[i] == ids[j] && departures[i] == departures[j]) {
                        hasDuplicates = true;
                        System.out.println("ID e Department idêntico em: " + ids[i]);
                    }
                }
            }

            // Se não houver IDs e departamentos idênticos
            if (!hasDuplicates) {
                System.out.println("Não há IDs e Departamentos idênticos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
