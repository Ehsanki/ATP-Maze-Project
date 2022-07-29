package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;


public class MyViewController implements Initializable, Observer {
    public MyViewModel viewModel;
    public Button solve_BTN;
    public Pane pane;


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;


    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
        mazeDisplayer.widthProperty().bind(pane.widthProperty());
        mazeDisplayer.heightProperty().bind(pane.heightProperty());






    }

    @FXML
     public void generateMaze(ActionEvent actionEvent) {

        try {
            int rows = Integer.valueOf(textField_mazeRows.getText());
            int cols = Integer.valueOf(textField_mazeColumns.getText());
            solve_BTN.setDisable(false);

            viewModel.generateMaze(rows, cols);
        }
        catch(NumberFormatException e )
        {

            Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid ROWS / COLUMNS Value. ");
            alert.setTitle("ERROR");
            alert.setHeaderText("INVALID INPUT!");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStyleClass().add("myDialog");
            Optional<ButtonType> result = alert.showAndWait();

            return;
        }


    }

    public void solveMaze(ActionEvent actionEvent) {
        if(mazeDisplayer.mazeObject==null)
        {

            Alert alert = new Alert(Alert.AlertType.ERROR,"MAZE NOT GENERATED YET ! ");
            alert.setTitle("EMPTY MAZE ERROR");
            alert.setHeaderText("EMPTY MAZE ERROR");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStyleClass().add("myDialog");
            Optional<ButtonType> result = alert.showAndWait();

            return;
        }

        viewModel.solveMaze();
    }

    public void openFile(ActionEvent actionEvent) throws IOException {

        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null); // Byte Maze File

        //...
        if(chosen!=null)
        {
            viewModel.openMaze(chosen);

        }
        else
        {}




    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent.getCode());
        keyEvent.consume();
    }

    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);


    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();
            case "player won" -> gameDone();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void gameDone() {



        String path =System.getProperty("user.dir") + "/resources/images/win.gif";

        Image win = null;
        try {
            win = new Image(new FileInputStream(path));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ImageView winGif =new ImageView( win);

        winGif.setFitWidth(600);
        winGif.setFitHeight(400);



        Pane pane=new Pane();
        Scene scene = new Scene(pane, 600, 400);
        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setTitle("MISSION ACCOMPLISHED !");


        winGif.setImage(win);
        pane.getChildren().addAll(winGif);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();







    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());





    }

    private void mazeGenerated() {
       // mazeDisplayer.drawMaze(viewModel.getMaze());
        mazeDisplayer.setSolution(null);
        mazeDisplayer.drawMaze(viewModel.getMaze(),viewModel.getMazeObject());
    }

    public void ExitFunc(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION,"Are you sure you want to exit?");
        alert.setTitle("Exit");
        alert.setHeaderText("Exit");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("myDialog");
        Optional<ButtonType> result = alert.showAndWait();

        try {
            if (result.get() == ButtonType.OK) {
                // player press OK
                // Close the program
                viewModel.exit();
            }
        }
        catch (RuntimeException x)
        {
            //Close Button
            return;
        }

        actionEvent.consume();



    }

    public void mouseScrolling(ScrollEvent scrollEvent) {

        if(scrollEvent.isControlDown()) {
            double zoomFactor = 1.05;
            double deltaY = scrollEvent.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomFactor);
            mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomFactor);
        }
    }


    public void mouseDrag(MouseEvent mouseEvent) {
        if(viewModel.getMaze() != null) {
            int maximumSize = Math.max(viewModel.getMaze()[0].length, viewModel.getMaze().length);
            double mousePosX= mouseDragPos(maximumSize,mazeDisplayer.getHeight(),
                    viewModel.getMaze().length,mouseEvent.getX(),mazeDisplayer.getWidth() / maximumSize);
            double mousePosY= mouseDragPos(maximumSize,mazeDisplayer.getWidth(),
                    viewModel.getMaze()[0].length,mouseEvent.getY(),mazeDisplayer.getHeight() / maximumSize);
            if ( mousePosX == viewModel.getPlayerCol() && mousePosY < viewModel.getPlayerRow() )
                viewModel.movePlayer(KeyCode.UP);
            else if (mousePosY == viewModel.getPlayerRow() && mousePosX > viewModel.getPlayerCol() )
                viewModel.movePlayer(KeyCode.RIGHT);
            else if ( mousePosY == viewModel.getPlayerRow() && mousePosX < viewModel.getPlayerCol() )
                viewModel.movePlayer(KeyCode.LEFT);
            else if (mousePosX == viewModel.getPlayerCol() && mousePosY > viewModel.getPlayerRow()  )
                viewModel.movePlayer(KeyCode.DOWN);

        }
    }
    private  double mouseDragPos(int maxsize, double canvasSize, int mazeSize, double mouseEvent, double temp){
        double cellSize=canvasSize/maxsize;
        double start = (canvasSize / 2 - (cellSize * mazeSize / 2)) / cellSize;
        double mouse = (int) ((mouseEvent) / (temp) - start);
        return mouse;
    }


    public void saveMaze(ActionEvent actionEvent) {

        if(viewModel.getMazeObject()!=null)
        {

            FileChooser fc = new FileChooser();
            fc.setTitle("Save Maze");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
            fc.setInitialDirectory(new File(System.getProperty("user.home")));
            File chosen = fc.showSaveDialog(null); // Byte Maze File

            viewModel.saveMAze(chosen);

        }
        else
        {
            //TODO add alert or something

            Alert alert = new Alert(Alert.AlertType.ERROR,"MAZE NOT GENERATED YET ! ");
            alert.setTitle("EMPTY MAZE ERROR");
            alert.setHeaderText("EMPTY MAZE ERROR");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStyleClass().add("myDialog");
            Optional<ButtonType> result = alert.showAndWait();

        }
    }

    public void openPropWindow(ActionEvent actionEvent) throws IOException {

            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader propFXML = new FXMLLoader(getClass().getResource("/View/Properties.fxml"));
            Parent root = propFXML.load();
            //PropertiesController propController = propFXML.getController();
            Scene scene = new Scene(root, 380, 250);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }

    public void openHelpWindow(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        stage.setTitle("Help");
        FXMLLoader propFXML = new FXMLLoader(getClass().getResource("/View/Help.fxml"));
        Parent root = propFXML.load();

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }


    public void openAboutWindow(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        stage.setTitle("About");
        FXMLLoader propFXML = new FXMLLoader(getClass().getResource("/View/About.fxml"));
        Parent root = propFXML.load();

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }



}
