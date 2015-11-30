package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide additional state information for both normal and supersonic flight.
 */
      
/* 1-5   Format type code = 19
 * 6-8   Subtype code
 * 9     Intent change flag
 * 10    Reserved-A
 * 11-13 Navigation accuracy category for velocity
 * 14    Direction bit for E-W velocity (0 = East, 1 = West)
 * 15-24 East-West velocity
 * 25    Direction bit for N-S velocity (0 = North, 1 = South)
 * 26-35 North-South velocity
 * 36    Source bit for vertical rate (0 = Geometric, 1 = Baro)
 * 37    Sign bit for vertical rate (0 = Up, 1 = Down)
 * 38-46 Vertical rate
 * 47-48 Reserved-B
 * 49    Difference sign bit (0 = Above baro, 1 = Below baro)
 * 50-56 Geometric height difference from baro altitude)
 *
 * Subtype coding
 * 0 = Reserved
 * 1 = Ground speed, normal
 * 2 = Ground speed, supersonic
 * 3 = Airspeed and heading, normal
 * 4 = Airspeed and heading, supersonic
 * 5 = Not assigned
 * 6 = Not assigned
 * 7 = Not assigned
 *
 * Reference ARINC labels for velocity
 * East-West   GPS: 174
 * East-West   INS: 367
 * North-South GPS: 166
 * North-South INS: 366
 *
 * Reference ARINC labels
 * GNSS height (HAE)   GPS: 370
 * GNSS Altitude (MSL) GPS: 076
 */

public class VelocityOverGround extends AirborneVelocity {

    private boolean eastWestVelocityDirection;
    private boolean eastWestVelocityAvailable;
    private boolean eastWestVelocityOverflow;
    private int eastWestVelocity;
    private boolean northSouthVelocityDirection;
    private boolean northSouthVelocityAvailable;
    private boolean northSouthVelocityOverflow;
    private int northSouthVelocity;

    private VelocityOverGround() {

    }

