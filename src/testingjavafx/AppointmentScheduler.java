/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testingjavafx;

import Model.DBConnection;
import View_Controller.Main_ScreenController;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author simon
 */
public class AppointmentScheduler extends Application {
    
    public static String currentSessionUser;
  
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/LogIn_Screen.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        
    }
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        
        
        DBConnection.makeConnection();
        launch(args);
        DBConnection.closeConnection();
    }
    
}
