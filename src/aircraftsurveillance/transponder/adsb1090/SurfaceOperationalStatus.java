package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide the capability class and current operational mode of ATC-related applications and other operational information.
 */

/* 1-5   Format type code = 31
 * 6-8   Subtype code
 * 9-20  Surface capability class codes
 * 21-24 Length/width codes
 * 25-40 Surface operational mode (OM) codes
 * 41-43 Version number
 * 44    NIC supplement-A
 * 45-48 Navigational accuracy category - position
 * 49-50 Reserved
 * 51-52 Source integrity level
 * 53    TRK/HDG
 * 54    HRD
 * 55    Source integrity level supplement
 * 56    Reserved
 */

public class SurfaceOperationalStatus extends OperationalStatus {

    private int surfaceCapabilityClassCodes;
    private int lengthWidthCodes;
    private int surfaceOperationalModeCodes;
    private boolean reserved1;
    private boolean reserved2;
    private boolean trkHdg;
    private boolean reserved3;

    private SurfaceOperationalStatus() {

    }

    /**
     * Decodes 7 bytes of data into an ADS-B Surface Operational Status message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Surface Operational Status message.
     * @throws Adsb1090ParseException
     */
    public static SurfaceOperationalStatus parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("SurfaceOperationalStatus.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("SurfaceOperationalStatus.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        SurfaceOperationalStatus message = new SurfaceOperationalStatus();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type code is 31
        if (message.typeCode != 31) {
            throw new Adsb1090ParseException("SurfaceOperationalStatus.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 2);
        // valid sub type code is 1
        if (message.subtypeCode != 1) {
            throw new Adsb1090ParseException("SurfaceOperationalStatus.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        message.surfaceCapabilityClassCodes = extractInt(data, 8, 12);
        message.lengthWidthCodes = extractInt(data, 20, 4);
        message.surfaceOperationalModeCodes = extractInt(data, 24, 16);
        message.versionNumber = extractInt(data, 40, 3);
        message.nicSupplementA = extractBoolean(data, 43);
        message.nacPosition = extractInt(data, 44, 4);
        message.reserved1 = extractBoolean(data, 48);
        message.reserved2 = extractBoolean(data, 49);
        message.sourceIntegrityLevel = extractInt(data, 50, 2);
        message.trkHdg = extractBoolean(data, 52);
        message.hrd = extractBoolean(data, 53);
        message.silSupplement = extractBoolean(data, 54);
        message.reserved3 = extractBoolean(data, 55);

        return message;
    }

    /**
     * @return a String representing the ADS-B Surface Operational Status message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("AdsbSufaceOperationalStatus");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int p = 0; p < originalMessage.length; p++)
            sb.append(String.format("%02x", originalMessage[p]));
        sb.append(System.lineSeparator());
        sb.append("type code = " + typeCode);
        sb.append(System.lineSeparator());
        sb.append("sub type code = " + subtypeCode);
        sb.append(System.lineSeparator());
        sb.append("surface capability class codes = " + surfaceCapabilityClassCodes);
        sb.append(System.lineSeparator());
        sb.append("length width codes = " + lengthWidthCodes);
        sb.append(System.lineSeparator());
        sb.append("surface operational mode codes = " + surfaceOperationalModeCodes);
        sb.append(System.lineSeparator());
        sb.append("version number = " + versionNumber);
        sb.append(System.lineSeparator());
        sb.append("NIC supplement A = " + nicSupplementA);
        sb.append(System.lineSeparator());
        sb.append("NAC position = " + nacPosition);
        sb.append(System.lineSeparator());
        sb.append("reserved 1 = " + reserved1);
        sb.append(System.lineSeparator());
        sb.append("reserved 2 = " + reserved2);
        sb.append(System.lineSeparator());
        sb.append("source integrity level = " + sourceIntegrityLevel);
        sb.append(System.lineSeparator());
        sb.append("TRK HDG = " + trkHdg);
        sb.append(System.lineSeparator());
        sb.append("HRD = " + hrd);
        sb.append(System.lineSeparator());
        sb.append("SIL supplement = " + silSupplement);
        sb.append(System.lineSeparator());
        sb.append("reserved 3 = " + reserved3);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    public int getSurfaceCapabilityClassCodes() {
        return surfaceCapabilityClassCodes;
    }

    public int getLengthWidthCodes() {
        return lengthWidthCodes;
    }

    /**
     * @return Maximum aircraft length in meters
     */
    public int getMaxLength() {
        switch (lengthWidthCodes) {
            case 0:
                return Integer.MAX_VALUE;
            case 1:
                return 15;
            case 2:
                return 25;
            case 3:
                return 25;
            case 4:
                return 35;
            case 5:
                return 35;
            case 6:
                return 45;
            case 7:
                return 45;
            case 8:
                return 55;
            case 9:
                return 55;
            case 10:
                return 65;
            case 11:
                return 65;
            case 12:
                return 75;
            case 13:
                return 75;
            case 14:
                return 85;
            case 15:
                return 85;
            default:
                return Integer.MAX_VALUE;
        }
    }

    /**
     * @return Maximum aircraft width in meters
     */
    public double getMaxWidth() {
        switch (lengthWidthCodes) {
            case 0:
                return Integer.MAX_VALUE;
            case 1:
                return 23;
            case 2:
                return 28.5;
            case 3:
                return 34;
            case 4:
                return 33;
            case 5:
                return 38;
            case 6:
                return 39.5;
            case 7:
                return 45;
            case 8:
                return 45;
            case 9:
                return 52;
            case 10:
                return 59.5;
            case 11:
                return 67;
            case 12:
                return 72.5;
            case 13:
                return 80;
            case 14:
                return 80;
            case 15:
                return 90;
            default:
                return Integer.MAX_VALUE;
        }
    }

    /**
     * @return surface operaional mode codes
     */
    public int getSurfaceOperationalModeCodes() {
        return surfaceOperationalModeCodes;
    }

    /**
     * @return the 1st reserved bit
     */
    public boolean getReserved1() {
        return reserved1;
    }

    /**
     * @return the 2nd reserved bit
     */
    public boolean getReserved2() {
        return reserved2;
    }

    /**
     * Track Angle/Heading
     *
     * @return true if heading, false if track angle
     */
    public boolean getTrkHdg() {
        return trkHdg;
    }

    /**
     * @return the 3rd reserved bit
     */
    public boolean getReserved3() {
        return reserved3;
    }

}
