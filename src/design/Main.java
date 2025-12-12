package design;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author hassanzmn
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Design.fxml"));
        Scene root = new Scene(loader.load());
        DesignController  dc = loader.getController();
        primaryStage.setMinWidth(810);
        primaryStage.setScene(root);
        primaryStage.setTitle("Night Sky Design");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Icon.jpg")));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void main(String args[]){
        launch(args);
    }

}
