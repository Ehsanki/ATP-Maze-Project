package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public int[][] getMaze(){
        return model.getMaze();
    }

    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyCode Code){
        MovementDirection direction;
        switch (Code){
            case UP, NUMPAD8 -> direction = MovementDirection.UP;
            case DOWN, NUMPAD2 -> direction = MovementDirection.DOWN;
            case LEFT, NUMPAD4 -> direction = MovementDirection.LEFT;
            case RIGHT, NUMPAD6 -> direction = MovementDirection.RIGHT;
            case  NUMPAD7 -> direction = MovementDirection.UPLEFT;
            case  NUMPAD9 -> direction = MovementDirection.UPRIGHT;
            case  NUMPAD3 -> direction = MovementDirection.DOWNRIGHT;
            case  NUMPAD1 -> direction = MovementDirection.DOWNLEFT;


            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }




    public void solveMaze(){
        model.solveMaze();
    }

    public void exit()
    {

        model.exit();
        System.exit(0);

    }

    public Maze getMazeObject() {
        return model.getMazeObject();
    }

    public void openMaze(File chosen) throws IOException {
        model.openMaze(chosen);

    }

    public void saveMAze(File chosen) {
        model.saveMaze(chosen);
    }
}
