package io.sim.simulator.simulation;

import io.sim.App;
import io.sim.simulator.report.AuxEscalonamento;
import it.polito.appeal.traci.SumoTraciConnection;

// A classe ExecutaSimulador é responsável por executar a simulação no SUMO
public class ExecutaSimulador extends Thread {
    private SumoTraciConnection sumo;
    private long taxaAquisicao;
    private boolean funcionando;

    // Atributos escalonamento de sistema em tempo real
    private int av2Parte;
    private long startTime;

    public ExecutaSimulador(SumoTraciConnection _sumo, long _taxaAquisicao) {
        this.sumo = _sumo;
        this.taxaAquisicao = _taxaAquisicao;
        this.funcionando = true;

        // Inicializa startTime e parte de AV2
        this.av2Parte = App.AV2PARTE;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long inicio = System.currentTimeMillis();

         // Loop infinito para continuar a execução do simulador
        boolean primeiraVez = true;
        while(funcionando) {
            try {
                this.sumo.do_timestep();
                // Executa um passo de simulação no SUMO
                sleep(taxaAquisicao);
                // Aguarda um determinado tempo (taxa de aquisição) antes de continuar
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if ((av2Parte == 2) && primeiraVez) {
                AuxEscalonamento aux = new AuxEscalonamento("ExecutaSimulador", 8, startTime, inicio);
                aux.start();
                primeiraVez = false;
            }
        }
        System.out.println("Executa Simulador encerrado!!!");
    }

    public void setFuncionando(boolean _funcionando) {
        funcionando = _funcionando;
    }
}
