package aircraftsurveillance;

import aircraftsurveillance.transponder.ModeSMessage;
import aircraftsurveillance.transponder.TransponderMessage;

import java.io.*;
import java.time.Instant;

/**
 * Reads aircraft surveillance messages from a log file written using either KineticMessageLogger or SdrMessageLogger.
 */
public class SurveillanceLogFileReader {

    private BufferedReader bufferedReader;

    /**
     * Opens the log file.
     *
     * @param file file to open
     * @throws FileNotFoundException
     */
    public SurveillanceLogFileReader(File file) throws FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(file));
    }

    private static AircraftSurveillanceMessage parse(String line) {
        if (line == null) {
            return null;
        }

        String[] split = line.trim().split("\t");
        if (split.length == 7) {
            if ((split[0].trim().length() >= 10) &
                    (split[1].trim().length() >= 1) &
                    (split[2].trim().length() >= 1) &
                    (split[3].trim().length() >= 1) &
                    (split[4].trim().length() >= 1) &
                    (split[5].trim().length() >= 1) &
                    (split[6].trim().length() >= 1)) {

                if (split[5].trim().length() > split[6].trim().length()) {
                    // kinetic format
                    return parseKinetic(line);
                } else if (split[5].trim().length() < split[6].trim().length()) {
                    // sdr format
                    return parseSdr(line);
                }
            }
        }

        return null;
    }

    private static TransponderMessage parseKinetic(String line) {
        if (line == null) {
            return null;
        }

        String[] split = line.trim().split("\t");
        if (split.length != 7) {
            return null;
        }

        String split0 = split[0].trim();
        String split1 = split[1].trim();
        String split2 = split[2].trim();
        String split3 = split[3].trim();
        String split4 = split[4].trim();
        String split5 = split[5].trim();
        String split6 = split[6].trim();

        if ((split0.length() < 10) |
                (split1.length() < 1) |
                (split2.length() < 1) |
                (split3.length() < 1) |
                (split4.length() < 1) |
                (split5.length() < 1) |
                (split6.length() < 1)) {
            return null;
        }

        long seconds = Long.parseLong(split0);
        long microSeconds = Long.parseLong(split1);
        Instant timestamp = Instant.ofEpochSecond(seconds, microSeconds * 1000);
        double latitude = Double.parseDouble(split2);
        double longitude = Double.parseDouble(split3);
        double altitude = Double.parseDouble(split4);
        int[] data = parseHexString(split5);
        int crc[] = parseHexString(split6);
        int crc2 = (crc[0] << 8) + crc[1];

        int packetType = data[0];
        if (packetType == 0x01 | packetType == 0x05) {
            // Mode S long data
            if (data.length < 19) {
                return null;
            }

            int[] modeSData = new int[14];
            for (int i = 0; i < 14; i++) {
                modeSData[i] = data[i + 5];
            }

            return ModeSMessage.parse(timestamp, latitude, longitude, altitude, modeSData);
        } else if (packetType == 0x07) {
            // Mode S short data
            if (data.length < 12) {
                return null;
            }

            int[] modeSData = new int[7];
            for (int i = 0; i < 7; i++) {
                modeSData[i] = data[i + 5];
            }

            return ModeSMessage.parse(timestamp, latitude, longitude, altitude, modeSData);
        } else if (packetType == 0x09) {
            // Mode AC data
            if (data.length < 7) {
                return null;
            }

            int[] modeCData = new int[2];
            for (int i = 0; i < 2; i++) {
                modeCData[i] = data[i + 5];
            }

            // todo - might want to change this to ModeACMessage.parse()
            return TransponderMessage.parse(timestamp, latitude, longitude, altitude, modeCData);
        }

        return null;
    }

    private static TransponderMessage parseSdr(String line) {
        if (line == null) {
            return null;
        }

        String[] split = line.trim().split("\t");
        if (split.length != 7) {
            return null;
        }

        String split0 = split[0].trim();
        String split1 = split[1].trim();
        String split2 = split[2].trim();
        String split3 = split[3].trim();
        String split4 = split[4].trim();
        String split5 = split[5].trim();
        String split6 = split[6].trim();

        if ((split0.length() < 10) |
                (split1.length() < 1) |
                (split2.length() < 1) |
                (split3.length() < 1) |
                (split4.length() < 1) |
                (split5.length() < 1) |
                (split6.length() < 1)) {
            return null;
        }

        long seconds = Long.parseLong(split0);
        long microSeconds = Long.parseLong(split1);
        Instant timestamp = Instant.ofEpochSecond(seconds, microSeconds * 1000);
        double latitude = Double.parseDouble(split2);
        double longitude = Double.parseDouble(split3);
        double altitude = Double.parseDouble(split4);
        int[] mlat = parseHexString(split5);
        int[] data = parseHexString(split6);

        return TransponderMessage.parse(timestamp, latitude, longitude, altitude, data);
    }

    private static int[] parseHexString(String hexString) {
        int length = (hexString.length() / 2) - 1;
        int data[] = new int[length];
        for (int i = 0; i < data.length; i++) {
            String byteString = hexString.substring((i * 2) + 2, (i * 2) + 4);
            data[i] = Integer.parseInt(byteString, 16);
        }
        return data;
    }

    /**
     * Reads the next aircraft surveillance message.
     *
     * @return the next AircraftSurveillanceMessage or null if no more messages are available
     * @throws IOException
     */
    public AircraftSurveillanceMessage read() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (!line.startsWith("#")) {
                AircraftSurveillanceMessage aircraftSurveillanceMessage = parse(line);
                if (aircraftSurveillanceMessage != null) {
                    return aircraftSurveillanceMessage;
                }
            }
        }
        return null;
    }

    /**
     * Closes the log file.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (bufferedReader != null)
            bufferedReader.close();
    }

}
