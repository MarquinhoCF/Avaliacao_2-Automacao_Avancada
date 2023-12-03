package io.sim;

// import io.sim.reconciliation.CalcularEstatisticas;
import io.sim.simulator.simulation.EnvSimulator;

/**
 * Classe que inicia toda a aplicação
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
	    int parte = 1;
        long taxaAquisicao = 100;
        boolean considerarConsumoComb = false;

        // Cria uma instância da classe EnvSimulator
        EnvSimulator ev;
        if (parte == 1) {
            int numDrivers = 1;
            int numeroDeAmostras = 100;
            String rotasXML = "data/dadosAV2.xml";
            ev = new EnvSimulator(taxaAquisicao, numDrivers, numeroDeAmostras, rotasXML, considerarConsumoComb);
        } else {
            int numDrivers = 100;
            int numeroDeAmostras = 0;
            String rotasXML = "data/dados.xml";
            // TODO: Para AV1 é necessário retirar essa configuração do aquivo map.sumo.cfg: <route-files value="map.rou.xml"/>
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

        if (parte == 1) {
            // CalcularEstatisticas calc = new CalcularEstatisticas(taxaAquisicao, numeroDeAmostras);
            // calc.start();
            // calc.join();
        }

        System.out.println("Encerando APP!");
        System.exit(0);
    }
}
