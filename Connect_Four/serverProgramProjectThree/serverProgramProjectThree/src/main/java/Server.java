import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server extends Thread {

    ClientThread c1 = null, c2 = null;
    int portNum;
    GameState state;

    ServerGUI serverGUI;

    public Server(int port, ServerGUI serverGUI) {
        this.portNum = port;
        state = GameState.GAME_EMPTY;
        this.serverGUI = serverGUI;
    }


    public void run() {
        try {
            ServerSocket mysocket = new ServerSocket(portNum);

            while (true) {
                Thread.sleep(10);
                Socket s = mysocket.accept();

                if (c1 == null) {
                    ClientThread c = new ClientThread(s, true, this);
                    c.start();
                    c1 = c;
                    state = GameState.ONE_PLAYER;
                    Platform.runLater(() -> serverGUI.addInfo("Client #1 has connected."));
                    if (c2 != null) {
                        //Game Starts
                        Platform.runLater(() -> serverGUI.addInfo("Both players connect, the game has begun"));
                        CFourInfo cFourInfo = new CFourInfo("START");

                        while (!c1.ready) {
                            Thread.sleep(100);
                        }

                        c2.sendInfo(cFourInfo);
                        state = GameState.P1;
                    }
                } else if (c2 == null) {
                    ClientThread c = new ClientThread(s, false, this);
                    Platform.runLater(() -> serverGUI.addInfo("Client #2 has connected."));
                    c.start();
                    c2 = c;
                    state = GameState.P1;
                    Platform.runLater(() -> serverGUI.addInfo("Both players have connected, the game has begun"));
                    CFourInfo cFourInfo = new CFourInfo("START");

                    while (!c2.ready) {
                        Thread.sleep(100);
                    }

                    c1.sendInfo(cFourInfo);
                    c2.sendInfo(cFourInfo);
                } else {
                    //Game is full
                    s.close();
                }
            }
        } catch (Exception e) {

        }

    }//end of while

    public void handleDisconnect(Socket connection) throws Exception{
        if(c1 != null && c1.connection == connection){
            c1 = null;
        } else if(c2 != null && c2.connection == connection){
            c2 = null;
        }
        if(state != GameState.GAME_EMPTY && state != GameState.ONE_PLAYER){
            if(c1 == null){
                CFourInfo cfi = new CFourInfo("STOP");
                c2.sendInfo(cfi);
            }else {
                CFourInfo cfi = new CFourInfo("STOP");
                c1.sendInfo(cfi);
            }
            state = GameState.ONE_PLAYER;
        }else {
            state = GameState.GAME_EMPTY;
        }
    }

    class ClientThread extends Thread {

        Server server;
        Socket connection;
        ObjectInputStream in;
        ObjectOutputStream out;
        boolean playerOne;

        boolean ready = false;

        ClientThread(Socket s, boolean p1, Server server) {
            this.connection = s;
            this.playerOne = p1;
            this.server = server;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                connection.setTcpNoDelay(true);
                CFourInfo cfi = new CFourInfo("P1");
                cfi.getP1Checkers().add((short) (playerOne ? 1 : 0));
                out.writeObject(cfi);
                out.flush();
                ready = true;
                //Will constantly check for CFourInfo object being sent towards server
                while (true) {
                    Object object = in.readObject();
                    cfi = (CFourInfo) object;

                    if (playerOne) {
                        server.c2.sendInfo(cfi);
                    } else {
                        server.c1.sendInfo(cfi);
                    }
                    CFourInfo finalCfi = cfi;
                    //Displays move made by player
                    if (cfi.getState() == "PLAY") {
                        Platform.runLater(() -> serverGUI.addInfo("Player #" +
                                (playerOne ? "1" : "2") + " made move on block " + (playerOne ?
                                finalCfi.getP1Checkers().get(finalCfi.getP1Checkers().size()-1) : finalCfi.getP2Checkers().get(finalCfi.getP2Checkers().size()-1))));
                    }
                    //Displays winner
                    if(cfi.getState().equals("WON")) {
                        Platform.runLater(() -> serverGUI.addInfo("Player #" + (playerOne ? "1" : "2") + " has won"));
                    }
                    //Displays if tie game
                    if(cfi.getState().equals("TIE")){
                        Platform.runLater(() -> serverGUI.addInfo("The game ends in a Tie"));
                    }
                }

            } catch (Exception e) {
                try {
                    server.handleDisconnect(connection);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                Platform.runLater(() -> serverGUI.addInfo("Client #" + (playerOne ? "1" : "2") + " has disconnected."));
            }
        }
        public void sendInfo(CFourInfo cfi) throws IOException {
            out.writeObject(cfi);
            out.flush();
        }
    }
}







