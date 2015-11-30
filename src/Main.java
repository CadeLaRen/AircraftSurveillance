import aircraftsurveillance.SurveillanceSimulator;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        Instant start = Instant.now();

        SurveillanceSimulator simulator = new SurveillanceSimulator();
        simulator.enableTrackCreation(new File("C:\\kml\\charleston"));
        simulator.addDirectory(new File("C:\\adsb\\charleston"));
        simulator.endSimulation();
        simulator.writeStats(new File("C:\\kml\\charleston\\stats.txt"));

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println(duration);
    }

}
