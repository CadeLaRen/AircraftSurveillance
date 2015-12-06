package aircraftsurveillance.transponder.adsb1090;

public abstract class Adsb1090Message {
    int[] originalMessage;

    int typeCode;
    int subtypeCode;

    /**
     * @return 5-bit type code
     */
    public int getTypeCode() {
        return typeCode;
    }

    /**
     * @return 3-bit sub type code
     */
    public int getSubtypeCode() {
        return subtypeCode;
    }

    public abstract String toString();

    /**
     * Decodes 7 bytes of data into an ADS-B message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B message
     * @throws Adsb1090ParseException
     */
    public static Adsb1090Message parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("Adsb1090Message.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("Adsb1090Message.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        int typeCode = extractInt(data, 0, 5);
        switch (typeCode) {
            case 0:
                return AirbornePosition.parse(data);

            case 1:
                return IdentificationAndCategory.parse(data);
            case 2:
                return IdentificationAndCategory.parse(data);
            case 3:
                return IdentificationAndCategory.parse(data);
            case 4:
                return IdentificationAndCategory.parse(data);

            case 5:
                return SurfacePosition.parse(data);
            case 6:
                return SurfacePosition.parse(data);
            case 7:
                return SurfacePosition.parse(data);
            case 8:
                return SurfacePosition.parse(data);

            case 9:
                return AirbornePosition.parse(data);
            case 10:
                return AirbornePosition.parse(data);
            case 11:
                return AirbornePosition.parse(data);
            case 12:
                return AirbornePosition.parse(data);
            case 13:
                return AirbornePosition.parse(data);
            case 14:
                return AirbornePosition.parse(data);
            case 15:
                return AirbornePosition.parse(data);
            case 16:
                return AirbornePosition.parse(data);
            case 17:
                return AirbornePosition.parse(data);
            case 18:
                return AirbornePosition.parse(data);

            case 19:
                return AirborneVelocity.parse(data);

            case 20:
                return AirbornePosition.parse(data);
            case 21:
                return AirbornePosition.parse(data);
            case 22:
                return AirbornePosition.parse(data);

            case 23:
                return Adsb1090UnknownMessage.parse(data);

            case 24:
                return SurfaceSystemStatus.parse(data);

            case 25:
                return Adsb1090UnknownMessage.parse(data);
            case 26:
                return Adsb1090UnknownMessage.parse(data);
            case 27:
                return Adsb1090UnknownMessage.parse(data);

            case 28:
                return AircraftStatus.parse(data);

            case 29:
                return TargetStateAndStatus.parse(data);

            case 30:
                return Adsb1090UnknownMessage.parse(data);

            case 31:
                return OperationalStatus.parse(data);

            default:
                throw new Adsb1090ParseException("Adsb1090Message.parse(data): type code is not valid (type code == " + typeCode + ")");
        }
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
    static int extractInt(int[] data, int startBit, int length) {
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
        // this loop could be combined with the loop in the previous step, but is kept separate for better readability
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
    static boolean extractBoolean(int[] data, int startBit) {
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
