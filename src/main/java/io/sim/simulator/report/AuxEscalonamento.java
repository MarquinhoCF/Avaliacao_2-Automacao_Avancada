package io.sim.simulator.report;

import io.sim.processing.scheduling.excel.RelatorioEscalonamento;

public class AuxEscalonamento extends Thread {
    private String nome;
    private int numeroTarefa;
    private long startTime;
    private long inicio;
    private long finalização;
    private long estimativaManualDi;

    public AuxEscalonamento(String _nome, int _numeroTarefa, long _startTime, long _inicio, long _estimativaManualDi) {
        this.nome = _nome;
        this.numeroTarefa = _numeroTarefa;
        this.startTime = _startTime;
        this.inicio = _inicio;
        this.finalização = System.currentTimeMillis();
        this.estimativaManualDi = _estimativaManualDi;
    }

    @Override
    public void run() {
        RelatorioEscalonamento.registrarTempo(nome, numeroTarefa, startTime, inicio, finalização, estimativaManualDi);
    }

}
