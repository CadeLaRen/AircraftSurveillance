package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide accurate airborne position information.
 */

/* 1-5   Format type code
 * 6-7   Surveillance status
 * 8     NIC supplement-B
 * 9-20  Altitude
 * 21    Time
 * 22    CPR format
 * 23-39 CPR encoded latitude
 * 40-56 CPR encoded longitude
 *
 * Surveillance status coding
 * 0 = no condition information
 * 1 = permanent alert (emergency condition)
 * 2 = temporary alert (change in Mode A identity code other than emergency condition)
 * 3 = SPI condition (codes 1 and 2 take precedence over code 3)
 */

public class AirbornePosition extends Adsb1090Message {

    public enum SurveillanceStatus {
        NO_CONDITION_INFORMATION,
        PERMANENT_ALERT,
        TEMPORARY_ALERT,
        SPI_CONDITION
    }

    private SurveillanceStatus surveillanceStatus;
    private boolean nicSupplementB;
    private boolean altitudeAvailable;
    private int altitude;
    private boolean baroAltitude;
    private boolean modeSAltitude;
    private boolean timeSynchronization;
    private CompactPositionReport cpr;
    private int navigationIntegrityCategory;
    private double horizontalContainmentRadiusFalse;  // Horizontal Containment Radius if NIC supplement A is false
    private double horizontalContainmentRadiusTrue;  // Horizontal Containment Radius if NIC supplement A is true

    private AirbornePosition() {
    }

