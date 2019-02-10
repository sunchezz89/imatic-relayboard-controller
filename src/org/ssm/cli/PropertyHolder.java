package org.ssm.cli;

import org.kohsuke.args4j.Option;
import org.ssm.control.RelayState;

/**
 * This class holds the properties for the commandline usage of the Controller.
 * It has default values which are used if none parameters are given.
 * The values are filled by the args4j Framework.
 * http://args4j.kohsuke.org/
 */

public class PropertyHolder {

    @Option(name = "-p", aliases = "--port", usage = "Sets the target port of the board.")
    int port = 3000;

    @Option(name = "-h", aliases = "--host", usage = "Sets the target host (IP, or qualified name).")
    String host = "192.168.178.1";

    @Option(name = "-a", aliases = "--action", usage = "Sets the action. Available actions: "
            + "\n set-all: set <state> to ALL relays on the board (ignores -r)"
            + "\n get-all: print states of all relays to standard output (ignores -r)"
            + "\n toggle-all: set the opposite state of the current of specified relay(s) (ignores -r) (FUTURE VERSION)"
            + "\n get-state: get state of relay(s) specified in --relay option (requires -r)"
            + "\n set-state: get state of relay(s) specified in --relay option (requires -r & -s)"
            + "\n toggle|toggle-state: set the opposite state of the current of specified relay(s) (requires -r)"
            + "\n touch: sets the relay(-r) to the given state(-s) for a given time(-t) (requires -r|s|t)"
    )
    String action = "get-all";


    @Option(name = "-r", aliases = {"--relay", "--relays"}, usage = "Set the number of relay(s) to interact with. Like '1,4,6'", handler = IntArrayOptionHandler.class)
    int[] relayNumbers = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

    @Option(name = "-s", aliases = "--state", usage = "Set the desired state for targeted relays.")
    RelayState state = RelayState.ON;

    @Option(name = "-t", aliases = "--touchTime", usage = "Set the time, relay(s) is/are touched.")
    int touchTime;

    @Option(name = "--timeout", usage = "Set connect timeout in milliseconds.")
    int timeout;

    @Option(name = "--debug", usage = "Sets to debugmode and prints full stacktraces.")
    boolean debug;

    @Option(name = "--help", usage = "Prints this usage help.")
    boolean help;

}
