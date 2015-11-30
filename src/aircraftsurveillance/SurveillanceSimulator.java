package aircraftsurveillance;

import aircraftsurveillance.transponder.ExtendedSquitter;
import aircraftsurveillance.transponder.ModeSMessage;
import aircraftsurveillance.transponder.TransponderMessage;
import aircraftsurveillance.transponder.adsb1090.Adsb1090Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SurveillanceSimulator {

    private Position receiverPosition = null;
    private List<Aircraft> aircraftList = new LinkedList<Aircraft>();
    private Instant simulationTime = Instant.MIN;

    private boolean trackEnabled = false;
    private File trackDirectory = null;

    private static final Duration TRACK_TIME_LIMIT = Duration.ofMinutes(5);
    private static final int MIN_TRACK_POINTS = 100;
    long aircraftTrackCount = 0;

    // message counts
    long surveillanceCount = 0;
    long transponderCount = 0;
    long modeSCount = 0;
    long extendedSquitterCount = 0;
    long[] adsb1090TypeCounts = new long[32];

    // receiver performance
    double[] distances = new double[360];
    String[] aircraftAtMaxDistance = new String[360];
    List<Position> positionList = new LinkedList<Position>();

    public SurveillanceSimulator() {
        for (int i = 0; i < adsb1090TypeCounts.length; i++) {
            adsb1090TypeCounts[i] = 0;
        }
        for (int i = 0; i < distances.length; i++) {
            distances[i] = 0;
            aircraftAtMaxDistance[i] = "";
        }
    }

    public void enableTrackCreation(File trackDirectory) {
        this.trackDirectory = trackDirectory;
        trackEnabled = true;
    }

    public void addDirectory(File directory) {
        if (directory == null) {
            return;
        }
        if (!directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        files = filterAndSortFiles(files);

        System.out.println("Reading input from " + directory);
        for (File file : files) {
            if (file.getName().endsWith(".txt")) {
                System.out.println("  Processing " + file);
                addFile(file);
            }
        }
    }

    public void addFile(File file) {
        try {
            SurveillanceLogFileReader surveillanceLogFileReader = new SurveillanceLogFileReader(file);
            AircraftSurveillanceMessage aircraftSurveillanceMessage;
            while ((aircraftSurveillanceMessage = surveillanceLogFileReader.read()) != null) {
                simulationTime = aircraftSurveillanceMessage.getTimestamp();
                receiverPosition = new Position(aircraftSurveillanceMessage);
                trimList();
                update(aircraftSurveillanceMessage);
            }
            surveillanceLogFileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endSimulation() {
        simulationTime = Instant.MAX;
        trimList();
    }

    public void writeStats(File file) {
        try {
            PrintWriter pw = new PrintWriter(file);

            pw.println("Message Counts");
            pw.println("Surveillance Messages: " + surveillanceCount);
            pw.println("Transponder Messages: " + transponderCount);
            pw.println("Mode S Messages: " + modeSCount);
            pw.println("Extended Squitter Messages: " + extendedSquitterCount);
            pw.println();

            pw.println("ADSB 1090 MHz Messages");
            pw.println("Type\tCount");
            for (int i = 0; i < adsb1090TypeCounts.length; i++) {
                pw.println(i + "\t" + adsb1090TypeCounts[i]);
            }
            pw.println();

            pw.println("Maximum Reception Distance Table");
            pw.println("bearing\tdistance\taircraft");
            double maxDistance = 0;
            String maxAircraft = "";
            for (int i = 0; i < distances.length; i++) {
                pw.println(i + "\t" + distances[i] + "\t" + aircraftAtMaxDistance[i]);
                if (distances[i] > maxDistance) {
                    maxDistance = distances[i];
                    maxAircraft = aircraftAtMaxDistance[i];
                }
            }
            pw.println();

            pw.println("Maximum Reception Distance: " + maxDistance);
            pw.println("Aircraft: " + maxAircraft);

            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void update(AircraftSurveillanceMessage aircraftSurveillanceMessage) {
        if (aircraftSurveillanceMessage == null) {
            return;
        }

        surveillanceCount++;

        if (aircraftSurveillanceMessage instanceof TransponderMessage) {
            updateTransponderMessage((TransponderMessage) aircraftSurveillanceMessage);
        }
    }

    private void updateTransponderMessage(TransponderMessage transponderMessage) {
        if (transponderMessage == null) {
            return;
        }

        transponderCount++;

        if (transponderMessage instanceof ModeSMessage) {
            updateModeSMessage((ModeSMessage) transponderMessage);
        }
    }

    private void updateModeSMessage(ModeSMessage modeSMessage) {
        if (modeSMessage == null) {
            return;
        }

        modeSCount++;

        if (modeSMessage instanceof ExtendedSquitter) {
            updateExtendedSquitter((ExtendedSquitter) modeSMessage);
        }
    }

    private void updateExtendedSquitter(ExtendedSquitter extendedSquitter) {
        if (extendedSquitter == null) {
            return;
        }

        extendedSquitterCount++;

        Adsb1090Message adsb1090Message = extendedSquitter.getExtendedSquitterMessage();
        if (adsb1090Message != null) {
            adsb1090TypeCounts[adsb1090Message.getTypeCode()]++;
        }

        for (Aircraft aircraft : aircraftList) {
            if (aircraft.getAddress() == extendedSquitter.getAddressAnnounced()) {
                aircraft.update(extendedSquitter);
                return;
            }
        }

        Aircraft aircraft = new Aircraft(extendedSquitter.getAddressAnnounced());
        aircraft.update(extendedSquitter);
        aircraftList.add(aircraft);
    }

    private void trimList() {
        Instant timestamp = simulationTime;

        for (int i = 0; i < aircraftList.size(); i++) {
            Aircraft aircraft = aircraftList.get(i);
            if (Duration.between(aircraft.getUpdateTimestamp(), timestamp).compareTo(TRACK_TIME_LIMIT) >= 0) {
                List<AircraftState> collapsedStateList = aircraft.getCollapsedAircraftStateList();
                if (collapsedStateList.size() >= MIN_TRACK_POINTS) {
                    aircraftTrackCount++;

                    if (trackEnabled) {
                        aircraft.writeKmlFile(trackDirectory);
                    }

                    for (AircraftState aircraftState : collapsedStateList) {
                        Position aircraftPosition = new Position(aircraftState);
                        positionList.add(aircraftPosition);

                        if (receiverPosition != null) {
                            try {
                                double distance = Position.distance(receiverPosition, aircraftPosition) * 0.000539957;  // convert meters to nautical miles
                                double course = Position.course(receiverPosition, aircraftPosition);
                                int bearing = (int) Math.floor(course);

                                if (distance > distances[bearing]) {
                                    distances[bearing] = distance;

                                    Instant updateTimestamp = collapsedStateList.get(0).getUpdateTimestamp();
                                    LocalDateTime localDateTime = LocalDateTime.ofInstant(updateTimestamp, ZoneId.systemDefault());
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                                    String name = dtf.format(localDateTime) + "." + String.format("%06X", aircraft.getAddress());
                                    aircraftAtMaxDistance[bearing] = name;
                                }
                            } catch (Position.IterationLimitExceeded iterationLimitExceeded) {
                            }

                        }
                    }
                }

                aircraftList.remove(i);
                i--;
            }
        }
    }

    private static File[] filterAndSortFiles(File[] files) {
        List<File> fileList = new LinkedList<File>();

        for (File file : files) {
            if (file.getName().startsWith("sdr.") | file.getName().startsWith("kinetic.")) {
                if (file.getName().endsWith(".txt")) {
                    fileList.add(file);
                }
            }
        }

        fileList.sort(new FileComparator());
        File[] fileArray = new File[fileList.size()];
        for (int i = 0; i < fileList.size(); i++) {
            fileArray[i] = fileList.get(i);
        }

        return fileArray;
    }

    private static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            String name1 = file1.getName();
            String name2 = file2.getName();

            int i = name1.indexOf(".");
            int j = name1.lastIndexOf(".");
            int timestamp1 = Integer.parseInt(name1.substring(i + 1, j));

            i = name2.indexOf(".");
            j = name2.lastIndexOf(".");
            int timestamp2 = Integer.parseInt(name2.substring(i + 1, j));

            return Integer.compare(timestamp1, timestamp2);
        }
    }
}
