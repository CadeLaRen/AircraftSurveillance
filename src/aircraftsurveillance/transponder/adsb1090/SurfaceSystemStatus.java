package aircraftsurveillance.transponder.adsb1090;

public class SurfaceSystemStatus extends Adsb1090Message {

    private SurfaceSystemStatus() {

    }

    /**
     * Decodes 7 bytes of data into an ADS-B Surface System Status message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Surface System Status message
     * @throws Adsb1090ParseException
     */
    public static SurfaceSystemStatus parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("SurfaceSystemStatus.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("SurfaceSystemStatus.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        SurfaceSystemStatus message = new SurfaceSystemStatus();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type code is 24
        if (message.typeCode != 24) {
            throw new Adsb1090ParseException("SurfaceSystemStatus.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        // verify the subtype code
        message.subtypeCode = extractInt(data, 5, 3);
        // valid sub type code is 1
        if (message.subtypeCode != 1) {
            throw new Adsb1090ParseException("SurfaceSystemStatus.parse(data): sub type code is not valid (sub type code == " + message.subtypeCode + ")");
        }

        throw new Adsb1090ParseException("SurfaceSystemStatus.parse(data): not implemented");
    }

    /**
     * @return a String representing the ADS-B Surface System Status message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("SurfaceSystemStatus");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int p = 0; p < originalMessage.length; p++)
            sb.append(String.format("%02x", originalMessage[p]));
        sb.append(System.lineSeparator());
        sb.append("type code = " + typeCode);
        sb.append(System.lineSeparator());
        sb.append("sub type code = " + subtypeCode);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

}
