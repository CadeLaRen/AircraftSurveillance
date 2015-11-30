package aircraftsurveillance.transponder.adsb1090;

/**
 * Class to represent a Compact Position Report used for ADS-B position reporting.
 * The most significant bits in an aircraft's latitude and longitude rarely change.  The compact
 * position report is used to reduce the number of bits transmitted in each message while still
 * allowing the aircraft position to be determined.  A single even or odd report can be used to
 * determine a local position.  If both a even and odd report are known, a globally unique position
 * can be determined.
 */
public class CompactPositionReport {
    private final boolean cprFormat;
    private final int encodedLatitude;
    private final int encodedLongitude;

    public CompactPositionReport(boolean cprFormat, int encodedLatitude, int encodedLongitude) {
        this.cprFormat = cprFormat;
        this.encodedLatitude = encodedLatitude;
        this.encodedLongitude = encodedLongitude;
    }

    public CompactPositionReport(CompactPositionReport cpr) {
        cprFormat = cpr.cprFormat;
        encodedLatitude = cpr.encodedLatitude;
        encodedLongitude = cpr.encodedLongitude;
    }

    /**
     * @return true if odd position, false if even position
     */
    public boolean getCprFormat() {
        return cprFormat;
    }

    /**
     * @return encoded latitude
     */
    public int getEncodedLatitude() {
        return encodedLatitude;
    }

    /**
     * @return encoded longitude
     */
    public int getEncodedLongitude() {
        return encodedLongitude;
    }

    /**
     * @return true if even position, false otherwise
     */
    public boolean isEvenPosition() {
        return !cprFormat;
    }

    /**
     * @return true if odd position, false otherwise
     */
    public boolean isOddPosition() {
        return cprFormat;
    }

}
