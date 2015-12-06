package aircraftsurveillance;

import java.time.Instant;

public abstract class AircraftSurveillanceMessage {

    protected double receiverLatitude;
    protected double receiverLongitude;
    protected double receiverAltitude;  // meters

    protected Instant timestamp;

    public static AircraftSurveillanceMessage parse(Instant timestamp, Double receiverLatitude, Double receiverLongitude, Double receiverAltitude, int[] data) {
        return null;
    }

    public double getReceiverLatitude() {
        return receiverLatitude;
    }

    public void setReceiverLatitude(double receiverLatitude) {
        this.receiverLatitude = receiverLatitude;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getReceiverAltitude() {
        return receiverAltitude;
    }

    public void setReceiverAltitude(double receiverAltitude) {
        this.receiverAltitude = receiverAltitude;
    }

    public double getReceiverLongitude() {
        return receiverLongitude;
    }

    public void setReceiverLongitude(double receiverLongitude) {
        this.receiverLongitude = receiverLongitude;
    }

    /**
     * Returns an int extracted bit by bit from the array.  Useful for parsing a binary message.
     * The input array is type int but each entry in the array represents a single byte.
     * The starting bit and length do not need to follow any alignment rules.
     *
     * @param data     int array representing a series of bytes
     * @param startBit a zero-based index of the first bit (MSB) to extract
     * @param length   the total number of bits to extract, not to exceed 31
     * @return the int extracted from the array
     */
    protected static int extractInt(int[] data, int startBit, int length) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        if (startBit < 0) {
            throw new IllegalArgumentException("startBit < 0");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("length <= 0");
        }
        if (length > 31) {
            throw new IllegalArgumentException("length > 31");
        }
        if ((length + startBit) > (data.length * 8)) {
            throw new IllegalArgumentException("(length + startBit) > (data.length * 8)");
        }

        // create an int array where each entry represents a single bit
        int[] bits = new int[data.length * 8];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < 8; j++) {
                bits[(i * 8) + j] = (data[i] >>> (7 - j)) & 1;
            }
        }

        // extractInt the bits needed for the return value
        int[] returnBits = new int[length];
        System.arraycopy(bits, startBit, returnBits, 0, returnBits.length);

        // convert the return bits into an integer
        int returnInt = 0;
        for (int i = 0; i < returnBits.length; i++) {
            returnInt |= (returnBits[i] << ((returnBits.length - 1) - i));
        }

        return returnInt;
    }

    /**
     * Performs a bit test on an integer array.  Useful for parsing a binary message.
     * The input array is type int but each entry in the array represents a single byte.
     *
     * @param data     int array representing a series of bytes
     * @param startBit a zero-based index of the first bit (MSB) to extract
     * @return true if the bit is set, false otherwise
     */
    protected static boolean extractBoolean(int[] data, int startBit) {
        return extractInt(data, startBit, 1) == 1;
    }

    /**
     * Inserts an int into an array bit by bit.  Useful for composing a binary message.
     * The array is type int but each entry in the array represents a single byte.
     * The starting bit and length do not need to follow any alignment rules.
     *
     * @param data     int array representing a series of bytes to be changed
     * @param startBit zero-based index of the first bit (MSB) to be changed
     * @param length   total number of bits to be inserted into the array, not to exceed 31
     * @param insert   int containing the bits to be inserted
     */
    protected static void insertInt(int[] data, int startBit, int length, int insert) {
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        if (startBit < 0) {
            throw new IllegalArgumentException("startBit < 0");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("length <= 0");
        }
        if (length > 31) {
            throw new IllegalArgumentException("length > 31");
        }
        if ((length + startBit) > (data.length * 8)) {
            throw new IllegalArgumentException("(length + startBit) > (data.length * 8)");
        }

        // create an int array where each entry represents a single bit
        int[] bits = new int[data.length * 8];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < 8; j++) {
                bits[(i * 8) + j] = (data[i] >>> (7 - j)) & 1;
            }
        }

        // change the bits
        for (int i = 0; i < length; i++) {
            bits[startBit + i] = (insert >>> ((length - 1) - i)) & 1;
        }

        // write the bits back into the original array
        for (int i = 0; i < data.length; i++) {
            int tempInt = 0;
            for (int j = 0; j < 8; j++) {
                tempInt |= (bits[(i * 8) + j] << (7 - j));
            }
            data[i] = tempInt;
        }
    }

}
