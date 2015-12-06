package aircraftsurveillance.transponder.adsb1090;

/**
 * Purpose: To provide aircraft identification and category.
 */

/* 1-5   Format type code
 * 6-8   Aircraft emitter category
 * 9-14  Character 1
 * 15-20 Character 2
 * 21-26 Character 3
 * 27-32 Character 4
 * 33-38 Character 5
 * 39-44 Character 6
 * 45-50 Character 7
 * 51-56 Character 8
 *
 * Type coding
 * 1 = Aircraft identification, Category Set D
 * 2 = Aircraft identification, Category Set C
 * 3 = Aircraft identification, Category Set B
 * 4 = Aircraft identification, Category Set A
 *
 * ADS-B Aircraft emitter category coding
 *
 * Set A
 * 0 = No ADS-B emitter category information
 * 1 = Light (< 15,500 lbs)
 * 2 = Small (15,500 to 75,000 lbs)
 * 3 = Large (75000 to 300000 lbs)
 * 4 = High vortex large (aircraft such as B-757)
 * 5 = Heavy (> 300,000 lbs)
 * 6 = High performance (>5g acceleration and 400 kts)
 * 7 = Rotorcraft
 *
 * Set B
 * 0 = No ADS-B emitter category information
 * 1 = Glider / sailplane
 * 2 = Lighter-than-air
 * 3 = Parachutist / Skydiver
 * 4 = Ultralight / hang-glider / paraglider
 * 5 = Reserved
 * 6 = Unmanned Aerial Vehicle
 * 7 =  Space / Trans-atmospheric vehicle
 *
 * Set C
 * 0 = No ADS-B emitter category information
 * 1 = Surface vehicle - Emergency vehicle
 * 2 = Surface vehicle - Service vehicle
 * 3 = Point obstacle (includes tethered balloons)
 * 4 = Cluster obstacle
 * 5 = Line obstacle
 * 6 = Reserved
 * 7 = Reserved
 *
 * Set D (reserved)
 */

public class IdentificationAndCategory extends Adsb1090Message {

    public enum EmitterCategory {
        NO_INFORMATION_SET_A,
        LIGHT,
        SMALL,
        LARGE,
        HIGH_VORTEX_LARGE,
        HEAVY,
        HIGH_PERFORMANCE,
        ROTORCRAFT,

        NO_INFORMATION_SET_B,
        GLIDER_SAILPLANE,
        LIGHTER_THAN_AIR,
        PARACHUTIST_SKYDIVER,
        ULTRALIGHT_HANGGLIDER_PARAGLIDER,
        RESERVED_5_SET_B,
        UNMANNED_AERIAL_VEHICLE,
        SPACE_TRANSATMOSPHERIC_VEHICLE,

        NO_INFORMATION_SET_C,
        SURFACE_EMERGENCY_VEHICLE,
        SURFACE_SERVICE_VEHICLE,
        POINT_OBSTACLE,
        CLUSTER_OBSTACLE,
        LINE_OBSTACLE,
        RESERVED_6_SET_C,
        RESERVED_7_SET_C,

        RESERVED_0_SET_D,
        RESERVED_1_SET_D,
        RESERVED_2_SET_D,
        RESERVED_3_SET_D,
        RESERVED_4_SET_D,
        RESERVED_5_SET_D,
        RESERVED_6_SET_D,
        RESERVED_7_SET_D
    }

    private EmitterCategory emitterCategory;
    private char character1;
    private char character2;
    private char character3;
    private char character4;
    private char character5;
    private char character6;
    private char character7;
    private char character8;

    private IdentificationAndCategory() {
    }

    /**
     * Decodes 7 bytes of data into an ADS-B Identification and Category message.
     *
     * @param data an int array containing the 7 bytes to be decoded
     * @return the decoded ADS-B Identification and Category message
     * @throws Adsb1090ParseException
     */
    public static IdentificationAndCategory parse(int[] data) throws Adsb1090ParseException {
        // verify the data is not null
        if (data == null) {
            throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): data == null");
        }

