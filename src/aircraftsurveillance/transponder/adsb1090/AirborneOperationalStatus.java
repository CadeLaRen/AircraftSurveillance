package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide the capability class and current operational mode of ATC-related applications and other operational information.
 */

/* 1-5   Format type code = 31
 * 6-8   Subtype code
 * 9-24  Airborne capability class codes
 * 25-40 Airborne operational mode (OM) codes
 * 41-43 Version number
 * 44    NIC supplement-A
 * 45-48 Navigational accuracy category - position
 * 49-50 Geometric Vertical Accuracy (GVA)
 * 51-52 Source integrity level
 * 53    NIC baro
 * 54    HRD
 * 55    Source integrity level supplement
 * 56    Reserved
 */

public class AirborneOperationalStatus extends OperationalStatus {

    private int airborneCapabilityClassCodes;
    private int airborneOperationalModeCodes;
    private int gva;
    private boolean nicBaro;
    private boolean reserved;

    private AirborneOperationalStatus() {
    }

    /**
     * Decodes 7 bytes of data into an ADS-B Airborne Operational Status message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Airborne Operational Status message.
     * @throws Adsb1090ParseException
     */
    public static AirborneOperationalStatus parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("AirborneOperationalStatus.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("AirborneOperationalStatus.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        AirborneOperationalStatus message = new AirborneOperationalStatus();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type code is 31
        if (message.typeCode != 31) {
            throw new Adsb1090ParseException("AirborneOperationalStatus.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 2);
        // valid sub type code is 0
        if (message.subtypeCode != 0) {
            throw new Adsb1090ParseException("AirborneOperationalStatus.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        message.airborneCapabilityClassCodes = extractInt(data, 8, 16);
        message.airborneOperationalModeCodes = extractInt(data, 24, 16);
        message.versionNumber = extractInt(data, 40, 3);
        message.nicSupplementA = extractBoolean(data, 43);
        message.nacPosition = extractInt(data, 44, 4);
        message.gva = extractInt(data, 48, 2);
        message.sourceIntegrityLevel = extractInt(data, 50, 2);
        message.nicBaro = extractBoolean(data, 52);
        message.hrd = extractBoolean(data, 53);
        message.silSupplement = extractBoolean(data, 54);
        message.reserved = extractBoolean(data, 55);

        return message;
    }

    /**
     * @return a String representing the ADS-B Airborne Operational Status message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("AirborneOperationalStatus");
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
        sb.append("airborne capability class codes = " + airborneCapabilityClassCodes);
        sb.append(System.lineSeparator());
        sb.append("airborne operational mode codes = " + airborneOperationalModeCodes);
        sb.append(System.lineSeparator());
        sb.append("version number = " + versionNumber);
        sb.append(System.lineSeparator());
        sb.append("NIC supplement A = " + nicSupplementA);
        sb.append(System.lineSeparator());
        sb.append("NAC position = " + nacPosition);
        sb.append(System.lineSeparator());
        sb.append("GVA = " + gva);
        sb.append(System.lineSeparator());
        sb.append("source integrity level = " + sourceIntegrityLevel);
        sb.append(System.lineSeparator());
        sb.append("NIC baro = " + nicBaro);
        sb.append(System.lineSeparator());
        sb.append("HRD = " + hrd);
        sb.append(System.lineSeparator());
        sb.append("SIL supplement = " + silSupplement);
        sb.append(System.lineSeparator());
        sb.append("reserved = " + reserved);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    /**
     * @return 16-bit Airborne Capability Class Codes
     */
    public int getAirborneCapabilityClassCodes() {
        return airborneCapabilityClassCodes;
    }

    /**
     * @return 16-bit Airborne Operational Mode Codes
     */
    public int getAirborneOperationalModeCodes() {
        return airborneOperationalModeCodes;
    }

    /**
     * When a GPS or GNSS system is used as the position source, this is also known as Vertical Figure of Merit.
     *
     * @return 0 if unknown or > 45 meters, 1 if <= 45 meters, 2 and 3 are reserved but should be treated as less than 45 meters
     */
    public int getGeometricVerticalAccuracy() {
        return gva;
    }

    /**
     * Navigation Integrity Category for Barometric Altitude.
     *
     * @return true if cross-checked (or uses a non-Gilham coded source), false if not cross-checked
     */
    public boolean getNicBaro() {
        return nicBaro;
    }

    /**
     * @return reserved bit
     */
    public boolean getReserved() {
        return reserved;
    }

}
