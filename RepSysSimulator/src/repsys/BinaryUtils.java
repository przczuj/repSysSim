package repsys;

/**
 *
 * @author Przemysław Czuj
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
