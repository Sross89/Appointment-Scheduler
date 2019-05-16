/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Address;
import static Model.Address.addressList;
import Model.Appointment;
import static Model.Appointment.appointmentList;
import Model.City;
import static Model.City.cityList;
import Model.Country;
import static Model.Country.countryList;
import Model.Customer;
import static Model.Customer.customerList;
import static Model.DBConnection.conn;
import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import testingjavafx.AppointmentScheduler;

/**
 * FXML Controller class
 *
 * @author simon
 */
public class Main_ScreenController implements Initializable {
    
    @FXML
    Label appointmentLabel;
    
    @FXML
    Button newAppointmentBtn;
    @FXML
    Button editAppointmentBtn;
    @FXML
    Button newCustomerBtn;
    @FXML
    Button editCustomerBtn;
    @FXML
    Button deleteAppointmentBtn;
    @FXML
    Button deleteCustomerBtn;
    @FXML
    Button exitBtn;
    @FXML
    Button weeklyBtn;
    @FXML
    Button monthlyBtn;
    @FXML
    Button allBtn;
    @FXML
    Button reportsBtn;
    
    @FXML
    ObservableList<Appointment> appointmentListener = FXCollections.observableArrayList();
    @FXML
    ObservableList<Appointment> appointmentObservableList = FXCollections.observableArrayList(appointmentList);
    @FXML
    ObservableList<Customer> customerObservableList = FXCollections.observableArrayList(customerList);
    
    @FXML
    TableView appointmentsTable = new TableView<Appointment>(appointmentObservableList);
    @FXML
    TableView customerTable = new TableView<Customer>(customerObservableList);
    
    
    @FXML
    TableColumn customerId;
    @FXML
    TableColumn customerName;
    @FXML
    TableColumn customerPhone;
    
    @FXML
    TableColumn startTime;
    @FXML
    TableColumn appointmentEndTime;
    @FXML
    TableColumn appointmentCustomer;
    @FXML
    TableColumn appointmentUser;
    @FXML
    TableColumn appointmentType;
    
