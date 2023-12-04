package io.sim.processing;

// import io.sim.processing.reconciliation.CalcularEstatisticas;
import io.sim.processing.scheduling.EscalonadorTempoReal;

public class AuxiliarAV2Parte1 {
    
    public static void main(String[] args) throws InterruptedException {
        // long taxaAquisicao = 40;
        // int numeroDeAmostras = 100;

        // CalcularEstatisticas calc = new CalcularEstatisticas(taxaAquisicao, numeroDeAmostras);
        // calc.start();
        // calc.join();

        EscalonadorTempoReal esc = new EscalonadorTempoReal();
        esc.start();
        
    }
}
