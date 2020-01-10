package Game;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.util.Duration;
import openeye.oechem.oechem;

import java.io.IOException;


public class GameController {

    @FXML
    private GridPane rootGrid;
    @FXML
    private TextField AtomicInput;
    @FXML
    private Label AtomicLabel;
    @FXML
    private Button submitBtn;
    @FXML
    private Button quitBtn;
    @FXML
    private Button answerBtn;
    @FXML
    private Button genNewBtn;

    private int incorrectCount = 1;


    public void setLabelToRandom(){
        String rand = String.valueOf(Math.round((Math.random() * 118)) + 1);

        AtomicLabel.setText(rand);
        AtomicInput.setText("");
    }

    void popup(String title, String text) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label label = new Label(text);
        label.setFont(new Font("Cambria", 15));
        label.setWrapText(true);
        label.setPadding(new Insets(10, 20, 10, 20));

        VBox layout = new VBox();
        layout.getChildren().addAll(label);
        layout.setAlignment(Pos.CENTER);

        window.setScene(new Scene(layout, 400, 200));
        window.showAndWait();
    }

    String incorrectQuestion(int atomicNumber, String input){
        if (incorrectCount == 1) {
            String atomicMass = String.valueOf(oechem.OEGetDefaultMass(atomicNumber));
            incorrectCount += 1;

            return "The Atomic Mass is " + atomicMass;
        }
        else if (incorrectCount == 2) {
            incorrectCount += 1;

            if (atomicNumber > oechem.OEGetAtomicNum(input)) {
                return "The Atom is has a higher Atomic Number than what you entered";
            }
            else {
                return "The Atom is has a lower Atomic Number than what you entered";
            }
        }
        else if (incorrectCount == 3){
            incorrectCount += 1;

            if ((atomicNumber > 0 && atomicNumber < 3) || (atomicNumber > 5 && atomicNumber < 11)
                    || (atomicNumber > 14 && atomicNumber < 19) || (atomicNumber > 33 && atomicNumber < 37)
                    || (atomicNumber > 52 && atomicNumber < 55) || (atomicNumber == 86)){
                return "The Atom is a Non-Metal.";
            }
            else if (atomicNumber == 5 || atomicNumber == 14 || atomicNumber == 33 || atomicNumber == 52
                    || atomicNumber == 85){
                return "The Atom is a Metalloid.";
            }
            else {
                return "The Atom is a Metal.";
            }
        }
        else if(incorrectCount == 4){
            incorrectCount += 1;

            if (atomicNumber == 1 || atomicNumber == 2){
                return "The Atom is in the First Period";
            }
            else if (atomicNumber > 2 && atomicNumber < 11){
                return "The Atom is in the Second Period";
            }
            else if (atomicNumber > 10 && atomicNumber < 19){
                return "The Atom is in the Third Period";
            }
            else if (atomicNumber > 18 && atomicNumber < 37){
                return "The Atom is in the Fourth Period";
            }
            else if (atomicNumber > 36 && atomicNumber < 55){
                return "The Atom is in the Fifth Period";
            }
            else if (atomicNumber > 54 && atomicNumber < 87){
                return "The Atom is in the Sixth Period";
            }
            else {
                return "The Atom is in the Last (Seventh) Period";
            }
        }
        else{
            return "Lost";
        }
    }

    @FXML
    void answerBtnPressed(ActionEvent event) {
        String atomicSymbol = oechem.OEGetAtomicSymbol(Integer.parseInt(AtomicLabel.getText()));
        popup("Answer","The Answer was " + atomicSymbol);
        setLabelToRandom();
    }

    @FXML
    void newBtnPressed(ActionEvent event) {
        setLabelToRandom();
    }

    @FXML
    void quitBtnPressed(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void submitBtnPressed(ActionEvent event) {
        String input = AtomicInput.getText().replaceAll("\\s","");;
        int label = Integer.parseInt(AtomicLabel.getText());

        if (!input.equals("")) {
            if (oechem.OEGetAtomicNum(input) == label) {
                popup("Correct", "Congratulations, your input was correct.");
                setLabelToRandom();
            } else {
                String hint = incorrectQuestion(label, input);

                if (hint.equals("Lost")){
                    popup("Lost", "You Lost. Better luck next time.");
                    System.exit(0);
                }
                else {
                    popup("Hint", hint);
                }
            }
        }
        else {
            popup("Empty", "Your input was empty!");
        }
    }

    public void initialize() throws IOException {
        setLabelToRandom();

        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(400));
        fade.setNode(rootGrid);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

}
