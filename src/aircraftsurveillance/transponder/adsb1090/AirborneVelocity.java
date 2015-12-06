package aircraftsurveillance.transponder.adsb1090;

/*
 * Subtype coding
 * 0 = Reserved
 * 1 = Ground speed, normal
 * 2 = Ground speed, supersonic
 * 3 = Airspeed heading, normal
 * 4 = Airspeed heading, supersonic
 * 5 = Not assigned
 * 6 = Not assigned
 * 7 = Not assigned
 */

public abstract class AirborneVelocity extends Adsb1090Message {

    boolean intentChangeFlag;
    boolean reservedA;
    int navigationAccuracyCategoryVelocity;
    boolean verticalRateSource;
    boolean verticalRateSign;
    boolean verticalRateAvailable;
    boolean verticalRateOverflow;
    int verticalRate;
    boolean reservedB1;
    boolean reservedB2;
    boolean geometricHeightDifferenceSign;
    boolean geometricHeightDifferenceAvailable;
    boolean geometricHeightDifferenceOverflow;
    int geometricHeightDifference;

    /**
     * Decodes 7 bytes of data into an ADS-B Airborne Velocity message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Airborne Velocity message
     * @throws Adsb1090ParseException
     */
    public static AirborneVelocity parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("AirborneVelocity.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("AirborneVelocity.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        // verify the type code
        int typeCode = extractInt(data, 0, 5);
        // valid type code is 19
        if (typeCode != 19) {
            throw new Adsb1090ParseException("AirborneVelocity.parse(data): type code is not valid (type code == " + typeCode + ")");
        }

        // verify the subtype code
        int subtypeCode = extractInt(data, 5, 3);
        // valid sub type codes are 1-4
        if ((subtypeCode < 1) | (subtypeCode > 4)) {
            throw new Adsb1090ParseException("AirborneVelocity.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }

        if (subtypeCode == 1) {
            return VelocityOverGround.parse(data);
        } else if (subtypeCode == 2) {
            return VelocityOverGround.parse(data);
        } else if (subtypeCode == 3) {
            return AirspeedAndHeading.parse(data);
        } else if (subtypeCode == 4) {
            return AirspeedAndHeading.parse(data);
        } else {
            throw new Adsb1090ParseException("AirborneVelocity.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }
    }

    /**
     * @return true if intent has changed, false if no change in intent
     */
    public boolean getIntentChangeFlag() {
        return intentChangeFlag;
    }

    /**
     * @return reserved-A bit
     */
    public boolean getReservedA() {
        return reservedA;
    }

    /**
     * Navigation Accuracy Category for Velocity
     *
     * @return 0 if >= 10 m/s, 1 if < 10 m/s, 2 if < 3 m/s, 3 if < 1 m/s, 4 if < 0.3 m/s
     */
    public int getNavigationAccuracyCategoryVelocity() {
        return navigationAccuracyCategoryVelocity;
    }

    /**
     * @return true if vertical source is geometric, false if barometric
     */
    public boolean isVerticalSourceGeometric() {
        return !verticalRateSource;
    }

    /**
     * @return true if vertical source is barometric, false if geometric
     */
    public boolean isVerticalSourceBaro() {
        return verticalRateSource;
    }

    /**
     * @return true if vertical rate is positive or zero, false if negative
     */
    public boolean isVerticalRatePositive() {
        return !verticalRateSign;
    }

    /**
     * @return true if vertical rate is negative, false otherwise
     */
    public boolean isVerticalRateNegative() {
        return verticalRateSign;
    }

    /**
     * @return true if the vertical rate is available, otherwise false
     */
    public boolean isVerticalRateAvailable() {
        return verticalRateAvailable;
    }

    /**
     * Determines if the vertical rate is too large to fit in the space allocated.
     *
     * @return true if the vertical rate is available and is in excess of 32608 ft/min, otherwise false
     */
    public boolean isVerticalRateOverflow() {
        return verticalRateOverflow;
    }

    /**
     * @return vertical rate in feet per minute
     */
    public int getVerticalRate() {
        return verticalRate;
    }

    /**
     * @return reserved-B1 bit
     */
    public boolean getReservedB1() {
        return reservedB1;
    }

    /**
     * @return reserved-B2 bit
     */
    public boolean getReservedB2() {
        return reservedB2;
    }

    /**
     * @return true if geometric height is above barometric altitude, false if geometric height is below barometric altitude
     */
    public boolean isGeometricHeightAboveBaroAltitude() {
        return !geometricHeightDifferenceSign;
    }

    /**
     * @return true if geometric height is below barometric altitude, false if geometric height is below barometric altitude
     */
    public boolean isGeometricHeightBelowBaroAltitude() {
        return geometricHeightDifferenceSign;
    }

    /**
     * @return true if the geometric height difference is available, otherwise false
     */
    public boolean isGeometricHeightDifferenceAvailable() {
        return geometricHeightDifferenceAvailable;
    }

    /**
     * Determines if the geometric height difference is too large to fit in the space allocated.
     *
     * @return true if the geometric height difference is available and is in excess of 3137.5 feet, otherwise false
     */
    public boolean isGeometricHeightDifferenceOverflow() {
        return geometricHeightDifferenceOverflow;
    }

    /**
     * @return difference between geometric and barometric height in feet
     */
    public int getGeometricHeightDifference() {
        return geometricHeightDifference;
    }

    /**
     * @param encodedVelocity encoded velocity from raw message, valid range is 0-1023
     * @param subtypeCode     sub type code from raw message, valid range is 1-4
     * @return velocity in knots
     * @throws Adsb1090ParseException
     */
    static int decodeVelocity(int encodedVelocity, int subtypeCode) throws Adsb1090ParseException {
        if ((subtypeCode < 1) | (subtypeCode > 4)) {
            throw new Adsb1090ParseException("AirborneVelocity.decodeVelocity(encodedVelocity == " + encodedVelocity + ", subtypeCode == " + subtypeCode + "): invalid sub type code (subtypeCode < 1) | (subtypeCode > 4)");
        }
        if ((encodedVelocity < 0) | (encodedVelocity > 1023)) {
            throw new Adsb1090ParseException("AirborneVelocity.decodeVelocity(encodedVelocity == " + encodedVelocity + ", subtypeCode == " + subtypeCode + "): invalid encoded velocity (encodedVelocity < 0) | (encodedVelocity > 1023)");
        }

        if (encodedVelocity == 0) {
            return Integer.MIN_VALUE;
        }
        if (encodedVelocity == 1023) {
            return Integer.MAX_VALUE;
        }

        if ((subtypeCode == 1) | (subtypeCode == 3)) {
            // normal velocity
            return encodedVelocity - 1;
        } else if ((subtypeCode == 2) | (subtypeCode == 4)) {
            // supersonic velocity
            return ((encodedVelocity - 1) * 4);
        } else {
            throw new Adsb1090ParseException("AirborneVelocity.decodeVelocity(encodedVelocity == " + encodedVelocity + ", subtypeCode == " + subtypeCode + "): sub type code != 1,2,3,4");
        }
    }


    /**
     * @param encodedVerticalRate encoded vertical rate from raw message, valid range is 0-511
     * @return vertical rate in feet per minute
     * @throws Adsb1090ParseException
     */
    static int decodeVerticalRate(int encodedVerticalRate) throws Adsb1090ParseException {
        if ((encodedVerticalRate < 0) | (encodedVerticalRate > 511)) {
            throw new Adsb1090ParseException("AirborneVelocity.decodeVerticalRate(encodedVerticalRate == " + encodedVerticalRate + "): encoded vertical rate is not valid (encodedVerticalRate < 0) | (encodedVerticalRate > 511)");
        }

        if (encodedVerticalRate == 0) {
            return Integer.MIN_VALUE;
        }
        if (encodedVerticalRate == 511) {
            return Integer.MAX_VALUE;
        }

        return ((encodedVerticalRate - 1) * 64);
    }

    /**
     * @param encodedGeometricHeightDifference encoded height difference from raw message, valid range is 0-127
     * @return geometric height difference in feet
     */
    static int decodeGeometricHeightDifference(int encodedGeometricHeightDifference) throws Adsb1090ParseException {
        if ((encodedGeometricHeightDifference < 0) | (encodedGeometricHeightDifference > 127)) {
            throw new Adsb1090ParseException("AirborneVelocity.decodeGeometricHeightDifference(encodedGeometricHeightDifference == " + encodedGeometricHeightDifference + "): encoded geometric height differrerence is not valid (encodedGeometricHeightDifference < 0) | (encodedGeometricHeightDifference > 127)");
        }

        if (encodedGeometricHeightDifference == 0) {
            return Integer.MIN_VALUE;
        }
        if (encodedGeometricHeightDifference == 127) {
            return Integer.MAX_VALUE;
        }

        return ((encodedGeometricHeightDifference - 1) * 25);
    }

}