    /**
     * Decodes 7 bytes of data into an ADS-B Velocity Over Ground message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Velocity Over Ground message
     * @throws Adsb1090ParseException
     */
    public static VelocityOverGround parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("VelocityOverGround.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("VelocityOverGround.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        VelocityOverGround message = new VelocityOverGround();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type code is 19
        if (message.typeCode != 19) {
            throw new Adsb1090ParseException("VelocityOverGround.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 3);
        // valid sub type codes are 1-2
        if ((message.subtypeCode != 1) & (message.subtypeCode != 2)) {
            throw new Adsb1090ParseException("VelocityOverGround.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        message.intentChangeFlag = extractBoolean(data, 8);
        message.reservedA = extractBoolean(data, 9);
        message.navigationAccuracyCategoryVelocity = extractInt(data, 10, 3);
        message.eastWestVelocityDirection = extractBoolean(data, 13);

        int eastWestVelocity = extractInt(data, 14, 10);
        message.eastWestVelocityAvailable = eastWestVelocity != 0;
        message.eastWestVelocityOverflow = eastWestVelocity == 1023;
        message.eastWestVelocity = decodeVelocity(eastWestVelocity, message.subtypeCode);

        message.northSouthVelocityDirection = extractBoolean(data, 24);

        int northSouthVelocity = extractInt(data, 25, 10);
        message.northSouthVelocityAvailable = northSouthVelocity != 0;
        message.northSouthVelocityOverflow = northSouthVelocity == 1023;
        message.northSouthVelocity = decodeVelocity(northSouthVelocity, message.subtypeCode);

        message.verticalRateSource = extractBoolean(data, 35);
        message.verticalRateSign = extractBoolean(data, 36);

        int verticalRate = extractInt(data, 37, 9);
        message.verticalRateAvailable = verticalRate != 0;
        message.verticalRateOverflow = verticalRate == 511;
        message.verticalRate = decodeVerticalRate(verticalRate);

        message.reservedB1 = extractBoolean(data, 46);
        message.reservedB2 = extractBoolean(data, 47);
        message.geometricHeightDifferenceSign = extractBoolean(data, 48);

        int geometricHeightDifference = extractInt(data, 49, 7);
        message.geometricHeightDifferenceAvailable = geometricHeightDifference != 0;
        message.geometricHeightDifferenceOverflow = geometricHeightDifference == 127;
        message.geometricHeightDifference = decodeGeometricHeightDifference(geometricHeightDifference);

        return message;
    }

    /**
     * @return a String representing the ADS-B Velocity Over Ground message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("VelocityOverGround");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int p = 0; p < originalMessage.length; p++)
            sb.append(String.format("%02x", originalMessage[p]));
        sb.append(System.lineSeparator());
        sb.append("type code = " + typeCode);
        sb.append(System.lineSeparator());
        sb.append("sub type code = " + subtypeCode);
        sb.append(System.lineSeparator());
        sb.append("intent change flag = " + intentChangeFlag);
        sb.append(System.lineSeparator());
        sb.append("reserved A = " + reservedA);
        sb.append(System.lineSeparator());
        sb.append("navigation accuracy category velocity = " + navigationAccuracyCategoryVelocity);
        sb.append(System.lineSeparator());
        sb.append("east west velocity direction = " + eastWestVelocityDirection);
        sb.append(System.lineSeparator());
        sb.append("east west velocity available = " + eastWestVelocityAvailable);
        sb.append(System.lineSeparator());
        sb.append("east west velocity overflow = " + eastWestVelocityOverflow);
        sb.append(System.lineSeparator());
        sb.append("east west velocity = " + eastWestVelocity);
        sb.append(System.lineSeparator());
        sb.append("north south velocity direction = " + northSouthVelocityDirection);
        sb.append(System.lineSeparator());
        sb.append("north south velocity available = " + northSouthVelocityAvailable);
        sb.append(System.lineSeparator());
        sb.append("north south velocity overflow = " + northSouthVelocityOverflow);
        sb.append(System.lineSeparator());
        sb.append("north south velocity = " + northSouthVelocity);
        sb.append(System.lineSeparator());
        sb.append("vertical rate source = " + verticalRateSource);
        sb.append(System.lineSeparator());
        sb.append("vertical rate sign = " + verticalRateSign);
        sb.append(System.lineSeparator());
        sb.append("vertical rate available = " + verticalRateAvailable);
        sb.append(System.lineSeparator());
        sb.append("vertical rate overflow = " + verticalRateOverflow);
        sb.append(System.lineSeparator());
        sb.append("vertical rate = " + verticalRate);
        sb.append(System.lineSeparator());
        sb.append("reserved B1 = " + reservedB1);
        sb.append(System.lineSeparator());
        sb.append("reserved B2 = " + reservedB2);
        sb.append(System.lineSeparator());
        sb.append("geometric height difference sign = " + geometricHeightDifferenceSign);
        sb.append(System.lineSeparator());
        sb.append("geometric height difference available = " + geometricHeightDifferenceAvailable);
        sb.append(System.lineSeparator());
        sb.append("geometric height difference overflow = " + geometricHeightDifferenceOverflow);
        sb.append(System.lineSeparator());
        sb.append("geometric height difference = " + geometricHeightDifference);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    /**
     * @return true if velocity is to the east, false if velocity is to the west
     */
    public boolean isEastVelocity() {
        return !eastWestVelocityDirection;
    }

    /**
     * @return true if velocity is to the west, false if velocity is to the east
     */
    public boolean isWestVelocity() {
        return eastWestVelocityDirection;
    }

    /**
     * @return true if the east west velocity is available, otherwise false
     */
    public boolean isEastWestVelocityAvailable() {
        return eastWestVelocityAvailable;
    }

    /**
     * Determines if east west velocity is too large to fit in the space allocated.
     *
     * @return true if the east west velocity is available and is too large to fit in the space available, otherwise false
     */
    public boolean isEastWestVelocityOverflow() {
        return eastWestVelocityOverflow;
    }

    /**
     * @return East west velocity in knots
     */
    public int getEastWestVelocity() {
        return eastWestVelocity;
    }

    /**
     * @return true if velocity is to the north, false is velocity is to the south
     */
    public boolean isNorthVelocity() {
        return !northSouthVelocityDirection;
    }

    /**
     * @return true if velocity is to the south, false is velocity is to the north
     */
    public boolean isSouthVelocity() {
        return northSouthVelocityDirection;
    }

    /**
     * @return true if the east west velocity is available, otherwise false
     */
    public boolean isNorthSouthVelocityAvailable() {
        return northSouthVelocityAvailable;
    }

    /**
     * Determines if north south velocity is too large to fit in the space allocated.
     *
     * @return true if the north south velocity is available and is too large to fit in the space available, otherwise false
     */
    public boolean isNorthSouthVelocityOverflow() {
        return northSouthVelocityOverflow;
    }

    /**
     * @return North south velocity in knots
     */
    public int getNorthSouthVelocity() {
        return northSouthVelocity;
    }

}
