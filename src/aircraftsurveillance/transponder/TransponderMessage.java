package aircraftsurveillance.transponder;

import aircraftsurveillance.AircraftSurveillanceMessage;

import java.time.Instant;

public abstract class TransponderMessage extends AircraftSurveillanceMessage {

    public static TransponderMessage parse(Instant timestamp, Double receiverLatitude, Double receiverLongitude, Double receiverAltitude, int[] data) {
        if (data == null) {
            return null;
        }

        if (data.length == 7) {

        } else if (data.length == 14) {
            return ModeSMessage.parse(timestamp, receiverLatitude, receiverLongitude, receiverAltitude, data);
        }

        return null;
    }
}
