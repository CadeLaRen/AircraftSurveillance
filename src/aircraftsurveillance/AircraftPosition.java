package aircraftsurveillance;

import aircraftsurveillance.transponder.adsb1090.CompactPositionReport;

import java.time.Duration;
import java.time.Instant;

class AircraftPosition {

    private Position position = null;
    private Instant positionTimestamp = Instant.MIN;
    private boolean airborne = false;
    private boolean surface = false;

    private CompactPositionReport previousAirborneCpr = null;
    private Instant previousAirborneTimestamp = Instant.MIN;

    private Position receiverPosition = null;

    private CompactPositionReport previousSurfaceCpr = null;
    private Instant previousSurfaceTimestamp = Instant.MIN;

    private static final Duration AGE_LIMIT = Duration.ofSeconds(10);
    private static final int MAX_TRACK_SPEED = 1000;  // maximum speed in knots
    private static final int MAX_RECEIVER_DISTANCE = 350;  // maximum realistic reception distance in nautical miles
    private static final int MAX_PREVIOUS_POSITION_DISTANCE = 250;  // maximum distance from previous position in nautical miles

    public AircraftPosition() {
    }

    public Position getPosition() {
        return position;
    }

    public Instant getPositionTimestamp() {
        return positionTimestamp;
    }

    public Duration getPositionAge() {
        return getPositionAge(Instant.now());
    }

    private Duration getPositionAge(Instant timestamp) {
        return Duration.between(positionTimestamp, timestamp);
    }

    public boolean isAirborne() {
        return airborne;
    }

    public boolean isSurface() {
        return surface;
    }

    public void updateAirborne(CompactPositionReport cpr, Instant cprTimestamp) {
        if (cpr == null) {
            return;
        }

        Duration airborneAge = Duration.between(previousAirborneTimestamp, cprTimestamp);

        if ((previousAirborneCpr != null) & (airborneAge.compareTo(AGE_LIMIT) < 0)) {
            Position tempPosition = null;
            if (previousAirborneCpr.isEvenPosition() & cpr.isOddPosition()) {
                tempPosition = CPRDecoder.decodeGlobalAirbornePositionOdd(previousAirborneCpr, cpr);
            } else if (previousAirborneCpr.isOddPosition() & cpr.isEvenPosition()) {
                tempPosition = CPRDecoder.decodeGlobalAirbornePositionEven(cpr, previousAirborneCpr);
            }
            if (tempPosition != null) {
                double receiverDistance = 0;
                double previousDistance = 0;

                if (receiverPosition != null) {
                    try {
                        receiverDistance = Position.distance(receiverPosition, tempPosition) * 0.000539957;  // convert meters to nautical miles
                    } catch (Position.IterationLimitExceeded iterationLimitExceeded) {
                        iterationLimitExceeded.printStackTrace();
                        receiverDistance = Double.MAX_VALUE;
                    }
                }

                if (position != null) {
                    try {
                        previousDistance = Position.distance(position, tempPosition) * 0.000539957;  // convert meters to nautical miles
                    } catch (Position.IterationLimitExceeded iterationLimitExceeded) {
                        iterationLimitExceeded.printStackTrace();
                        previousDistance = Double.MAX_VALUE;
                    }

                }

                double maxPreviousDistance = MAX_PREVIOUS_POSITION_DISTANCE;
                if (position != null & positionTimestamp != Instant.MIN) {
                    // reduce the maximum previous distance to a more realistic value
                    Duration positionAge = Duration.between(positionTimestamp, cprTimestamp);
                    double positionSeconds = positionAge.getSeconds() + 10; // 10 seconds extra to prevent false rejections
                    maxPreviousDistance = (positionSeconds / 3600.0) * MAX_TRACK_SPEED;
                }

                if (receiverDistance < MAX_RECEIVER_DISTANCE & previousDistance < maxPreviousDistance) {
                    position = tempPosition;
                    positionTimestamp = cprTimestamp;
                    airborne = true;
                    surface = false;
                }
            }
        }

        previousAirborneCpr = cpr;
        previousAirborneTimestamp = cprTimestamp;
    }

    public void setReceiverPosition(Position position) {
        receiverPosition = position;
    }

    public Position getReceiverPosition() {
        return receiverPosition;
    }

    public void updateSurface(CompactPositionReport cpr, Instant cprTimestamp) {
        if (cpr == null) {
            return;
        }

        if (position == null) {
            Duration age = Duration.between(previousSurfaceTimestamp, cprTimestamp);
            Duration limit = Duration.ofSeconds(10);
            if ((previousSurfaceCpr != null) & (age.compareTo(limit) < 0) & (receiverPosition != null)) {
                Position tempPosition = null;
                if (previousSurfaceCpr.isEvenPosition() & cpr.isOddPosition()) {
                    tempPosition = CPRDecoder.decodeGlobalSurfacePositionOdd(previousSurfaceCpr, cpr, receiverPosition);
                } else if (previousSurfaceCpr.isOddPosition() & cpr.isEvenPosition()) {
                    tempPosition = CPRDecoder.decodeGlobalSurfacePositionEven(cpr, previousSurfaceCpr, receiverPosition);
                }
                if (tempPosition != null) {
                    position = tempPosition;
                    positionTimestamp = cprTimestamp;
                    surface = true;
                    airborne = false;
                }
            }
        } else {
            position = CPRDecoder.decodeLocalSurfacePosition(cpr, position);
            positionTimestamp = cprTimestamp;
            surface = true;
            airborne = false;
        }

        previousSurfaceCpr = cpr;
        previousSurfaceTimestamp = cprTimestamp;
    }

}
