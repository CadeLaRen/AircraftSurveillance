package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide additional information on aircraft status.
 */

/* 1-5   Format type code = 28
 * 6-8   Subtype code = 1
 * 9-11  Emergency state
 * 12-24 Mode A (4096) code
 * 25-56 Reserved
 *
 * Emergency state coding
 * 0 = No emergency
 * 1 = General emergency
 * 2 = Lifeguard/Medical
 * 3 = Minimum fuel
 * 4 = No communications
 * 5 = Unlawful interference
 * 6 = Downed aircraft
 * 7 = Reserved
 */

public class EmergencyStatus extends AircraftStatus {

    public enum EmergencyState {
        NO_EMERGENCY,
        GENERAL_EMERGENCY,
        LIFEGUARD_MEDICAL,
        MINIMUM_FUEL,
        NO_COMMUNICATIONS,
        UNLAWFUL_INTERFERENCE,
        DOWNED_AIRCRAFT,
        RESERVED
    }

    private EmergencyState emergencyState;
    private int modeACode;
    private int reserved1;
    private int reserved2;
    private int reserved3;
    private int reserved4;

    private EmergencyStatus() {

    }

    /**
     * Decodes 7 bytes of data into an ADS-B Emergency Status message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Emergency Status message
     * @throws Adsb1090ParseException
     */
    public static EmergencyStatus parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("EmergencyStatus.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("EmergencyStatus.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        EmergencyStatus message = new EmergencyStatus();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type codes is 28
        if (message.typeCode != 28) {
            throw new Adsb1090ParseException("EmergencyStatus.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 3);
        // valid sub type code is 1
        if (message.subtypeCode != 1) {
            throw new Adsb1090ParseException("EmergencyStatus.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        int emergencyState = extractInt(data, 8, 3);
        if (emergencyState == 0) {
            message.emergencyState = EmergencyState.NO_EMERGENCY;
        } else if (emergencyState == 1) {
            message.emergencyState = EmergencyState.GENERAL_EMERGENCY;
        } else if (emergencyState == 2) {
            message.emergencyState = EmergencyState.LIFEGUARD_MEDICAL;
        } else if (emergencyState == 3) {
            message.emergencyState = EmergencyState.MINIMUM_FUEL;
        } else if (emergencyState == 4) {
            message.emergencyState = EmergencyState.NO_COMMUNICATIONS;
        } else if (emergencyState == 5) {
            message.emergencyState = EmergencyState.UNLAWFUL_INTERFERENCE;
        } else if (emergencyState == 6) {
            message.emergencyState = EmergencyState.DOWNED_AIRCRAFT;
        } else if (emergencyState == 7) {
            message.emergencyState = EmergencyState.RESERVED;
        } else {
            throw new Adsb1090ParseException("EmergencyStatus.parse(data): emergency state is not valid (emergency state == " + emergencyState + ")");
        }

        message.modeACode = extractInt(data, 11, 13);

        message.reserved1 = extractInt(data, 24, 8);
        message.reserved2 = extractInt(data, 32, 8);
        message.reserved3 = extractInt(data, 40, 8);
        message.reserved4 = extractInt(data, 48, 8);

        return message;
    }

    /**
     * @return a String representing the ADS-B Emergency Status message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("EmergencyStatus");
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
        sb.append("emergency state = " + emergencyState);
        sb.append(System.lineSeparator());
        sb.append("mode A code = " + modeACode);
        sb.append(System.lineSeparator());
        sb.append("reserved 1 = " + reserved1);
        sb.append(System.lineSeparator());
        sb.append("reserved 2 = " + reserved2);
        sb.append(System.lineSeparator());
        sb.append("reserved 3 = " + reserved3);
        sb.append(System.lineSeparator());
        sb.append("reserved 4 = " + reserved4);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    /**
     * @return emergency state
     */
    public EmergencyState getEmergencyState() {
        return emergencyState;
    }

    /**
     * @return mode A (4096) code
     */
    public int getModeACode() {
        return modeACode;
    }

    /**
     * @return reserved byte 1
     */
    public int getReserved1() {
        return reserved1;
    }

    /**
     * @return reserved byte 2
     */
    public int getReserved2() {
        return reserved2;
    }

    /**
     * @return reserved byte 3
     */
    public int getReserved3() {
        return reserved3;
    }

    /**
     * @return reserved byte 4
     */
    public int getReserved4() {
        return reserved4;
    }

}
