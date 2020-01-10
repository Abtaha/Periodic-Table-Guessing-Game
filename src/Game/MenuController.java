package Game;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.util.Duration;


public class MenuController {
    @FXML
    public Button startBtn;
    @FXML
    public Button quitBtn;

    @FXML
    void quitButtonClicked(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    void startButtonClicked(MouseEvent event) {
        if (event.getSource() == startBtn){
            Stage stage = (Stage)startBtn.getScene().getWindow();

            FadeTransition fade = new FadeTransition();
            fade.setDuration(Duration.millis(400));
            fade.setNode(stage.getScene().getRoot());
            fade.setFromValue(1);
            fade.setToValue(0);

            fade.setOnFinished(e -> {
                Parent mainGame = null;
                try {
                    mainGame = FXMLLoader.load(getClass().getResource("/Game/Game.fxml"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                stage.setScene(new Scene(mainGame, 780,500));
            });
            fade.play();
        }
    }
}
