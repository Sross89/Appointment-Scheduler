/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import static Model.Appointment.appointmentList;
import static Model.Customer.customerList;
import static Model.DBConnection.conn;
import static Model.User.userList;
import java.io.IOException;
import java.sql.*;
import java.net.URL;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import testingjavafx.AppointmentScheduler;

/**
 * FXML Controller class
 *
 * @author simon
 */
public class Appointment_ScreenController implements Initializable {
    
    @FXML
    Label appIdLabel;
    
    @FXML
    DatePicker datePicker;

    @FXML
    ComboBox startTimeHour;
    @FXML
    ComboBox endTimeHour;
    @FXML
    ComboBox startTimeMinute;
    @FXML
    ComboBox endTimeMinute;
    @FXML
    ComboBox customer;
    @FXML
    ComboBox type;
    @FXML
    ComboBox consultant;
    
    @FXML
    Button confirmBtn;
    @FXML
    Button backBtn;
    
    ZoneId zid = ZoneId.systemDefault();
    public static Boolean update = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        setTimeComboBoxOptions();
        setCustomerComboBoxOptions();
        setConsultantComboBoxOptions();
        setTypeComboBoxOptions();
        setDefaultComboBoxValues();
        
        confirmBtn.setOnAction((event) -> {
            try {
                if (!update) {
                   if (validateMeetingTime() && validateDate() && validateBusinessHours() && validateConflictingAppointments())
                    {
                        createNewAppointment();
                        switchSceneToMain(confirmBtn);
                    }
                }
                else
                {
                    if(validateMeetingTime() && validateDate() && validateBusinessHours() && validateConflictingAppointmentsForUpdate())
                    {
                        updateAppointment();
                    }
                }
                
                
            } catch (SQLException ex) {
                Logger.getLogger(Appointment_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Appointment_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        backBtn.setOnAction((event) -> {
            try {        
                switchSceneToMain(backBtn);
            } catch (IOException ex) {
                Logger.getLogger(Appointment_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void setTimeComboBoxOptions()
    {
        ArrayList<String> hours = new ArrayList<>()
        {
            {
              add("07");
              add("08");
              add("09");
              add("10");
              add("11");
              add("12");
              add("13");
              add("14");
              add("15");
              add("16");
              add("17");
            }
        };
        
        ArrayList<String> minutes = new ArrayList<>()
        {
            {
              add("00");
              add("15");
              add("30");
              add("45");
            }
        };
        
        startTimeHour.getItems().clear();
        endTimeHour.getItems().clear();
        startTimeMinute.getItems().clear();
        endTimeMinute.getItems().clear();
        startTimeHour.getItems().addAll(hours);
        endTimeHour.getItems().addAll(hours);
        startTimeMinute.getItems().addAll(minutes);
        endTimeMinute.getItems().addAll(minutes);
    }

    private void setCustomerComboBoxOptions()
    {
        customer.getItems().clear();
        for (int i = 0; i < customerList.size(); i++) {
            customer.getItems().add(customerList.get(i).getCustomerID() + ", " + customerList.get(i).getCustomerName());   
        }        
    }
    
    private void setTypeComboBoxOptions()
    {
        ArrayList<String> types = new ArrayList<>()
        {
            {
              add("New Account");
              add("Credit Review");
              add("Loan Application");
              add("Fraud Review");
              add("Other");
            }
        };
        type.getItems().clear();
        type.getItems().addAll(types);
    }
    
    private void setConsultantComboBoxOptions()
    {
        consultant.getItems().clear();
        for (int i = 0; i < userList.size(); i++) {
            consultant.getItems().add(userList.get(i).getUserId() + ", " + userList.get(i).getUserName());   
        }  
    }
    
    private void setDefaultComboBoxValues()
    {
        startTimeHour.getSelectionModel().selectFirst();
        endTimeHour.getSelectionModel().selectFirst();
        startTimeMinute.getSelectionModel().selectFirst();
        endTimeMinute.getSelectionModel().selectFirst();
        customer.getSelectionModel().selectFirst();
        type.getSelectionModel().selectFirst();
        consultant.getSelectionModel().selectFirst();
        datePicker.setValue(LocalDate.now());
    }
    
    private Boolean validateMeetingTime()
    {
        LocalDate date = datePicker.getValue();
        int startHour = Integer.parseInt(startTimeHour.getValue().toString());
        int endHour = Integer.parseInt(endTimeHour.getValue().toString());
        int startMinute = Integer.parseInt(startTimeMinute.getValue().toString());
        int endMinute = Integer.parseInt(endTimeMinute.getValue().toString());
        LocalDateTime ldtStart = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), startHour, startMinute);
        LocalDateTime ldtEnd = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), endHour, endMinute);
        
        if (!ldtStart.isBefore(ldtEnd))
        {
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("ERROR");
            alert.setContentText("Invalid meeting time.\nEnd time can't be before or the same as start time.");
            alert.showAndWait();
            return false;
        }
        return true;
    }
    
    private Boolean validateDate()
    {
        if(datePicker.getValue().isBefore(LocalDate.now()))
        {
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("ERROR");
            alert.setContentText("You can't schedule meetings for the past");
            alert.showAndWait();
            return false;
        }
        else if (datePicker.getValue().equals(LocalDate.now()))
        {
            LocalDate date = datePicker.getValue();
            int startHour = Integer.parseInt(startTimeHour.getValue().toString());;
            int startMinute = Integer.parseInt(startTimeMinute.getValue().toString());
            LocalDateTime ldtStart = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), startHour, startMinute);
            if (ldtStart.isBefore(LocalDateTime.now()))
            {
                Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("ERROR");
            alert.setContentText("You can't schedule meetings for the past");
            alert.showAndWait();
            return false;
            }            
        }
        return true;
    }
    
    private Boolean validateConflictingAppointmentsForUpdate()
    {
        for (int i = 0; i < appointmentList.size(); i++) {
            if (appointmentList.get(i).getAppointmentID() == Integer.parseInt(appIdLabel.getText()))
            {
                continue;
            }
            if (appointmentList.get(i).getUserID() == Integer.valueOf(consultant.getValue().toString().substring(0, consultant.getValue().toString().indexOf(",")))) 
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDate date = datePicker.getValue();
                int startHour = Integer.parseInt(startTimeHour.getValue().toString());
                int endHour = Integer.parseInt(endTimeHour.getValue().toString());
                int startMinute = Integer.parseInt(startTimeMinute.getValue().toString());
                int endMinute = Integer.parseInt(endTimeMinute.getValue().toString());
                LocalDateTime ldtStart = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), startHour, startMinute);
                ldtStart.format(formatter);
                LocalDateTime ldtEnd = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), endHour, endMinute);
                ldtEnd.format(formatter);
                if ((ldtStart.isAfter(LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter)) &&
                    ldtStart.isBefore(LocalDateTime.parse(appointmentList.get(i).getEndTime(), formatter))) ||
                    (ldtEnd.isAfter(LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter)) &&
                    ldtEnd.isBefore(LocalDateTime.parse(appointmentList.get(i).getEndTime(), formatter))) ||
                    (ldtStart.isEqual(LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter))) ||
                     ((LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter).isAfter(ldtStart)) &&
                      (LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter).isBefore(ldtEnd)))) 
                {
                    Alert alert = new Alert(INFORMATION);
                    alert.setHeaderText("ERROR");
                    alert.setContentText("That consultant already has a meeting that overlaps that timeslot");
                    alert.showAndWait();
                    return false;
                }
            }
        }
        return true;
    }
    
    private Boolean validateConflictingAppointments()
    {
        for (int i = 0; i < appointmentList.size(); i++) 
        {
            if (appointmentList.get(i).getUserID() == Integer.valueOf(consultant.getValue().toString().substring(0, consultant.getValue().toString().indexOf(",")))) 
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDate date = datePicker.getValue();
                int startHour = Integer.parseInt(startTimeHour.getValue().toString());
                int endHour = Integer.parseInt(endTimeHour.getValue().toString());
                int startMinute = Integer.parseInt(startTimeMinute.getValue().toString());
                int endMinute = Integer.parseInt(endTimeMinute.getValue().toString());
                LocalDateTime ldtStart = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), startHour, startMinute);
                ldtStart.format(formatter);
                LocalDateTime ldtEnd = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), endHour, endMinute);
                ldtEnd.format(formatter);
                if ((ldtStart.isAfter(LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter)) &&
                    ldtStart.isBefore(LocalDateTime.parse(appointmentList.get(i).getEndTime(), formatter))) ||
                    (ldtEnd.isAfter(LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter)) &&
                    ldtEnd.isBefore(LocalDateTime.parse(appointmentList.get(i).getEndTime(), formatter))) ||
                    (ldtStart.isEqual(LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter))) ||
                     ((LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter).isAfter(ldtStart)) &&
                      (LocalDateTime.parse(appointmentList.get(i).getStartTime(), formatter).isBefore(ldtEnd)))) 
                {
                    Alert alert = new Alert(INFORMATION);
                    alert.setHeaderText("ERROR");
                    alert.setContentText("That consultant already has a meeting that overlaps that timeslot");
                    alert.showAndWait();
                    return false;
                }
            }
        }
    
        return true;
    }
    
    private void updateAppointment() throws SQLException, IOException
    {
        Statement stmt = conn.createStatement();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate date = datePicker.getValue();
        int startHour = Integer.parseInt(startTimeHour.getValue().toString());
        int endHour = Integer.parseInt(endTimeHour.getValue().toString());
        int startMinute = Integer.parseInt(startTimeMinute.getValue().toString());
        int endMinute = Integer.parseInt(endTimeMinute.getValue().toString());
        LocalDateTime ldtStart = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), startHour, startMinute);
        ldtStart.format(formatter);
        LocalDateTime ldtEnd = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), endHour, endMinute);
        ldtEnd.format(formatter);
        ZonedDateTime zdtStart = ldtStart.atZone(zid);
        ZonedDateTime zdtEnd = ldtEnd.atZone(zid);
        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));
        ldtStart = utcStart.toLocalDateTime();
        ldtEnd = utcEnd.toLocalDateTime();
        Timestamp startsqlts = Timestamp.valueOf(ldtStart);
        Timestamp endsqlts = Timestamp.valueOf(ldtEnd);
        
        int customerId = Integer.valueOf(customer.getValue().toString().substring(0, customer.getValue().toString().indexOf(",")));
        String lastUpdate = ZonedDateTime.now().format(formatter);
        String lastUpdateBy = AppointmentScheduler.currentSessionUser; 
        String typeString = type.getValue().toString();
        int userId = Integer.valueOf(consultant.getValue().toString().substring(0, consultant.getValue().toString().indexOf(",")));
        
       int rs =  stmt.executeUpdate("update appointment"
               + " set customerId = '" + customerId + "',"
               + " start = '" + startsqlts + "',"
               + " end = '" + endsqlts + "',"
               + " lastUpdate = '" + lastUpdate + "',"
               + " lastUpdateBy = '" + lastUpdateBy + "',"
               + " type = '" + typeString + "',"
               + " userId = '" + userId + "'"
               + " where appointmentId = '" + appIdLabel.getText() + "';");
               
       Stage stage = (Stage) confirmBtn.getScene().getWindow();
       Parent root = FXMLLoader.load(getClass().getResource("Main_Screen.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
    }
        
    private void createNewAppointment() throws SQLException
    {
        Statement stmt = conn.createStatement();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate date = datePicker.getValue();
        int startHour = Integer.parseInt(startTimeHour.getValue().toString());
        int endHour = Integer.parseInt(endTimeHour.getValue().toString());
        int startMinute = Integer.parseInt(startTimeMinute.getValue().toString());
        int endMinute = Integer.parseInt(endTimeMinute.getValue().toString());
        LocalDateTime ldtStart = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), startHour, startMinute);
        ldtStart.format(formatter);
        LocalDateTime ldtEnd = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), endHour, endMinute);
        ldtEnd.format(formatter);
        ZonedDateTime zdtStart = ldtStart.atZone(zid);
        ZonedDateTime zdtEnd = ldtEnd.atZone(zid);
        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));
        ldtStart = utcStart.toLocalDateTime();
        ldtEnd = utcEnd.toLocalDateTime();
        Timestamp startsqlts = Timestamp.valueOf(ldtStart);
        Timestamp endsqlts = Timestamp.valueOf(ldtEnd);
        
        int customerId = Integer.valueOf(customer.getValue().toString().substring(0, customer.getValue().toString().indexOf(",")));
        String createDate = ZonedDateTime.now().format(formatter);
        String createdBy = AppointmentScheduler.currentSessionUser;
        String lastUpdate = ZonedDateTime.now().format(formatter);
        String lastUpdateBy = AppointmentScheduler.currentSessionUser; 
        String typeString = type.getValue().toString();
        int userId = Integer.valueOf(consultant.getValue().toString().substring(0, consultant.getValue().toString().indexOf(",")));
        
        String sql = ("insert into appointment (customerId, start, end, createDate, createdBy, lastUpdate, lastUpdateBy, type, userId) values "
                + "('" + customerId + "', "
                + "'" + startsqlts + "', "
                + "'" + endsqlts + "', "
                + "'" + createDate + "', "
                + "'" + createdBy + "', "
                + "'" + lastUpdate + "', "
                + "'" + lastUpdateBy + "', "
                + "'" + typeString + "', "
                + "'" + userId + "');");
        int rs =  stmt.executeUpdate(sql);
        Alert alert = new Alert(INFORMATION);
        alert.setHeaderText("SUCCESS");
        alert.setContentText("Appointment created successfully");
        alert.showAndWait();
    }
    
    public void setFieldsForUpdate(Appointment appointment)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tempStart = appointment.getStartTime();
        String tempEnd = appointment.getEndTime();
        int year = Integer.parseInt(tempStart.substring(0, 4));
        int month= Integer.parseInt(tempStart.substring(5, 7));
        int day = Integer.parseInt(tempStart.substring(8, 10));
        datePicker.setValue(LocalDate.of(year, month, day));
        startTimeHour.getSelectionModel().select(tempStart.substring(11, 13));
        endTimeHour.getSelectionModel().select(tempEnd.substring(11, 13));
        startTimeMinute.getSelectionModel().select(tempStart.substring(14, 16));
        endTimeMinute.getSelectionModel().select(tempEnd.substring(14, 16));
        appIdLabel.setText(String.valueOf(appointment.getAppointmentID()));
        appIdLabel.setVisible(false);
        for (int i = 0; i < customerList.size(); i++) {
            if(customerList.get(i).getCustomerID() == appointment.getCustomerID())
            {
                customer.getSelectionModel().select(i);
            }
        }
        for (int i = 0; i < userList.size(); i++) {
            if(userList.get(i).getUserId() == appointment.getUserID())
            {
                consultant.getSelectionModel().select(i);
            }
        }
        
        type.getSelectionModel().select(appointment.getType());
    }
    
    private void switchSceneToMain(Button btn) throws IOException
    {
        Stage stage;
        Parent root;
        stage = (Stage) btn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main_Screen.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    private Boolean validateBusinessHours()
    {
        LocalDate date = datePicker.getValue();
        {
        if(date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY)
        {
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("ERROR");
            alert.setContentText("The office is not open on the weekends");
            alert.showAndWait();
            return false;
        }
        return true;
    }
        
    }
 
    
}


