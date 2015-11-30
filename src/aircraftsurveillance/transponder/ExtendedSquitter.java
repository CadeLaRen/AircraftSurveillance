package aircraftsurveillance.transponder;

import aircraftsurveillance.transponder.adsb1090.Adsb1090Message;
import aircraftsurveillance.transponder.adsb1090.Adsb1090ParseException;

import java.time.Instant;

public class ExtendedSquitter extends ModeSMessage {
    private int capability = 0;
    private int addressAnnounced = 0;
    private Adsb1090Message extendedSquitterMessage = null;
    private int parity;

    public static ExtendedSquitter parse(Instant timestamp, Double receiverLatitude, Double receiverLongitude, Double receiverAltitude, int[] data) {
        if (data == null) {
            return null;
        }

        ExtendedSquitter extendedSquitter = new ExtendedSquitter();
        extendedSquitter.timestamp = timestamp;
        extendedSquitter.receiverLatitude = receiverLatitude;
        extendedSquitter.receiverLongitude = receiverLongitude;
        extendedSquitter.receiverAltitude = receiverAltitude;

        extendedSquitter.dataFormat = extractInt(data, 0, 5);
        extendedSquitter.capability = extractInt(data, 5, 3);
        extendedSquitter.addressAnnounced = extractInt(data, 8, 24);

        // message is from bit index 32 to 87
        int[] adsbData = new int[7];
        System.arraycopy(data, 4, adsbData, 0, 7);
        try {
            extendedSquitter.extendedSquitterMessage = Adsb1090Message.parse(adsbData);
        } catch (Adsb1090ParseException e) {
            //e.printStackTrace();
            extendedSquitter.extendedSquitterMessage = null;
        }

        extendedSquitter.parity = extractInt(data, 88, 24);

        return extendedSquitter;
    }

    public int getCapability() {
        return capability;
    }

    public int getAddressAnnounced() {
        return addressAnnounced;
    }

    public Adsb1090Message getExtendedSquitterMessage() {
        return extendedSquitterMessage;
    }

    public int getParity() {
        return parity;
    }
}

