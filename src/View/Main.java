package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;



public class Main extends Application {

   public Stage priSatge;
    @Override
    public void start(Stage primaryStage) throws Exception{

        priSatge=primaryStage;
        Scene myViewScene;
        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("FirstScene.fxml"));
        Parent root1 = fxmlLoader1.load();
        primaryStage.setTitle("Welcome");
        FirstSceneController firstSceneController = fxmlLoader1.getController();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();

        myViewScene=new Scene(root,1000,700);

        firstSceneController.setStage(primaryStage);
        firstSceneController.changeScene(myViewScene);


        primaryStage.setScene(new Scene(root1, 1000, 700));
        primaryStage.show();


        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();



        view.setViewModel(viewModel);

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            view.ExitFunc(new ActionEvent());
        });



    }

    public static void main(String[] args) {
        launch(args);
    }
}
