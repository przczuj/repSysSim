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
    
    public int isStable(RepSys repSys, int normStrat) {
        long startTime = System.nanoTime();
        SimulationEnvirenment sys = new SimulationEnvirenment(repSys, populationSize, mutantCount);
        long endTime = System.nanoTime();
        System.out.printf("init time = %d\n", endTime - startTime);
        
        for (int mutStratBin = 0; mutStratBin < 16; mutStratBin++) {
            if (normStrat != mutStratBin) {
                for (int c = 0; c < stabilityCheckRepetition; c++) {
                    sys.repopulate(normStrat, mutStratBin, agentErrChance, repGen);
                    sys.simulate(transactionsPerAgent);
                    if (sys.getMutantsMeanScore() > sys.getNormalsMeanScore()) {
                        //System.out.println("unstable! " + binPrint(s1, 4) + " " + binPrint(s2, 4));
                        return c;
                    }
                }
                System.out.printf("strategy mut: %s mutScore=%f normScore=%f\n", 
                        binPrint(mutStratBin, 4), sys.getMutantsMeanScore(), sys.getNormalsMeanScore());
            }
        }
        return stabilityCheckRepetition;
    }

    public void execute(double minMeanScore) {
        
        double bestMeanScore = 0;
        int bestStrategy = 0;
        int bestStrategyBehavior = 0;
        
        for (int repSysBin = 0; repSysBin < 256; repSysBin++) {                 System.out.println("rep: " + binPrint(repSysBin, 8));
            RepSys repSys = new RepSys(repSysBin, repSysErrChance);
            SimulationEnvirenment simEnv = new SimulationEnvirenment(repSys, populationSize, mutantCount);
            
            for (int normStratBin = 0; normStratBin < 16; normStratBin++) {     System.out.println("strategy norm: " + binPrint(normStratBin, 4));
                int stabilityLevel = isStable(repSys, normStratBin);
                
                if (stabilityLevel == stabilityCheckRepetition) {
                    simEnv.repopulate(normStratBin, normStratBin, agentErrChance, repGen);
                    simEnv.simulate(transactionsPerAgent);
                    if (simEnv.getNormalsMeanScore() > minMeanScore) {
                        System.out.printf("stable strategy = rep:%s(%d) strat:%s(%d) flow:%s(%d) normMean:%f\n",
                                binPrint(repSysBin, 8), repSysBin,
                                binPrint(normStratBin, 4), normStratBin,
                                binPrint(repFlow(repSysBin, normStratBin), 4), repFlow(repSysBin, normStratBin),
                                simEnv.getNormalsMeanScore());
                    }
                    if(simEnv.getNormalsMeanScore() > bestMeanScore) {
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
                10,     // stabilityCheckRepetition
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

