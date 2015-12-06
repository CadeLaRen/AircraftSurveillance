package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide aircraft state and status information.
 * <p/>
 * Compatible with DO-260B, ADS-B version number = 2.
 */

/* 1-5   Format type code = 29
 * 6-7   Subtype code = 1
 * 8     SIL supplement (0 = per hour, 1 = per sample)
 * 9     Selected altitude type (0 = MCP/FCU, 1 = FMS)
 * 10-20 Selected altitude
 * 21-29 Barometric pressure setting
 * 30    Status (0 = Invalid, 1 = Valid)
 * 31    Sign (0 = Positive, 1 = Negative)
 * 32-39 Selected selectedHeading
 * 40-43 Navigation accuracy category for position
 * 44    Navigation integrity category for baro
 * 45-46 Source integrity level
 * 47    Status of MCP/FCU mode bits (0 = Invalid, 1 = Valid)
 * 48    Autopilot engaged (0 = Not engaged, 1 = Engaged)
 * 49    VNAV mode engaged (0 = Not engaged, 1 = Engaged)
 * 50    Altitude hold mode (0 = Not engaged, 1 = Engaged)
 * 51    Reserved for ADS-R flag
 * 52    Approach mode (0 = Not engaged, 1 = Engaged)
 * 53    TCAS operational (0 = Not operational, 1 = Operational)
 * 54-56 Reserved
 */

public class TargetStateAndStatusVersion2 extends TargetStateAndStatus {

    private boolean silSupplement;
    private boolean selectedAltitudeType;
    private boolean selectedAltitudeAvailable;
    private int selectedAltitude;
    private boolean barometricPressureSettingAvailable;
    private double barometricPressureSetting;
    private boolean selectedHeadingStatus;
    private double selectedHeading;
    private int nacPosition;
    private boolean nicBaro;
    private int sourceIntegrityLevel;
    private boolean modeBitsStatus;
    private boolean autopilotEngaged;
    private boolean vnavModeEngaged;
    private boolean altitudeHoldMode;
    private boolean adsrFlag;
    private boolean approachMode;
    private boolean tcasOperational;
    private boolean reserved1;
    private boolean reserved2;
    private boolean reserved3;

    private TargetStateAndStatusVersion2() {

    }

