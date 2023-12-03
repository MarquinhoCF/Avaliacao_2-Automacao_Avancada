package io.sim.processing;

public class AuxiliarAV2Parte1 {
    public void main(String[] args) throws InterruptedException {
        long taxaAquisicao = 40;
        int numeroDeAmostras = 100;

        CalcularEstatisticas calc = new CalcularEstatisticas(taxaAquisicao, numeroDeAmostras);
        calc.start();
        calc.join();
        
    }
}
