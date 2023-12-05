package io.sim.processing.scheduling;

import java.util.ArrayList;

import io.sim.processing.scheduling.excel.RelatorioEscalonamento;

/**
 * A classe EscalonadorTempoReal representa um escalonador para sistemas de tempo real, que executa como uma Thread.
 * Ela realiza cálculos relacionados a tarefas de tempo real, verifica escalonabilidade e gera um relatório de escalonamento.
 */
public class EscalonadorTempoReal extends Thread {

    /**
     * Construtor padrão da classe EscalonadorTempoReal.
     */
    public EscalonadorTempoReal() {

    }

    // Método principal que será executado quando a thread for iniciada.
    // Realiza cálculos relacionados a tarefas de tempo real, verifica escalonabilidade e gera um relatório de escalonamento.
    @Override
    public void run() {
        // Defina os parâmetros das tarefas
        ArrayList<Double> Ji = RelatorioEscalonamento.obterDadosColuna(2);   // Tempo de Chegada
        ArrayList<Double> Ci = RelatorioEscalonamento.obterDadosColuna(3);   // Tempo de Conclusão
        ArrayList<Double> Pi = RelatorioEscalonamento.obterDadosColuna(4);   // Período
        ArrayList<Double> Di = RelatorioEscalonamento.obterDadosColuna(5);   // Deadline

        int numTarefas = Ji.size();

        // Calcula o tempo de resposta máximo
        ArrayList<Double> Wi = calcularWi(Ji, Ci, Pi, Di);
        ArrayList<Double> Ri = calcularRi(Wi, Ji);
        double tempoRespostaMaximo = calcularTempoRespostaMaximo(Ri);

        // Verifica a utilização do processador
        double utilizacaoProcessador = calcularUtilizacaoProcessador(Ci, Pi);

        // Exibe os resultados
        System.out.println("Tempo de Resposta Máximo: " + tempoRespostaMaximo);
        System.out.println("Utilização do Processador: " + utilizacaoProcessador);

        // Verifica escalonabilidade
        double limiteUtilizacao = calculaLimiteUtilizacao(numTarefas, Pi, Di);
        RelatorioEscalonamento.criarAnaliseEscalonamento();

        String escalonavel;
        if (utilizacaoProcessador <= limiteUtilizacao) {
            escalonavel = "Sim";
        } else {
            escalonavel = "Não";
        }
        System.out.println("O conjunto de tarefas é escalonável? " + escalonavel);

        RelatorioEscalonamento.adicionarValoresAnaliseEscalonamento(tempoRespostaMaximo, utilizacaoProcessador, limiteUtilizacao, escalonavel);

        System.out.println("RelatorioEscalonamento.xlsx criado com sucesso!!");
    }

    // Método para calcular o tempo de espera individual (Wi) para cada tarefa
    private static ArrayList<Double> calcularWi(ArrayList<Double> Ji, ArrayList<Double> Ci, ArrayList<Double> Pi, ArrayList<Double> Di) {
        int numTarefas = Ji.size();
        ArrayList<Double> Wi = new ArrayList<>();

        for (int i = 0; i < numTarefas; i++) {
            Wi.add(calcularWiIndividual(Ji, Ci, Pi, Di, i));
        }

        return Wi;
    }

    // Calcula o tempo de espera individual (Wi) para uma tarefa específica.
    private static double calcularWiIndividual(ArrayList<Double> Ji, ArrayList<Double> Ci, ArrayList<Double> Pi, ArrayList<Double> Di, int i) {
        double Wi = Ci.get(i);

        for (int j = 0; j < Ji.size(); j++) {
            if (i != j) {
                Wi += Math.ceil((Wi + Ji.get(j)) / Pi.get(i)) * Ci.get(j);
            }
        }

        return Wi;
    }

    // Calcula os tempos de resposta (Ri) para todas as tarefas.
    private static ArrayList<Double> calcularRi(ArrayList<Double> Wi, ArrayList<Double> Ji) {
        int numTarefas = Wi.size();
        ArrayList<Double> Ri = new ArrayList<>();

        for (int i = 0; i < numTarefas; i++) {
            Ri.add(Wi.get(i) + Ji.get(i));
        }

        return Ri;
    }

    // Calcula a utilização do processador para o conjunto de tarefas.
    private static double calcularUtilizacaoProcessador(ArrayList<Double> Ci, ArrayList<Double> Pi) {
        int numTarefas = Ci.size();
        double utilizacao = 0;

        for (int i = 0; i < numTarefas; i++) {
            utilizacao += (Ci.get(i) / Pi.get(i));
        }

        return utilizacao;
    }

    // Calcula o tempo de resposta máximo entre todas as tarefas.
    private static double calcularTempoRespostaMaximo(ArrayList<Double> Ri) {
        double tempoRespostaMaximo = 0;
        for (double valor : Ri) {
            tempoRespostaMaximo = Math.max(tempoRespostaMaximo, valor);
        }
        return tempoRespostaMaximo;
    }

    // Calcula o limite de utilização do processador para verificar a escalonabilidade.
    private static double calculaLimiteUtilizacao(int numTarefas, ArrayList<Double> Pi, ArrayList<Double> Di) {
        double limiteUtilizacao = numTarefas * (Math.pow(2, 1.0 / numTarefas) - 1);

        for (int i = 0; i < numTarefas; i++) {
            limiteUtilizacao += Di.get(i) / Pi.get(i);
        }

        return limiteUtilizacao;
    }
}
