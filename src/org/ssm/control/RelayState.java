package org.ssm.control;

/**
 * Represents the State on the RelayBoard.
 */
public enum RelayState {

    /**
     * If the LED near to the relay on the board is ON, this is the RelayState.ON
     */
    ON,

    /**
     * If the LED near to the relay on the board is OFF, this is the RelayState.OFF
     */
    OFF;

    /**
     * Parses the given string into a RelayState.
     * It checks for numbers (0 == OFF and > 0 == ON)<br/>
     * It checks case insensitive for Strings ([ON|oN|On|on] = ON and [OFF|off|Off|...] = OFF)<br/>
     * It checks for boolean (true == ON and false == OFF)
     *
     * @param s the value to parse
     * @return The state of the given value, or OFF if nothing matched.
     */
    public static RelayState toState(String s) {

        try {
            return RelayState.valueOf(s.toUpperCase());
        } catch (ClassCastException e) {
            // is Not a State value like "ON", "on" or "OFF", "off";
        }

        // Is Integer in String?
        try {
            int i = Integer.valueOf(s);
            return i > 0 ? RelayState.ON : RelayState.OFF;
        } catch (NumberFormatException e) {
            // is not a number
        }

        // Is a boolean?
        try {
            boolean i = Boolean.valueOf(s);
            return i ? RelayState.ON : RelayState.OFF;
        } catch (NumberFormatException e) {
            // is not a number
        }

        // return default
        return OFF;
    }

    /**
     * This toggles the state.
     *
     * @return Returns the opposite/reverse state.
     */
    public RelayState toggle() {
        return this == ON ? OFF : ON;
    }

    /**
     * Gives a boolean representation of the state, where ON == true and OFF == false
     *
     * @return returns the state as boolean.
     */
    public boolean asBoolean() {
        return this == ON;
    }
}
