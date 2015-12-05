package aircraftsurveillance;

import aircraftsurveillance.transponder.adsb1090.EmergencyStatus;
import aircraftsurveillance.transponder.adsb1090.IdentificationAndCategory;

import java.time.Instant;

public class AircraftState {

    // update timestamp
    private Instant updateTimestamp = Instant.MIN;

    public Instant getUpdateTimestamp() {
        return updateTimestamp;
    }


    // identification
    private String identification = "";
    private Instant identificationTimestamp = Instant.MIN;

    public void setIdentification(String identification, Instant identificationTimestamp) {
        this.identification = identification;
        this.identificationTimestamp = identificationTimestamp;
        updateTimestamp = identificationTimestamp;
    }

    public String getIdentification() {
        return identification;
    }

    public Instant getIdentificationTimestamp() {
        return identificationTimestamp;
    }

    public boolean identificationHasBeenSet() {
        return identificationTimestamp != Instant.MIN;
    }


    // position
    private double latitude = 0;
    private double longitude = 0;
    private boolean airborne = false;
    private double distanceFromReceiver = 0;
    private double bearingFromReceiver = 0;
    private Instant positionTimestamp = Instant.MIN;

    public void setPosition(AircraftPosition aircraftPosition) {
        if (aircraftPosition.getPosition() != null) {
            latitude = aircraftPosition.getPosition().getLatitude();
            longitude = aircraftPosition.getPosition().getLongitude();
            airborne = aircraftPosition.isAirborne();

            if (aircraftPosition.getReceiverPosition() != null) {
                try {
                    distanceFromReceiver = Position.distance(aircraftPosition.getReceiverPosition(), aircraftPosition.getPosition()) * 0.000539957;  // convert meters to nautical miles
                    bearingFromReceiver = Position.course(aircraftPosition.getReceiverPosition(), aircraftPosition.getPosition());
                } catch (Position.IterationLimitExceeded iterationLimitExceeded) {
                    distanceFromReceiver = 0;
                    bearingFromReceiver = 0;
                }
            } else {
                distanceFromReceiver = 0;
                bearingFromReceiver = 0;
            }

            positionTimestamp = aircraftPosition.getPositionTimestamp();
            updateTimestamp = aircraftPosition.getPositionTimestamp();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isAirborne() {
        return airborne;
    }

    public double getDistanceFromReceiver() {
        return distanceFromReceiver;
    }

    public double getBearingFromReceiver() {
        return bearingFromReceiver;
    }

    public Instant getPositionTimestamp() {
        return positionTimestamp;
    }

    public boolean positionHasBeenSet() {
        return positionTimestamp != Instant.MIN;
    }


    // altitude
    private int altitude = 0;
    private Instant altitudeTimestamp = Instant.MIN;

    public void setAltitude(int altitude, Instant altitudeTimestamp) {
        this.altitude = altitude;
        this.altitudeTimestamp = altitudeTimestamp;
        updateTimestamp = altitudeTimestamp;
    }

    public int getAltitude() {
        return altitude;
    }

    public Instant getAltitudeTimestamp() {
        return altitudeTimestamp;
    }

    public boolean altitudeHasBeenSet() {
        return altitudeTimestamp != Instant.MIN;
    }


    // heading
    private double heading = 0;
    private Instant headingTimestamp = Instant.MIN;

    public void setHeading(double heading, Instant headingTimestamp) {
        this.heading = heading;
        this.headingTimestamp = headingTimestamp;
        updateTimestamp = headingTimestamp;
    }

    public double getHeading() {
        return heading;
    }

    public Instant getHeadingTimestamp() {
        return headingTimestamp;
    }

    public boolean headingHasBeenSet() {
        return headingTimestamp != Instant.MIN;
    }


    // airspeed
    private int airspeed = 0;
    private Instant airspeedTimestamp = Instant.MIN;

    public void setAirspeed(int airspeed, Instant airspeedTimestamp) {
        this.airspeed = airspeed;
        this.airspeedTimestamp = airspeedTimestamp;
        updateTimestamp = airspeedTimestamp;
    }

    public int getAirspeed() {
        return airspeed;
    }

    public Instant getAirspeedTimestamp() {
        return airspeedTimestamp;
    }

    public boolean airspeedHasBeenSet() {
        return headingTimestamp != Instant.MIN;
    }


    // ground velocity
    private double groundTrack = 0;
    private double groundSpeed = 0;
    private Instant groundTimestamp = Instant.MIN;

    public void setGroundVelocity(double groundTrack, double groundSpeed, Instant groundTimestamp) {
        this.groundTrack = groundTrack;
        this.groundSpeed = groundSpeed;
        this.groundTimestamp = groundTimestamp;
        updateTimestamp = groundTimestamp;
    }

    public double getGroundTrack() {
        return groundTrack;
    }

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public Instant getGroundVelocityTimestamp() {
        return groundTimestamp;
    }

    public boolean groundVelocityHasBeenSet() {
        return groundTimestamp != Instant.MIN;
    }


    // vertical rate
    private int verticalRate = 0;
    private Instant verticalRateTimestamp = Instant.MIN;

    public void setVerticalRate(int verticalRate, Instant verticalRateTimestamp) {
        this.verticalRate = verticalRate;
        this.verticalRateTimestamp = verticalRateTimestamp;
        updateTimestamp = verticalRateTimestamp;
    }

    public int getVerticalRate() {
        return verticalRate;
    }

    public Instant getVerticalRateTimestamp() {
        return verticalRateTimestamp;
    }

    public boolean verticalRateHasBeenSet() {
        return verticalRateTimestamp != Instant.MIN;
    }


    // geometric height difference
    private int geometricHeightDifference = 0;
    private Instant geometricHeightDifferenceTimestamp = Instant.MIN;

    public void setGeometricHeightDifference(int geometricHeightDifference, Instant geometricHeightDifferenceTimestamp) {
        this.geometricHeightDifference = geometricHeightDifference;
        this.geometricHeightDifferenceTimestamp = geometricHeightDifferenceTimestamp;
        updateTimestamp = geometricHeightDifferenceTimestamp;
    }

    public int getGeometricHeightDifference() {
        return geometricHeightDifference;
    }

    public Instant getGeometricHeightDifferenceTimestamp() {
        return geometricHeightDifferenceTimestamp;
    }

    public boolean geometricHeightDifferenceHasBeenSet() {
        return geometricHeightDifferenceTimestamp != Instant.MIN;
    }


    // emitter category
    private IdentificationAndCategory.EmitterCategory emitterCategory = null;
    private Instant emitterCategoryTimestamp = Instant.MIN;

    public void setEmitterCategory(IdentificationAndCategory.EmitterCategory emitterCategory, Instant emitterCategoryTimestamp) {
        this.emitterCategory = emitterCategory;
        this.emitterCategoryTimestamp = emitterCategoryTimestamp;
        updateTimestamp = emitterCategoryTimestamp;
    }

    public IdentificationAndCategory.EmitterCategory getEmitterCategory() {
        return emitterCategory;
    }

    public Instant getEmitterCategoryTimestamp() {
        return emitterCategoryTimestamp;
    }

    public boolean emitterCategoryHasBeenSet() {
        return emitterCategoryTimestamp != Instant.MIN;
    }


    // emergency state
    private EmergencyStatus.EmergencyState emergencyState = null;
    private Instant emergencyStateTimestamp = Instant.MIN;

    public void setEmergencyState(EmergencyStatus.EmergencyState emergencyState, Instant emergencyStateTimestamp) {
        this.emergencyState = emergencyState;
        this.emergencyStateTimestamp = emergencyStateTimestamp;
        updateTimestamp = emergencyStateTimestamp;
    }

    public EmergencyStatus.EmergencyState getEmergencyState() {
        return emergencyState;
    }

    public Instant getEmergencyStateTimestamp() {
        return emergencyStateTimestamp;
    }

    public boolean emergencyStateHasBeenSet() {
        return emergencyStateTimestamp != Instant.MIN;
    }


    // mode A code
    private int modeACode = 0;
    private Instant modeACodeTimestamp = Instant.MIN;

    public void setModeACode(int modeACode, Instant modeACodeTimestamp) {
        this.modeACode = modeACode;
        this.modeACodeTimestamp = modeACodeTimestamp;
        updateTimestamp = modeACodeTimestamp;
    }

    public int getModeACode() {
        return modeACode;
    }

    public Instant getModeACodeTimestamp() {
        return modeACodeTimestamp;
    }

    public boolean modeACodeHasBeenSet() {
        return modeACodeTimestamp != Instant.MIN;
    }


    // target altitude
    private int targetAltitude = 0;
    private Instant targetAltitudeTimestamp = Instant.MIN;

    public void setTargetAltitude(int targetAltitude, Instant targetAltitudeTimestamp) {
        this.targetAltitude = targetAltitude;
        this.targetHeadingTimestamp = targetAltitudeTimestamp;
        updateTimestamp = targetAltitudeTimestamp;
    }

    public int getTargetAltitude() {
        return targetAltitude;
    }

    public Instant getTargetAltitudeTimestamp() {
        return targetAltitudeTimestamp;
    }

    public boolean targetAltitudeHasBeenSet() {
        return targetHeadingTimestamp != Instant.MIN;
    }


    // target heading
    private int targetHeading = 0;
    private Instant targetHeadingTimestamp = Instant.MIN;

    public void setTargetHeading(int targetHeading, Instant targetHeadingTimestamp) {
        this.targetHeading = targetHeading;
        this.targetHeadingTimestamp = targetHeadingTimestamp;
        updateTimestamp = targetHeadingTimestamp;
    }

    public int getTargetHeading() {
        return targetHeading;
    }

    public Instant getTargetHeadingTimestamp() {
        return targetHeadingTimestamp;
    }

    public boolean targetHeadingHasBeenSet() {
        return targetHeadingTimestamp != Instant.MIN;
    }


    // selected altitude
    private int selectedAltitude = 0;
    private Instant selectedAltitudeTimestamp = Instant.MIN;

    public void setSelectedAltitude(int selectedAltitude, Instant selectedAltitudeTimestamp) {
        this.selectedAltitude = selectedAltitude;
        this.selectedAltitudeTimestamp = selectedAltitudeTimestamp;
        updateTimestamp = selectedAltitudeTimestamp;
    }

    public int getSelectedAltitude() {
        return selectedAltitude;
    }

    public Instant getSelectedAltitudeTimestamp() {
        return selectedAltitudeTimestamp;
    }

    public boolean selectedAltitudeHasBeenSet() {
        return selectedAltitudeTimestamp != Instant.MIN;
    }


    // selected heading
    private double selectedHeading = 0;
    private Instant selectedHeadingTimestamp = Instant.MIN;

    public void setSelectedHeading(double selectedHeading, Instant selectedHeadingTimestamp) {
        this.selectedHeading = selectedHeading;
        this.selectedHeadingTimestamp = selectedHeadingTimestamp;
        updateTimestamp = selectedHeadingTimestamp;
    }

    public double getSelectedHeading() {
        return selectedHeading;
    }

    public Instant getSelectedHeadingTimestamp() {
        return selectedHeadingTimestamp;
    }

    public boolean selectedHeadingHasBeenSet() {
        return selectedHeadingTimestamp != Instant.MIN;
    }


    // barometric pressure
    private double barometricPressure = 0;
    private Instant barometricPressureTimestamp = Instant.MIN;

    public void setBarometricPressure(double barometricPressure, Instant barometricPressureTimestamp) {
        this.barometricPressure = barometricPressure;
        this.barometricPressureTimestamp = barometricPressureTimestamp;
        updateTimestamp = barometricPressureTimestamp;
    }

    public double getBarometricPressure() {
        return barometricPressure;
    }

    public Instant getBarometricPressureTimestamp() {
        return barometricPressureTimestamp;
    }

    public boolean barometricPressureHasBeenSet() {
        return barometricPressureTimestamp != Instant.MIN;
    }


    // mode bits
    private boolean autopilotEngaged = false;
    private boolean vnavModeEngaged = false;
    private boolean altitudeHoldModeEngaged = false;
    private boolean approachModeEngaged = false;
    private boolean tcasOperational = false;
    private Instant modeBitsTimestamp = Instant.MIN;

    public void setModeBits(boolean autopilotEngaged, boolean vnavModeEngaged, boolean altitudeHoldModeEngaged, boolean approachModeEngaged, boolean tcasOperational, Instant modeBitsTimestamp) {
        this.autopilotEngaged = autopilotEngaged;
        this.vnavModeEngaged = vnavModeEngaged;
        this.altitudeHoldModeEngaged = altitudeHoldModeEngaged;
        this.approachModeEngaged = approachModeEngaged;
        this.tcasOperational = tcasOperational;
        this.modeBitsTimestamp = modeBitsTimestamp;
        updateTimestamp = modeBitsTimestamp;
    }

    public boolean isAutopilotEngaged() {
        return autopilotEngaged;
    }

    public boolean isVnavModeEngaged() {
        return vnavModeEngaged;
    }

    public boolean isAltitudeHoldModeEngaged() {
        return altitudeHoldModeEngaged;
    }

    public boolean isApproachModeEngaged() {
        return approachModeEngaged;
    }

    public boolean isTcasOperational() {
        return tcasOperational;
    }

    public Instant getModeBitsTimestamp() {
        return modeBitsTimestamp;
    }

    public boolean modeBitsHaveBeenSet() {
        return modeBitsTimestamp != Instant.MIN;
    }


    public static AircraftState copy(AircraftState aircraftState) {
        AircraftState copy = new AircraftState();

        copy.updateTimestamp = aircraftState.updateTimestamp;

        copy.identification = aircraftState.identification;
        copy.identificationTimestamp = aircraftState.identificationTimestamp;

        copy.latitude = aircraftState.latitude;
        copy.longitude = aircraftState.longitude;
        copy.airborne = aircraftState.airborne;
        copy.distanceFromReceiver = aircraftState.distanceFromReceiver;
        copy.bearingFromReceiver = aircraftState.bearingFromReceiver;
        copy.positionTimestamp = aircraftState.positionTimestamp;

        copy.altitude = aircraftState.altitude;
        copy.altitudeTimestamp = aircraftState.altitudeTimestamp;

        copy.heading = aircraftState.heading;
        copy.headingTimestamp = aircraftState.headingTimestamp;

        copy.airspeed = aircraftState.airspeed;
        copy.airspeedTimestamp = aircraftState.airspeedTimestamp;

        copy.groundTrack = aircraftState.groundTrack;
        copy.groundSpeed = aircraftState.groundSpeed;
        copy.groundTimestamp = aircraftState.groundTimestamp;

        copy.verticalRate = aircraftState.verticalRate;
        copy.verticalRateTimestamp = aircraftState.verticalRateTimestamp;

        copy.geometricHeightDifference = aircraftState.geometricHeightDifference;
        copy.geometricHeightDifferenceTimestamp = aircraftState.geometricHeightDifferenceTimestamp;

        copy.emitterCategory = aircraftState.emitterCategory;
        copy.emitterCategoryTimestamp = aircraftState.emitterCategoryTimestamp;

        copy.emergencyState = aircraftState.emergencyState;
        copy.emergencyStateTimestamp = aircraftState.emergencyStateTimestamp;

        copy.modeACode = aircraftState.modeACode;
        copy.modeACodeTimestamp = aircraftState.modeACodeTimestamp;

        copy.targetAltitude = aircraftState.targetAltitude;
        copy.targetAltitudeTimestamp = aircraftState.targetAltitudeTimestamp;

        copy.targetHeading = aircraftState.targetHeading;
        copy.targetHeadingTimestamp = aircraftState.targetHeadingTimestamp;

        copy.selectedAltitude = aircraftState.selectedAltitude;
        copy.selectedAltitudeTimestamp = aircraftState.selectedAltitudeTimestamp;

        copy.selectedHeading = aircraftState.selectedHeading;
        copy.selectedAltitudeTimestamp = aircraftState.selectedHeadingTimestamp;

        copy.barometricPressure = aircraftState.barometricPressure;
        copy.barometricPressureTimestamp = aircraftState.barometricPressureTimestamp;

        copy.autopilotEngaged = aircraftState.autopilotEngaged;
        copy.vnavModeEngaged = aircraftState.vnavModeEngaged;
        copy.altitudeHoldModeEngaged = aircraftState.altitudeHoldModeEngaged;
        copy.approachModeEngaged = aircraftState.approachModeEngaged;
        copy.tcasOperational = aircraftState.tcasOperational;
        copy.modeBitsTimestamp = Instant.MIN;

        return copy;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof AircraftState)) {
            return false;
        }
        if (object == this) {
            return true;
        }

        AircraftState aircraftState = (AircraftState) object;

        if (updateTimestamp != aircraftState.updateTimestamp) {
            return false;
        }

        if (identification != aircraftState.identification) {
            return false;
        }
        if (identificationTimestamp != aircraftState.identificationTimestamp) {
            return false;
        }

        if (latitude != aircraftState.latitude) {
            return false;
        }
        if (longitude != aircraftState.longitude) {
            return false;
        }
        if (airborne != aircraftState.airborne) {
            return false;
        }
        if (distanceFromReceiver != aircraftState.distanceFromReceiver) {
            return false;
        }
        if (bearingFromReceiver != aircraftState.bearingFromReceiver) {
            return false;
        }
        if (positionTimestamp != aircraftState.positionTimestamp) {
            return false;
        }

        if (altitude != aircraftState.altitude) {
            return false;
        }
        if (altitudeTimestamp != aircraftState.altitudeTimestamp) {
            return false;
        }

        if (heading != aircraftState.heading) {
            return false;
        }
        if (headingTimestamp != aircraftState.headingTimestamp) {
            return false;
        }

        if (airspeed != aircraftState.airspeed) {
            return false;
        }
        if (airspeedTimestamp != aircraftState.airspeedTimestamp) {
            return false;
        }

        if (groundTrack != aircraftState.groundTrack) {
            return false;
        }
        if (groundSpeed != aircraftState.groundSpeed) {
            return false;
        }
        if (groundTimestamp != aircraftState.groundTimestamp) {
            return false;
        }

        if (verticalRate != aircraftState.verticalRate) {
            return false;
        }
        if (verticalRateTimestamp != aircraftState.verticalRateTimestamp) {
            return false;
        }

        if (geometricHeightDifference != aircraftState.geometricHeightDifference) {
            return false;
        }
        if (geometricHeightDifferenceTimestamp != aircraftState.geometricHeightDifferenceTimestamp) {
            return false;
        }

        if (emitterCategory != aircraftState.emitterCategory) {
            return false;
        }
        if (emitterCategoryTimestamp != aircraftState.emitterCategoryTimestamp) {
            return false;
        }

        if (emergencyState != aircraftState.emergencyState) {
            return false;
        }
        if (emergencyStateTimestamp != aircraftState.emergencyStateTimestamp) {
            return false;
        }

        if (modeACode != aircraftState.modeACode) {
            return false;
        }
        if (modeACodeTimestamp != aircraftState.modeACodeTimestamp) {
            return false;
        }

        if (targetAltitude != aircraftState.targetAltitude) {
            return false;
        }
        if (targetAltitudeTimestamp != aircraftState.targetAltitudeTimestamp) {
            return false;
        }

        if (targetHeading != aircraftState.targetHeading) {
            return false;
        }
        if (targetHeadingTimestamp != aircraftState.targetHeadingTimestamp) {
            return false;
        }

        if (selectedAltitude != aircraftState.selectedAltitude) {
            return false;
        }
        if (selectedAltitudeTimestamp != aircraftState.selectedAltitudeTimestamp) {
            return false;
        }

        if (selectedHeading != aircraftState.selectedHeading) {
            return false;
        }
        if (selectedHeadingTimestamp != aircraftState.selectedHeadingTimestamp) {
            return false;
        }

        if (barometricPressure != aircraftState.barometricPressure) {
            return false;
        }
        if (barometricPressureTimestamp != aircraftState.barometricPressureTimestamp) {
            return false;
        }

        if (autopilotEngaged != aircraftState.autopilotEngaged) {
            return false;
        }
        if (vnavModeEngaged != aircraftState.vnavModeEngaged) {
            return false;
        }
        if (altitudeHoldModeEngaged != aircraftState.altitudeHoldModeEngaged) {
            return false;
        }
        if (approachModeEngaged != aircraftState.approachModeEngaged) {
            return false;
        }
        if (tcasOperational != aircraftState.tcasOperational) {
            return false;
        }
        if (modeBitsTimestamp != aircraftState.modeBitsTimestamp) {
            return false;
        }

        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("updateTimestamp = " + updateTimestamp);
        sb.append(System.lineSeparator());

        sb.append("identification = " + identification);
        sb.append(System.lineSeparator());
        sb.append("identificationTimestamp = " + identificationTimestamp);
        sb.append(System.lineSeparator());

        sb.append("latitude = " + latitude);
        sb.append(System.lineSeparator());
        sb.append("longitude = " + longitude);
        sb.append(System.lineSeparator());
        sb.append("airborne = " + airborne);
        sb.append(System.lineSeparator());
        sb.append("distanceFromReceiver = " + distanceFromReceiver);
        sb.append(System.lineSeparator());
        sb.append("bearingFromReceiver = " + bearingFromReceiver);
        sb.append(System.lineSeparator());
        sb.append("positionTimestamp = " + positionTimestamp);
        sb.append(System.lineSeparator());

        sb.append("altitude = " + altitude);
        sb.append(System.lineSeparator());
        sb.append("altitudeTimestamp = " + altitudeTimestamp);
        sb.append(System.lineSeparator());

        sb.append("heading = " + heading);
        sb.append(System.lineSeparator());
        sb.append("headingTimestamp = " + headingTimestamp);
        sb.append(System.lineSeparator());

        sb.append("airspeed = " + airspeed);
        sb.append(System.lineSeparator());
        sb.append("airspeedTimestamp = " + airspeedTimestamp);
        sb.append(System.lineSeparator());

        sb.append("groundTrack = " + groundTrack);
        sb.append(System.lineSeparator());
        sb.append("groundSpeed = " + groundSpeed);
        sb.append(System.lineSeparator());
        sb.append("groundTimestamp = " + groundTimestamp);
        sb.append(System.lineSeparator());

        sb.append("verticalRate = " + verticalRate);
        sb.append(System.lineSeparator());
        sb.append("verticalRateTimestamp = " + verticalRateTimestamp);
        sb.append(System.lineSeparator());

        sb.append("geometricHeightDifference = " + geometricHeightDifference);
        sb.append(System.lineSeparator());
        sb.append("geometricHeightDifferenceTimestamp = " + geometricHeightDifferenceTimestamp);
        sb.append(System.lineSeparator());

        sb.append("emitterCategory = " + emitterCategory);
        sb.append(System.lineSeparator());
        sb.append("emitterCategoryTimestamp = " + emitterCategoryTimestamp);
        sb.append(System.lineSeparator());

        sb.append("emergencyState = " + emergencyState);
        sb.append(System.lineSeparator());
        sb.append("emergencyStateTimestamp = " + emergencyStateTimestamp);
        sb.append(System.lineSeparator());

        sb.append("modeACode = " + modeACode);
        sb.append(System.lineSeparator());
        sb.append("modeACodeTimestamp = " + modeACodeTimestamp);
        sb.append(System.lineSeparator());

        sb.append("targetAltitude = " + targetAltitude);
        sb.append(System.lineSeparator());
        sb.append("targetAltitudeTimestamp = " + targetAltitudeTimestamp);
        sb.append(System.lineSeparator());

        sb.append("targetHeading = " + targetHeading);
        sb.append(System.lineSeparator());
        sb.append("targetHeadingTimestamp = " + targetHeadingTimestamp);
        sb.append(System.lineSeparator());

        sb.append("selectedAltitude = " + selectedAltitude);
        sb.append(System.lineSeparator());
        sb.append("selectedAltitudeTimestamp = " + selectedAltitudeTimestamp);
        sb.append(System.lineSeparator());

        sb.append("selectedHeading = " + selectedHeading);
        sb.append(System.lineSeparator());
        sb.append("selectedHeadingTimestamp = " + selectedHeadingTimestamp);
        sb.append(System.lineSeparator());

        sb.append("barometricPressure = " + barometricPressure);
        sb.append(System.lineSeparator());
        sb.append("barometricPressureTimestamp = " + barometricPressureTimestamp);
        sb.append(System.lineSeparator());

        sb.append("autopilotEngaged = " + autopilotEngaged);
        sb.append(System.lineSeparator());
        sb.append("vnavModeEngaged = " + vnavModeEngaged);
        sb.append(System.lineSeparator());
        sb.append("altitudeHoldModeEngaged = " + altitudeHoldModeEngaged);
        sb.append(System.lineSeparator());
        sb.append("approachModeEngaged = " + approachModeEngaged);
        sb.append(System.lineSeparator());
        sb.append("tcasOperational = " + tcasOperational);
        sb.append(System.lineSeparator());
        sb.append("modeBitsTimestamp = " + modeBitsTimestamp);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

}
