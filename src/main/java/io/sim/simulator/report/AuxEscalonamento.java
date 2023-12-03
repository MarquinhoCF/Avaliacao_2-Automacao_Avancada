package io.sim.simulator.report;

public class AuxEscalonamento extends Thread {
    private String nome;
    private int numeroTarefa;
    private long startTime;
    private long inicio;
    private long finalização;

    public AuxEscalonamento(String _nome, int _numeroTarefa, long _startTime, long _inicio) {
        this.nome = _nome;
        this.numeroTarefa = _numeroTarefa;
        this.startTime = _startTime;
        this.inicio = _inicio;
        this.finalização = System.currentTimeMillis();
    }

    @Override
    public void run() {
        EscalonamentoTempoReal.registrarTempo(nome, numeroTarefa, startTime, inicio, finalização);
    }

}
