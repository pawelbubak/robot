package pl.symulacja.robota.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import pl.symulacja.robota.utils.FxmlUtils;

public class SymulacjaRobota extends Application{
    private final static String FXML_PATH = "/fxml/okno.fxml";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pair<Pane, FXMLLoader> pair = FxmlUtils.fxmlLoader(FXML_PATH);
        assert pair != null;
        Pane pane = pair.getKey();
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Symulacja Robota");
        primaryStage.show();
    }
}
