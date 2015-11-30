package aircraftsurveillance;

import aircraftsurveillance.transponder.adsb1090.CompactPositionReport;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CPRDecoder {

    private static final double[] nlTable = new double[]{
            10.4704713000, 14.8281743687, 18.1862635707, 21.0293949260, 23.5450448656, 25.8292470706, 27.9389871012,
            29.9113568573, 31.7720970768, 33.5399343630, 35.2289959780, 36.8502510759, 38.4124189241, 39.9225668433,
            41.3865183226, 42.8091401224, 44.1945495142, 45.5462672266, 46.8673325250, 48.1603912810, 49.4277643926,
            50.6715016555, 51.8934246917, 53.0951615280, 54.2781747227, 55.4437844450, 56.5931875621, 57.7274735387,
            58.8476377615, 59.9545927669, 61.0491777425, 62.1321665921, 63.2042747938, 64.2661652257, 65.3184530968,
            66.3617100838, 67.3964677408, 68.4232202208, 69.4424263114, 70.4545107499, 71.4598647303, 72.4588454473,
            73.4517744167, 74.4389341573, 75.4205625665, 76.3968439079, 77.3678946133, 78.3337408292, 79.2942822546,
            80.2492321328, 81.1980134927, 82.1395698051, 83.0719944472, 83.9917356298, 84.8916619070, 85.7554162094,
            86.5353699751, 87.0000000000
    };

    public static Position decodeGlobalAirbornePositionEven(CompactPositionReport cprEven, CompactPositionReport cprOdd) {
        if (cprEven == null) {
            return null;
        }
        if (cprOdd == null) {
            return null;
        }

        if (!cprEven.isEvenPosition()) {
            return null;
        }
        if (!cprOdd.isOddPosition()) {
            return null;
        }

        if (cprEven.getEncodedLatitude() == 0 | cprEven.getEncodedLongitude() == 0) {
            return null;
        }
        if (cprOdd.getEncodedLatitude() == 0 | cprOdd.getEncodedLatitude() == 0) {
            return null;
        }

        int yz0 = cprEven.getEncodedLatitude();
        int xz0 = cprEven.getEncodedLongitude();
        int yz1 = cprOdd.getEncodedLatitude();
        int xz1 = cprOdd.getEncodedLongitude();

        double j = Math.floor((((59 * yz0) - (60 * yz1)) / Math.pow(2, 17)) + 0.5);
        double dlat0 = 360.0 / 60.0;
        double dlat1 = 360.0 / 59.0;

        double rlat0 = dlat0 * (mod(j, 60) + (yz0 / Math.pow(2, 17)));
        double rlat1 = dlat1 * (mod(j, 59) + (yz1 / Math.pow(2, 17)));

        if (nl(rlat0) != nl(rlat1)) {
            return null;
        }
        int nl = nl(rlat1);

        double m = Math.floor(((((nl - 1) * xz0) - (nl * xz1)) / Math.pow(2, 17)) + 0.5);

        double dlon0 = 360.0;
        if (nl > 0) {
            dlon0 /= nl;
        }

        double rlon0 = dlon0 * (mod(m, nl) + (xz0 / Math.pow(2, 17)));

        rlat0 = mod(rlat0 + 180.0, 360.0) - 180.0;
        rlon0 = mod(rlon0 + 180.0, 360.0) - 180.0;

        if (Double.isNaN(rlat0)) {
            return null;
        }
        if (Double.isNaN(rlon0)) {
            return null;
        }

        return new Position(rlat0, rlon0);
    }

    public static Position decodeGlobalAirbornePositionOdd(CompactPositionReport cprEven, CompactPositionReport cprOdd) {
        if (cprEven == null) {
            return null;
        }
        if (cprOdd == null) {
            return null;
        }

        if (!cprEven.isEvenPosition()) {
            return null;
        }
        if (!cprOdd.isOddPosition()) {
            return null;
        }

        if (cprEven.getEncodedLatitude() == 0 | cprEven.getEncodedLongitude() == 0) {
            return null;
        }
        if (cprOdd.getEncodedLatitude() == 0 | cprOdd.getEncodedLatitude() == 0) {
            return null;
        }

        int yz0 = cprEven.getEncodedLatitude();
        int xz0 = cprEven.getEncodedLongitude();
        int yz1 = cprOdd.getEncodedLatitude();
        int xz1 = cprOdd.getEncodedLongitude();

        double j = Math.floor((((59 * yz0) - (60 * yz1)) / Math.pow(2, 17)) + 0.5);
        double dlat0 = 360.0 / 60.0;
        double dlat1 = 360.0 / 59.0;

        double rlat0 = dlat0 * (mod(j, 60) + (yz0 / Math.pow(2, 17)));
        double rlat1 = dlat1 * (mod(j, 59) + (yz1 / Math.pow(2, 17)));

        if (nl(rlat0) != nl(rlat1)) {
            return null;
        }
        int nl = nl(rlat1);

        double m = Math.floor(((((nl - 1) * xz0) - (nl * xz1)) / Math.pow(2, 17)) + 0.5);

        double dlon1 = 360.0;
        if (nl - 1 > 0) {
            dlon1 /= (nl - 1);
        }

        double rlon1 = dlon1 * (mod(m, nl - 1) + (xz1 / Math.pow(2, 17)));

        rlat1 = mod(rlat1 + 180.0, 360.0) - 180.0;
        rlon1 = mod(rlon1 + 180.0, 360.0) - 180.0;

        if (Double.isNaN(rlat1)) {
            return null;
        }
        if (Double.isNaN(rlon1)) {
            return null;
        }

        return new Position(rlat1, rlon1);
    }


    public static Position decodeGlobalSurfacePositionEven(CompactPositionReport cprEven, CompactPositionReport cprOdd, Position localPosition) {
        if (cprEven == null) {
            return null;
        }
        if (cprOdd == null) {
            return null;
        }

        if (!cprEven.isEvenPosition()) {
            return null;
        }
        if (!cprOdd.isOddPosition()) {
            return null;
        }

        if (cprEven.getEncodedLatitude() == 0 | cprEven.getEncodedLongitude() == 0) {
            return null;
        }
        if (cprOdd.getEncodedLatitude() == 0 | cprOdd.getEncodedLatitude() == 0) {
            return null;
        }

        int yz0 = cprEven.getEncodedLatitude();
        int xz0 = cprEven.getEncodedLongitude();
        int yz1 = cprOdd.getEncodedLatitude();
        int xz1 = cprOdd.getEncodedLongitude();

        double j = Math.floor((((59 * yz0) - (60 * yz1)) / Math.pow(2, 17)) + 0.5);
        double dlat0 = 90.0 / 60.0;
        double dlat1 = 90.0 / 59.0;

        double rlat0 = dlat0 * (mod(j, 60) + (yz0 / Math.pow(2, 17)));
        double rlat1 = dlat1 * (mod(j, 59) + (yz1 / Math.pow(2, 17)));

        if (nl(rlat0) != nl(rlat1)) {
            return null;
        }
        int nl = nl(rlat1);

        double m = Math.floor(((((nl - 1) * xz0) - (nl * xz1)) / Math.pow(2, 17)) + 0.5);

        double dlon0 = 90.0;
        if (nl > 0) {
            dlon0 /= nl;
        }

        double rlon0 = dlon0 * (mod(m, nl) + (xz0 / Math.pow(2, 17)));

        if (Double.isNaN(rlat0)) {
            return null;
        }
        if (Double.isNaN(rlon0)) {
            return null;
        }

        return bestSurfacePosition(rlat0, rlon0, localPosition);
    }

    public static Position decodeGlobalSurfacePositionOdd(CompactPositionReport cprEven, CompactPositionReport cprOdd, Position localPosition) {
        if (cprEven == null) {
            return null;
        }
        if (cprOdd == null) {
            return null;
        }

        if (!cprEven.isEvenPosition()) {
            return null;
        }
        if (!cprOdd.isOddPosition()) {
            return null;
        }

        if (cprEven.getEncodedLatitude() == 0 | cprEven.getEncodedLongitude() == 0) {
            return null;
        }
        if (cprOdd.getEncodedLatitude() == 0 | cprOdd.getEncodedLatitude() == 0) {
            return null;
        }

        int yz0 = cprEven.getEncodedLatitude();
        int xz0 = cprEven.getEncodedLongitude();
        int yz1 = cprOdd.getEncodedLatitude();
        int xz1 = cprOdd.getEncodedLongitude();

        double j = Math.floor((((59 * yz0) - (60 * yz1)) / Math.pow(2, 17)) + 0.5);
        double dlat0 = 90.0 / 60.0;
        double dlat1 = 90.0 / 59.0;

        double rlat0 = dlat0 * (mod(j, 60) + (yz0 / Math.pow(2, 17)));
        double rlat1 = dlat1 * (mod(j, 59) + (yz1 / Math.pow(2, 17)));

        if (nl(rlat0) != nl(rlat1)) {
            return null;
        }
        int nl = nl(rlat1);

        double m = Math.floor(((((nl - 1) * xz0) - (nl * xz1)) / Math.pow(2, 17)) + 0.5);

        double dlon1 = 90.0;
        if (nl - 1 > 0) {
            dlon1 /= (nl - 1);
        }

        double rlon1 = dlon1 * (mod(m, nl) + (xz1 / Math.pow(2, 17)));

        if (Double.isNaN(rlat1)) {
            return null;
        }
        if (Double.isNaN(rlon1)) {
            return null;
        }

        return bestSurfacePosition(rlat1, rlon1, localPosition);
    }


    public static Position decodeLocalAirbornePosition(CompactPositionReport cpr, Position localPosition) {
        if (cpr == null) {
            return null;
        }

        if (cpr.getEncodedLatitude() == 0 | cpr.getEncodedLongitude() == 0) {
            return null;
        }

        int i = cpr.getCprFormat() ? 1 : 0;
        int yz = cpr.getEncodedLatitude();
        int xz = cpr.getEncodedLongitude();

        double latS = localPosition.getLatitude();
        double lonS = localPosition.getLongitude();

        double dlat = 360.0 / (60.0 - i);
        double j = Math.floor(latS / dlat) + Math.floor(0.5 + (mod(latS, dlat) / dlat) - (yz / Math.pow(2, 17)));
        double rlat = dlat * (j + (yz / Math.pow(2, 17)));

        double dLon = 360.0;
        if ((nl(rlat) - i) > 0)
            dLon /= (nl(rlat) - i);
        double m = Math.floor(lonS / dLon) + Math.floor(0.5 + (mod(lonS, dLon) / dLon) - (xz / Math.pow(2, 17)));
        double rlon = dLon * (m + (xz / Math.pow(2, 17)));

        rlat = mod(rlat + 180.0, 360.0) - 180.0;
        rlon = mod(rlon + 180.0, 360.0) - 180.0;

        if (Double.isNaN(rlat)) {
            return null;
        }
        if (Double.isNaN(rlon)) {
            return null;
        }

        return new Position(rlat, rlon);
    }

    public static Position decodeLocalSurfacePosition(CompactPositionReport cpr, Position localPosition) {
        if (cpr == null) {
            return null;
        }

        if (cpr.getEncodedLatitude() == 0 | cpr.getEncodedLongitude() == 0) {
            return null;
        }

        int i = cpr.getCprFormat() ? 1 : 0;
        int yz = cpr.getEncodedLatitude();
        int xz = cpr.getEncodedLongitude();

        double latS = localPosition.getLatitude();
        double lonS = localPosition.getLongitude();

        double dlat = 90.0 / (60.0 - i);
        double j = Math.floor(latS / dlat) + Math.floor(0.5 + (mod(latS, dlat) / dlat) - (yz / Math.pow(2, 17)));
        double rlat = dlat * (j + (yz / Math.pow(2, 17)));

        double dLon = 90.0;
        if ((nl(rlat) - i) > 0)
            dLon /= (nl(rlat) - i);
        double m = Math.floor(lonS / dLon) + Math.floor(0.5 + (mod(lonS, dLon) / dLon) - (xz / Math.pow(2, 17)));
        double rlon = dLon * (m + (xz / Math.pow(2, 17)));

        rlat = mod(rlat + 180.0, 360.0) - 180.0;
        rlon = mod(rlon + 180.0, 360.0) - 180.0;

        if (Double.isNaN(rlat)) {
            return null;
        }
        if (Double.isNaN(rlon)) {
            return null;
        }

        return new Position(rlat, rlon);
    }


    private static int nl(double latitude) {
        for (int i = 0; i < 58; i++) {
            if (latitude < nlTable[i])
                return (59 - i);
        }
        return 1;
    }

    private static double mod(double x, double y) {
        return x - y * Math.floor(x / y);
    }

    private static Position bestSurfacePosition(double rlat, double rlon, final Position localPosition) {
        List<Position> list = new LinkedList<Position>();

        // northern hemisphere
        list.add(new Position(rlat, rlon));
        list.add(new Position(rlat, rlon + 90.0));
        list.add(new Position(rlat, rlon - 180.0));
        list.add(new Position(rlat, rlon - 90.0));

        // southern hemisphere
        list.add(new Position(rlat - 90.0, rlon));
        list.add(new Position(rlat - 90.0, rlon + 90.0));
        list.add(new Position(rlat - 90.0, rlon - 180.0));
        list.add(new Position(rlat - 90.0, rlon - 90.0));

        // return the position closest to localPosition
        list.sort(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                try {
                    double distance1 = Position.distance(localPosition, o1);
                    double distance2 = Position.distance(localPosition, o2);
                    return Double.compare(distance1, distance2);
                } catch (Position.IterationLimitExceeded iterationLimitExceeded) {
                }
                return 0;
            }
        });

        return list.get(0);
    }

}
