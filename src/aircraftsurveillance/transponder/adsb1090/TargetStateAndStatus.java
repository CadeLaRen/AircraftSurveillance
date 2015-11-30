package aircraftsurveillance.transponder.adsb1090;

/* Subtype coding
 * 0 = DO-260A, ADS-B version 1
 * 1 = DO-260B, ADS-B version 2
 * 2 = Reserved
 * 3 = Reserved
 */

public abstract class TargetStateAndStatus extends Adsb1090Message {

    /**
     * Decodes 7 bytes of data into an ADS-B Target State and Status message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Target State and Status message
     * @throws Adsb1090ParseException
     */
    public static TargetStateAndStatus parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("TargetStateAndStatus.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("TargetStateAndStatus.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        // verify the type code
        int typeCode = extractInt(data, 0, 5);
        // valid type code is 29
        if (typeCode != 29) {
            throw new Adsb1090ParseException("TargetStateAndStatus.parse(data): type code is not valid (type code == " + typeCode + ")");
        }

        // verify the subtype code
        int subtypeCode = extractInt(data, 5, 2);
        // valid sub type codes are 0-1
        if ((subtypeCode < 0) | (subtypeCode > 1)) {
            throw new Adsb1090ParseException("TargetStateAndStatus.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }

        if (subtypeCode == 0) {
            return TargetStateAndStatusVersion1.parse(data);
        } else if (subtypeCode == 1) {
            return TargetStateAndStatusVersion2.parse(data);
        } else {
            throw new Adsb1090ParseException("TargetStateAndStatus.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }

    }

}
