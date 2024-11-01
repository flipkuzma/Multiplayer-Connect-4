import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

public class ServerGUI extends Application {

    Scene startScene;
    Scene scene2;
    BorderPane startPane;
    BorderPane secondPane;
    VBox portBox;
    VBox startBox;
    VBox mainBox;
    VBox secondWindow;

    ListView<String> clientConnected;
    String portNumber;

    ListView<String> listView1;

    ActionEvent startAction;

    Server server;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);

    }

    //Intro Scene
    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("Welcome To the Server!");

        Button startButton = new Button();
        startButton.setText("Start Server");
        startButton.setDisable(true);
        Button portButton = new Button();
        portButton.setText("Continue");
        clientConnected = new ListView<>();



        TextField portText = new TextField();
        portText.setPromptText("Enter Port: ");

        Text textAbovePort = new Text();
        textAbovePort.setText("Enter Port # Then Press Continue");
        Text welcomeText = new Text();
        welcomeText.setText("Connect 4 Server!");

        mainBox = new VBox(20,welcomeText);
        portBox = new VBox(5,textAbovePort, portText, portButton);
        startBox = new VBox( 50, mainBox, portBox, startButton);

        portBox.setMaxWidth(100); portButton.setMaxWidth(100); startButton.setMaxWidth(100);
        startPane = new BorderPane();
        startPane.setStyle("-fx-background-color: lightgray");

        mainBox.setAlignment(Pos.TOP_CENTER);
        portBox.setAlignment(Pos.CENTER);
        startBox.setAlignment(Pos.CENTER);

        startPane.setCenter(startBox);
        startScene = new Scene(startPane, 400, 400) ;

        primaryStage.setScene(startScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> System.exit(0));

        //Port Button Pressed, grabs port number
        portButton.setOnAction(actionEvent -> {
            portNumber = portText.getText();
            int port;
            try{
                port = Integer.parseInt(portNumber);
                if(port < 1 || port > 65535){
                    throw new Exception();
                }
            }catch (Exception e){
                portText.setText("Invalid Number");
                return;
            }

            //Checks if port # is empty
                portButton.setDisable(true);
                startButton.setDisable(false);
                portText.setEditable(false);
        });

        //Start Button Pressed, creates a new scene with listview and data
        startButton.setOnAction(actionEvent -> {
            server = new Server(Integer.parseInt(portText.getText()), this);
            server.start();
              Text listText = new Text();
              listText.setText("Server Information");

              listView1 = new ListView<String>();
              listView1.setEditable(false);
              listView1.setPrefSize(500,500);
              listView1.setStyle("-fx-background-color: lightgray");
               clientConnected.setStyle("-fx-background-color: lightgray");clientConnected.setPrefSize(500,500);
              clientConnected.setEditable(false);
              secondWindow = new VBox(20, listText, clientConnected, listView1);
              secondWindow.setAlignment(Pos.CENTER);
              secondPane = new BorderPane();
              secondPane.setCenter(secondWindow);

              scene2 = new Scene(secondPane, 600, 600);
              primaryStage.setScene(scene2);
              primaryStage.show();
        });
    }

    public void addInfo(String string){
        clientConnected.getItems().add(string);
    }

    public void addMoves(String string){
        //client
    }

}