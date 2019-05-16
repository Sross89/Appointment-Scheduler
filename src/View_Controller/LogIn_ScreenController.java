/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import static Model.DBConnection.conn;
import Model.User;
import static Model.User.userList;
import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.logging.*;
import testingjavafx.AppointmentScheduler;

/**
 * FXML Controller class
 *
 * @author simon
 */
public class LogIn_ScreenController implements Initializable {
    
    @FXML
    Label logInTitle;
    @FXML
    Label usernameLabel;
    @FXML
    Label passwordLabel;
    @FXML
    Label currentLanguageLabel;
    @FXML
    Label errorMessageLabel;
    @FXML
    Label currentDateLabel;
    @FXML
    Button signInBtn;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    
    Locale locale = Locale.getDefault();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        errorMessageLabel.setVisible(false);
        currentDateLabel.setText(locale.toString());       
        internationalizeText();
        
        try 
        {
            updateUserArrayFromDatabase();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(LogIn_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Lambda to set button event since it is quicker than using a method
        signInBtn.setOnAction((event) -> {
            try {
                if(authenticateUser(usernameField.getText(), passwordField.getText())) 
                {
                    try
                    {
                        Stage stage = (Stage) signInBtn.getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("Main_Screen.fxml"));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show(); 
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(LogIn_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    errorMessageLabel.setVisible(true);
                }
            } catch (IOException ex) {
                Logger.getLogger(LogIn_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }

    private void updateUserArrayFromDatabase() throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select * from user");
        
        while(rs.next())
        {
            int id = rs.getInt("userId");
            String userName = rs.getString("userName");
            String password = rs.getString("password");
            int active = rs.getInt("active");
            String createBy = rs.getString("createBy");
            Date createDate = rs.getDate("createDate");
            Date lastUpdate = rs.getDate("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            User user = new User(id, userName, password, active, createBy, createDate, lastUpdate, lastUpdateBy);
            userList.add(user);
        }

   }
    
    private Boolean authenticateUser(String username, String password) throws IOException
    {
        for (int i = 0; i < userList.size(); i++) {
            
            if(userList.get(i).getUserName().equals(username) && userList.get(i).getPassword().equals(password))
            {
                AppointmentScheduler.currentSessionUser = userList.get(i).getUserName();
                logTimeStamps();
                try 
                {
                    Main_ScreenController.updateAppointmentListFromDatabase();
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
                Main_ScreenController.alertIfMeetingWithin15Minutes();
                return true;
            }
        }
        return false;
    }
    
    @FXML
    private void internationalizeText()
    {
        ResourceBundle rb = ResourceBundle.getBundle("View_Controller.lang");
        System.out.println(ZonedDateTime.now());
        logInTitle.setText(rb.getString("signInTitle"));
        usernameLabel.setText(rb.getString("usernameLabel"));
        passwordLabel.setText(rb.getString("passwordLabel"));
        currentLanguageLabel.setText(rb.getString("currentLanguageLabel"));
        errorMessageLabel.setText(rb.getString("errorMessageLabel"));
        signInBtn.setText(rb.getString("signInBtn"));
    }
    
    private void logTimeStamps() throws IOException
    {
        Logger log = Logger.getLogger("log.txt");
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        FileHandler fh = new FileHandler("log.txt", true);
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);
        log.addHandler(fh);
        
        log.info("This message is informational");
        log.log(Level.INFO, "User: {0} successfully signed in on: {1}", new Object[]{AppointmentScheduler.currentSessionUser, currentTime});
        
    }
        
}