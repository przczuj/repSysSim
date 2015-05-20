/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repsys;

import static repsys.BinaryUtils.*;

/**
 *
 * @author ETIS
 */
public class Agent {
    public boolean rep;
    public int strat;
    private double errChance;
    
    public int scoreB;
    public int scoreC;
    public boolean lastAction;
    
    int transactionCount;

    public Agent(boolean rep, int strat, double errChance) {
        this.rep = rep;
        this.strat = strat;
        this.errChance = errChance;
        this.scoreB = 0;
        this.scoreC = 0;
        this.transactionCount = 0;
    }

    public Agent(int strat) {
        this(false, strat, 0.0);
    }

    public boolean getAction(Agent partner) {
        int pos = (this.rep?0:1 << 1) + (partner.rep?0:1);
        boolean action = bitIsTrue(this.strat, pos);
        return Math.random() < errChance ? !action : action;
    }

    public static void makeTransaction(Agent a, Agent b) {
        a.lastAction = a.getAction(b);
        b.lastAction = b.getAction(a);
        if (a.lastAction) {
            a.scoreC--;
            b.scoreB++;
        }
        if (b.lastAction) {
            b.scoreC--;
            a.scoreB++;
        }
        a.transactionCount++;
        b.transactionCount++;
    }
    
    public double getMeanScore(int bValue, int cValue) {
        return ((double)scoreB * bValue + (double)scoreC * cValue) / transactionCount;
    }
}
