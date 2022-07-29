package View;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class FirstSceneController {

    Scene scene;
    Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void changeScene(Scene a){
        this.scene=a;

    }

    public void newGame()
    {
    stage.setScene(this.scene);
    }
}
