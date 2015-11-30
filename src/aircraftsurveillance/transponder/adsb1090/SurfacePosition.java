package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide accurate surface position information.
 */

/* 1-5   Format type code
 * 6-12  Movement
 * 13    Status for Heading (1 = valid, 0 = not valid)
 * 14-20 Heading (resolution = 360/128 degrees)
 * 21    Time
 * 22    CPR format
 * 23-39 CPR encoded latitude
 * 40-56 CPR encoded longitude
 */

public class SurfacePosition extends Adsb1090Message {

    private int movement;
    private boolean status;
    private double heading;
    private boolean timeSynchronization;
    private CompactPositionReport cpr;

    private SurfacePosition() {

    }

    /**
     * Decodes 7 bytes of data into an ADS-B Surface Position message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Surface Position message
     * @throws Adsb1090ParseException
     */
    public static SurfacePosition parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("SurfacePosition.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("SurfacePosition.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        SurfacePosition message = new SurfacePosition();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid types codes are 5-8
        if ((message.typeCode < 5) | (message.typeCode > 8)) {
            throw new Adsb1090ParseException("SurfacePosition.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        message.subtypeCode = -1;

        message.movement = extractInt(data, 5, 7);
        message.status = extractBoolean(data, 12);
        message.heading = decodeHeading(extractInt(data, 13, 7));
        message.timeSynchronization = extractBoolean(data, 20);

        boolean cprFormat = extractBoolean(data, 21);
        int encodedLatitude = extractInt(data, 22, 17);
        int encodedLongitude = extractInt(data, 39, 17);
        message.cpr = new CompactPositionReport(cprFormat, encodedLatitude, encodedLongitude);

        return message;
    }

    /**
     * @return a String representing the ADS-B Surface Position message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("SurfacePosition");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int p = 0; p < originalMessage.length; p++)
            sb.append(String.format("%02x", originalMessage[p]));
        sb.append(System.lineSeparator());
        sb.append("type code = " + typeCode);
        sb.append(System.lineSeparator());
        sb.append("sub type code = " + subtypeCode);
        sb.append(System.lineSeparator());
        sb.append("movement = " + movement);
        sb.append(System.lineSeparator());
        sb.append("status = " + status);
        sb.append(System.lineSeparator());
        sb.append("heading = " + heading);
        sb.append(System.lineSeparator());
        sb.append("time synchronization = " + timeSynchronization);
        sb.append(System.lineSeparator());
        sb.append("compact position report format = " + cpr.getCprFormat());
        sb.append(System.lineSeparator());
        sb.append("compact position report encoded latitude = " + cpr.getEncodedLatitude());
        sb.append(System.lineSeparator());
        sb.append("compact position report encoded longitude = " + cpr.getEncodedLongitude());
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    /**
     * Provides information on the aircraft ground speed.
     *
     * @return encoded movement information
     */
    public int getMovement() {
        return movement;
    }

    /**
     * @return true if movement information is available, otherwise false
     */
    public boolean isMovementInformationAvailable() {
        return movement != 0;
    }

    /**
     * @return true if aircraft is stopped, otherwise false
     */
    public boolean isAircraftStopped() {
        return movement == 1;
    }

    /**
     * @return true if aircraft is decelerating, otherwise false
     */
    public boolean isAircraftDecelerating() {
        return movement == 125;
    }

    /**
     * @return true if aircraft is accelerating, otherwise false
     */
    public boolean isAircraftAccelerating() {
        return movement == 126;
    }

    /**
     * @return true if aircraft is backing up, otherwise false
     */
    public boolean isAircraftBackingUp() {
        return movement == 127;
    }

    /**
     * @return true if the aircraft ground speed is available, otherwise false
     */
    public boolean isAircraftGroundSpeedAvailable() {
        return movement != 0;
    }

    /**
     * Determines if the aircraft ground speed is too large to fit in the space allocated.
     *
     * @return true if the aircraft ground speed is available and is in excess of 175 knots, otherwise false
     */
    public boolean isAircraftGroundSpeedOverflow() {
        return movement == 124;
    }

    /**
     * Determines if the output of the getAircraftGroundSpeed() method is valid.
     *
     * @return true if the output of the getAircraftGroundSpeed() method is valid, otherwise false
     */
    public boolean isAircraftGroundSpeedValid() {
        return ((movement >= 1) & (movement <= 123));
    }

    /**
     * If the aircraft ground speed is available, returns the ground speed rounded to the nearest knot.
     * If the the aircraft ground speed is not available, return Integer.MIN_VALUE.  Use the
     * isAircraftGroundSpeedAvailable() method to determine if the ground speed is available.
     *
     * @return aircraft ground speed rounded to the nearest knot or Integer.MIN_VALUE if ground speed is not available
     */
    public int getAircraftGroundSpeed() {
        if (movement == 0) {
            return Integer.MIN_VALUE;
        } else if ((movement >= 1) & (movement <= 4)) {
            return 0;
        } else if (movement == 2) {
            return 0;
        } else if ((movement >= 3) & (movement <= 8)) {
            return (int) Math.round(1 - ((8 - movement) * 0.14583331533));
        } else if ((movement >= 9) & (movement <= 12)) {
            return (int) Math.round(2 - ((12 - movement) * 0.25));
        } else if ((movement >= 13) & (movement <= 38)) {
            return (int) Math.round(15 - ((38 - movement) * 0.5));
        } else if ((movement >= 39) & (movement <= 93)) {
            return (int) Math.round(70 - ((93 - movement) * 1));
        } else if ((movement >= 94) & (movement <= 108)) {
            return (int) Math.round(100 - ((108 - movement) * 2));
        } else if ((movement >= 109) & (movement <= 123)) {
            return (int) Math.round(175 - ((123 - movement) * 5));
        } else if (movement == 124) {
            return Integer.MIN_VALUE;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Provides a lower bound for the aircraft ground speed.  This method will always return a valid value.
     * However, in certain situations the lower bound is Double.MIN_VALUE.
     *
     * @return the minimum possible aircraft ground speed in knots
     */
    public double getMinAircraftGroundSpeed() {
        if (movement == 0) {
            return Double.MIN_VALUE;
        } else if (movement == 1) {
            return 0;
        } else if (movement == 2) {
            return 0;
        } else if ((movement >= 3) & (movement <= 8)) {
            return 1 - ((8 - movement) * 0.14583331533);
        } else if ((movement >= 9) & (movement <= 12)) {
            return 2 - ((12 - movement) * 0.25);
        } else if ((movement >= 13) & (movement <= 38)) {
            return 15 - ((38 - movement) * 0.5);
        } else if ((movement >= 39) & (movement <= 93)) {
            return 70 - ((93 - movement) * 1);
        } else if ((movement >= 94) & (movement <= 108)) {
            return 100 - ((108 - movement) * 2);
        } else if ((movement >= 109) & (movement <= 123)) {
            return 175 - ((123 - movement) * 5);
        } else if (movement == 124) {
            return 175;
        } else {
            return Double.MIN_VALUE;
        }
    }

    /**
     * Provides an upper bound for the aircraft ground speed.  This method will always return a valid value.
     * However, in certain situations the upper bound is Double.MAX_VALUE;
     *
     * @return the maximum possible aircraft ground speed in knots
     */
    public double getMaxAircraftGroundSpeed() {
        if (movement == 0) {
            return Double.MAX_VALUE;
        } else if (movement == 1) {
            return 0;
        } else if (movement == 2) {
            return 0.125;
        } else if ((movement >= 3) & (movement <= 8)) {
            return 1 - ((8 - movement) * 0.14583331533);
        } else if ((movement >= 9) & (movement <= 12)) {
            return 2 - ((12 - movement) * 0.25);
        } else if ((movement >= 13) & (movement <= 38)) {
            return 15 - ((38 - movement) * 0.5);
        } else if ((movement >= 39) & (movement <= 93)) {
            return 70 - ((93 - movement) * 1);
        } else if ((movement >= 94) & (movement <= 108)) {
            return 100 - ((108 - movement) * 2);
        } else if ((movement >= 109) & (movement <= 123)) {
            return 175 - ((123 - movement) * 5);
        } else if (movement == 124) {
            return Double.MAX_VALUE;
        } else {
            return Double.MAX_VALUE;
        }
    }

    /**
     * @return true if heading is valid, otherwise false
     */
    public boolean isHeadingValid() {
        return status;
    }

    /**
     * @return heading in degrees
     */
    public double getHeading() {
        return heading;
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
     * @return compact position report
     */
    public CompactPositionReport getCompactPositionReport() {
        return cpr;
    }

    /**
     * @param encodedHeading encoded heading from raw message, valid range is 0-127
     * @return heading in degrees
     * @throws Adsb1090ParseException
     */
    private static double decodeHeading(int encodedHeading) throws Adsb1090ParseException {
        if ((encodedHeading < 0) | (encodedHeading > 127)) {
            throw new Adsb1090ParseException("SurfacePosition.decodeHeading(encodedHeading == " + encodedHeading + "): encoded heading is not valid (encodedHeading < 0) | (encodedHeading > 127)");
        }

        return encodedHeading * (360.0 / 128.0);
    }

}
