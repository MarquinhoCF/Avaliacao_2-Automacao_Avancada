package io.sim.simulator.company;

import io.sim.App;
import io.sim.simulator.report.AuxEscalonamento;
import io.sim.simulator.report.ExcelReport;

/**
 *      A classe CompanyAttExcel é responsável por atualizar relatórios no formato Excel, com base nas informações de comunicação 
 * recebidas pela classe Company.
 */
public class CompanyAttExcel extends Thread {
    private Company company; // Uma instância da classe Company
    private boolean funcionando;

    // Atributos escalonamento de sistema em tempo real
    private int av2Parte;
    private long startTime;
    private long estimativaManualDi = 90;

    public CompanyAttExcel(Company _company) {
        this.company = _company;
        this.funcionando = true;

        // Inicializa startTime e parte de AV2
        this.av2Parte = App.AV2PARTE;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            // Loop principal que verifica a disponibilidade de rotas
            boolean primeiraVez = true;
            boolean escreveuUmaVez = false;
            while (funcionando) {
                long inicio = System.currentTimeMillis();
                Thread.sleep(10); // Aguarda por um curto período (10 milissegundos) para evitar uso intensivo da CPU
                if (company.temReport()) {
                    // Se a instância da classe Company possui relatórios para serem atualizados
                    ExcelReport.atualizaPlanilhaCar(company.pegaComunicacao());  // Atualiza o relatório no Excel
                    if (primeiraVez) {
                        escreveuUmaVez = true;
                    }
                }

                if ((av2Parte == 2) && primeiraVez && escreveuUmaVez) {
                    AuxEscalonamento aux = new AuxEscalonamento("CompanyAttExcel", 6, startTime, inicio, estimativaManualDi);
                    aux.start();
                    primeiraVez = false;
                    escreveuUmaVez = false;
                }
            }
            System.out.println("CompanyAttExcel encerrou!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setFuncionando(boolean _funcionando) {
        funcionando = _funcionando;
    }
}
