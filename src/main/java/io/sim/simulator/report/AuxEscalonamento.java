package io.sim.simulator.report;

import io.sim.processing.scheduling.excel.RelatorioEscalonamento;

/**
 * A classe AuxEscalonamento representa uma thread auxiliar para registrar o tempo
 * de execução de uma tarefa e gerar um relatório de escalonamento.
 */
public class AuxEscalonamento extends Thread {
    private String nome;        // Nome da tarefa
    private int numeroTarefa;   // Número da tarefa
    private long startTime;     // Tempo de início da tarefa
    private long inicio;        // Tempo de início real da execução
    private long finalizacao;   // Tempo de finalização da execução
    private long estimativaManualDi;    // Estimativa manual da duração da tarefa

    public AuxEscalonamento(String _nome, int _numeroTarefa, long _startTime, long _inicio, long _estimativaManualDi) {
        this.nome = _nome;
        this.numeroTarefa = _numeroTarefa;
        this.startTime = _startTime;
        this.inicio = _inicio;
        this.finalizacao = System.currentTimeMillis();
        this.estimativaManualDi = _estimativaManualDi;
    }

    // Método run da thread que registra o tempo de execução da tarefa no Relatório de Escalonamento.
    @Override
    public void run() {
        RelatorioEscalonamento.registrarTempo(nome, numeroTarefa, startTime, inicio, finalizacao, estimativaManualDi);
    }
}

