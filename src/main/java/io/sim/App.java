package io.sim;

import io.sim.reconciliation.Rec;
import io.sim.simulator.EnvSimulator;

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
        
        Rec rec = new Rec();
        rec.start();
        rec.join();

        try {
            // Executa um passo de simulação no SUMO
            Thread.sleep(1000);
            // Aguarda um determinado tempo (taxa de aquisição) antes de continuar
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Encerando APP!");
        System.exit(0);
    }
}
