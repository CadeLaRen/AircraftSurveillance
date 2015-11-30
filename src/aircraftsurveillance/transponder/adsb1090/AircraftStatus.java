package aircraftsurveillance.transponder.adsb1090;

/* Subtype coding
 * 0 = No information
 * 1 = Emergency/Priority status and Mode A code
 * 2 = TCAS RA broadcast
 * 3 = Reserved
 * 4 = Reserved
 * 5 = Reserved
 * 6 = Reserved
 * 7 = Reserved
 */

public abstract class AircraftStatus extends Adsb1090Message {

    /**
     * Decodes 7 bytes of data into an ADS-B Aircraft Status message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Aircraft Status message
     * @throws Adsb1090ParseException
     */
    public static AircraftStatus parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("AircraftStatus.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("AircraftStatus.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        // verify the type code
        int typeCode = extractInt(data, 0, 5);
        // valid type code is 28
        if (typeCode != 28) {
            throw new Adsb1090ParseException("AircraftStatus.parse(data): type code is not valid (type code == " + typeCode + ")");
        }

        // verify the subtype code
        int subtypeCode = extractInt(data, 5, 3);
        // valid sub type codes are 1-2
        if ((subtypeCode < 1) | (subtypeCode > 2)) {
            throw new Adsb1090ParseException("AircraftStatus.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }

        if (subtypeCode == 1) {
            return EmergencyStatus.parse(data);
        } else if (subtypeCode == 2) {
            return ResolutionAdvisory.parse(data);
        } else {
            throw new Adsb1090ParseException("AircraftStatus.parse(data): sub type code is not valid (sub type code == " + subtypeCode + ")");
        }

    }

}
