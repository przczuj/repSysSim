package repsys;

import static repsys.BinaryUtils.*;
import static repsys.RepSys.repFlow;

/**
 *
 * @author Przemysław Czuj
 */
public class SystemSimulation {
    public final int stabilityCheckRepetition;
    public final int populationSize;
    public final int mutantCount;
    public final int transactionsPerAgent;
    public final double agentErrChance;
    public final double repSysErrChance;
    public final SimulationEnvirenment.ReputatuinGenerator repGen;
    
    public static int[] ALL_REP_SYS() {
        int[] repSysList = new int[256];
        for (int repSysBin = 0; repSysBin < 256; repSysBin++) {
            repSysList[repSysBin] = repSysBin;
        }
        return repSysList;
    }
    
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
    
    public boolean isStable(RepSys repSys, int normStrat, double mutantScoreBonus, boolean printStable, boolean skipOnUnstable) {
        SimulationEnvirenment sys = new SimulationEnvirenment(repSys, populationSize, mutantCount);
        boolean isStable = true;
        
        for (int mutStratBin = 0; mutStratBin < 16; mutStratBin++) {
            if (normStrat != mutStratBin) {
                for (int c = 0; c < stabilityCheckRepetition; c++) {
                    sys.repopulate(normStrat, mutStratBin, agentErrChance, repGen);
                    sys.simulate(transactionsPerAgent);
                    if (sys.getMutantsMeanScore() + mutantScoreBonus > sys.getNormalsMeanScore()) {
                        if (skipOnUnstable) {
                            return false;
                        }
                        isStable = false;
                    }
                }
                if (printStable) {
                    System.out.printf("rep:%s strat:%s mutStrat:%s mutScore=%f normScore=%f diff=%f %s\n",
                            binPrintHelp(repSys.getBin(), 8),
                            binPrintHelp(normStrat, 4),
                            binPrintHelp(mutStratBin, 4), 
                            sys.getMutantsMeanScore(), sys.getNormalsMeanScore(),
                            sys.getNormalsMeanScore() - sys.getMutantsMeanScore(),
                            (sys.getMutantsMeanScore() + mutantScoreBonus > sys.getNormalsMeanScore()) ? "unstable" : "");
                }
            }
        }
        return isStable;
    }

    public void execute(int[] repSysList, double minMeanScore, double mutantScoreBonus, 
            boolean mutantStablePrint, boolean skipOnUnstable, boolean normalUnstablePrint, boolean normalLesserPrint) {
        if (repSysList == null) {
            repSysList = ALL_REP_SYS();
        }
        
        String repGenName = "Unknown";
        repGenName = this.repGen == SimulationEnvirenment.REP_RANDOM ? "random" : repGenName;
        repGenName = this.repGen == SimulationEnvirenment.REP_TRUE ? "all on 1" : repGenName;
        repGenName = this.repGen == SimulationEnvirenment.REP_FALSE ? "all on 0" : repGenName;
        repGenName = this.repGen == SimulationEnvirenment.REP_TURN ? "on turns" : repGenName;
        System.out.printf("Simulation for %d reputation systems with parameters:\n"
                + "populationSize = %d\n"
                + "mutantCount = %d\n"
                + "transactionsPerAgent = %d\n"
                + "agentErrChance = %f\n"
                + "repSysErrChance = %f\n"
                + "reputationInitiator = %s\n"
                + "minimumMeanScore = %f\n"
                + "mutantScoreBonus = %f\n",
                repSysList.length,
                this.populationSize,
                this.mutantCount,
                this.transactionsPerAgent,
                this.agentErrChance,
                this.repSysErrChance,
                repGenName,
                minMeanScore,
                mutantScoreBonus);
        
        double bestMeanScore = 0;
        int bestStrategy = 0;
        int bestStrategyBehavior = 0;
        
        for (int repSysBin : repSysList) {
            RepSys repSys = new RepSys(repSysBin, repSysErrChance);
            SimulationEnvirenment simEnv = new SimulationEnvirenment(repSys, populationSize, mutantCount);
            
            for (int normStratBin = 0; normStratBin < 16; normStratBin++) {
                boolean isStable = isStable(repSys, normStratBin, mutantScoreBonus, mutantStablePrint, skipOnUnstable);
                boolean toPrint = isStable | normalUnstablePrint;
                
                if (toPrint) {
                    simEnv.repopulate(normStratBin, normStratBin, agentErrChance, repGen);
                    simEnv.simulate(transactionsPerAgent);
                    if (normalLesserPrint | simEnv.getNormalsMeanScore() > minMeanScore) {
                        System.out.printf("%srep:%s strat:%s flow:%s normScore:%f %s\n",
                                    isStable ? "!stable strategy! " : "",
                                    binPrintHelp(repSysBin, 8),
                                    binPrintHelp(normStratBin, 4),
                                    binPrintHelp(repFlow(repSysBin, normStratBin), 4),
                                    simEnv.getNormalsMeanScore(),
                                    simEnv.getNormalsMeanScore() > minMeanScore ? "" : " < " + minMeanScore);
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
                SimulationEnvirenment.REP_TURN
        ).execute(
                ALL_REP_SYS(), // repSysList (ALL_REP_SYS())
                    // new int[]{25, 29, 139, 155},
                0.11,   // minMeanScore (0.0)
                0.05,   // mutantScoreBonus (0.03)
                true,   // mutantStablePrint (false)
                true,   // skipOnUnstable (false)
                false,  // normalUnstablePrint (false)
                false   // normalLesserPrint (false)
        );
    }
}

