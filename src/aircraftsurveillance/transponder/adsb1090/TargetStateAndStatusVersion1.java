package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide aircraft state and status information.
 */

/* 1-5   Format type code = 29
 * 6-7   Subtype code = 0
 * 8-9   Vertical data available / source indicator
 * 10    Target altitude type
 * 11    Backward compatibility flag = 0
 * 12-13 Target altitude capability
 * 14-15 Vertical mode indicator
 * 16-25 Target altitude
 * 26-27 Horizontal data available / source indicator
 * 28-36 Target heading / track angle
 * 37    Target heading / track indicator
 * 38-39 Horizontal mode indicator
 * 40-43 Navigation accuracy category - position
 * 44    Navigation integrity category - baro
 * 45-46 Surveillance integrity level
 * 47-51 Reserved
 * 52-53 Capability / mode codes
 * 54-56 Emergency / priority status
 */

public class TargetStateAndStatusVersion1 extends TargetStateAndStatus {

    private int verticalDataAvailable;
    private boolean targetAltitudeType;
    private boolean backwardCompatibilityFlag;
    private int targetAltitudeCapability;
    private int verticalModeIndicator;
    private int targetAltitude;
    private int horizontalDataAvailable;
    private int targetHeading;
    private boolean targetHeadingIndicator;
    private int horizontalModeIndicator;
    private int nacPosition;
    private boolean nicBaro;
    private int surveillanceIntegrityLevel;
    private int reserved;
    private int capabilityModeCodes;
    private int emergencyStatus;

    private TargetStateAndStatusVersion1() {

    }

    /**
     * Decodes 7 bytes of data into an ADS-B Target State and Status (version 1) message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Target State and Status (version 1) message
     * @throws Adsb1090ParseException
     */
    public static TargetStateAndStatusVersion1 parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion1.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion1.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        TargetStateAndStatusVersion1 message = new TargetStateAndStatusVersion1();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type code is 29
        if (message.typeCode != 29) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion1.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 2);
        // valid sub type code is 0
        if (message.subtypeCode != 0) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion1.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        message.verticalDataAvailable = extractInt(data, 7, 2);
        message.targetAltitudeType = extractBoolean(data, 9);
        message.backwardCompatibilityFlag = extractBoolean(data, 10);
        message.targetAltitudeCapability = extractInt(data, 11, 2);
        message.verticalModeIndicator = extractInt(data, 13, 2);
        message.targetAltitude = extractInt(data, 15, 10);
        message.horizontalDataAvailable = extractInt(data, 25, 2);
        message.targetHeading = extractInt(data, 27, 9);
        message.targetHeadingIndicator = extractBoolean(data, 36);
        message.horizontalModeIndicator = extractInt(data, 37, 2);
        message.nacPosition = extractInt(data, 39, 4);
        message.nicBaro = extractBoolean(data, 43);
        message.surveillanceIntegrityLevel = extractInt(data, 44, 2);
        message.reserved = extractInt(data, 46, 5);
        message.capabilityModeCodes = extractInt(data, 51, 2);
        message.emergencyStatus = extractInt(data, 53, 3);

        return message;
    }

    /**
     * @return a String representing the ADS-B Target State and Status (version 1) message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("TargetStateAndStatusVersion1");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int p = 0; p < originalMessage.length; p++)
            sb.append(String.format("%02x", originalMessage[p]));
        sb.append(System.lineSeparator());
        sb.append("type code = " + typeCode);
        sb.append(System.lineSeparator());
        sb.append("sub type code = " + subtypeCode);
        sb.append(System.lineSeparator());
        sb.append("vertical data available = " + verticalDataAvailable);
        sb.append(System.lineSeparator());
        sb.append("target altitude type = " + targetAltitudeType);
        sb.append(System.lineSeparator());
        sb.append("backward compatibility flag = " + backwardCompatibilityFlag);
        sb.append(System.lineSeparator());
        sb.append("target altitude capability = " + targetAltitudeCapability);
        sb.append(System.lineSeparator());
        sb.append("vertical mode indicator = " + verticalModeIndicator);
        sb.append(System.lineSeparator());
        sb.append("target altitude = " + targetAltitude);
        sb.append(System.lineSeparator());
        sb.append("horizontal data available = " + horizontalDataAvailable);
        sb.append(System.lineSeparator());
        sb.append("target heading = " + targetHeading);
        sb.append(System.lineSeparator());
        sb.append("target heading indicator = " + targetHeadingIndicator);
        sb.append(System.lineSeparator());
        sb.append("horizontal mode indicator = " + horizontalModeIndicator);
        sb.append(System.lineSeparator());
        sb.append("NAC position = " + nacPosition);
        sb.append(System.lineSeparator());
        sb.append("NIC baro = " + nicBaro);
        sb.append(System.lineSeparator());
        sb.append("surveillance integrity level = " + surveillanceIntegrityLevel);
        sb.append(System.lineSeparator());
        sb.append("reserved = " + reserved);
        sb.append(System.lineSeparator());
        sb.append("capability mode codes = " + capabilityModeCodes);
        sb.append(System.lineSeparator());
        sb.append("emergency status = " + emergencyStatus);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    public int getVerticalDataAvailable() {
        return verticalDataAvailable;
    }

    public boolean getTargetAltitudeType() {
        return targetAltitudeType;
    }

    public boolean getBackwardCompatibilityFlag() {
        return backwardCompatibilityFlag;
    }

    public int getTargetAltitudeCapability() {
        return targetAltitudeCapability;
    }

    public int getVerticalModeIndicator() {
        return verticalModeIndicator;
    }

    public int getTargetAltitude() {
        return targetAltitude;
    }

    public int getHorizontalDataAvailable() {
        return horizontalDataAvailable;
    }

    public int getTargetHeading() {
        return targetHeading;
    }

    public boolean getTargetHeadingIndicator() {
        return targetHeadingIndicator;
    }

    public int getHorizontalModeIndicator() {
        return horizontalModeIndicator;
    }

    public int getNacPosition() {
        return nacPosition;
    }

    public boolean getNicBaro() {
        return nicBaro;
    }

    public int getSurveillanceIntegrityLevel() {
        return surveillanceIntegrityLevel;
    }

    public int getReserved() {
        return reserved;
    }

    public int getCapabilityModeCodes() {
        return capabilityModeCodes;
    }

    public int getEmergencyStatus() {
        return emergencyStatus;
    }

}
