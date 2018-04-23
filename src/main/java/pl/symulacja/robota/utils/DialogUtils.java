package pl.symulacja.robota.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class DialogUtils {
    public static void errorDialog(String error){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        //Stage stage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
        errorAlert.getDialogPane().getStyleClass().add("myAlert");
        errorAlert.setTitle("Błąd!");
        errorAlert.setHeaderText("Uwaga coś poszło nie tak!");

        TextArea textArea = new TextArea(error);
        errorAlert.getDialogPane().setContent(textArea);
        errorAlert.showAndWait();
    }
}
