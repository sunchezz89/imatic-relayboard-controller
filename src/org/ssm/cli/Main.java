package org.ssm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.ssm.control.BoardController;
import org.ssm.control.RelayState;

import java.io.IOException;

/**
 * Main-entrypoint for command line usage.
 * Interprets command line arguments and instantiate the @{@link BoardController} with given parameters.
 * Also responsible for commandline result output.
 */
public class Main {

    private static String version = "1.0.1";

    public static void main(String[] args) {
        PropertyHolder parameter = new PropertyHolder();
        CmdLineParser parser = new CmdLineParser(parameter);
        try {
            // Get Parameter and put into parameter
            parser.parseArgument(args);
            if (parameter.help) {
                printUsage(parser, null);
            } else {
                runApi(parameter);
            }
        } catch (CmdLineException e) {
            // handling of wrong arguments
            printUsage(parser, e);
        } catch (Exception e) {
            if (parameter.debug) {
                e.printStackTrace();
            } else {
                System.err.println("Error: " + e.getMessage());
            }
            System.exit(1);
        }
    }

    private static void runApi(PropertyHolder properties) throws IOException {

        // Create the instance of the BoardController with the given parameter
        BoardController controller = new BoardController(properties.host, properties.port);

        // connect to the board, with timeout given.
        controller.connect(properties.timeout);

        RelayState[] result;
        switch (properties.action) {
            case "set-all":
                // sets and retrieve the states for all relays and print
                result = controller.setAllRelay(properties.state);
                printAllRelayStates(result);
                break;
            case "get-all":
                // retrieve the states and print
                result = controller.getAllStates();
                printAllRelayStates(result);
                break;
            case "set-state":
                // Set the relays to the given state
                controller.setRelay(properties.relayNumbers, properties.state);

                // wait for relays to switch, for asccurate states.
                waitForRelays(properties.relayNumbers.length);

                // retrieve the states for given relays and print
                printCurrentStates(controller, properties.relayNumbers);
                break;
            case "touch":
                // Set the relays to the given state for a given time
                controller.touchRelayTimed(properties.relayNumbers, properties.state, properties.touchTime);

                // wait for relays to switch, for asccurate states.
                waitForRelays(properties.relayNumbers.length);

                // retrieve the states for given relays and print
                printCurrentStates(controller, properties.relayNumbers);
                break;
            case "get-state":
                // retrieve the states for given relays and print
                result = controller.getRelayStates(properties.relayNumbers);
                printRelayStates(properties.relayNumbers, result);
                break;
            case "toggle-state":
            case "toggle":
                // retrieve the state of every given relay, and sets the opposite.
                controller.toggleRelay(properties.relayNumbers);

                // wait for relays to switch, for asccurate states.
                waitForRelays(properties.relayNumbers.length);

                // retrieve the states for given relays and print
                printCurrentStates(controller, properties.relayNumbers);
                break;
            default:
                System.out.println("unknown action. use --help for usage hints.");
        }
        controller.close();
    }

    private static void printCurrentStates(BoardController controller, int[] relayNumbers) {
        RelayState[] result = controller.getRelayStates(relayNumbers);
        printRelayStates(relayNumbers, result);
    }

    private static void printAllRelayStates(RelayState[] states) {
        for (int i = 0; i < states.length; i++) {
            printRelayState((i), states[i]);
        }
    }

    private static void printRelayStates(int[] numbers, RelayState[] states) {
        for (int i = 0; i < states.length; i++) {
            printRelayState((numbers[i]), states[i]);
        }
    }

    private static void printRelayState(int realRelayNumber, RelayState state) {
        System.out.println(realRelayNumber + 1 + ": " + state);
    }

    private static void printUsage(CmdLineParser parser, Exception e) {
        if (e != null) {
            System.err.println(e.getMessage());
        }

        System.out.println("\n----- Relay Controller v" + version + " -----\n");
        parser.printUsage(System.err);
    }

    private static void waitForRelays(int count) {
        try {
            // Wait for every relay 500ms to be switched
            // If we do not wait, the result is maybe incorrect.
            Thread.sleep(500 * count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}