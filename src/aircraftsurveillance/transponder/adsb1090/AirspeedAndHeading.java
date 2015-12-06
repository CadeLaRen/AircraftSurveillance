package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide additional state information for both normal and supersonic flight based on airspeed and heading.
 */

/* 1-5   Format type code = 19
 * 6-8   Subtype code
 * 9     Intent change flag
 * 10    Reserved-A
 * 11-13 Navigation accuracy category for velocity
 * 14    Status bit (1 = Heading available, 0 = Not available)
 * 15-24 Heading
 * 25    Airspeed type (0 = IAS, 1 = TAS)
 * 26-35 Airspeed
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
 * Reference ARINC 429 labels for air data source
 * IAS: 206
 * TAS: 210
 *
 * Reference ARINC labels
 * GNSS height (HAE)   GPS: 370
 * GNSS Altitude (MSL) GPS: 076
 */

public class AirspeedAndHeading extends AirborneVelocity {

    private boolean headingStatus;
    private double heading;
    private boolean airspeedType;
    private boolean airspeedAvailable;
    private boolean airspeedOverflow;
    private int airspeed;

    private AirspeedAndHeading() {
    }

    /**
     * Decodes 7 bytes of data into an ADS-B Airspeed and Heading message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Airspeed and Heading message
     * @throws Adsb1090ParseException
     */
    public static AirspeedAndHeading parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("AirspeedAndHeading.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("AirspeedAndHeading.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        AirspeedAndHeading message = new AirspeedAndHeading();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type code is 19
        if (message.typeCode != 19) {
            throw new Adsb1090ParseException("AirspeedAndHeading.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 3);
        // valid sub type codes are 3-4
        if ((message.subtypeCode < 3) | (message.subtypeCode > 4)) {
            throw new Adsb1090ParseException("AirspeedAndHeading.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        message.intentChangeFlag = extractBoolean(data, 8);
        message.reservedA = extractBoolean(data, 9);
        message.navigationAccuracyCategoryVelocity = extractInt(data, 10, 3);
        message.headingStatus = extractBoolean(data, 13);
        message.heading = decodeHeading(extractInt(data, 14, 10));
        message.airspeedType = extractBoolean(data, 24);

        int airspeed = extractInt(data, 25, 10);
        message.airspeedAvailable = airspeed != 0;
        message.airspeedOverflow = airspeed == 1023;
        message.airspeed = decodeVelocity(airspeed, message.subtypeCode);

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
     * @return a String representing the ADS-B Airspeed and Heading message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("AirspeedAndHeading");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int i : originalMessage) {
            sb.append(String.format("%02x", i));
        }
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
        sb.append("heading status = " + headingStatus);
        sb.append(System.lineSeparator());
        sb.append("heading = " + heading);
        sb.append(System.lineSeparator());
        sb.append("airspeed type = " + airspeedType);
        sb.append(System.lineSeparator());
        sb.append("airspeed available = " + airspeedAvailable);
        sb.append(System.lineSeparator());
        sb.append("airspeed overflow = " + airspeedOverflow);
        sb.append(System.lineSeparator());
        sb.append("airspeed = " + airspeed);
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
     * @return true if the heading is available, otherwise false
     */
    public boolean isHeadingAvailable() {
        return headingStatus;
    }

    /**
     * @return heading in degrees
     */
    public double getHeading() {
        return heading;
    }

    /**
     * @return true if airspeed is indicated airspeed, false if true airspeed
     */
    public boolean isAirspeedIndicated() {
        return !airspeedType;
    }

    /**
     * @return true if airspeed is true airspeed, false if indicated airspeed
     */
    public boolean isAirspeedTrue() {
        return airspeedType;
    }

    /**
     * @return true if the airspeed is available, otherwise false
     */
    public boolean isAirspeedAvailable() {
        return airspeedAvailable;
    }

    /**
     * Determines if the airspeed is too large to fit in the space allocated.
     *
     * @return true if the airspeed is available and is too large to fit in the space available, otherwise false
     */
    public boolean isAirspeedOverflow() {
        return airspeedOverflow;
    }

    /**
     * @return airspeed in knots
     */
    public int getAirspeed() {
        return airspeed;
    }


    /**
     * @param encodedHeading encoded heading from raw message, valid range is 0-1023
     * @return heading in degrees
     */
    private static double decodeHeading(int encodedHeading) throws Adsb1090ParseException {
        if ((encodedHeading < 0) | (encodedHeading > 1023)) {
            throw new Adsb1090ParseException("AirspeedAndHeading.decodeHeading(encodedHeading == " + encodedHeading + "): encoded heading is not valid (encodedHeading < 0) | (encodedHeading > 1023)");
        }

        return encodedHeading * (360.0 / 1024.0);
    }

}
