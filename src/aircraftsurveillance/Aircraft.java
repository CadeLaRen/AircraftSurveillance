package aircraftsurveillance;

import aircraftsurveillance.transponder.ExtendedSquitter;
import aircraftsurveillance.transponder.ModeSMessage;
import aircraftsurveillance.transponder.TransponderMessage;
import aircraftsurveillance.transponder.adsb1090.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class Aircraft {
    private int address = 0;
    private Instant updateTimestamp = Instant.MIN;
    private AircraftPosition aircraftPosition = new AircraftPosition();

    private AircraftState aircraftState = new AircraftState();
    private List<AircraftState> aircraftStateList = new LinkedList<AircraftState>();

    public Aircraft(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public Instant getUpdateTimestamp() {
        return updateTimestamp;
    }

    public int getUpdateCount() {
        return aircraftStateList.size();
    }

    public String getIdentification() {
        if (aircraftState.identificationHasBeenSet()) {
            return aircraftState.getIdentification();
        } else {
            return null;
        }
    }

    public List<AircraftState> getRawAircraftStateList() {
        return aircraftStateList;
    }

    public List<AircraftState> getCollapsedAircraftStateList() {
        return collapseStateList(aircraftStateList);
    }

    public Position getPosition() {
        if (aircraftState.positionHasBeenSet()) {
            return new Position(aircraftState.getLatitude(), aircraftState.getLongitude());
        } else {
            return null;
        }
    }

    public int getAltitude() {
        if (aircraftState.altitudeHasBeenSet()) {
            return aircraftState.getAltitude();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public double getGroundTrack() {
        if (aircraftState.groundVelocityHasBeenSet()) {
            return aircraftState.getGroundTrack();
        } else {
            return Double.MAX_VALUE;
        }
    }

    public double getGroundSpeed() {
        if (aircraftState.groundVelocityHasBeenSet()) {
            return aircraftState.getGroundSpeed();
        } else {
            return Double.MAX_VALUE;
        }
    }

    public void writeKmlFile(File directory) {
        writeKmlFile(address, collapseStateList(aircraftStateList), directory);
    }

    private static void writeKmlFile(long address, List<AircraftState> aircraftStateList, File directory) {
        if (aircraftStateList.size() == 0)
            return;

        try {
            Instant updateTimestamp = aircraftStateList.get(0).getUpdateTimestamp();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(updateTimestamp, ZoneId.systemDefault());

            String year = String.format("%04d", localDateTime.getYear());
            String month = String.format("%02d", localDateTime.getMonthValue());
            String day = String.format("%02d", localDateTime.getDayOfMonth());
            String fs = System.getProperty("file.separator");
            String kmlDirectory = directory.getAbsolutePath() + fs + year + fs + month + fs + day;
            File file = new File(kmlDirectory);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return;
                }
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm-ss");
            String name = dtf.format(localDateTime) + "." + String.format("%06X", address);
            String path = kmlDirectory + fs + name + ".kml";
            PrintWriter kml = new PrintWriter(path);

            kml.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            kml.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
            kml.println("<Document>");
            kml.println("<name>" + name + "</name>");

            kml.println("<Folder>");
            kml.println("<name>Track - Surface</name>");
            kml.println("<Placemark>");
            kml.println("<name>" + String.format("%06X", address) + "</name>");
            kml.println("<Snippet maxLines=\"0\"></Snippet>");
            kml.println("<description>&amp;nbsp;</description>");
            kml.println("<Style>");
            kml.println("<LineStyle>");
            kml.println("<color>ffe60000</color>");
            kml.println("<width>4</width>");
            kml.println("</LineStyle>");
            kml.println("</Style>");
            kml.println("<MultiGeometry>");
            kml.println("<LineString>");
            kml.println("<extrude>1</extrude>");
            kml.println("<altitudeMode>clampToGround</altitudeMode>");
            kml.println("<coordinates>");

            for (int i = 0; i < aircraftStateList.size(); i++) {
                AircraftState state = aircraftStateList.get(i);
                if (!state.isAirborne()) {
                    kml.println(state.getLongitude() + "," + state.getLatitude() + "," + (((double) state.getAltitude()) * 0.3048) + " ");
                }
            }

            kml.println("</coordinates>");
            kml.println("</LineString>");
            kml.println("</MultiGeometry>");
            kml.println("</Placemark>");
            kml.println("</Folder>");

            kml.println("<Folder>");
            kml.println("<name>Track - Airborne</name>");
            kml.println("<Placemark>");
            kml.println("<name>" + String.format("%06X", address) + "</name>");
            kml.println("<Snippet maxLines=\"0\"></Snippet>");
            kml.println("<description>&amp;nbsp;</description>");
            kml.println("<Style>");
            kml.println("<LineStyle>");
            kml.println("<color>ff0000e6</color>");
            kml.println("<width>4</width>");
            kml.println("</LineStyle>");
            kml.println("<PolyStyle>");
            kml.println("<color>cc0000e6</color>");
            kml.println("</PolyStyle>");
            kml.println("</Style>");
            kml.println("<MultiGeometry>");
            kml.println("<LineString>");
            kml.println("<extrude>1</extrude>");
            kml.println("<altitudeMode>absolute</altitudeMode>");
            kml.println("<coordinates>");

            for (int i = 0; i < aircraftStateList.size(); i++) {
                AircraftState state = aircraftStateList.get(i);
                if (state.isAirborne()) {
                    kml.println(state.getLongitude() + "," + state.getLatitude() + "," + (((double) state.getAltitude()) * 0.3048) + " ");
                }
            }

            kml.println("</coordinates>");
            kml.println("</LineString>");
            kml.println("</MultiGeometry>");
            kml.println("</Placemark>");
            kml.println("</Folder>");

            kml.println("<Folder>");
            kml.println("<name>Data</name>");

            Instant previousUpdateTimestamp = Instant.MIN;
            for (int i = 0; i < aircraftStateList.size(); i++) {
                AircraftState state = aircraftStateList.get(i);

                kml.println("<Placemark>");
                if (previousUpdateTimestamp.equals(Instant.MIN)) {
                    kml.println("<name>" + i + " - " + state.getUpdateTimestamp() + "</name>");
                } else {
                    kml.println("<name>" + i + " - " + state.getUpdateTimestamp() + " (" + Duration.between(previousUpdateTimestamp, state.getUpdateTimestamp()).getSeconds() + ")</name>");
                }
                kml.println("<description>");
                kml.println("<![CDATA[");
                kml.println("<table border=\"1\">");
                kml.println("<tr><td>Key</td><td>Value</td><td>Age (s)</td></tr>");
                if (state.identificationHasBeenSet()) {
                    kml.println("<tr><td>Identification</td><td>" + state.getIdentification() + "</td><td>" + Duration.between(state.getIdentificationTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.positionHasBeenSet()) {
                    kml.println("<tr><td>Latitude</td><td>" + state.getLatitude() + "</td><td>" + Duration.between(state.getPositionTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                    kml.println("<tr><td>Longitude</td><td>" + state.getLongitude() + "</td><td>" + Duration.between(state.getPositionTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                    kml.println("<tr><td>Airborne</td><td>" + state.isAirborne() + "</td><td>" + Duration.between(state.getPositionTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.altitudeHasBeenSet()) {
                    kml.println("<tr><td>Altitude</td><td>" + state.getAltitude() + "</td><td>" + Duration.between(state.getAltitudeTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.headingHasBeenSet()) {
                    kml.println("<tr><td>Heading</td><td>" + state.getHeading() + "</td><td>" + Duration.between(state.getHeadingTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.airspeedHasBeenSet()) {
                    kml.println("<tr><td>Air Speed</td><td>" + state.getAirspeed() + "</td><td>" + Duration.between(state.getAirspeedTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.groundVelocityHasBeenSet()) {
                    kml.println("<tr><td>Ground Track</td><td>" + state.getGroundTrack() + "</td><td>" + Duration.between(state.getGroundVelocityTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                    kml.println("<tr><td>Ground Speed</td><td>" + state.getGroundSpeed() + "</td><td>" + Duration.between(state.getGroundVelocityTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.verticalRateHasBeenSet()) {
                    kml.println("<tr><td>Vertical Rate</td><td>" + state.getVerticalRate() + "</td><td>" + Duration.between(state.getVerticalRateTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.geometricHeightDifferenceHasBeenSet()) {
                    kml.println("<tr><td>Geometric Height Difference</td><td>" + state.getGeometricHeightDifference() + "</td><td>" + Duration.between(state.getGeometricHeightDifferenceTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.emitterCategoryHasBeenSet()) {
                    kml.println("<tr><td>Emitter Category</td><td>" + state.getEmitterCategory() + "</td><td>" + Duration.between(state.getEmitterCategoryTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.emergencyStateHasBeenSet()) {
                    kml.println("<tr><td>Emergency State</td><td>" + state.getEmergencyState() + "</td><td>" + Duration.between(state.getEmergencyStateTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.modeACodeHasBeenSet()) {
                    kml.println("<tr><td>Mode C Code</td><td>" + state.getModeACode() + "</td><td>" + Duration.between(state.getModeACodeTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.targetAltitudeHasBeenSet()) {
                    kml.println("<tr><td>Target Altitude</td><td>" + state.getTargetAltitude() + "</td><td>" + Duration.between(state.getTargetAltitudeTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.targetHeadingHasBeenSet()) {
                    kml.println("<tr><td>Target Heading</td><td>" + state.getTargetHeading() + "</td><td>" + Duration.between(state.getTargetHeadingTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.selectedAltitudeHasBeenSet()) {
                    kml.println("<tr><td>Selected Altitude</td><td>" + state.getSelectedAltitude() + "</td><td>" + Duration.between(state.getSelectedAltitudeTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.selectedHeadingHasBeenSet()) {
                    kml.println("<tr><td>Selected Heading</td><td>" + state.getSelectedHeading() + "</td><td>" + Duration.between(state.getSelectedHeadingTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.barometricPressureHasBeenSet()) {
                    kml.println("<tr><td>Barometric Pressure</td><td>" + state.getBarometricPressure() + "</td><td>" + Duration.between(state.getBarometricPressureTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }
                if (state.modeBitsHaveBeenSet()) {
                    kml.println("<tr><td>Auto Pilot</td><td>" + state.isAutopilotEngaged() + "</td><td>" + Duration.between(state.getModeBitsTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                    kml.println("<tr><td>VNAV Mode</td><td>" + state.isVnavModeEngaged() + "</td><td>" + Duration.between(state.getModeBitsTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                    kml.println("<tr><td>Altitude Hold</td><td>" + state.isAltitudeHoldModeEngaged() + "</td><td>" + Duration.between(state.getModeBitsTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                    kml.println("<tr><td>Approach</td><td>" + state.isApproachModeEngaged() + "</td><td>" + Duration.between(state.getModeBitsTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                    kml.println("<tr><td>TCAS Operational</td><td>" + state.isTcasOperational() + "</td><td>" + Duration.between(state.getModeBitsTimestamp(), state.getUpdateTimestamp()).getSeconds() + "</td></tr>");
                }

                kml.println("</table>");
                kml.println("]]>");
                kml.println("</description>");
                kml.println("<Point>");
                if (state.isAirborne()) {
                    kml.println("<altitudeMode>absolute</altitudeMode>");
                } else {
                    kml.println("<altitudeMode>clampToGround</altitudeMode>");
                }
                kml.println("<coordinates>" + state.getLongitude() + "," + state.getLatitude() + "," + (((double) state.getAltitude()) * 0.3048) + "</coordinates>");
                kml.println("</Point>");
                kml.println("</Placemark>");

                previousUpdateTimestamp = state.getUpdateTimestamp();
            }
            kml.println("</Folder>");

            kml.println("</Document>");
            kml.println("</kml>");

            kml.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private List<AircraftState> collapseStateList(List<AircraftState> inputList) {
        List<AircraftState> outputList = new LinkedList<AircraftState>();

        double previousLatitude = Double.MAX_VALUE;
        double previousLongitude = Double.MAX_VALUE;
        for (int i = 0; i < aircraftStateList.size(); i++) {
            AircraftState state = aircraftStateList.get(i);
            if (state.positionHasBeenSet() & state.altitudeHasBeenSet() & (state.getLatitude() != previousLatitude | state.getLongitude() != previousLongitude)) {
                outputList.add(state);
                previousLatitude = state.getLatitude();
                previousLongitude = state.getLongitude();
            }
        }

        return outputList;
    }

    public void update(AircraftSurveillanceMessage aircraftSurveillanceMessage) {
        if (aircraftSurveillanceMessage == null) {
            return;
        }

        updateTimestamp = aircraftSurveillanceMessage.getTimestamp();
        aircraftPosition.setReceiverPosition(new Position(aircraftSurveillanceMessage));

        if (aircraftSurveillanceMessage instanceof TransponderMessage) {
            updateTransponderMessage((TransponderMessage) aircraftSurveillanceMessage);
        }

        // if necessary, update the aircraft state list
        if (aircraftStateList.size() == 0) {
            if (aircraftState.getUpdateTimestamp() != Instant.MIN) {
                aircraftStateList.add(AircraftState.copy(aircraftState));
            }
        } else {
            if (!aircraftStateList.get(aircraftStateList.size() - 1).equals(aircraftState)) {
                aircraftStateList.add(AircraftState.copy(aircraftState));
            }
        }

    }

    private void updateTransponderMessage(TransponderMessage transponderMessage) {
        if (transponderMessage == null) {
            return;
        }

        if (transponderMessage instanceof ModeSMessage) {
            updateModeSMessage((ModeSMessage) transponderMessage);
        }
    }

    private void updateModeSMessage(ModeSMessage modeSMessage) {
        if (modeSMessage == null) {
            return;
        }

        if (modeSMessage instanceof ExtendedSquitter) {
            updateExtendedSquitter((ExtendedSquitter) modeSMessage);
        }
    }

    private void updateExtendedSquitter(ExtendedSquitter extendedSquitter) {
        if (extendedSquitter == null) {
            return;
        }

        Adsb1090Message adsb1090Message = extendedSquitter.getExtendedSquitterMessage();

        if (adsb1090Message instanceof AirbornePosition) {
            updateAirbornePosition((AirbornePosition) adsb1090Message, extendedSquitter.timestamp);
        } else if (adsb1090Message instanceof AirborneVelocity) {
            updateAirborneVelocity((AirborneVelocity) adsb1090Message, extendedSquitter.timestamp);
        } else if (adsb1090Message instanceof AircraftStatus) {
            updateAircraftStatus((AircraftStatus) adsb1090Message, extendedSquitter.timestamp);
        } else if (adsb1090Message instanceof IdentificationAndCategory) {
            updateIdentificationAndCategory((IdentificationAndCategory) adsb1090Message, extendedSquitter.timestamp);
        } else if (adsb1090Message instanceof OperationalStatus) {
            updateOperationalStatus((OperationalStatus) adsb1090Message, extendedSquitter.timestamp);
        } else if (adsb1090Message instanceof SurfacePosition) {
            updateSurfacePosition((SurfacePosition) adsb1090Message, extendedSquitter.timestamp);
        } else if (adsb1090Message instanceof SurfaceSystemStatus) {
            updateSurfaceSystemStatus((SurfaceSystemStatus) adsb1090Message, extendedSquitter.timestamp);
        } else if (adsb1090Message instanceof TargetStateAndStatus) {
            updateTargetStateAndStatus((TargetStateAndStatus) adsb1090Message, extendedSquitter.timestamp);
        }
    }

    private void updateAirbornePosition(AirbornePosition airbornePosition, Instant timestamp) {
        aircraftPosition.updateAirborne(airbornePosition.getCompactPositionReport(), timestamp);

        if (aircraftPosition.getPosition() != null) {
            aircraftState.setPosition(aircraftPosition);

            if (airbornePosition.isAltitudeAvailable()) {
                aircraftState.setAltitude(airbornePosition.getAltitude(), timestamp);
            }
        }
    }

    private void updateAirborneVelocity(AirborneVelocity airborneVelocity, Instant timestamp) {
        if (airborneVelocity instanceof AirspeedAndHeading) {
            updateAirspeedAndHeading((AirspeedAndHeading) airborneVelocity, timestamp);
        } else if (airborneVelocity instanceof VelocityOverGround) {
            updateVelocityOverGround((VelocityOverGround) airborneVelocity, timestamp);
        }
    }

    private void updateAirspeedAndHeading(AirspeedAndHeading airspeedAndHeading, Instant timestamp) {
        if (airspeedAndHeading.isAirspeedAvailable()) {
            aircraftState.setAirspeed(airspeedAndHeading.getAirspeed(), timestamp);
        }
        if (airspeedAndHeading.isHeadingAvailable()) {
            aircraftState.setHeading(airspeedAndHeading.getHeading(), timestamp);
        }
        if (airspeedAndHeading.isVerticalRateAvailable()) {
            int temp = airspeedAndHeading.getVerticalRate();
            if (airspeedAndHeading.isVerticalRateNegative()) {
                temp *= -1;
            }
            aircraftState.setVerticalRate(temp, timestamp);
        }
        if (airspeedAndHeading.isGeometricHeightDifferenceAvailable()) {
            int temp = airspeedAndHeading.getGeometricHeightDifference();
            if (airspeedAndHeading.isGeometricHeightBelowBaroAltitude()) {
                temp *= -1;
            }
            aircraftState.setGeometricHeightDifference(temp, timestamp);
        }
    }

    private void updateVelocityOverGround(VelocityOverGround velocityOverGround, Instant timestamp) {
        if (velocityOverGround.isEastWestVelocityAvailable() & velocityOverGround.isNorthSouthVelocityAvailable()) {
            int eastWestVelocity = velocityOverGround.getEastWestVelocity();
            if (velocityOverGround.isWestVelocity()) {
                eastWestVelocity *= -1;
            }
            int northSouthVelocity = velocityOverGround.getNorthSouthVelocity();
            if (velocityOverGround.isSouthVelocity()) {
                northSouthVelocity *= -1;
            }

            double groundTrack = Math.toDegrees(Math.atan2(eastWestVelocity, northSouthVelocity));
            groundTrack += 360;
            groundTrack %= 360;

            double groundSpeed = Math.sqrt((eastWestVelocity * eastWestVelocity) + (northSouthVelocity * northSouthVelocity));

            aircraftState.setGroundVelocity(groundTrack, groundSpeed, timestamp);
        }
        if (velocityOverGround.isVerticalRateAvailable()) {
            int temp = velocityOverGround.getVerticalRate();
            if (velocityOverGround.isVerticalRateNegative()) {
                temp *= -1;
            }
            aircraftState.setVerticalRate(temp, timestamp);
        }
        if (velocityOverGround.isGeometricHeightDifferenceAvailable()) {
            int temp = velocityOverGround.getGeometricHeightDifference();
            if (velocityOverGround.isGeometricHeightBelowBaroAltitude()) {
                temp *= -1;
            }
            aircraftState.setGeometricHeightDifference(temp, timestamp);
        }
    }

    private void updateAircraftStatus(AircraftStatus aircraftStatus, Instant timestamp) {
        if (aircraftStatus instanceof EmergencyStatus) {
            updateEmergencyStatus((EmergencyStatus) aircraftStatus, timestamp);
        } else if (aircraftStatus instanceof ResolutionAdvisory) {
            updateResolutionAdvisory((ResolutionAdvisory) aircraftStatus, timestamp);
        }
    }

    private void updateEmergencyStatus(EmergencyStatus emergencyStatus, Instant timestamp) {
        aircraftState.setEmergencyState(emergencyStatus.getEmergencyState(), timestamp);
        aircraftState.setModeACode(emergencyStatus.getModeACode(), timestamp);
    }

    private void updateResolutionAdvisory(ResolutionAdvisory resolutionAdvisory, Instant timestamp) {
        // todo
    }

    private void updateIdentificationAndCategory(IdentificationAndCategory identificationAndCategory, Instant timestamp) {
        aircraftState.setIdentification(identificationAndCategory.getCharactersAsString(), timestamp);
        aircraftState.setEmitterCategory(identificationAndCategory.getEmitterCategory(), timestamp);
    }

    private void updateOperationalStatus(OperationalStatus operationalStatus, Instant timestamp) {
        if (operationalStatus instanceof AirborneOperationalStatus) {
            updateAirborneOperationalStatus((AirborneOperationalStatus) operationalStatus, timestamp);
        } else if (operationalStatus instanceof SurfaceOperationalStatus) {
            updateSurfaceOperationalStatus((SurfaceOperationalStatus) operationalStatus, timestamp);
        }
    }

    private void updateAirborneOperationalStatus(AirborneOperationalStatus airborneOperationalStatus, Instant timestamp) {
        // todo
    }

    private void updateSurfaceOperationalStatus(SurfaceOperationalStatus surfaceOperationalStatus, Instant timestamp) {
        // todo
    }

    private void updateSurfacePosition(SurfacePosition surfacePosition, Instant timestamp) {
        aircraftPosition.updateSurface(surfacePosition.getCompactPositionReport(), timestamp);

        if (aircraftPosition.getPosition() != null) {
            aircraftState.setPosition(aircraftPosition);
        }
    }

    private void updateSurfaceSystemStatus(SurfaceSystemStatus surfaceSystemStatus, Instant timestamp) {
        // todo
    }

    private void updateTargetStateAndStatus(TargetStateAndStatus targetStateAndStatus, Instant timestamp) {
        if (targetStateAndStatus instanceof TargetStateAndStatusVersion1) {
            updateTargetStateAndStatusVersion1((TargetStateAndStatusVersion1) targetStateAndStatus, timestamp);
        } else if (targetStateAndStatus instanceof TargetStateAndStatusVersion2) {
            updateTargetStateAndStatusVersion2((TargetStateAndStatusVersion2) targetStateAndStatus, timestamp);
        }
    }

    private void updateTargetStateAndStatusVersion1(TargetStateAndStatusVersion1 targetStateAndStatusVersion1, Instant timestamp) {
        aircraftState.setTargetAltitude(targetStateAndStatusVersion1.getTargetAltitude(), timestamp);
        aircraftState.setTargetHeading(targetStateAndStatusVersion1.getTargetHeading(), timestamp);
    }

    private void updateTargetStateAndStatusVersion2(TargetStateAndStatusVersion2 targetStateAndStatusVersion2, Instant timestamp) {
        if (targetStateAndStatusVersion2.isSelectedAltitudeAvailable()) {
            aircraftState.setSelectedAltitude(targetStateAndStatusVersion2.getSelectedAltitude(), timestamp);
        }

        if (targetStateAndStatusVersion2.isSelectedHeadingAvailable()) {
            aircraftState.setSelectedHeading(targetStateAndStatusVersion2.getSelectedHeading(), timestamp);
        }

        if (targetStateAndStatusVersion2.isBarometricPressureSettingAvailable()) {
            aircraftState.setBarometricPressure(targetStateAndStatusVersion2.getBarometricPressureSetting(), timestamp);
        }

        if (targetStateAndStatusVersion2.areModeBitsAvailable()) {
            boolean autopilot = targetStateAndStatusVersion2.isAutopilotEngaged();
            boolean vnavMode = targetStateAndStatusVersion2.isVnavModeEngaged();
            boolean altitudeHold = targetStateAndStatusVersion2.isAltitudeHoldModeEngaged();
            boolean approachMode = targetStateAndStatusVersion2.isApproachModeEngaged();
            boolean tcas = targetStateAndStatusVersion2.isTcasOperational();
            aircraftState.setModeBits(autopilot, vnavMode, altitudeHold, approachMode, tcas, timestamp);
        }
    }

}
