package io.sim;

import io.sim.processing.reconciliation.CalcularEstatisticas;
import io.sim.simulator.simulation.EnvSimulator;

/**
 * Classe que inicia toda a aplicação
 */
public class App {
    public static final int AV2PARTE = 2;

    public static void main(String[] args) throws InterruptedException {
	    // Escolha os parâmetros de controle aqui:
        long taxaAquisicao = 300;
        boolean considerarConsumoComb = true;
        int numeroDeAmostras = 100;

        // Cria uma instância da classe EnvSimulator
        int av2Parte = AV2PARTE;
        EnvSimulator ev;
        if (av2Parte == 1) {
            int numDrivers = 1; // A parte 1 precisa ser com 1 carro!
            String rotasXML = "data/dadosAV2.xml";
            // TODO: Para AV2 é necessário colocar essa configuração no aquivo map.sumo.cfg: <route-files value="map.rou.xml"/>
            ev = new EnvSimulator(taxaAquisicao, numDrivers, numeroDeAmostras, rotasXML, considerarConsumoComb);
        } else {
            int numDrivers = 1;
            numeroDeAmostras = 0; // A parte 2 precisa que o número de amostras seja 0!
            String rotasXML = "data/dadosAV2Parte2.xml";
            // TODO: Para AV1 é necessário retirar essa configuração no aquivo map.sumo.cfg: <route-files value="map.rou.xml"/>
            ev = new EnvSimulator(taxaAquisicao, numDrivers, numeroDeAmostras, rotasXML, considerarConsumoComb);
        }
        
        // Inicia a execução da simulação chamando o método "start" na instância
        ev.start();
        ev.join();
        
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (av2Parte == 1) {
            CalcularEstatisticas calc = new CalcularEstatisticas(taxaAquisicao, numeroDeAmostras);
            calc.start();
            calc.join();
        }

        System.out.println("Encerando APP!");
        System.exit(0);
    }
}
