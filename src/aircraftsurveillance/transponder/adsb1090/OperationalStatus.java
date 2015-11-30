package aircraftsurveillance.transponder.adsb1090;

/* Subtype coding
 * 0 = Airborne status message
 * 1 = Surfaced status message
 * 2 = Reserved
 * 3 = Reserved
 * 4 = Reserved
 * 5 = Reserved
 * 6 = Reserved
 * 7 = Reserved
 */

public abstract class OperationalStatus extends Adsb1090Message {

    protected int versionNumber;
    protected boolean nicSupplementA;
    protected int nacPosition;
    protected int sourceIntegrityLevel;
    protected boolean hrd;
    protected boolean silSupplement;

    /**
     * Decodes 7 bytes of data into an ADS-B Operational Status message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Operational Status message
     * @throws Adsb1090ParseException
     */
    public static OperationalStatus parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("OperationalStatus.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("OperationalStatus.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        // verify the type code
        int typeCode = extractInt(data, 0, 5);
        // valid type code is 31
        if (typeCode != 31) {
            throw new Adsb1090ParseException("OperationalStatus.parse(data): type code is not valid (type code == " + typeCode + ")");
        }

        // verify the subtype code
        int subtypeCode = extractInt(data, 5, 3);
        // valid sub type codes are 0-1
        if ((subtypeCode < 0) | (subtypeCode > 1)) {
            throw new Adsb1090ParseException("OperationalStatus.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }

        if (subtypeCode == 0) {
            return AirborneOperationalStatus.parse(data);
        } else if (subtypeCode == 1) {
            return SurfaceOperationalStatus.parse(data);
        } else {
            throw new Adsb1090ParseException("OperationalStatus.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }

    }

    /**
     * Version number of the formats and protocols in use.
     *
     * @return 0 if DO-260 and DO-242, 1 if DO-260A and DO-242A, 2 if DO-260B and DO-242B
     */
    public int getVersionNumber() {
        return versionNumber;
    }

    /**
     * Navigation Integrity Category Supplement A.
     * Used to fine tune the radius of containment.
     *
     * @return NIC Supplement A
     */
    public boolean getNicSupplementA() {
        return nicSupplementA;
    }

    /**
     * Navigation Accuracy Category for Position.
     * Indicates the Estimated Position Uncertainty of the navigation source used for reporting position.
     * When a GPS or GNSS system is used as the position source, this is also known as Horizontal Figure of Merit.
     * Valid values are 0 to 11 with a higher value indicating more accuracy.  The values 12 to 15 are reserved.
     *
     * @return Navigation Accuracy Category for Position
     */
    public int getNacPosition() {
        return nacPosition;
    }

    /**
     * Source Integrity Level.
     * Defines the probability of the reported horizontal position exceeding the radius of containment.
     *
     * @return 0 if unknown or > 1x10^-3, 1 if <= 1X10^-3, 2 if <= 1x10^-5, 3 if <= 1x10^-7
     */
    public int getSourceIntegrityLevel() {
        return sourceIntegrityLevel;
    }

    /**
     * Horizontal Reference Direction.
     * Determines if the reference direction is magnetic north or true north.
     *
     * @return true if magnetic north, false if true north
     */
    public boolean getHorizontalReferenceDirection() {
        return hrd;
    }

    /**
     * Source Integrity Level Supplement.
     * Determines if the probability of exceeding NIC radius of containment is based on per sample or per hour.
     *
     * @return true if per sample, false if per hour
     */
    public boolean getSilSupplement() {
        return silSupplement;
    }

}
