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
    
    public boolean isStable(RepSys repSys, int normStrat) {
        SimulationEnvirenment sys = new SimulationEnvirenment(repSys, populationSize, mutantCount);
        
        for (int mutStratBin = 0; mutStratBin < 16; mutStratBin++) {
            if (normStrat != mutStratBin) {
                for (int c = 0; c < stabilityCheckRepetition; c++) {
                    sys.repopulate(normStrat, mutStratBin, agentErrChance, repGen);
                    sys.simulate(transactionsPerAgent);
                    if (sys.getMutantsMeanScore() > sys.getNormalsMeanScore()) {
                        //System.out.println("unstable! " + binPrint(s1, 4) + " " + binPrint(s2, 4));
                        return false;
                    }
                }
                System.out.printf("rep:%s(%d) strat:%s(%d) mutStrat: %s mutScore=%f normScore=%f\n", 
                        binPrint(repSys.getBin(), 8), repSys.getBin(),
                        binPrint(normStrat, 4), normStrat,
                        binPrint(mutStratBin, 4), sys.getMutantsMeanScore(), sys.getNormalsMeanScore());
            }
        }
        return true;
    }

    public void execute(double minMeanScore) {
        
        double bestMeanScore = 0;
        int bestStrategy = 0;
        int bestStrategyBehavior = 0;
        
        for (int repSysBin = 0; repSysBin < 256; repSysBin++) {
            RepSys repSys = new RepSys(repSysBin, repSysErrChance);
            SimulationEnvirenment simEnv = new SimulationEnvirenment(repSys, populationSize, mutantCount);
            
            for (int normStratBin = 0; normStratBin < 16; normStratBin++) {
                boolean isStable = isStable(repSys, normStratBin);
                boolean toPrint = isStable;
                
                if (toPrint) {
                    simEnv.repopulate(normStratBin, normStratBin, agentErrChance, repGen);
                    simEnv.simulate(transactionsPerAgent);
                    if (/*simEnv.getNormalsMeanScore() > minMeanScore*/true) {
                        System.out.printf("%srep:%s(%d) strat:%s(%d) flow:%s(%d) normMean:%f\n",
                                isStable ? "!stable strategy! " : "",
                                binPrint(repSysBin, 8), repSysBin,
                                binPrint(normStratBin, 4), normStratBin,
                                binPrint(repFlow(repSysBin, normStratBin), 4), repFlow(repSysBin, normStratBin),
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
        System.out.printf("best strategy = rep:%s(%d) strat:%s(%d) flow:%s(%d) normMean:%f\n",
                binPrint(bestStrategy, 8), bestStrategy,
                binPrint(bestStrategyBehavior, 4), bestStrategyBehavior,
                binPrint(repFlow(bestStrategy, bestStrategyBehavior), 4), repFlow(bestStrategy, bestStrategyBehavior),
                bestMeanScore);
    }
    
    public static void main(String [] args) {
        new SystemSimulation(
                4,     // stabilityCheckRepetition
                1000,   // populationSize
                50,     // mutantCount
                1000,   // transactionsPerAgent
                0.0,    // agentErrChance
                0.0,    // repSysErrChance
                SimulationEnvirenment.REP_RANDOM
        ).execute(
                0.0     // minMeanScore
        );
    }
}

