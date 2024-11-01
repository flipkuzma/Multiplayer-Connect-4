import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientServer extends Thread {

    private Socket socket;
    private ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    private ClientGUI clientGUI;

    boolean playerOne;
    ClientState clientState;

    CFourInfo cfi;

    boolean otherCont, cont;


    public ClientServer(Socket socket, ClientGUI clientGUI) {
        this.socket = socket;
        this.clientGUI = clientGUI;
        this.cfi = new CFourInfo("PLAY");
        this.otherCont = this.cont = false;
    }

    //Will constantly check for CFourInfo object being sent towards client
    @Override
    public void run() {
        try {
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.flush();
            this.inputStream = new ObjectInputStream(this.socket.getInputStream());
            Object object = inputStream.readObject();
            CFourInfo cfi = (CFourInfo) object;
            playerOne = cfi.getP1Checkers().get(0) == 1;
            clientState = ClientState.CONNECTED;
            Platform.runLater(() -> clientGUI.getStartMsg().setText("Waiting for player #" + (playerOne ? "2" : "1")));


        } catch (IOException a) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Thread.sleep(10);
                CFourInfo cfi = (CFourInfo) inputStream.readObject();

                if (cfi.getState().equals("STOP")) {
                    this.cfi = new CFourInfo("PLAY");
                    clientGUI.paint(this.cfi);
                    Platform.runLater(() -> {
                        clientGUI.getStartMsg().setText("Opponent disconnected waiting for new player.");
                        clientGUI.openWelcome();
                    });
                } else if (cfi.getState().equals("START")) {
                    clientState = playerOne ? ClientState.MOVE : ClientState.AWAIT;
                    Platform.runLater(() -> clientGUI.openGame());

                } else if (clientState == ClientState.AWAIT) {
                    this.cfi = cfi;
                    CFourInfo finalCfi = cfi;
                    Platform.runLater(() -> {
                        clientGUI.paint(finalCfi);
                        clientGUI.playerTurn.setText("Your Turn!");
                    });
                    clientState = ClientState.MOVE;

                    if (cfi.getState().equals("WON") || cfi.getState().equals("TIE")) {
                        clientState = ClientState.OVER;
                        Thread.sleep(300);
                        if (cfi.getState().equals("TIE")) {
                            clientGUI.playerMove.setText("Game has Tied");
                            Thread.sleep(4000);
                            Platform.runLater(() -> {
                                clientGUI.getStartMsg().setText("Tie Game... sad");
                                clientGUI.endGameState.setText("Tie Game... sad");
                                clientGUI.openEnd();
                            });
                        }
                        if (cfi.getState().equals("WON")) {


                            short move = playerOne ? cfi.getP2Checkers().get(cfi.getP2Checkers().size() - 1) : cfi.getP1Checkers().get(cfi.getP1Checkers().size() - 1);
                            ArrayList<Short> sqrs = GameBoard.checkWinner(playerOne ? cfi.getP2Checkers() : cfi.getP1Checkers(), move / 7, move - ((move / 7) * 7));

                            for (short s : sqrs) {
                                Platform.runLater(() -> ((GameButton) clientGUI.grid.getChildren().get(s)).setColor("black"));
                            }

                            clientGUI.playerMove.setText("You Lose");
                            Thread.sleep(4000);
                            Platform.runLater(() -> {
                                clientGUI.getStartMsg().setText("You Lose, sucks...");
                                clientGUI.endGameState.setText("You Lose, sucks...");
                                clientGUI.openEnd();
                            });
                        }
                    }
                } else if (clientState == ClientState.OVER) {
                    Platform.runLater(() -> clientGUI.endGameState.setText("Opponent continued."));

                    otherCont = true;

                    if (cont) {
                        Platform.runLater(() -> {
                            this.cfi = new CFourInfo("");
                            clientGUI.paint(this.cfi);
                            this.cfi.setState("PLAY");
                            clientGUI.openGame();
                            clientState = playerOne ? ClientState.MOVE : ClientState.AWAIT;
                            clientGUI.continueGame.setDisable(false);
                        });

                    }
                }
                //Once a win or tie is reached change window for both clients

            } catch (Exception a) {
            }
        }
    }

    //Sends CFourInfo object to server
    public void send(CFourInfo cfi) throws Exception {
        outputStream.writeObject(cfi);
        outputStream.flush();
    }

    public Socket getSocket() {
        return socket;
    }

}
