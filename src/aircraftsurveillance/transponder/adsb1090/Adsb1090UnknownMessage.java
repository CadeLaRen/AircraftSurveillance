package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: Represents an unknown, but properly formatted ADS-B message
 */
public class Adsb1090UnknownMessage extends Adsb1090Message {

    private Adsb1090UnknownMessage() {
    }

    /**
     * Decodes 7 bytes of data into an unknown ADS-B message
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded unknown ADS-B message.
     * @throws Adsb1090ParseException
     */
    public static Adsb1090UnknownMessage parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("Adsb1090UnknownMessage.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("Adsb1090UnknownMessage.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        Adsb1090UnknownMessage message = new Adsb1090UnknownMessage();
        message.originalMessage = data.clone();

        message.typeCode = extractInt(data, 0, 5);

        return message;
    }

    /**
     * @return a String representing the unknown ADS-B message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Adsb1090UnknownMessage");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int i : originalMessage) {
            sb.append(String.format("%02x", i));
        }
        sb.append(System.lineSeparator());
        sb.append("type code = " + typeCode);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

}
