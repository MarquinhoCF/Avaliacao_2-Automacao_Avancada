package io.sim.processing.scheduling;

import java.util.ArrayList;

import io.sim.processing.scheduling.excel.RelatorioEscalonamento;

public class EscalonadorTempoReal extends Thread {

    public EscalonadorTempoReal() {

    }

    @Override
    public void run() {
        // Defina os parâmetros das tarefas
        ArrayList<Double> Ji = RelatorioEscalonamento.obterDadosColuna(2);   // Tempo de Chegada
        ArrayList<Double> Ci = RelatorioEscalonamento.obterDadosColuna(3);   // Tempo de Conclusão
        ArrayList<Double> Pi = RelatorioEscalonamento.obterDadosColuna(4);  // Período
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

    private static double calcularWiIndividual(ArrayList<Double> Ji, ArrayList<Double> Ci, ArrayList<Double> Pi, ArrayList<Double> Di, int i) {
        double Wi = Ci.get(i);

        for (int j = 0; j < Ji.size(); j++) {
            if (i != j) {
                // Corrigindo a lógica para calcular Wi
                Wi += Math.ceil((Wi + Ji.get(j)) / Pi.get(i)) * Ci.get(j);
            }
        }

        return Wi;
    }

    

    // Método para calcular o Ri
    private static ArrayList<Double> calcularRi(ArrayList<Double> Wi, ArrayList<Double> Ji) {
        int numTarefas = Wi.size();
        ArrayList<Double> Ri = new ArrayList<>();

        for (int i = 0; i < numTarefas; i++) {
            Ri.add(Wi.get(i) + Ji.get(i));
        }

        return Ri;
    }

    // Método para calcular a utilização do processador
    private static double calcularUtilizacaoProcessador(ArrayList<Double> Ci, ArrayList<Double> Pi) {
        int numTarefas = Ci.size();
        double utilizacao = 0;

        for (int i = 0; i < numTarefas; i++) {
            utilizacao += (Ci.get(i) / Pi.get(i));
        }

        return utilizacao;
    }

    // Método para calcular o tempo de resposta máximo
    private static double calcularTempoRespostaMaximo(ArrayList<Double> Ri) {
        double tempoRespostaMaximo = 0;
        for (double valor : Ri) {
            tempoRespostaMaximo = Math.max(tempoRespostaMaximo, valor);
        }
        return tempoRespostaMaximo;
    }

    // Método para verificar escalonabilidade
    private static double calculaLimiteUtilizacao(int numTarefas, ArrayList<Double> Pi, ArrayList<Double> Di) {
        // Adicionando a lógica correta para calcular o limite de utilização
        double limiteUtilizacao = numTarefas * (Math.pow(2, 1.0 / numTarefas) - 1);

        // Corrigindo a verificação
        for (int i = 0; i < numTarefas; i++) {
            limiteUtilizacao += Di.get(i) / Pi.get(i);
        }

        return limiteUtilizacao;
    }
}
