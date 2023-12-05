package io.sim.processing;

// import io.sim.processing.reconciliation.CalcularEstatisticas;
import io.sim.processing.scheduling.EscalonadorTempoReal;

/**
 * A classe AuxiliarAV2Parte1e2 é responsável por iniciar a simulação, executando
 * um escalonador de tempo real. Ela contém um método principal (main) que inicia
 * o escalonador e pode ser usada para realizar simulações e testes.
 */
public class AuxiliarAV2Parte1e2 {
    
    public static void main(String[] args) throws InterruptedException {
        // long taxaAquisicao = 40;
        // int numeroDeAmostras = 100;

        // CalcularEstatisticas calc = new CalcularEstatisticas(taxaAquisicao, numeroDeAmostras);
        // calc.start();
        // calc.join();

        EscalonadorTempoReal escalonador = new EscalonadorTempoReal();
        escalonador.start();
    }
}
