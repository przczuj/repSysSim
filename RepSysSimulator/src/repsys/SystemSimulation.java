/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repsys;

import static repsys.BinaryUtils.*;
import static repsys.RepSys.repFlow;

/**
 *
 * @author ETIS
 */
public class SystemSimulation {
    public final int stabilityCheckRepetition;
    public final int populationSize;
    public final int mutantCount;
    public final int transactionsPerAgent;
    public final double agentErrChance;
    public final double repSysErrChance;
    public final SimulationEnvirenment.ReputatuinGenerator repGen;
    
    public SystemSimulation(
            int stabilityCheckRepetition, 
            int populationSize, 
            int mutantCount, 
            int transactionsPerAgent,
            double agentErrChance,
            double repSysErrChance,
            SimulationEnvirenment.ReputatuinGenerator repGen) {
        this.stabilityCheckRepetition = stabilityCheckRepetition;
        this.populationSize = populationSize;
        this.mutantCount = mutantCount;
        this.transactionsPerAgent = transactionsPerAgent;
        this.agentErrChance = agentErrChance;
        this.repSysErrChance = repSysErrChance;
        this.repGen = repGen;
    }
    
    public boolean isStable(RepSys repSys, int normStrat, double mutantScoreBonus, boolean print) {
        SimulationEnvirenment sys = new SimulationEnvirenment(repSys, populationSize, mutantCount);
        
        for (int mutStratBin = 0; mutStratBin < 16; mutStratBin++) {
            if (normStrat != mutStratBin) {
                for (int c = 0; c < stabilityCheckRepetition; c++) {
                    sys.repopulate(normStrat, mutStratBin, agentErrChance, repGen);
                    sys.simulate(transactionsPerAgent);
                    if (sys.getMutantsMeanScore() + mutantScoreBonus > sys.getNormalsMeanScore()) {
                        //System.out.println("unstable! " + binPrint(s1, 4) + " " + binPrint(s2, 4));
                        return false;
                    }
                }
                if (print) {
                    System.out.printf("rep:%s strat:%s mutStrat:%s mutScore=%f normScore=%f diff=%f\n",
                            binPrintHelp(repSys.getBin(), 8),
                            binPrintHelp(normStrat, 4),
                            binPrintHelp(mutStratBin, 4), 
                            sys.getMutantsMeanScore(), sys.getNormalsMeanScore(),
                            sys.getNormalsMeanScore() - sys.getMutantsMeanScore());
                }
            }
        }
        return true;
    }

    public void execute(double minMeanScore, double mutantScoreBonus, boolean mutationPrint) {
        
        double bestMeanScore = 0;
        int bestStrategy = 0;
        int bestStrategyBehavior = 0;
        
        for (int repSysBin = 0; repSysBin < 256; repSysBin++) {
            RepSys repSys = new RepSys(repSysBin, repSysErrChance);
            SimulationEnvirenment simEnv = new SimulationEnvirenment(repSys, populationSize, mutantCount);
            
            for (int normStratBin = 0; normStratBin < 16; normStratBin++) {
                boolean isStable = isStable(repSys, normStratBin, mutantScoreBonus, mutationPrint);
                boolean toPrint = isStable;
                
                if (toPrint) {
                    simEnv.repopulate(normStratBin, normStratBin, agentErrChance, repGen);
                    simEnv.simulate(transactionsPerAgent);
                    if (simEnv.getNormalsMeanScore() > minMeanScore) {
                        System.out.printf("%srep:%s strat:%s flow:%s normScore:%f\n",
                                isStable ? "!stable strategy! " : "",
                                binPrintHelp(repSysBin, 8),
                                binPrintHelp(normStratBin, 4),
                                binPrintHelp(repFlow(repSysBin, normStratBin), 4),
                                simEnv.getNormalsMeanScore());
                    }
                    if(isStable && simEnv.getNormalsMeanScore() > bestMeanScore) {
                        bestMeanScore = simEnv.getNormalsMeanScore();
                        bestStrategy = repSysBin;
                        bestStrategyBehavior = normStratBin;
                    }
                }
            }
        }
        System.out.printf("best strategy = rep:%s strat:%s flow:%s normScore:%f\n",
                binPrintHelp(bestStrategy, 8),
                binPrintHelp(bestStrategyBehavior, 4),
                binPrintHelp(repFlow(bestStrategy, bestStrategyBehavior), 4),
                bestMeanScore);
    }
    
    public static void main(String [] args) {
        new SystemSimulation(
                1,      // stabilityCheckRepetition (1)
                1000,   // populationSize (1000)
                50,     // mutantCount (50)
                1000,   // transactionsPerAgent (1000)
                0.1,    // agentErrChance (0)
                0.1,    // repSysErrChance (0)
                SimulationEnvirenment.REP_RANDOM
        ).execute(
                0.11,   // minMeanScore (0.0)
                0.05,   // mutantScoreBonus (1.0)
                true    // mutantPrint (false)
        );
    }
}

