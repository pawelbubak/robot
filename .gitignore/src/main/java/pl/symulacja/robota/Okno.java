package pl.symulacja.robota;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import pl.symulacja.robota.dane.Mapa;
import pl.symulacja.robota.dane.Point;
import pl.symulacja.robota.dane.Robot;
import pl.symulacja.robota.dane.Wektor;
import pl.symulacja.robota.utils.DialogUtils;

import java.util.ArrayList;

public class Okno {
    @FXML private AnchorPane panelRysowania;
    @FXML private TextField xTextField;
    @FXML private TextField yTextField;
    @FXML private TextField rTextField;
    @FXML private Button acceptButton;
    private Robot robot;
    private Mapa mapa;

    @FXML public void initialize(){
        acceptButton.disableProperty().bind(xTextField.textProperty().isEmpty().or(yTextField.textProperty().isEmpty().or(rTextField.textProperty().isEmpty())));
        pl.symulacja.robota.main.Robot.generujMape();
        this.mapa = pl.symulacja.robota.main.Robot.getMapa();
        rysujLinie(mapa.getMapa());
        xTextField.setText("420");
        yTextField.setText("320");
        rTextField.setText("220");
    }

    @FXML public void acceptButtonOnAction() {
        try {
            sprawdzPole(xTextField, "X");
            sprawdzPole(yTextField, "Y");
            sprawdzPole(rTextField, "R");
        } catch (Exception e) {
            DialogUtils.errorDialog(e.getMessage());
        }
        robot = new Robot(Integer.parseInt(xTextField.getText()),Integer.parseInt(yTextField.getText()),Integer.parseInt(rTextField.getText()));
        pl.symulacja.robota.main.Robot.wywolanie(robot.getX(),robot.getY(),robot.getR());
        rysujLinie2(pl.symulacja.robota.main.Robot.getWynikkoncowy());
    }

    private void rysujLinie2(ArrayList<Wektor> wewnatrz) {
        panelRysowania.getChildren().clear();
        rysujLinie(this.mapa.getMapa());
        rysujRobota();
        for (Wektor wektor: wewnatrz) {
            Line line = new Line(wektor.getStart().getX(), wektor.getStart().getY(), wektor.getEnd().getX(), wektor.getEnd().getY());
            // kolor
            line.setStroke(Color.RED);
            // rodzaj zakończenia (tu bez dekoracji)
            line.setStrokeLineCap(StrokeLineCap.BUTT);
            // szerokość
            line.setStrokeWidth(3);
            strzalka(wektor.getEnd());
            panelRysowania.getChildren().add(line);
        }

    }

    private void rysujRobota() {
        Circle zasiegRobota = new Circle(robot.getX(),robot.getY(),robot.getR());
        Line pozycjaRobota = new Line(robot.getX(),robot.getY(),robot.getX(),robot.getY());
        zasiegRobota.setStroke(Color.GREEN);
        zasiegRobota.setStrokeWidth(2);
        zasiegRobota.setFill(null);
        pozycjaRobota.setStroke(Color.GREEN);
        pozycjaRobota.setStrokeWidth(15);
        panelRysowania.getChildren().add(zasiegRobota);
        panelRysowania.getChildren().add(pozycjaRobota);
    }

    private void strzalka(Point point){
        Line strzalka = new Line(point.getX(),point.getY(),point.getX(),point.getY());
        strzalka.setStroke(Color.PURPLE);
        strzalka.setStrokeWidth(8);
        panelRysowania.getChildren().add(strzalka);
    }

    private void rysujLinie(ArrayList<Wektor> mapa) {
        for (Wektor wektor : mapa) {
            Line line = new Line(wektor.getStart().getX(), wektor.getStart().getY(), wektor.getEnd().getX(), wektor.getEnd().getY());
            panelRysowania.getChildren().add(line);
        }
    }

    @FXML private void sprawdzPole(TextField textField, String s) throws Exception {
        int temp = Integer.parseInt(textField.getText());
        if (temp < 0)
            try {
                throw new Exception();
            } catch (Exception e) {
                throw new Exception("Proszę podać wartość " + s + " całkowitą z przedziału [0,700]!");
            }
    }
}
