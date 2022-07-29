package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.io.File;
import java.io.IOException;
import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);
    int[][] getMaze();
    void updatePlayerLocation(MovementDirection direction);
    int getPlayerRow();
    int getPlayerCol();
    void assignObserver(Observer o);
    void solveMaze();
    Solution getSolution();
    void exit();

    Maze getMazeObject();

    void openMaze(File chosen) throws IOException;

    void saveMaze(File chosen);
}