        // verify the data is the correct length
        if (data.length != 7) {
            throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): data.length != 7 (data.length == " + data.length + ")");
        }

        IdentificationAndCategory message = new IdentificationAndCategory();
        message.originalMessage = data.clone();

        // verify the type code
        message.typeCode = extractInt(data, 0, 5);
        // valid type codes are 1-4
        if ((message.typeCode < 1) | (message.typeCode > 4)) {
            throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        message.subtypeCode = -1;

        int emitterCategory = extractInt(data, 5, 3);
        if (message.typeCode == 4) {
            if (emitterCategory == 0) {
                message.emitterCategory = EmitterCategory.NO_INFORMATION_SET_A;
            } else if (emitterCategory == 1) {
                message.emitterCategory = EmitterCategory.LIGHT;
            } else if (emitterCategory == 2) {
                message.emitterCategory = EmitterCategory.SMALL;
            } else if (emitterCategory == 3) {
                message.emitterCategory = EmitterCategory.LARGE;
            } else if (emitterCategory == 4) {
                message.emitterCategory = EmitterCategory.HIGH_VORTEX_LARGE;
            } else if (emitterCategory == 5) {
                message.emitterCategory = EmitterCategory.HEAVY;
            } else if (emitterCategory == 6) {
                message.emitterCategory = EmitterCategory.HIGH_PERFORMANCE;
            } else if (emitterCategory == 7) {
                message.emitterCategory = EmitterCategory.ROTORCRAFT;
            } else {
                throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): emitter category is not valid (type code == " + message.typeCode + ", emitter category == " + emitterCategory + ")");
            }
        } else if (message.typeCode == 3) {
            if (emitterCategory == 0) {
                message.emitterCategory = EmitterCategory.NO_INFORMATION_SET_B;
            } else if (emitterCategory == 1) {
                message.emitterCategory = EmitterCategory.GLIDER_SAILPLANE;
            } else if (emitterCategory == 2) {
                message.emitterCategory = EmitterCategory.LIGHTER_THAN_AIR;
            } else if (emitterCategory == 3) {
                message.emitterCategory = EmitterCategory.PARACHUTIST_SKYDIVER;
            } else if (emitterCategory == 4) {
                message.emitterCategory = EmitterCategory.ULTRALIGHT_HANGGLIDER_PARAGLIDER;
            } else if (emitterCategory == 5) {
                message.emitterCategory = EmitterCategory.RESERVED_5_SET_B;
            } else if (emitterCategory == 6) {
                message.emitterCategory = EmitterCategory.UNMANNED_AERIAL_VEHICLE;
            } else if (emitterCategory == 7) {
                message.emitterCategory = EmitterCategory.SPACE_TRANSATMOSPHERIC_VEHICLE;
            } else {
                throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): emitter category is not valid (type code == " + message.typeCode + ", emitter category == " + emitterCategory + ")");
            }
        } else if (message.typeCode == 2) {
            if (emitterCategory == 0) {
                message.emitterCategory = EmitterCategory.NO_INFORMATION_SET_C;
            } else if (emitterCategory == 1) {
                message.emitterCategory = EmitterCategory.SURFACE_EMERGENCY_VEHICLE;
            } else if (emitterCategory == 2) {
                message.emitterCategory = EmitterCategory.SURFACE_SERVICE_VEHICLE;
            } else if (emitterCategory == 3) {
                message.emitterCategory = EmitterCategory.POINT_OBSTACLE;
            } else if (emitterCategory == 4) {
                message.emitterCategory = EmitterCategory.CLUSTER_OBSTACLE;
            } else if (emitterCategory == 5) {
                message.emitterCategory = EmitterCategory.LINE_OBSTACLE;
            } else if (emitterCategory == 6) {
                message.emitterCategory = EmitterCategory.RESERVED_6_SET_C;
            } else if (emitterCategory == 7) {
                message.emitterCategory = EmitterCategory.RESERVED_7_SET_C;
            } else {
                throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): emitter category is not valid (type code == " + message.typeCode + ", emitter category == " + emitterCategory + ")");
            }
        } else if (message.typeCode == 1) {
            if (emitterCategory == 0) {
                message.emitterCategory = EmitterCategory.RESERVED_0_SET_D;
            } else if (emitterCategory == 1) {
                message.emitterCategory = EmitterCategory.RESERVED_1_SET_D;
            } else if (emitterCategory == 2) {
                message.emitterCategory = EmitterCategory.RESERVED_2_SET_D;
            } else if (emitterCategory == 3) {
                message.emitterCategory = EmitterCategory.RESERVED_3_SET_D;
            } else if (emitterCategory == 4) {
                message.emitterCategory = EmitterCategory.RESERVED_4_SET_D;
            } else if (emitterCategory == 5) {
                message.emitterCategory = EmitterCategory.RESERVED_5_SET_D;
            } else if (emitterCategory == 6) {
                message.emitterCategory = EmitterCategory.RESERVED_6_SET_D;
            } else if (emitterCategory == 7) {
                message.emitterCategory = EmitterCategory.RESERVED_7_SET_D;
            } else {
                throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): emitter category is not valid (type code == " + message.typeCode + ", emitter category == " + emitterCategory + ")");
            }
        } else {
            throw new Adsb1090ParseException("IdentificationAndCategory.parse(data): type code is not valid (type code == " + message.typeCode + ")");
        }

        message.character1 = decodeCharacter(extractInt(data, 8, 6));
        message.character2 = decodeCharacter(extractInt(data, 14, 6));
        message.character3 = decodeCharacter(extractInt(data, 20, 6));
        message.character4 = decodeCharacter(extractInt(data, 26, 6));
        message.character5 = decodeCharacter(extractInt(data, 32, 6));
        message.character6 = decodeCharacter(extractInt(data, 38, 6));
        message.character7 = decodeCharacter(extractInt(data, 44, 6));
        message.character8 = decodeCharacter(extractInt(data, 50, 6));

        return message;
    }

    /**
     * @return a String representing the ADS-B Identification and Category message
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("IdentificationAndCategory");
        sb.append(System.lineSeparator());
        sb.append("original message = 0x");
        for (int i : originalMessage) {
            sb.append(String.format("%02x", i));
        }
        sb.append(System.lineSeparator());
        sb.append("type code = " + typeCode);
        sb.append(System.lineSeparator());
        sb.append("sub type code = " + subtypeCode);
        sb.append(System.lineSeparator());
        sb.append("emitter category = " + emitterCategory);
        sb.append(System.lineSeparator());
        sb.append("character1 = " + character1);
        sb.append(System.lineSeparator());
        sb.append("character2 = " + character2);
        sb.append(System.lineSeparator());
        sb.append("character3 = " + character3);
        sb.append(System.lineSeparator());
        sb.append("character4 = " + character4);
        sb.append(System.lineSeparator());
        sb.append("character5 = " + character5);
        sb.append(System.lineSeparator());
        sb.append("character6 = " + character6);
        sb.append(System.lineSeparator());
        sb.append("character7 = " + character7);
        sb.append(System.lineSeparator());
        sb.append("character8 = " + character8);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    /**
     * @return aircraft emitter category
     */
    public EmitterCategory getEmitterCategory() {
        return emitterCategory;
    }

    /**
     * @return 1st identification character
     */
    public char getCharacter1() {
        return character1;
    }

    /**
     * @return 2nd identification character
     */
    public char getCharacter2() {
        return character2;
    }

    /**
     * @return 3rd identification character
     */
    public char getCharacter3() {
        return character3;
    }

    /**
     * @return 4th identification character
     */
    public char getCharacter4() {
        return character4;
    }

    /**
     * @return 5th identification character
     */
    public char getCharacter5() {
        return character5;
    }

    /**
     * @return 6th identification character
     */
    public char getCharacter6() {
        return character6;
    }

    /**
     * @return 7th identification character
     */
    public char getCharacter7() {
        return character7;
    }

    /**
     * @return 8th identification character
     */
    public char getCharacter8() {
        return character8;
    }

    /**
     * @return all of the identification characters appended together
     */
    public String getCharactersAsString() {
        StringBuilder sb = new StringBuilder();

        sb.append(character1);
        sb.append(character2);
        sb.append(character3);
        sb.append(character4);
        sb.append(character5);
        sb.append(character6);
        sb.append(character7);
        sb.append(character8);

        return sb.toString();
    }

    /**
     * @param encodedCharacter encoded character
     * @return decoded character
     */
    private static char decodeCharacter(int encodedCharacter) throws Adsb1090ParseException {
        switch (encodedCharacter) {
            case 1:
                return 'A';
            case 2:
                return 'B';
            case 3:
                return 'C';
            case 4:
                return 'D';
            case 5:
                return 'E';
            case 6:
                return 'F';
            case 7:
                return 'G';
            case 8:
                return 'H';
            case 9:
                return 'I';
            case 10:
                return 'J';
            case 11:
                return 'K';
            case 12:
                return 'L';
            case 13:
                return 'M';
            case 14:
                return 'N';
            case 15:
                return 'O';
            case 16:
                return 'P';
            case 17:
                return 'Q';
            case 18:
                return 'R';
            case 19:
                return 'S';
            case 20:
                return 'T';
            case 21:
                return 'U';
            case 22:
                return 'V';
            case 23:
                return 'W';
            case 24:
                return 'X';
            case 25:
                return 'Y';
            case 26:
                return 'Z';
            case 32:
                return ' ';
            case 48:
                return '0';
            case 49:
                return '1';
            case 50:
                return '2';
            case 51:
                return '3';
            case 52:
                return '4';
            case 53:
                return '5';
            case 54:
                return '6';
            case 55:
                return '7';
            case 56:
                return '8';
            case 57:
                return '9';
            default:
                throw new Adsb1090ParseException("IdentificationAndCategory.decodeCharacter(encodedCharacter == " + encodedCharacter + "): encoded character is not valid");
        }
    }

}
