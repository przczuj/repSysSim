package repsys;

import static repsys.BinaryUtils.*;

/**
 *
 * @author Przemysław Czuj
 */
public class SimulationEnvirenment {
    public final static ReputatuinGenerator REP_RANDOM = new ReputatuinGenerator() {
        @Override public boolean getReputation() {
            return Math.random() < 0.5;
        }
    };
    public final static ReputatuinGenerator REP_TRUE = new ReputatuinGenerator() {
        @Override public boolean getReputation() {
            return true;
        }
    };
    public final static ReputatuinGenerator REP_FALSE = new ReputatuinGenerator() {
        @Override public boolean getReputation() {
            return false;
        }
    };
    public final static ReputatuinGenerator REP_TURN = new ReputatuinGenerator() {
        boolean last = true;
        @Override
        public boolean getReputation() {
            return last = !last;
        }
    };
    
    public final static int SCORE_B_VALUE = 3;
    public final static int SCORE_C_VALUE = 2;
    
    private RepSys rSys;
    private int populationSize = 1000;
    private int mutantsCount = 50;

    private double normalsMeanScore = 0.0;
    private double mutantsMeanScore = 0.0;
    private Agent[] agents;
    
    public SimulationEnvirenment(RepSys rSys, int populationSize, int mutantsCount) {
        this.rSys = rSys;
        this.populationSize = populationSize;
        this.mutantsCount = mutantsCount;
        agents = new Agent[populationSize];
    }
    
    public interface ReputatuinGenerator {
        boolean getReputation();
    }

    public void repopulate(int normStrat, int mutStrat, double errChance, ReputatuinGenerator repGen) {
        normalsMeanScore = 0.0;
        mutantsMeanScore = 0.0;
        for (int i = 0; i < mutantsCount; i++) {
            agents[i] = new Agent(repGen.getReputation(), mutStrat, errChance);
        }
        for (int i = mutantsCount; i < populationSize; i++) {
            agents[i] = new Agent(repGen.getReputation(), normStrat, errChance);
        }
    }

    public void simulate(int transactionsPerAgent) {
        normalsMeanScore = 0.0;
        mutantsMeanScore = 0.0;
        for (int i = 0; i < populationSize * transactionsPerAgent; i++) {
            int a, b;
            do {
                a = (int)(Math.random() * populationSize);
                b = (int)(Math.random() * populationSize);
            } while (a == b);
            Agent.makeTransaction(agents[a], agents[b]);
            rSys.giveReputations(agents[a], agents[b]);
        }
    }

    public double getMeanScore(int first, int last) {
        double meanScore = 0.0;
        double rangeSize = (last - first + 1);
        for (int i = first; i <= last; i++) {
            meanScore += agents[i].getMeanScore(SCORE_B_VALUE, SCORE_C_VALUE);
        }
        return meanScore / rangeSize;
    }

    public double getMutantsMeanScore() {
        if (mutantsMeanScore == 0.0) {
            mutantsMeanScore = getMeanScore(0, mutantsCount - 1);
        }
        return mutantsMeanScore;
    }

    public double getNormalsMeanScore() {
        if (normalsMeanScore == 0.0) {
            normalsMeanScore = getMeanScore(mutantsCount, populationSize - 1);
        }
        return normalsMeanScore;
    }
}
