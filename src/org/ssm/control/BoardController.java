package org.ssm.control;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.BitSet;

/**
 * @author Sunchezz
 * @version 1.0
 * @since 09.02.2019
 */
public class BoardController {

    /**
     * The hostname of the iMatic board.
     * This can be an IP or fully qualified name, according to {@link InetSocketAddress#InetSocketAddress(String, int)}.
     */
    private String host;

    /**
     * The port of the iMatic board.
     * This can be an IP or fully qualified name, according to {@link InetSocketAddress#InetSocketAddress(String, int)}.
     */
    private int port;

    /**
     * The Socket which will be used to connect to the Board.
     */
    private Socket tcpSocket;


    /**
     * This constructor creates an instance of the Board controller, for the given host and port.
     * Internally it creates a raw socket, without connecting to the host.
     * You need to explicitly call {@link #connect()}
     *
     * @param host The hostname of the board {@link #host}.
     * @param port The port of the board.
     */
    public BoardController(String host, int port) {
        this.port = port;
        this.host = host;
        this.tcpSocket = new Socket();
    }

    /**
     * Connect the controller to the Board.
     *
     * @throws IOException See {@link Socket#connect(SocketAddress)}
     */
    public void connect() throws IOException {
        SocketAddress address = new InetSocketAddress(host, port);
        this.tcpSocket.connect(address);
    }

    /**
     * Connect the controller to the Board.
     * Throws an exception if not connected and timeout reached.
     *
     * @throws IOException See {@link Socket#connect(SocketAddress)}
     */
    public void connect(int timeout) throws IOException {
        SocketAddress address = new InetSocketAddress(host, port);
        this.tcpSocket.connect(address, timeout);
    }

    /**
     * closes the Socket.
     *
     * @throws IOException See {@link Socket#close()}
     */
    public void close() throws IOException {
        this.tcpSocket.close();
    }

    /**
     * Sends a command to the Board to set ALL Relays to the given State.
     * The return value is an array of states, ordered by relay numbers.
     * As a result, all States in the array should be the same like the given parameter
     *
     * @param onOff The State to switch the relays to.
     * @return returns an array of states
     */
    public RelayState[] setAllRelay(RelayState onOff) {
        byte[] command;
        if (onOff == RelayState.OFF) {
            command = new byte[]{88, 1, 19, 0, 0, 0, 0, 108};//ON for relay 1
        } else {
            command = new byte[]{88, 1, 19, 0, 0, (byte) 255, (byte) 255, 106};//ON for relay 1

        }
        sendCmd(command);
        return getAllStates();
    }

    /**
     * Sets the states for the given relays to 'onOff', by calling {@link #setRelay(int, RelayState)} for every given relaynumber.
     *
     * @param relayNumbers the relay numbers, to switch the state for.
     * @param onOff        the state which the relays should have.
     */
    public void setRelay(int[] relayNumbers, RelayState onOff) {

        for (int relayNumber : relayNumbers) {
            setRelay(relayNumber, onOff);
        }
    }

    /**
     * Sets the State of the given relay to the given State.
     * If the state on the board is ON, and a ON-state is send, nothing will change.
     *
     * @param relayNumber the relay which should change.
     * @param onOff       The desired state of the given relay.
     */
    public void setRelay(int relayNumber, RelayState onOff) {

        byte[] command = new byte[]{88, 1, 0, 0, 0, 0, 1, 107};//ON for relay 1
        command[6] = (byte) (command[6] + relayNumber);
        command[7] = (byte) (107 + relayNumber);

        if (onOff == RelayState.OFF) {
            //we want to set relay to OFF
            command[2] = 17;
        } else {
            //We want to set relay to ON
            command[7] += 1;
            command[2] = 18;
        }

        sendCmd(command);
    }

    /**
     * Switches the current state of the requested relays.
     * Calls internally {@link #setRelay(int, RelayState)} with the reversed state.
     * <br/>Example:<br/>
     * States on the Board before: {@code OFF,OFF,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON}<br/>
     * Parameter: {@code [0,1,15]}<br/>
     * States on the board after: {@code ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,ON,OFF}<br/>
     *
     * @param relayNumbers The relay numbers to toggle.
     */
    public void toggleRelay(int[] relayNumbers) {

        RelayState[] currentStates = getAllStates();
        for (int relayNumber : relayNumbers) {
            RelayState toggledState = currentStates[relayNumber];
            toggledState = toggledState.toggle();

            setRelay(relayNumber, toggledState);
        }
    }

    /**
     * Returns the current State for the given relay numbers as array.
     * If the requested relays are {1,4,6}, you receive an result array with also 3 entries in same order.
     *
     * @param relayNumbers the numbers for which we request the state.
     * @return the array as an result
     */
    public RelayState[] getRelayStates(int[] relayNumbers) {
        RelayState[] states = getAllStates();
        RelayState[] result = new RelayState[relayNumbers.length];

        for (int i = 0; i < relayNumbers.length; i++) {
            result[i] = states[relayNumbers[i]];
        }
        return result;
    }

    /**
     * Sends a request to the board to receive all states of the relays.
     * The Result is an ordered array of states.
     * State of Relay#1 is in array[0].
     * ...
     * State of Relay#16 is in array[15].
     *
     * @return the result as an array of states ordered from 0-15
     */
    public RelayState[] getAllStates() {
        // create an array with 16 entrys, where all entrys are OFF.
        RelayState[] states = new RelayState[16];
        Arrays.fill(states, RelayState.OFF);

        try {
            // skip possible income bytes, which we do not want
            if (tcpSocket.getInputStream().available() > 0) {
                tcpSocket.getInputStream().skip(tcpSocket.getInputStream().available());
            }


            // command to request all relays which are currently in state ON.
            byte[] command = new byte[]{88, 1, 16, 0, 0, 0, 0, 105};
            sendCmd(command);

            // receive the response
            byte[] responseBuffer = new byte[8];
            tcpSocket.getInputStream().read(responseBuffer);

            // Response contains 8 bytes where byte 5 & 6 represents the exact relay states. Make a reverse of both and convert into a Bit representation (BitSet).
            // Every entry in the BitSet contains the number of a relay which is turned on.
            BitSet stati = BitSet.valueOf(new byte[]{responseBuffer[6], responseBuffer[5]});

            // Set the ON state in the result array for alle entrys in the BitSet.
            stati.stream().forEach(pos -> states[pos] = RelayState.ON);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return states;
    }

    private void sendCmd(byte[] cmd) {
        try {
            tcpSocket.getOutputStream().write(cmd);
            tcpSocket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
