import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent menu = FXMLLoader.load(getClass().getResource("Menu.fxml"));

        primaryStage.setTitle("Periodic Table Game");
        primaryStage.setScene(new Scene(menu, 780, 500));

        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
