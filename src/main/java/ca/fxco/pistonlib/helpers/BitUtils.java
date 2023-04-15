package ca.fxco.pistonlib.helpers;

public class BitUtils {

    /**
     * Combines two 4-bit numbers into a byte using bitwise operations
     *
     * @param num1 The first 4-bit number to be combined
     * @param num2 The second 4-bit number to be combined
     * @return A byte that combines num1 and num2 using bitwise operations
     */
    public static byte combine4BitNumbers(int num1, int num2) {
        // Shift num1 4 bits to the left and combine with num2 using OR
        int combined = (num1 << 4) | num2;

        // Cast the combined value to a byte and return it
        return (byte) combined;
    }

    /**
     * Extracts two 4-bit numbers from a byte that combines them using bitwise operations
     *
     * @param combined The byte that combines two 4-bit numbers using bitwise operations
     * @return An array containing the two 4-bit numbers extracted from the combined byte
     */
    public static int[] extract4BitNumbers(byte combined) {
        // Shift the most significant 4 bits to the right and mask with 0xF to extract the first 4-bit number
        int num1 = (combined >> 4) & 0xF;

        // Mask the least significant 4 bits with 0xF to extract the second 4-bit number
        int num2 = combined & 0xF;

        // Return an array containing the extracted numbers
        return new int[] { num1, num2 };
    }
}
