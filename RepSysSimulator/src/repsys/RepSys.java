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
public class RepSys {
    private int rSys;
    private double errChance;

    public RepSys(int rSys, double errChance) {
        this.rSys = rSys;
        this.errChance = errChance;
    }
    
    public static int repFlow(int repSys, int strat) {
        return  ((bitIsTrue(strat, 3) ? bitIs(repSys, 6) : bitIs(repSys, 7)) << 3) |
                ((bitIsTrue(strat, 2) ? bitIs(repSys, 4) : bitIs(repSys, 5)) << 2) |
                ((bitIsTrue(strat, 1) ? bitIs(repSys, 2) : bitIs(repSys, 3)) << 1) |
                ((bitIsTrue(strat, 0) ? bitIs(repSys, 0) : bitIs(repSys, 1)) << 0);
    }
    
    public int repFlow(int strat) {
        return repFlow(rSys, strat);
    }
    
    public boolean getNextReputation(boolean oldRep, boolean partnerRep, boolean action) {
        int bit = (oldRep?0:1<<2) + (partnerRep?0:1<<1) + (action?0:1<<0);
        boolean rep = bitIsTrue(rSys, bit);
        return Math.random() < errChance ? !rep : rep;
    }

    public void giveReputations(Agent a, Agent b) {
        boolean aRep = getNextReputation(a.rep, b.rep, a.lastAction);
        boolean bRep = getNextReputation(b.rep, a.rep, b.lastAction);
        a.rep = aRep;
        b.rep = bRep;
    }

    public int getBin() {
        return rSys;
    }
    
}
