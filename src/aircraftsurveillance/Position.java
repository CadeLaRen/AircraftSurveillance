package aircraftsurveillance;

public class Position {
    private double latitude;
    private double longitude;

    private static final double a = 6378137.0; // semi-major axis in meters
    private static final double b = 6356752.314245; // semi-minor axis in meters
    private static final double f = 1 / 298.257223563; // flattening in meters
    private static final double r = 6367435.679716; // approximation of earth radius in meters

    private static final double eps = 1e-13;
    private static final int iterationLimit = 25;

    private static final double degree2radian = (Math.PI / 180.0);
    private static final double radian2degree = (180.0 / Math.PI);

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Position(Position position) {
        latitude = position.latitude;
        longitude = position.longitude;
    }

    public Position(AircraftSurveillanceMessage aircraftSurveillanceMessage) {
        latitude = aircraftSurveillanceMessage.getReceiverLatitude();
        longitude = aircraftSurveillanceMessage.getReceiverLongitude();
    }

    public Position(AircraftState aircraftState) {
        latitude = aircraftState.getLatitude();
        longitude = aircraftState.getLongitude();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }

        Position position = (Position) object;
        if (latitude != position.latitude) {
            return false;
        }
        if (longitude != position.longitude) {
            return false;
        }

        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Position");
        sb.append(System.lineSeparator());
        sb.append("Latitude: " + latitude);
        sb.append(System.lineSeparator());
        sb.append("Longitude: " + longitude);

