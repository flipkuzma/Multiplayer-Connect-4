
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class ClientGUI extends Application {
    private EventHandler<ActionEvent> myHandler;

    Text startMsg;

    Scene welcomeScene, gameScene, endScene;
    Stage primaryStage;

    GridPane grid;

    ClientServer client;

    Text playerTurn;

    Text playerMove;
    Text endGameState;

    VBox playScene;

    Button continueGame;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);

    }

    //feel free to remove the starter code from this method
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // TODO Auto-generated method stub
        primaryStage.setTitle("Connect Four!");

        //Elements of teh Start and Game Scene
        Text welcomeText = new Text();
        welcomeText.setText("Enter Port Number");
        startMsg = new Text();
        startMsg.setText("");
        playerTurn = new Text();
        playerTurn.setText("Player 1's Turn");
        playerMove = new Text();
        playerMove.setText("Moved to: ");
        VBox textFields = new VBox(5,playerTurn, playerMove);
        textFields.setAlignment(Pos.TOP_CENTER);
        TextField portText = new TextField();
        portText.setPromptText("Port #: ");
        portText.setPrefSize(250,20);
        HBox portBox = new HBox(5,portText);
        portBox.setAlignment(Pos.CENTER);
        Button portEnter = new Button();
        portEnter.setText("Enter");
        VBox infoBox = new VBox(10, welcomeText, portBox, portEnter, startMsg);
        infoBox.setAlignment(Pos.CENTER);

        //Elements of the End Game Scene
        endGameState = new Text();
        continueGame = new Button();
        continueGame.setText("Continue");
        Button quitGame = new Button();
        quitGame.setText("Quit");
        quitGame.setPrefSize(100,10);
        continueGame.setPrefSize(100,10);
        HBox endSceneH = new HBox(20, continueGame, quitGame);
        endSceneH.setAlignment(Pos.CENTER);
        VBox endSceneV = new VBox(20,endGameState, endSceneH);
        endSceneV.setAlignment(Pos.CENTER);

        grid = new GridPane();
        //Creates new Grid
        GameBoard.createGrid(grid, this);

        BorderPane welcomeBP = new BorderPane();
        welcomeBP.setStyle("-fx-background-color: lightgray");
        welcomeBP.setCenter(infoBox);

        BorderPane bpGame = new BorderPane();
        playScene = new VBox(20, textFields, grid);
        bpGame.setStyle("-fx-background-image: url('bg.png');");
        bpGame.setCenter(playScene);

        BorderPane endBP = new BorderPane();
        endBP.setStyle("-fx-background-color: lightgray");
        endBP.setCenter(endSceneV);

        gameScene = new Scene(bpGame, 600, 600);

        welcomeScene = new Scene(welcomeBP, 400, 200);

        endScene = new Scene(endBP, 400, 200);

        primaryStage.setScene(welcomeScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> System.exit(0));

        quitGame.setOnAction(e ->{
            System.exit(0);
        });

        portEnter.setOnAction(e->{
            int port;
            try{
                port = Integer.parseInt(portText.getText());
                if(port < 1 || port > 65535){
                    throw new Exception();
                }
            }catch (Exception a){
                portText.setText("Invalid Number");
                return;
            }
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(), port);
                client = new ClientServer(socket, this);
                client.start();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        continueGame.setOnAction(e ->{
            endGameState.setText("Waiting For Other Player To Continue...");
            CFourInfo cfi = new CFourInfo("");
            try {
                client.send(cfi);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            continueGame.setDisable(true);

            client.cont = true;

            if (client.otherCont) {
                client.cfi = new CFourInfo("");
                paint(client.cfi);
                client.cfi.setState("PLAY");
                openGame();
                client.clientState = client.playerOne ? ClientState.MOVE : ClientState.AWAIT;
                continueGame.setDisable(false);
            }
        });
    }



    public Text getStartMsg(){
        return startMsg;
    }

    public void openGame(){
        primaryStage.setScene(gameScene);
    }

    public void openWelcome(){
        primaryStage.setScene(welcomeScene);
    }

    public void openEnd(){ primaryStage.setScene(endScene);}

    //Sets color for default grid
    public void paint(CFourInfo cfi){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                GameBoard.getButton(grid, i , j).setColor("white");
            }
        }
        //Sets player one button color
        for(short s: cfi.getP1Checkers()){
            int row = s/7;
            int col = s - (row * 7);
            GameBoard.getButton(grid, row, col).setColor("red");
        }
        //Sets player two button color
        for(short s: cfi.getP2Checkers()){
            int row = s/7;
            int col = s - (row * 7);
            GameBoard.getButton(grid, row, col).setColor("yellow");
        }
    }

    public ClientServer getClient() {
        return client;
    }

    public void click(int row, int col) throws IOException {
        if(client.clientState == ClientState.MOVE){
            //If illegal move
            if(!GameBoard.legalMove(row, col, client.cfi.getP1Checkers(), client.cfi.getP2Checkers())){
                playerMove.setText("Invalid Move");
                return;
            }

            ArrayList<Short> sqrs = GameBoard.checkWinner(client.playerOne ? client.cfi.getP1Checkers() : client.cfi.getP2Checkers(), row, col);

            //Sets if winner or tie
            if(sqrs != null){
                for (short s : sqrs) {
                    Platform.runLater(() -> ((GameButton) grid.getChildren().get(s)).setColor("black"));
                }

                client.cfi.setState("WON");
                new Thread(() -> {
                    Platform.runLater(() -> {
                        playerMove.setText("You Win, Congrats");
                    });
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        client.clientState = ClientState.OVER;
                        getStartMsg().setText("You Win, Congrats!");
                        endGameState.setText("You Win, Congrats!");
                        openEnd();
                    });

                }).start();


            }

            if (GameBoard.checkTie(client.cfi.getP1Checkers(), client.cfi.getP2Checkers())) {
                client.cfi.setState("TIE");

                new Thread(() -> {
                    Platform.runLater(() -> {
                        playerMove.setText("Game has Tied");
                    });

                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        client.clientState = ClientState.OVER;
                        getStartMsg().setText("You Tie, Congrats or not...");
                        endGameState.setText("You Tie, Congrats or not...!");
                        openEnd();
                    });

                }).start();
            }

            if(client.playerOne){
                client.cfi.getP1Checkers().add((short) (row * 7 + col));
                client.outputStream.writeObject(client.cfi);
                playerMove.setText("Moved to: row - " + row + " col - " + col);
            }else {
                client.cfi.getP2Checkers().add((short) (row * 7 + col));
                client.outputStream.writeObject(client.cfi);
                playerMove.setText("Moved to: row - " + row + " col - " + col);
            }

            paint(client.cfi);

            playerTurn.setText(client.playerOne ? "Player 2's Turn" : "Player 1's Turn");

            client.clientState = ClientState.AWAIT;
        }
    }

}
