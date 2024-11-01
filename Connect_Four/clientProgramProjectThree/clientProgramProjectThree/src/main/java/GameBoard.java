import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class GameBoard{

    public static void createGrid(GridPane grid, ClientGUI gui) {
        for(int r = 0; r < 6; r++){
            for(int c = 0; c < 7; c++){
               GameButton gButton = new GameButton(r, c, gui);
                gButton.setMinSize(40,40);
                grid.setHgap(8);
                grid.setVgap(8);
                grid.add(gButton,c, r);
            }
        }
        grid.setPadding(new Insets(150, 50, 15, 100));
    }

    public static GameButton getButton(GridPane grid, int row, int col){
        return (GameButton) grid.getChildren().get(row * 7 + col);
    }

    public static boolean legalMove(int row, int col, ArrayList<Short> move1, ArrayList<Short> move2){

        //Checks if checker is already played in the button
        if(move1.contains((short) (row * 7 + col)) || move2.contains((short) (row * 7 + col))){
            return false;
        }
        int r = row + 1;
        while(r <= 5){
            //If empty space below not allow
            if(!move1.contains((short) (r * 7 + col)) && !move2.contains((short)(r * 7 + col))){
                return false;
            }
            r++;
        }
        return true;
    }

    public static boolean checkTie(ArrayList<Short> moves1, ArrayList<Short> moves2){
        return moves1.size() + moves2.size() == 41;
    }

    public static ArrayList<Short> checkWinner(ArrayList<Short> moves, int row, int col){
        ArrayList<Short> sqrs;

        sqrs = checkSide(moves, row, col);
        if(sqrs != null){
            return sqrs;
        }

        sqrs = checkVertical(moves, row, col);
        if(sqrs != null){
            return sqrs;
        }

        sqrs = checkDiag1(moves, row, col);
        if(sqrs != null){
            return sqrs;
        }

        sqrs = checkDiag2(moves, row, col);
        if(sqrs != null){
            return sqrs;
        }

        return null;
    }

    public static ArrayList<Short> checkSide(ArrayList<Short> moves, int row, int col){
        int count = 1;
        int tempCol = col - 1;
        ArrayList<Short> sqrs = new ArrayList<>();
        sqrs.add((short) (row * 7 + col));

        while(tempCol >= 0){
            if(moves.contains((short) (row * 7 + tempCol))){
                count++;
                sqrs.add((short) (row * 7 + tempCol));

                if(count == 4) return sqrs;
            } else{
                break;
            }
            tempCol--;
        }
        tempCol = col + 1;
        while(tempCol <= 6){
            if(moves.contains((short) (row * 7 + tempCol))){
                count++;
                sqrs.add((short) (row * 7 + tempCol));

                if(count == 4) return sqrs;
            } else{
                break;
            }
            tempCol++;
        }

        return null;
    }
    public static ArrayList<Short> checkVertical(ArrayList<Short> moves, int row, int col){
        int count = 1;
        int tempRow = row + 1;
        ArrayList<Short> sqrs = new ArrayList<>();
        sqrs.add((short) (row * 7 + col));

        while(tempRow <= 5){
         if(moves.contains((short) (tempRow * 7 + col))){
             count++;
             sqrs.add((short) (tempRow * 7 + col));

             if(count == 4) return sqrs;
         } else {
             break;
         }
         tempRow++;
        }
        return null;
    }
    //Check for Diag /
    public static ArrayList<Short> checkDiag1(ArrayList<Short> moves, int row, int col){
        int count = 1;
        int tempRow = row + 1;
        int tempCol = col - 1;
        ArrayList<Short> sqrs = new ArrayList<>();
        sqrs.add((short) (row * 7 + col));

        while(tempCol >= 0 && tempRow <= 5){
            if(moves.contains((short) (tempRow * 7 + tempCol))){
                count++;
                sqrs.add((short) (tempRow * 7 + tempCol));

                if(count == 4) return sqrs;
            } else {
                break;
            }

            tempRow++;
            tempCol--;
        }
        tempRow = row - 1;
        tempCol = col + 1;
        while (tempCol <= 6 && tempRow >= 0){
            if(moves.contains((short) (tempRow * 7 + tempCol))){
                count++;
                sqrs.add((short) (tempRow * 7 + tempCol));

                if(count == 4) return sqrs;
            } else {
                break;
            }
            tempRow--;
            tempCol++;
        }
        return null;
    }
    //Check for Diag \
    public static ArrayList<Short> checkDiag2(ArrayList<Short> moves, int row, int col){
        int count = 1;
        int tempRow = row + 1;
        int tempCol = col + 1;
        ArrayList<Short> sqrs = new ArrayList<>();
        sqrs.add((short) (row * 7 + col));

        while(tempCol <= 6  && tempRow <= 5){
            if(moves.contains((short) (tempRow * 7 + tempCol))){
                count++;
                sqrs.add((short) (tempRow * 7 + tempCol));

                if(count == 4) return sqrs;
            } else {
                break;
            }
            tempRow++;
            tempCol++;
        }
        tempRow = row - 1;
        tempCol = col - 1;
        while (tempCol >= 0 && tempRow >= 0){
            if(moves.contains((short) (tempRow * 7 + tempCol))){
                count++;
                sqrs.add((short) (tempRow * 7 + tempCol));

                if(count == 4) return sqrs;
            } else {
                break;
            }
            tempRow--;
            tempCol--;
        }
        return null;
    }

}