    /**
     * Decodes 7 bytes of data into an ADS-B Target State and Status Information (version 2) message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Target State and Status Information (version 2) message.
     * @throws Adsb1090ParseException
     */
    public static TargetStateAndStatusVersion2 parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion2.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion2.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        TargetStateAndStatusVersion2 message = new TargetStateAndStatusVersion2();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type code is 29
        if (message.typeCode != 29) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion2.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 2);
        // valid sub type code is 1
        if (message.subtypeCode != 1) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion2.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        message.silSupplement = extractBoolean(data, 7);
        message.selectedAltitudeType = extractBoolean(data, 8);

        int selectedAltitude = extractInt(data, 9, 11);
        message.selectedAltitudeAvailable = selectedAltitude != 0;
        message.selectedAltitude = decodeSelectedAltitude(selectedAltitude);

        int barometricPressurre = extractInt(data, 20, 9);
        message.barometricPressureSettingAvailable = barometricPressurre != 0;
        message.barometricPressureSetting = decodeBarometricPressureSetting(barometricPressurre);

        message.selectedHeadingStatus = extractBoolean(data, 29);
        message.selectedHeading = decodeHeading(extractInt(data, 30, 9));
        message.nacPosition = extractInt(data, 39, 4);
        message.nicBaro = extractBoolean(data, 43);
        message.sourceIntegrityLevel = extractInt(data, 44, 2);
        message.modeBitsStatus = extractBoolean(data, 46);
        message.autopilotEngaged = extractBoolean(data, 47);
        message.vnavModeEngaged = extractBoolean(data, 48);
        message.altitudeHoldMode = extractBoolean(data, 49);
        message.adsrFlag = extractBoolean(data, 50);
        message.approachMode = extractBoolean(data, 51);
        message.tcasOperational = extractBoolean(data, 52);
        message.reserved1 = extractBoolean(data, 53);
        message.reserved2 = extractBoolean(data, 54);
        message.reserved3 = extractBoolean(data, 55);

        return message;
    }

    /**
     * @return a String representing the ADS-B Target State and Status Information (version 2) message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("TargetStateAndStatusVersion2");
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
        sb.append("SIL supplement = " + silSupplement);
        sb.append(System.lineSeparator());
        sb.append("selected altitude type = " + selectedAltitudeType);
        sb.append(System.lineSeparator());
        sb.append("selected altitude available = " + selectedAltitudeAvailable);
        sb.append(System.lineSeparator());
        sb.append("selected altitude = " + selectedAltitude);
        sb.append(System.lineSeparator());
        sb.append("barometric pressure setting available = " + barometricPressureSettingAvailable);
        sb.append(System.lineSeparator());
        sb.append("barometric pressure setting = " + barometricPressureSetting);
        sb.append(System.lineSeparator());
        sb.append("selected heading status = " + selectedHeadingStatus);
        sb.append(System.lineSeparator());
        sb.append("selected heading = " + selectedHeading);
        sb.append(System.lineSeparator());
        sb.append("NAC position = " + nacPosition);
        sb.append(System.lineSeparator());
        sb.append("NIC baro = " + nicBaro);
        sb.append(System.lineSeparator());
        sb.append("source integrity level = " + sourceIntegrityLevel);
        sb.append(System.lineSeparator());
        sb.append("mode bits status = " + modeBitsStatus);
        sb.append(System.lineSeparator());
        sb.append("autopilot engaged = " + autopilotEngaged);
        sb.append(System.lineSeparator());
        sb.append("VNAV mode engaged = " + vnavModeEngaged);
        sb.append(System.lineSeparator());
        sb.append("altitude hold mode = " + altitudeHoldMode);
        sb.append(System.lineSeparator());
        sb.append("ADS-R flag = " + adsrFlag);
        sb.append(System.lineSeparator());
        sb.append("approach mode = " + approachMode);
        sb.append(System.lineSeparator());
        sb.append("TCAS operational = " + tcasOperational);
        sb.append(System.lineSeparator());
        sb.append("reserved 1 = " + reserved1);
        sb.append(System.lineSeparator());
        sb.append("reserved 2 = " + reserved2);
        sb.append(System.lineSeparator());
        sb.append("reserved 3 = " + reserved3);
        sb.append(System.lineSeparator());

        return sb.toString();
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

    /**
     * @return true if selected altitude is from FMS, false if selected altitude if from MCP/FCU
     */
    public boolean getSelectedAltitudeType() {
        return selectedAltitudeType;
    }

    /**
     * @return true if the selected altitude is available, otherwise false
     */
    public boolean isSelectedAltitudeAvailable() {
        return selectedAltitudeAvailable;
    }

    /**
     * @return selected altitude in feet
     */
    public int getSelectedAltitude() {
        return selectedAltitude;
    }

    /**
     * @return true if the barometric pressure setting is available, otherwise false
     */
    public boolean isBarometricPressureSettingAvailable() {
        return barometricPressureSettingAvailable;
    }

    /**
     * @return barometric pressure setting in millibars
     */
    public double getBarometricPressureSetting() {
        return barometricPressureSetting;
    }

    /**
     * @return true if the selectedHeading is available, otherwise false
     */
    public boolean isSelectedHeadingAvailable() {
        return selectedHeadingStatus;
    }

    /**
     * @return selectedHeading in degrees
     */
    public double getSelectedHeading() {
        return selectedHeading;
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
     * Navigation Integrity Category for Barometric Altitude.
     *
     * @return true if cross-checked (or uses a non-Gilham coded source), false if not cross-checked
     */
    public boolean getNicBaro() {
        return nicBaro;
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
     * @return true if the MCP/FCU mode bite are valid, otherwise false
     */
    public boolean areModeBitsAvailable() {
        return modeBitsStatus;
    }

    /**
     * @return true if the autopilot is engaged, otherwise false
     */
    public boolean isAutopilotEngaged() {
        return autopilotEngaged;
    }

    /**
     * @return true if VNAV mode is engaged, otherwise false
     */
    public boolean isVnavModeEngaged() {
        return vnavModeEngaged;
    }

    /**
     * @return true if altitude hole mode is engaged, otherwise false
     */
    public boolean isAltitudeHoldModeEngaged() {
        return altitudeHoldMode;
    }

    /**
     * @return bit reserved for ADS-R flag
     */
    public boolean getAdsrFlag() {
        return adsrFlag;
    }

    /**
     * @return true if approach mode is engaged, otherwise false
     */
    public boolean isApproachModeEngaged() {
        return approachMode;
    }

    /**
     * @return true if TCAS is operational, otherwise false
     */
    public boolean isTcasOperational() {
        return tcasOperational;
    }

    /**
     * @return 1st reserved bit
     */
    public boolean getReserved1() {
        return reserved1;
    }

    /**
     * @return 2nd reserved bit
     */
    public boolean getReserved2() {
        return reserved2;
    }

    /**
     * @return 3rd reserved bit
     */
    public boolean getReserved3() {
        return reserved3;
    }

    /**
     * @param encodedAltitude encoded altitude from raw message, valid range is 0-2047
     * @return altitude in feet
     * @throws Adsb1090ParseException
     */
    private static int decodeSelectedAltitude(int encodedAltitude) throws Adsb1090ParseException {
        if ((encodedAltitude < 0) | (encodedAltitude > 2047)) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion2.decodeSelectedAltitude(encodedAltitude == " + encodedAltitude + "): encoded altitude is not valid (encodedAltitude < 0) | (encodedAltitude > 2047)");
        }
        if (encodedAltitude == 0) {
            return Integer.MIN_VALUE;
        }

        return ((encodedAltitude - 1) * 32);
    }

    /**
     * @param encodedBarometricPressureSetting encoded barometric pressure setting from raw message, valid range is 0-511
     * @return barometric pressure setting in millibars
     * @throws Adsb1090ParseException
     */
    private static double decodeBarometricPressureSetting(int encodedBarometricPressureSetting) throws Adsb1090ParseException {
        if ((encodedBarometricPressureSetting < 0) | (encodedBarometricPressureSetting > 511)) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion2.decodeBarometricPressureSetting(encodedBarometricPressureSetting == " + encodedBarometricPressureSetting + "): encoded barometric pressure setting is not valid (encodedBarometricPressureSetting < 0) | (encodedBarometricPressureSetting > 511)");
        }
        if (encodedBarometricPressureSetting == 0) {
            return Double.NaN;
        }

        return ((encodedBarometricPressureSetting - 1.0) * 0.8) + 800;
    }

    /**
     * This method treats the sign bit as part of the encoded value.
     *
     * @param encodedHeading encoded selectedHeading from raw message, valid range is 0-511
     * @return selectedHeading in degrees
     * @throws Adsb1090ParseException
     */
    private static double decodeHeading(int encodedHeading) throws Adsb1090ParseException {
        if ((encodedHeading < 0) | (encodedHeading > 511)) {
            throw new Adsb1090ParseException("TargetStateAndStatusVersion2.decodeHeading(encodedHeading == " + encodedHeading + "): encoded heading is not valid (encodedHeading < 0) | (encodedHeading > 511)");
        }

        return (encodedHeading * 0.703125);
    }

}
