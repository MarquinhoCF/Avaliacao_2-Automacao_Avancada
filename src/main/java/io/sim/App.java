package io.sim;

import io.sim.reconciliation.Rec;
import io.sim.simulator.simulation.EnvSimulator;

/**
 * Classe que inicia toda a aplicação
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        // Cria uma instância da classe EnvSimulator
        EnvSimulator ev = new EnvSimulator();
        
        // Inicia a execução da simulação chamando o método "start" na instância
        ev.start();
        ev.join();
        
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Rec rec = new Rec();
        // rec.start();
        // rec.join();

        System.out.println("Encerando APP!");
        System.exit(0);
    }
}
