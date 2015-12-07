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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SurveillanceSimulator {

    private final List<Aircraft> aircraftList = new LinkedList<Aircraft>();
    private Instant simulationTime = Instant.MIN;

    private boolean trackEnabled = false;
    private File trackDirectory = null;

    private static final Duration TRACK_TIME_LIMIT = Duration.ofMinutes(5);
    private static final int MIN_TRACK_POINTS = 100;
    private long aircraftTrackCount = 0;

    // message counts
    private long surveillanceCount = 0;
    private long transponderCount = 0;
    private long modeSCount = 0;
    private long extendedSquitterCount = 0;
    private final long[] adsb1090TypeCounts = new long[32];

    // receiver performance
    private static final int MIN_PERFORMANCE_POINTS = 100;
    private final long[] distanceHistogram = new long[300];

    // time stats
    private Instant firstMessageTimestamp = Instant.MAX;
    private Instant lastMessageTimestamp = Instant.MIN;
    private Instant previousMessageTimestamp = Instant.MAX;
    private Duration maxMessageGap = Duration.ZERO;
    private Instant maxMessageGapStart = Instant.MAX;
    private Instant maxMessageGapEnd = Instant.MIN;

    public SurveillanceSimulator() {
        for (int i = 0; i < adsb1090TypeCounts.length; i++) {
            adsb1090TypeCounts[i] = 0;
        }
        for (int i = 0; i < distanceHistogram.length; i++) {
            distanceHistogram[i] = 0;
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

        Instant start = Instant.now();
        System.out.println("Reading input from " + directory + "  " + start);
        Instant previous = start;
        for (File file : files) {
            if (file.getName().endsWith(".txt")) {
                System.out.print("  Processing " + file);
                addFile(file);
                Instant now = Instant.now();
                System.out.println("  " + Duration.between(previous, now) + " (" + Duration.between(start, now) + ")");
                previous = now;
            }
        }
    }

    public void addFile(File file) {
        try {
            SurveillanceLogFileReader surveillanceLogFileReader = new SurveillanceLogFileReader(file);
            AircraftSurveillanceMessage aircraftSurveillanceMessage;
            while ((aircraftSurveillanceMessage = surveillanceLogFileReader.read()) != null) {
                simulationTime = aircraftSurveillanceMessage.getTimestamp();
                updateTimeStats();
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

            pw.println("Time Statistics");
            pw.println("First Message Timestamp: " + firstMessageTimestamp);
            pw.println("Last Message Timestamp: " + lastMessageTimestamp);
            pw.println("Duration: " + Duration.between(firstMessageTimestamp, lastMessageTimestamp));
            pw.println("Max Message Gap: " + maxMessageGap);
            pw.println("Max Message Gap Start: " + maxMessageGapStart);
            pw.println("Max Message Gap End: " + maxMessageGapEnd);
            pw.println();

            pw.println("Number of Tracks: " + aircraftTrackCount);
            pw.println();

            long total = 0;
            for (long count : distanceHistogram) {
                total += count;
            }
            int percentileIndex = 0;
            for (long j = 0; (j < (total * 0.95)) & (percentileIndex < distanceHistogram.length); percentileIndex++) {
                j += distanceHistogram[percentileIndex];
            }
            percentileIndex--;
            pw.println("Receiver Distance 95th Percentile: " + percentileIndex);
            pw.println();

            pw.println("ADSB 1090 MHz Messages");
            pw.println("Type\tCount");
            for (int i = 0; i < adsb1090TypeCounts.length; i++) {
                pw.println(i + "\t" + adsb1090TypeCounts[i]);
            }
            pw.println();

            pw.println("Receiver Distance Histogram");
            pw.println("Distance\tCount");
            for (int i = 0; i < distanceHistogram.length; i++) {
                pw.println(i + "\t" + distanceHistogram[i]);
            }
            pw.println();

            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateTimeStats() {
        if (firstMessageTimestamp.equals(Instant.MAX)) {
            firstMessageTimestamp = simulationTime;
            previousMessageTimestamp = simulationTime;
        }

        lastMessageTimestamp = simulationTime;

        Duration gap = Duration.between(previousMessageTimestamp, simulationTime);
        if (gap.compareTo(maxMessageGap) > 0) {
            maxMessageGap = gap;
            maxMessageGapStart = previousMessageTimestamp;
            maxMessageGapEnd = simulationTime;
        }
        previousMessageTimestamp = simulationTime;
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
                }

                if (collapsedStateList.size() >= MIN_PERFORMANCE_POINTS) {
                    for (AircraftState aircraftState : collapsedStateList) {
                        int distance = (int) Math.floor(aircraftState.getDistanceFromReceiver());
                        if (distance < distanceHistogram.length) {
                            distanceHistogram[distance]++;
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