        return sb.toString();
    }

    private static class Result {
        public Position position1;
        public Position position2;
        public double course12;  // great circle course from position1 to position2
        public double course21;  // great circle course from position2 to position1
        public double distance;  // meters
        public int iterations;
    }

    public static class IterationLimitExceeded extends Exception {

    }

    /**
     * @param position1
     * @param course
     * @param distance  in meters
     * @return result of the direct calculation
     * @throws IterationLimitExceeded
     */
    private static Result direct(Position position1, double course, double distance) throws IterationLimitExceeded {
        Result result = new Result();
        result.position1 = position1;
        result.distance = distance;
        result.course12 = course;

        double sinAlpha1 = Math.sin(course * degree2radian);
        double cosAlpha1 = Math.cos(course * degree2radian);

        double tanU1 = (1 - f) * Math.tan(position1.getLatitude() * degree2radian);
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        double sinAlpha = cosU1 * sinAlpha1;
        double cosSquaredAlpha = (1 - sinAlpha * sinAlpha);
        double usquared = cosSquaredAlpha * ((a * a) - (b * b)) / (b * b);
        double A = 1 + usquared / 16384 * (4096 + usquared * (-768 + usquared * (320 - 175 * usquared)));
        double B = usquared / 1024 * (256 + usquared * (-128 + usquared * (74 - 47 * usquared)));

        double sigma = distance / (b * A);
        double sinSigma = Math.sin(sigma);
        double cosSigma = Math.cos(sigma);
        double sigmaPrevious = Double.MAX_VALUE;
        double sigma1 = Math.atan2(tanU1, cosAlpha1);
        double cos2SigmaM = Math.cos(2 * sigma1 + sigma);

        int i = 0;
        while ((Math.abs(sigmaPrevious - sigma) > eps) & (i < iterationLimit)) {
            cos2SigmaM = Math.cos(2 * sigma1 + sigma);
            sinSigma = Math.sin(sigma);
            cosSigma = Math.cos(sigma);
            double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (2 * cos2SigmaM * cos2SigmaM - 1) - B / 6 * cos2SigmaM * (4 * sinSigma * sinSigma - 3) * (4 * cos2SigmaM * cos2SigmaM - 3)));
            sigmaPrevious = sigma;
            sigma = (distance / (b * A)) + deltaSigma;
            i++;
        }
        result.iterations = i;

        if (i >= iterationLimit) {
            // failed to converge
            throw new IterationLimitExceeded();
        }

        double y = sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1;
        double x = (1 - f) * Math.sqrt(sinAlpha * sinAlpha + (sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1) * (sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1));
        double latitude = Math.atan2(y, x) * radian2degree;

        double lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1);
        double C = f / 16 * cosSquaredAlpha * (4 + f * (4 - 3 * cosSquaredAlpha));
        double L = lambda - (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (2 * cos2SigmaM * cos2SigmaM - 1)));
        double longitude = ((position1.getLongitude() * degree2radian + L + 3 * Math.PI) % (2 * Math.PI) - Math.PI) * radian2degree;

        result.position2 = new Position(latitude, longitude);
        result.course21 = (Math.atan2(sinAlpha, cosU1 * cosSigma * cosAlpha1 - sinU1 * sinSigma) + Math.PI) * radian2degree;

        return result;
    }

    /**
     * @param position1
     * @param position2
     * @return result of the inverse calculation
     * @throws IterationLimitExceeded
     */
    private static Result inverse(Position position1, Position position2) throws IterationLimitExceeded {
        Result result = new Result();
        result.position1 = position1;
        result.position2 = position2;

        double L = (position2.getLongitude() * degree2radian) - (position1.getLongitude() * degree2radian);
        double tanU1 = (1 - f) * Math.tan(position1.getLatitude() * degree2radian);
        double cosU1 = 1 / Math.sqrt(1 + tanU1 * tanU1);
        double sinU1 = tanU1 * cosU1;
        double tanU2 = (1 - f) * Math.tan(position2.getLatitude() * degree2radian);
        double cosU2 = 1 / Math.sqrt(1 + tanU2 * tanU2);
        double sinU2 = tanU2 * cosU2;

        double cosLambda = 0;
        double sinLambda = 0;
        double sinSigma = 0;
        double cosSigma = 0;
        double sigma = 0;
        double cosSquaredAlpha = 0;
        double cos2SigmaM = 0;

        double lambda = L;
        double lambdaPrevious = Double.MAX_VALUE;
        int i = 0;
        while ((Math.abs(lambdaPrevious - lambda) > eps) & (i < iterationLimit)) {
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSquaredAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSquaredAlpha;
            double C = f / 16 * cosSquaredAlpha * (4 + f * (4 - 3 * cosSquaredAlpha));
            lambdaPrevious = lambda;
            lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (2 * cos2SigmaM * cos2SigmaM - 1)));
            i++;
        }
        result.iterations = i;

        if (i >= iterationLimit) {
            // failed to converge
            throw new IterationLimitExceeded();
        }

        double usquared = cosSquaredAlpha * ((a * a) - (b * b)) / (b * b);
        double A = 1 + usquared / 16384 * (4096 + usquared * (-768 + usquared * (320 - 175 * usquared)));
        double B = usquared / 1024 * (256 + usquared * (-128 + usquared * (74 - 47 * usquared)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (2 * cos2SigmaM * cos2SigmaM - 1) - B / 6 * cos2SigmaM * (4 * sinSigma * sinSigma - 3) * (4 * cos2SigmaM * cos2SigmaM - 3)));
        result.distance = b * A * (sigma - deltaSigma);
        result.course12 = (Math.atan2(cosU2 * sinLambda, cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) + 2 * Math.PI) % (2 * Math.PI) * radian2degree;
        result.course21 = (Math.atan2(cosU1 * sinLambda, -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda) + Math.PI) % (2 * Math.PI) * radian2degree;

        return result;
    }

    /**
     * @param a
     * @param b
     * @return distance between a and b in meters
     * @throws IterationLimitExceeded
     */
    public static double distance(Position a, Position b) throws IterationLimitExceeded {
        if (a.equals(b)) {
            return 0;
        } else {
            Result result = inverse(a, b);
            return result.distance;
        }
    }

    /**
     * @param a
     * @param b
     * @return the bearing from a to b in degrees
     * @throws IterationLimitExceeded
     */
    public static double course(Position a, Position b) throws IterationLimitExceeded {
        Result result = inverse(a, b);
        return result.course12;
    }

    /**
     * @param a        starting position
     * @param bearing  in degrees
     * @param distance in meters
     * @return a new position the specified distance and bearing from the starting position
     * @throws IterationLimitExceeded
     */
    public static Position createPosition(Position a, double bearing, double distance) throws IterationLimitExceeded {
        Result result = direct(a, bearing, distance);
        return result.position2;
    }

    /**
     * @param bearing  in degrees
     * @param distance in meters
     * @return a new position the specified distance and bearing from this position
     * @throws IterationLimitExceeded
     */
    public Position createPosition(double bearing, double distance) throws IterationLimitExceeded {
        return createPosition(this, bearing, distance);
    }

}
