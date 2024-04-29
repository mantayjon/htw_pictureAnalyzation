/**
 * @author Nico Hezel
 */
package de.htw.ba.ue05;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Ue05_Jonas_Mantay {

	public static void main(String[] args) throws IOException {
		Application.launch(FXApplication.class);
	}

	/**
	 * We need a separate class in order to trick Java 11 to start our JavaFX application without any module-path settings.
	 * https://stackoverflow.com/questions/52144931/how-to-add-javafx-runtime-to-eclipse-in-java-11/55300492#55300492
	 * 
	 * @author Nico Hezel
	 *
	 */
	public static class FXApplication extends Application {

		@Override
		public void start(Stage stage) throws Exception {

			Parent ui = new FXMLLoader(getClass().getResource("view/FaceDetectionView.fxml")).load();
			Scene scene = new Scene(ui);
			stage.setScene(scene);
			stage.setTitle("Face Detection - Your Name");
			stage.show();
		}
	}
}