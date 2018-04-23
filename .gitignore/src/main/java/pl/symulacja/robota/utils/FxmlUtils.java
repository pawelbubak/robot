package pl.symulacja.robota.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;

public class FxmlUtils {

    public static Pair<Pane, FXMLLoader> fxmlLoader(String fxmlPath){
        FXMLLoader loader = new FXMLLoader(FxmlUtils.class.getResource(fxmlPath));
        try {
            Pair<Pane,FXMLLoader> pair = new Pair<>(loader.load(), loader);
            return pair;
        }
        catch (Exception e){
            DialogUtils.errorDialog(e.getMessage());
        }
        return null;
    }
}