    /**
     * Decodes 7 bytes of data into an ADS-B Airborne Position message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Airborne Position message
     * @throws Adsb1090ParseException
     */
    public static AirbornePosition parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("AirbornePosition.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("AirbornePosition.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        AirbornePosition message = new AirbornePosition();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type codes are 0 and  9-18 and 20-22
        if ((message.typeCode < 0) | (message.typeCode > 0 & message.typeCode < 9) | (message.typeCode == 19) | (message.typeCode > 22)) {
            throw new Adsb1090ParseException("AirbornePosition.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        message.subtypeCode = -1;

        int surveillanceStatus = extractInt(data, 5, 2);
        if (surveillanceStatus == 0) {
            message.surveillanceStatus = SurveillanceStatus.NO_CONDITION_INFORMATION;
        } else if (surveillanceStatus == 1) {
            message.surveillanceStatus = SurveillanceStatus.PERMANENT_ALERT;
        } else if (surveillanceStatus == 2) {
            message.surveillanceStatus = SurveillanceStatus.TEMPORARY_ALERT;
        } else if (surveillanceStatus == 3) {
            message.surveillanceStatus = SurveillanceStatus.SPI_CONDITION;
        } else {
            throw new Adsb1090ParseException("AirbornePosition.parse(data): surveillanceStatus != 0,1,2,3 (surveillanceStatus == " + surveillanceStatus + ")");
        }

        message.nicSupplementB = extractBoolean(data, 7);

        int encodedAltitude = extractInt(data, 8, 12);
        message.altitudeAvailable = encodedAltitude != 0;
        message.altitude = decodeAltitude(encodedAltitude);
        message.modeSAltitude = (encodedAltitude & 0x10) == 0x10;  // check Q bit

        if ((message.typeCode == 0) | ((message.typeCode >= 9) & (message.typeCode <= 18))) {
            // typeCode is 0 or 9-18
            message.baroAltitude = true;
        } else if ((message.typeCode >= 20) & (message.typeCode <= 22)) {
            // typeCode is 20-22
            message.baroAltitude = false;
        } else {
            throw new Adsb1090ParseException("AirbornePosition.parse(data): unable to set baroAltitude (typeCode == " + message.typeCode + ")");
        }

        message.timeSynchronization = extractBoolean(data, 20);

        boolean cprFormat = extractBoolean(data, 21);
        int encodedLatitude = extractInt(data, 22, 17);
        int encodedLongitude = extractInt(data, 39, 17);
        message.cpr = new CompactPositionReport(cprFormat, encodedLatitude, encodedLongitude);

        message.navigationIntegrityCategory = decodeNavigationIntegrityCategory(message.typeCode, message.nicSupplementB);
        message.horizontalContainmentRadiusFalse = decodeHorizontalContainmentRadius(message.typeCode, false, message.nicSupplementB);
        message.horizontalContainmentRadiusTrue = decodeHorizontalContainmentRadius(message.typeCode, true, message.nicSupplementB);

        return message;
    }

    /**
     * @return a String representing the ADS-B Airborne Position message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("AirbornePosition");
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
        sb.append("surveillance status = " + surveillanceStatus);
        sb.append(System.lineSeparator());
        sb.append("NIC supplement B = " + nicSupplementB);
        sb.append(System.lineSeparator());
        sb.append("altitude available = " + altitudeAvailable);
        sb.append(System.lineSeparator());
        sb.append("altitude = " + altitude);
        sb.append(System.lineSeparator());
        sb.append("baro altitude = " + baroAltitude);
        sb.append(System.lineSeparator());
        sb.append("mode S altitude = " + modeSAltitude);
        sb.append(System.lineSeparator());
        sb.append("time synchronization = " + timeSynchronization);
        sb.append(System.lineSeparator());
        sb.append("compact position report format = " + cpr.getCprFormat());
        sb.append(System.lineSeparator());
        sb.append("compact position report encoded latitude = " + cpr.getEncodedLatitude());
        sb.append(System.lineSeparator());
        sb.append("compact position report encoded longitude = " + cpr.getEncodedLongitude());
        sb.append(System.lineSeparator());
        sb.append("navigation integrity category = " + navigationIntegrityCategory);
        sb.append(System.lineSeparator());
        sb.append("horizontal containment radius false = " + horizontalContainmentRadiusFalse);
        sb.append(System.lineSeparator());
        sb.append("horitzontal containment radius true = " + horizontalContainmentRadiusTrue);
        sb.append(System.lineSeparator());

        return sb.toString();
    }


    /**
     * @return surveillance status
     */
    public SurveillanceStatus getSurveillanceStatus() {
        return surveillanceStatus;
    }

    /**
     * @return NIC supplement B
     */
    public boolean getNICSupplementB() {
        return nicSupplementB;
    }

    /**
     * @return true if the altitude is available, otherwise false
     */
    public boolean isAltitudeAvailable() {
        return altitudeAvailable;
    }

    /**
     * @return altitude in feet
     */
    public int getAltitude() {
        return altitude;
    }

    /**
     * @return true if the altitude was a Mode S altitude with a 25-foot resolution, false otherwise
     */
    public boolean isModeSAltitude() {
        return modeSAltitude;
    }

    /**
     * @return true if the altitude is a barometric altitude, false if a GNSS altitude
     */
    public boolean isBaroAltitude() {
        return baroAltitude;
    }

    /**
     * @return true if the altitude is a GNSS altitude, false if a barometric altitude
     */
    public boolean isGNSSAltitude() {
        return !baroAltitude;
    }

    /**
     * Time of Applicability synchronized with UTC time.
     *
     * @return true if synchronized, false if not synchronized
     */
    public boolean isTimeSynchronized() {
        return timeSynchronization;
    }

    /**
     * @return true if the position is available, otherwise false
     */
    public boolean isPositionAvailable() {
        return typeCode != 0;
    }

    /**
     * @return compact position report
     */
    public CompactPositionReport getCompactPositionReport() {
        return cpr;
    }

    /**
     * @return Navigation Integrity Category (NIC)
     */
    public int getNavigationIntegrityCategory() {
        return navigationIntegrityCategory;
    }

    /**
     * @return Horizontal Containment Radius in meters assuming a worst case value for NIC supplement A
     */
    public double getHorizontalContainmentRadius() {
        if (horizontalContainmentRadiusTrue > horizontalContainmentRadiusFalse) {
            return horizontalContainmentRadiusTrue;
        } else {
            return horizontalContainmentRadiusFalse;
        }
    }

    /**
     * @param nicSupplementA NIC supplement A
     * @return Horizontal Containment Radius in meters
     */
    public double getHorizontalContainmentRadius(boolean nicSupplementA) {
        if (nicSupplementA) {
            return horizontalContainmentRadiusTrue;
        } else {
            return horizontalContainmentRadiusFalse;
        }
    }

    /**
     * Decodes an encoded altitude.
     *
     * @param encodedAltitude encoded altitude
     * @return altitude in feet
     * @throws Adsb1090ParseException
     */
    private static int decodeAltitude(int encodedAltitude) throws Adsb1090ParseException {
        if ((encodedAltitude < 0) | (encodedAltitude > 4095)) {
            throw new Adsb1090ParseException("AirbornePosition.decodeAltitude(encodedAltitude == " + encodedAltitude + "): Encoded altitude is not valid, (encodedAltitude < 0) | (encodedAltitude > 4095)");
        }

        // 0.0005 - altitude is not available
        // 0.9897 - mode S altitude with 25 foot resolution
        // 0.0097 - mode C altitude with 100 foot resolution

        // check to see if altitude is available
        if (encodedAltitude == 0) {
            return Integer.MIN_VALUE;
        }

        // the Q bit indicates the encoding resolution
        if ((encodedAltitude & 0x10) == 0x10) {
            // 25 foot resolution
            return decodeModeSAltitude(encodedAltitude);
        } else {
            // 100 foot resolution
            return decodeModeCAltitude(encodedAltitude);
        }
    }

    /**
     * Decodes an encoded Mode S altitude.
     *
     * @param encodedAltitude encoded altitude
     * @return altitude in feet
     * @throws Adsb1090ParseException
     */
    private static int decodeModeSAltitude(int encodedAltitude) throws Adsb1090ParseException {
        // verify Q bit is set
        if ((encodedAltitude & 0x10) != 0x10) {
            throw new Adsb1090ParseException("AirbornePosition.decodeModeSAltitude(encodedAltitude == " + encodedAltitude + "): Q bit is clear! (it should be set)");
        }

        int altitude = ((encodedAltitude & 0xFE0) >>> 1) | (encodedAltitude & 0x00F);
        altitude *= 25;
        altitude -= 1000;
        return altitude;
    }

    /**
     * Decodes an encoded Mode C altitude.
     *
     * @param encodedAltitude encoded altitude
     * @return altitude in feet
     * @throws Adsb1090ParseException
     */
    private static int decodeModeCAltitude(int encodedAltitude) throws Adsb1090ParseException {
        // verify Q bit is clear
        if ((encodedAltitude & 0x10) == 0x10) {
            throw new Adsb1090ParseException("AirbornePosition.decodeModeCAltitude(encodedAltitude == " + encodedAltitude + "): Q bit is set! (it should be clear)");
        }

        // Encoded Altitude: C1 A1 C2 A2 C4 A4 B1 Q B2 D2 B4 D4
        // Five Hundreds: D2 D4 A1 A2 A4 B1 B2 B4
        // One Hundreds: C1 C2 C4

        // Examples from ICAO Annex 10 Volume IV, Chapter 3 Appendix
        //
        //(Feet)  D2 D4 A1 A2 A4 B1 B2 B4  C1 C2 C4
        // -1000   0  0  0  0  0  0  0  0   0  1  0
        //  -900   0  0  0  0  0  0  0  0   1  1  0
        //  -800   0  0  0  0  0  0  0  0   1  0  0
        //  -700   0  0  0  0  0  0  0  1   1  0  0
        //  -600   0  0  0  0  0  0  0  1   1  1  0
        //  -500   0  0  0  0  0  0  0  1   0  1  0
        //  -400   0  0  0  0  0  0  0  1   0  1  1
        //  -300   0  0  0  0  0  0  0  1   0  0  1
        //  -200   0  0  0  0  0  0  1  1   0  0  1
        //  -100   0  0  0  0  0  0  1  1   0  1  1
        //     0   0  0  0  0  0  0  1  1   0  1  0
        //   100   0  0  0  0  0  0  1  1   1  1  0
        //   200   0  0  0  0  0  0  1  1   1  0  0
        //   300   0  0  0  0  0  0  1  0   1  0  0
        //   400   0  0  0  0  0  0  1  0   1  1  0
        //   500   0  0  0  0  0  0  1  0   0  1  0
        //   600   0  0  0  0  0  0  1  0   0  1  1
        //   700   0  0  0  0  0  0  1  0   0  0  1
        //   800   0  0  0  0  0  1  1  0   0  0  1
        //   900   0  0  0  0  0  1  1  0   0  1  1
        //  1000   0  0  0  0  0  1  1  0   0  1  0
        //  1100   0  0  0  0  0  1  1  0   1  1  0
        //  1200   0  0  0  0  0  1  1  0   1  0  0

        // extract the five hundreds portion
        int fiveHundreds = 0;
        if ((encodedAltitude & 0x004) == 0x004)  // D2
            fiveHundreds += 0x80;
        if ((encodedAltitude & 0x001) == 0x001)  // D4
            fiveHundreds += 0x40;
        if ((encodedAltitude & 0x400) == 0x400)  // A1
            fiveHundreds += 0x20;
        if ((encodedAltitude & 0x100) == 0x100)  // A2
            fiveHundreds += 0x10;
        if ((encodedAltitude & 0x040) == 0x040)  // A4
            fiveHundreds += 0x08;
        if ((encodedAltitude & 0x020) == 0x020)  // B1
            fiveHundreds += 0x04;
        if ((encodedAltitude & 0x008) == 0x008)  // B2
            fiveHundreds += 0x02;
        if ((encodedAltitude & 0x002) == 0x002)  // B4
            fiveHundreds += 0x01;
        // decode the gray code
        for (int mask = fiveHundreds >>> 1; mask != 0; mask = mask >>> 1) {
            fiveHundreds = fiveHundreds ^ mask;
        }

        // extract the one hundreds portion
        int oneHundreds = 0;
        if ((encodedAltitude & 0x800) == 0x800)  // C1
            oneHundreds += 0x04;
        if ((encodedAltitude & 0x200) == 0x200)  // C2
            oneHundreds += 0x02;
        if ((encodedAltitude & 0x080) == 0x080)  // C4
            oneHundreds += 0x01;
        // decode the gray code
        for (int mask = oneHundreds >>> 1; mask != 0; mask = mask >>> 1) {
            oneHundreds = oneHundreds ^ mask;
        }

        // valid values for one hundreds portion is 1,2,3,4,7
        if (oneHundreds == 0 | oneHundreds == 5 | oneHundreds == 6)
            throw new Adsb1090ParseException("AirbornePosition.decodeModeCAltitude(encodedAltitude == " + encodedAltitude + "): Unable to decode mode C altitude (one hundreds == " + oneHundreds + ")");

        // the one hundreds portion isn't a true gray code
        if (oneHundreds == 7)
            oneHundreds = 5;

        // unwrap the symetric nature of the one hundreds portion
        if (fiveHundreds % 2 != 0)
            oneHundreds = 6 - oneHundreds;

        int altitude = fiveHundreds * 500;
        altitude += oneHundreds * 100;
        altitude -= 1300;
        return altitude;
    }

    /**
     * Decodes the Navigation Integrity Category (NIC).
     *
     * @param typeCode       type code
     * @param nicSupplementB NIC supplement B
     * @return Navigation Integrity Category (NIC)
     * @throws Adsb1090ParseException
     */
    private static int decodeNavigationIntegrityCategory(int typeCode, boolean nicSupplementB) throws Adsb1090ParseException {
        switch (typeCode) {
            case 0:
                return 0;
            case 9:
                return 11;
            case 10:
                return 10;
            case 11:
                if (nicSupplementB)
                    return 9;
                else
                    return 8;
            case 12:
                return 7;
            case 13:
                return 6;
            case 14:
                return 5;
            case 15:
                return 4;
            case 16:
                if (nicSupplementB)
                    return 3;
                else
                    return 2;
            case 17:
                return 1;
            case 18:
                return 0;
            case 20:
                return 11;
            case 21:
                return 10;
            case 22:
                return 0;
            default:
                throw new Adsb1090ParseException("AirbornePosition.decodeNavigationIntegrityCategory(typeCode == " + typeCode + ", nicSupplementB == " + nicSupplementB + "): Unable to decode Navigation Integrity Category");
        }
    }

    /**
     * Decodes the horizontal containment radius.
     *
     * @param typeCode       type code
     * @param nicSupplementA NIC supplement A
     * @param nicSupplementB NIC Supplement B
     * @return Horizontal containment radius in meters
     * @throws Adsb1090ParseException
     */
    private static double decodeHorizontalContainmentRadius(int typeCode, boolean nicSupplementA, boolean nicSupplementB) throws Adsb1090ParseException {
        switch (typeCode) {
            case 0:
                return Double.MAX_VALUE;
            case 9:
                return 7.5;
            case 10:
                return 25;
            case 11:
                if (nicSupplementB)
                    return 75;
                else
                    return 185.2;
            case 12:
                return 370.4;
            case 13:
                if (nicSupplementA) {
                    if (nicSupplementB)
                        return 1111.2;
                    else
                        return -1;
                } else {
                    if (nicSupplementB)
                        return 555.6;
                    else
                        return 925;
                }
            case 14:
                return 1852;
            case 15:
                return 3704;
            case 16:
                if (nicSupplementB)
                    return 7408;
                else
                    return 14816;
            case 17:
                return 37040;
            case 18:
                return Double.MAX_VALUE;
            case 20:
                return 7.5;
            case 21:
                return 25;
            case 22:
                return Double.MAX_VALUE;
            default:
                throw new Adsb1090ParseException("AirbornePosition.decodeHorizontalContainmentRadius(typeCode == " + typeCode + ", nicSupplementA == " + nicSupplementA + ", nicSupplementB == " + nicSupplementB + "): Unable to decode horizontal containment radius");
        }
    }
}
