package aircraftsurveillance.transponder;

import java.time.Instant;

public abstract class ModeSMessage extends TransponderMessage {

    int dataFormat = 0;

    public static ModeSMessage parse(Instant timestamp, Double receiverLatitude, Double receiverLongitude, Double receiverAltitude, int[] data) {
        if (data == null) {
            return null;
        }

        int dataFormat = extractInt(data, 0, 5);
        if (dataFormat == 17) {
            return ExtendedSquitter.parse(timestamp, receiverLatitude, receiverLongitude, receiverAltitude, data);
        }

        return null;
    }

    public int getDataFormat() {
        return dataFormat;
    }

}
