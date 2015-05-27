/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repsys;

/**
 *
 * @author ETIS
 */
public final class BinaryUtils {
    
    public static int bitIs(int bits, int bit) {
        return (bits >> bit) & 1;
    }
    
    public static boolean bitIsTrue(int bits, int bit) {
        return ((bits >> bit) & 1) == 1;
    }
    
    public static String binPrint(int a, int nums) {
        return String.format("%" + nums + "s", Integer.toBinaryString(a)).replace(' ', '0');
    }
    
    public static String binPrintHelp(int a, int nums) {
        return String.format("%s(%03d)", binPrint(a, nums), a);
    }
}
