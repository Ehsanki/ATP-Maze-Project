package Model;

import Client.*;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.*;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {
    private int[][] mazeMatrix;
    private int playerRow;
    private int playerCol;
    private Solution solution;
    Maze maze;
    private Server mazeGeneratingServer;
    private Server mazeSolvingSerever ;

    public MyModel() {
        mazeGeneratingServer=new Server(5400,1000,new ServerStrategyGenerateMaze());
        mazeSolvingSerever=new Server(5401,1000,new ServerStrategySolveSearchProblem());

        mazeGeneratingServer.start();
        mazeSolvingSerever.start();
    }
    public Maze getMazeObject(){return maze;}

    @Override
    public void openMaze(File chosen) throws IOException {


        byte[] savedMazeBytes;

        InputStream in = new MyDecompressorInputStream(new FileInputStream(chosen));
        savedMazeBytes = new byte[10000];
        in.read(savedMazeBytes);
        in.close();


        maze = new Maze(savedMazeBytes);
        mazeMatrix=maze.getMaze();
        solution=null;

        setChanged();

        notifyObservers("maze generated");
        movePlayer(0, 0);


    }




    @Override
    public void saveMaze(File chosen) {
        //TODO

        OutputStream out = null;
        try {
            out = new MyCompressorOutputStream(new FileOutputStream(chosen));
            out.write(maze.toByteArray());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void generateMaze(int rows, int cols) {


        try {

            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();

                        InputStream decompressorIS = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[compressedMaze.length*8+10];
                        decompressorIS.read(decompressedMaze);
                        Maze newMaze = new Maze(decompressedMaze);


                        maze=newMaze;
                        mazeMatrix=newMaze.getMaze();
                        solution=null;
                        setChanged();
                        notifyObservers("maze generated");
                        movePlayer(0,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int[][] getMaze() {
        return mazeMatrix;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        switch (direction) {
            case UP -> {
                if (playerRow > 0 && mazeMatrix[playerRow-1][playerCol]!=1)
                    movePlayer(playerRow - 1, playerCol);
            }
            case DOWN -> {
                if (playerRow < mazeMatrix.length - 1 && mazeMatrix[playerRow+1][playerCol]!=1)
                    movePlayer(playerRow + 1, playerCol);
            }
            case LEFT -> {
                if (playerCol > 0 && mazeMatrix[playerRow][playerCol-1]!=1)
                    movePlayer(playerRow, playerCol - 1 );
            }
            case RIGHT -> {
                if (playerCol < mazeMatrix  [0].length - 1 && mazeMatrix[playerRow][playerCol+1]!=1)
                    movePlayer(playerRow, playerCol + 1);
            }
            case UPLEFT -> {
                if(maze.getIndexVal(playerRow-1,playerCol-1)==0 && (maze.getIndexVal(playerRow,playerCol-1)==0 || maze.getIndexVal(playerRow-1,playerCol)==0))
                    movePlayer(playerRow-1,playerCol-1);


            }
            case UPRIGHT -> {
                if(maze.getIndexVal(playerRow-1,playerCol+1)==0 && (maze.getIndexVal(playerRow,playerCol+1)==0  || maze.getIndexVal(playerRow-1,playerCol)==0))
                    movePlayer(playerRow-1,playerCol+1);

            }
            case DOWNLEFT -> {
                if(maze.getIndexVal(playerRow+1,playerCol-1)==0 &&(maze.getIndexVal(playerRow,playerCol-1)==0 ||maze.getIndexVal(playerRow+1,playerCol)==0 ))
                    movePlayer(playerRow+1,playerCol-1);

            }
            case DOWNRIGHT -> {
                if(maze.getIndexVal(playerRow+1,playerCol+1)==0 && (maze.getIndexVal(playerRow,playerCol+1)==0   || maze.getIndexVal(playerRow+1,playerCol)==0 ) )
                    movePlayer(playerRow+1,playerCol+1);

            }

        }



    }

    private void movePlayer(int row, int col){
        this.playerRow = row;
        this.playerCol = col;

        setChanged();
        notifyObservers("player moved");

        if(playerRow==maze.getGoalPosition().getRowIndex()&&playerCol==maze.getGoalPosition().getColumnIndex())
        {
            setChanged();

            notifyObservers("player won");
            generateMaze(maze.getRowsSize(),maze.getColumnsSize());

        }
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze() {
        //solve the maze

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();

                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read the solution from the server
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                                 // draw the path from the start place to the goal

                        solution = mazeSolution;
                        setChanged();
                        notifyObservers("maze solved");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }




    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    public void exit(){
        mazeGeneratingServer.stop();
        mazeSolvingSerever.stop();
    }
}
