import javafx.scene.control.Button;

import java.awt.event.ActionListener;
import java.io.IOException;

public class GameButton extends Button {

    int row;
    int col;



    public GameButton(int row, int col, ClientGUI gui){
        this.row = row;
        this.col = col;

        setStyle("-fx-background-color: white");

        setOnAction(e ->{
            try {
                gui.click(row, col);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void setColor(String color){
        setStyle("-fx-background-color: " + color);
    }
}