    private final static ZoneId zoneId = ZoneId.systemDefault();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try 
        {
            updateCustomerListFromDatabase();
            updateAppointmentListFromDatabase();
            updateAddressListFromDatabase();
            updateCityListFromDatabase();
            updateCountryListFromDatabase();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
        
        appointmentLabel.setText("Viewing all future appointments");
        appointmentObservableList.setAll(appointmentList);
        customerObservableList.setAll(customerList);
        appointmentsTable.setItems(FXCollections.observableArrayList(appointmentObservableList)); 
        customerTable.setItems(FXCollections.observableArrayList(customerObservableList));        
        
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        appointmentUser.setCellValueFactory(new PropertyValueFactory<>("userName"));
        appointmentCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        // Lambda to set button event since it is quicker than using a method
        exitBtn.setOnAction((event) -> {
            Platform.exit();
        });
        
        // Lambda to set button event since it is quicker than using a method
        weeklyBtn.setOnAction((event) -> {
            appointmentObservableList.clear();
            
            for (int i = 0; i < appointmentList.size(); i++) {
                if(isInWeekRange(appointmentList.get(i)))
                {
                    appointmentObservableList.add(appointmentList.get(i));
                    appointmentsTable.setItems(FXCollections.observableArrayList(appointmentObservableList));
                    appointmentLabel.setText("Viewing appointments for the next week");
                }
                
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        monthlyBtn.setOnAction((event) -> {
            appointmentObservableList.clear();
            
            for (int i = 0; i < appointmentList.size(); i++) {
                if(isInMonthRange(appointmentList.get(i)))
                {
                    appointmentObservableList.add(appointmentList.get(i));
                    appointmentsTable.setItems(FXCollections.observableArrayList(appointmentObservableList));
                    appointmentLabel.setText("Viewing appointments for the next month");
                }
                
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        allBtn.setOnAction((event) -> {
            appointmentObservableList.setAll(appointmentList);
            appointmentsTable.setItems(FXCollections.observableArrayList(appointmentObservableList));
        });
        
        // Lambda to set button event since it is quicker than using a method
        deleteCustomerBtn.setOnAction((event) -> {
            try {
                if(deleteCustomer())
                {
                    updateCustomerListFromDatabase();
                    customerTable.setItems(FXCollections.observableArrayList(customerList));
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        deleteAppointmentBtn.setOnAction((event) -> {
            try {
                if(deleteAppointment())
                {
                    updateAppointmentListFromDatabase();
                    appointmentsTable.setItems(FXCollections.observableArrayList(appointmentList));
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        newAppointmentBtn.setOnAction((event) -> {
            try 
            {
                Appointment_ScreenController.update = false;
                Stage stage = (Stage) newAppointmentBtn.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("Appointment_Screen.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        editAppointmentBtn.setOnAction((event) -> {
            try 
            {
                if(validateThatItemHasBeenSelected(appointmentsTable))
                {
                    Appointment_ScreenController.update = true;
                    setAppointmentInformationForEdit();                   
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        newCustomerBtn.setOnAction((event) -> {
            try 
            {
                Stage stage = (Stage) newCustomerBtn.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("Customer_Screen.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        editCustomerBtn.setOnAction((event) -> {
            try {
                if(validateThatItemHasBeenSelected(customerTable))
                {
                    setCustomerInformationForEdit();                    
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Main_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Lambda to set button event since it is quicker than using a method
        reportsBtn.setOnAction((event) -> {
            try 
            {
                Stage stage = (Stage) newCustomerBtn.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("reports.fxml"));
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

    public static void updateCustomerListFromDatabase() throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select * from customer");
        customerList.clear();
        Customer customer;
                
        while(rs.next())
        {
            int id = rs.getInt("customerId");
            String customerName = rs.getString("customerName");
            int addressId = rs.getInt("addressId");
            int active = rs.getInt("active");
            String createDate = rs.getString("createDate");
            String createBy = rs.getString("createdBy");
            String lastUpdate = rs.getString("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            customer = new Customer(id, customerName, addressId, active, createDate, createBy, lastUpdate, lastUpdateBy);
            customerList.add(customer);
        }
        rs = stmt.executeQuery("select * from address");
        
        while(rs.next())
        {
            int id = rs.getInt("addressId");
            String phone = rs.getString("phone");
            
            for (int i = 0; i < customerList.size(); i++) {
                if(customerList.get(i).getAddressID() == id)
                {
                    customerList.get(i).setPhoneNumber(phone);
                }
                
            }
        }

   }
    
    public static void updateAddressListFromDatabase() throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select * from address");
        addressList.clear();
        
        while(rs.next())
        {
            int id = rs.getInt("addressId");
            String addressName = rs.getString("address");
            String address2Name = rs.getString("address2");
            int cityId = rs.getInt("cityId");
            String postalCode = rs.getString("postalCode");
            String phone = rs.getString("phone");
            String createDate = rs.getString("createDate");
            String createBy = rs.getString("createdBy");            
            String lastUpdate = rs.getString("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            Address address = new Address(id, addressName, address2Name, cityId, postalCode, phone, createDate, createBy, lastUpdate, lastUpdateBy);
            addressList.add(address);
        }

   }
    
    public static void updateCityListFromDatabase() throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select * from city");
        cityList.clear();
        
        while(rs.next())
        {
            int id = rs.getInt("cityId");
            String cityName = rs.getString("city");
            int countryId = rs.getInt("countryId");
            String createDate = rs.getString("createDate");
            String createdBy = rs.getString("createdBy");            
            String lastUpdate = rs.getString("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            City city = new City(id, cityName, countryId, createDate, createdBy, lastUpdate, lastUpdateBy);
            cityList.add(city);
        }

   }
    
    public static void updateCountryListFromDatabase() throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select * from country");
        countryList.clear();
        
        while(rs.next())
        {
            int countryId = rs.getInt("countryId");
            String countryName = rs.getString("country");
            String createDate = rs.getString("createDate");
            String createdBy = rs.getString("createdBy");            
            String lastUpdate = rs.getString("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            Country country = new Country(countryId, countryName, createdBy, createDate, lastUpdate, lastUpdateBy);
            countryList.add(country);
        }

   }

    public static void updateAppointmentListFromDatabase() throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select * from appointment");
        appointmentList.clear();
        Appointment appointment;
        
        while(rs.next())
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int appointmentId = rs.getInt("appointmentId");
            Timestamp startTimeStamp =rs.getTimestamp("start");
            ZonedDateTime zdtStartTime = startTimeStamp.toLocalDateTime().atZone(ZoneId.of("UTC"));
            Timestamp endTimeStamp =rs.getTimestamp("end");
            ZonedDateTime zdtEndTime = endTimeStamp.toLocalDateTime().atZone(ZoneId.of("UTC"));
            
            String startTime = zdtStartTime.format(formatter).toString();
            String endTime = zdtEndTime.format(formatter).toString();
            String createDate = rs.getString("createDate");
            String createBy = rs.getString("createdBy");
            String lastUpdate = rs.getString("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            String type = rs.getString("type");
            int customerId = rs.getInt("customerId");
            int userId = rs.getInt("userId");
            
            appointment = new Appointment(appointmentId, customerId, startTime, endTime, createDate, createBy, lastUpdate, lastUpdateBy, type, userId);           
            appointmentList.add(appointment);
        }
        rs =  stmt.executeQuery("select * from customer");
        
        while(rs.next())
        {
            int customerId = rs.getInt("customerId");
            String customerName = rs.getString("customerName");
            
            for (int i = 0; i < appointmentList.size(); i++) {
                if(appointmentList.get(i).getCustomerID() == customerId)
                {
                    appointmentList.get(i).setCustomerName(customerName);                    
                }
            }            
        }
        rs = stmt.executeQuery("select * from user");
        
        while(rs.next())
        {
            int userId = rs.getInt("userId");
            String userName = rs.getString("userName");
            
            for (int i = 0; i < appointmentList.size(); i++) {
                if(appointmentList.get(i).getUserID() == userId)
                {
                    appointmentList.get(i).setUserName(userName);                    
                }
            }
        }      
        
        
   }
    
    private void setCustomerInformationForEdit() throws IOException
    {
        Stage stage;
        Parent root;
        stage = (Stage) editCustomerBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Customer_Screen.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        Customer customer;
        Address address;
        City city;
        Customer_ScreenController controller = loader.getController();
        customer = (Customer)customerTable.getSelectionModel().getSelectedItem();
        for (int i = 0; i < addressList.size(); i++) {
            if(addressList.get(i).getAddressID() == customer.getAddressID())
            {
                address = addressList.get(i);
                
                    for (int j = 0; j < cityList.size(); j++) {
                    if(cityList.get(j).getCityID() == address.getCityID())
                    {
                        city = cityList.get(j);
                        controller.setFieldsForUpdate(customer, address, city);
                    }
                }
            }
        }              
    }
    
    private void setAppointmentInformationForEdit() throws IOException
    {
        Stage stage;
        Parent root;
        stage = (Stage) editAppointmentBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment_Screen.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        Appointment appointment;
        Appointment_ScreenController controller = loader.getController();
        appointment = (Appointment)appointmentsTable.getSelectionModel().getSelectedItem();
        controller.setFieldsForUpdate(appointment);
    }
    
    private Boolean validateThatItemHasBeenSelected(TableView table)
    {
        if (table.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("NO CUSTOMER SELECTED");
            alert.setContentText("A customer must be selected");
            alert.showAndWait();
            return false;
        }
        return true;       
    }
    
    private Boolean deleteCustomer() throws SQLException
    {
        
        if (customerTable.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("NO CUSTOMER SELECTED");
            alert.setContentText("A customer must be selected");
            alert.showAndWait();
        }
        else
        {
            Customer customer = (Customer)customerTable.getSelectionModel().getSelectedItem();
            int id = customer.getCustomerID();
            Statement stmt = conn.createStatement();
            Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + customer.getCustomerName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) 
            {
                for (int i = 0; i < appointmentList.size(); i++) {
                    System.out.println("for list: " + appointmentList.get(i).getCustomerID());
                    if (appointmentList.get(i).getCustomerID() == id)
                    {
                        Alert a = new Alert(INFORMATION);
                        a.setHeaderText("INVALID DELETE");
                        a.setContentText("You cannot delete a customer for whom\n an appointment is currently scheduled");
                        a.showAndWait();
                        return false;
                    }                   
                }
                String sql = "delete from customer where customerId = " + id + ";";
                int rs =  stmt.executeUpdate(sql);
                return true;
            }
        }
        return false;
    }

    private Boolean deleteAppointment() throws SQLException
    {
        
        if (appointmentsTable.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("NO APPOINTMENT SELECTED");
            alert.setContentText("An appointment must be selected");
            alert.showAndWait();
        }
        else
        {
            Appointment appointment = (Appointment)appointmentsTable.getSelectionModel().getSelectedItem();
            int id = appointment.getAppointmentID();
            Statement stmt = conn.createStatement();
            Alert alert = new Alert(AlertType.CONFIRMATION, "Delete this appointment ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) 
            {
                String sql = "delete from appointment where appointmentId = " + id + ";";
                int rs =  stmt.executeUpdate(sql);
                return true;
            }
        }
        return false;
    }
    
    @FXML
    private Boolean isInWeekRange(Appointment appointment)
    {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime appointmentDate = LocalDateTime.parse(appointment.getStartTime(), inputFormat);
        if(appointmentDate.isAfter(today.plusDays(7)))
        {
            return false;
        }
        return true;
    }
    
    @FXML
    private Boolean isInMonthRange(Appointment appointment)
    {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime appointmentDate = LocalDateTime.parse(appointment.getStartTime(), inputFormat);
        if(appointmentDate.isAfter(today.plusMonths(1)))
        {
            return false;
        }
        return true;
    }
    
    public static void alertIfMeetingWithin15Minutes()
    {
        String customer = " ";
        String time = " ";
        Boolean meeting = false;
        for (int i = 0; i < appointmentList.size(); i++) 
        {
            if (appointmentList.get(i).getUserName().equals(AppointmentScheduler.currentSessionUser)) 
            {
                System.out.println(appointmentList.get(i).getUserName() + "    " + AppointmentScheduler.currentSessionUser);
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime appointmentTime = LocalDateTime.parse(appointmentList.get(i).getStartTime(), inputFormat);
                if(appointmentTime.isEqual(LocalDateTime.now().plusMinutes(15)) || (appointmentTime.isBefore(LocalDateTime.now().plusMinutes(15)) && appointmentTime.isAfter(LocalDateTime.now())))
                {
                    meeting = true;
                    customer = appointmentList.get(i).getCustomerName();
                    time = appointmentList.get(i).getStartTime();
                    
                }
            }
        }
        
        if (meeting) {
            String alertMsg = "Upcoming appointment with: " + customer + "\nat " + time;
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("Calendar Notification");
            alert.setContentText(alertMsg);
            alert.showAndWait();
        }
        else
        {
            String alertMsg = "You have no appointments in the next 15 minutes";
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("Calendar Notification");
            alert.setContentText(alertMsg);
            alert.showAndWait();
        }
    }
}