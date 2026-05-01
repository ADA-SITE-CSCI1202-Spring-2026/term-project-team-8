package aresbase;

import aresbase.ui.DashboardController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new DashboardController().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}