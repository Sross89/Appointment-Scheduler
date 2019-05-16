/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import static Model.Appointment.appointmentList;
import Model.City;
import Model.CityWrapper;
import Model.Customer;
import Model.DBConnection;
import static Model.DBConnection.conn;
import Model.User;
import static Model.User.userList;
import Model.appTypeMonth;
import java.sql.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author simon
 */
public class ReportsController implements Initializable {
    
    @FXML
    TableView reportsTable = new TableView();
    
    @FXML
    Button backBtn;
    @FXML
    Button appByMonthBtn;
    @FXML
    Button scheduleReportBtn;
    @FXML
    Button custByCityBtn;
    
    @FXML
    ComboBox consultantList;
    
    @FXML
    Label title;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        for (int i = 0; i < userList.size(); i++) {
            consultantList.getItems().add(userList.get(i).getUserName());
        }
        
        consultantList.getSelectionModel().selectFirst();
        
        appByMonthBtn.setOnAction((event) -> {
            try {
                setTableForAppByMonthReport();
            } catch (SQLException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        custByCityBtn.setOnAction((event) -> {
            try {
                setTableForCustomersByCityReport();
            } catch (SQLException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        scheduleReportBtn.setOnAction((event) -> {
            try {
                String userName = consultantList.getSelectionModel().getSelectedItem().toString();
                User user = null;
                for (int i = 0; i < userList.size(); i++) {
                    if(userList.get(i).getUserName().equals(userName))
                    {
                        user = userList.get(i);
                    }
                        
                }
                setTableForUserScheduleReport(user);
            } catch (SQLException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        backBtn.setOnAction((event) -> {
            try 
            {
                Stage stage = (Stage) backBtn.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("Main_Screen.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void setTableForAppByMonthReport() throws SQLException{
        reportsTable.getItems().clear();
        reportsTable.getColumns().clear();
        ObservableList<appTypeMonth> appTypeByMonthList = FXCollections.observableArrayList();
        
        PreparedStatement statement = DBConnection.conn.prepareStatement(
            "SELECT start as 'Month', type, COUNT(*) as 'Number of Meetings' FROM appointment GROUP BY ('start'), type");
        
        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {
                
                String meetingTime = rs.getString("Month");
                String type = rs.getString("Type");
                int count = rs.getInt("Number of Meetings");
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Month month = LocalDateTime.parse(meetingTime, formatter).getMonth();
                      
                appTypeByMonthList.add(new appTypeMonth(count, type, month));
        
        }
        
        TableColumn count = new TableColumn("Number of Meetings");
        TableColumn type = new TableColumn("Type");
        TableColumn month = new TableColumn("Month");
        reportsTable.getColumns().addAll(count, type, month);
        reportsTable.setItems(FXCollections.observableArrayList(appTypeByMonthList));        
        
        count.setCellValueFactory(new PropertyValueFactory<>("count"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        month.setCellValueFactory(new PropertyValueFactory<>("month"));
        
    }
    
    private void setTableForUserScheduleReport(User user) throws SQLException
    {
        
        ObservableList<Appointment> userScheduleList = FXCollections.observableArrayList();
        reportsTable.getItems().clear();
        reportsTable.getColumns().clear();
        int userId = user.getUserId();

        for (int i = 0; i < appointmentList.size(); i++) {
            if(appointmentList.get(i).getUserID() == userId)
            {
                userScheduleList.add(appointmentList.get(i));
            }
        }
        TableColumn customer = new TableColumn("Customer");
        TableColumn type = new TableColumn("Type");
        TableColumn start = new TableColumn("Start Time");
        TableColumn end = new TableColumn("End Time");  
        reportsTable.getColumns().addAll(customer, type, start, end);
        reportsTable.setItems(FXCollections.observableArrayList(userScheduleList));        
        
        customer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        start.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        end.setCellValueFactory(new PropertyValueFactory<>("endTime"));
    }
    
    private void setTableForCustomersByCityReport() throws SQLException
    {
        reportsTable.getItems().clear();
        reportsTable.getColumns().clear();
        ObservableList<CityWrapper> cities = FXCollections.observableArrayList();
        
        PreparedStatement statement = DBConnection.conn.prepareStatement(
            "select ci.city, count(*)\n" +
            "from customer c join address a\n" +
            "on c.addressId = a.addressId\n" +
            "left outer join city ci\n" +
            "on a.cityId = ci.cityId\n" +
            "group by ci.city;");
        
        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {
                
                String city = rs.getString("city");
                int count = rs.getInt("count(*)");

                cities.add(new CityWrapper(count, city));
        
        }
        
        TableColumn city = new TableColumn("City");
        TableColumn count = new TableColumn("# of Customers");
        reportsTable.getColumns().addAll(city, count);
        reportsTable.setItems(FXCollections.observableArrayList(cities));        
        
        city.setCellValueFactory(new PropertyValueFactory<>("city"));
        count.setCellValueFactory(new PropertyValueFactory<>("sum"));
        
    }
}

