package aircraftsurveillance.transponder.adsb1090;

public class Adsb1090ParseException extends Exception {

    public Adsb1090ParseException() {
        super();
    }

    public Adsb1090ParseException(String message) {
        super(message);
    }

    public Adsb1090ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public Adsb1090ParseException(Throwable cause) {
        super(cause);
    }

}
