package View;

import Server.Configurations;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesController implements Initializable {

    public ChoiceBox generator;
    public ChoiceBox solver;

    public void initialize(URL location, ResourceBundle resources){
        generator.getItems().addAll("EmptyMazeGenerator","SimpleMazeGenerator","MyMazeGenerator");
        solver.getItems().addAll("BreadthFirstSearch","DepthFirstSearch", "BestFirstSearch");
        try{
            Properties properties = new Properties();
            properties.load(new FileInputStream("resources/config.properties"));

            String a1= properties.getProperty("mazeSearchingAlgorithm");
            String a2= properties.getProperty("mazeGeneratingAlgorithm");
            if(a1.equals("BestFirstSearch")){
                solver.setValue("BestFirstSearch");}
            else if(a1.equals("DepthFirstSearch")){
                solver.setValue("DepthFirstSearch");}
            else if(a1.equals("BreadthFirstSearch")){
                solver.setValue("BreadthFirstSearch");}
            if(a2.equals("MyMazeGenerator")){
                generator.setValue("MyMazeGenerator");}
            else if(a2.equals("SimpleMazeGenerator")){
                generator.setValue("SimpleMazeGenerator");}
            else if(a2.equals("EmptyMazeGenerator")){
                generator.setValue("EmptyMazeGenerator");}


        }
        catch (Exception e){}
    }



    public void UpdateClicked(){
        //Configurations.setGeneratorAlgo((String) generator.getValue());
        //Configurations.setSearchingAlgo((String) solver.getValue());

    }




}
