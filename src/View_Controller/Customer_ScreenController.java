/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Address;
import static Model.Address.addressList;
import Model.City;
import static Model.City.cityList;
import Model.Country;
import static Model.Country.countryList;
import Model.Customer;
import static Model.Customer.customerList;
import static Model.DBConnection.conn;
import java.sql.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import testingjavafx.AppointmentScheduler;

/**
 * FXML Controller class
 *
 * @author simon
 */
public class Customer_ScreenController implements Initializable {
    
    @FXML
    Button backBtn;
    @FXML
    Button confirmBtn;
    
    @FXML
    RadioButton activeRadioBtn;
    @FXML
    RadioButton notActiveRadioBtn;
    @FXML
    ToggleGroup radioButtonGroup = new ToggleGroup();
    
    @FXML
    TextField idField;
    @FXML
    TextField nameField;
    @FXML
    TextField addressField;
    @FXML
    TextField cityField;
    @FXML
    TextField phoneField;
    @FXML
    TextField address2Field;
    @FXML
    TextField postalCodeField;
    
    @FXML
    Label currentUserLabel;
    
    @FXML
    ComboBox countryComboBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        activeRadioBtn.setToggleGroup(radioButtonGroup);
        notActiveRadioBtn.setToggleGroup(radioButtonGroup);
        activeRadioBtn.setSelected(true);        
        currentUserLabel.setText("Current user: " + AppointmentScheduler.currentSessionUser);
        idField.setDisable(true);
        
        try 
        {
            populateCountryComboBox();
        } 
        catch (SQLException ex)
        {
            Logger.getLogger(Customer_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setDefaultvalues();
        
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
        
        confirmBtn.setOnAction((event) -> {
        
            if (validateFields() && validatePhoneNumber())
            {
                try 
                {
                    if(!checkIfCityExists(cityField.getText()))
                    {
                        createCity(cityField.getText(), getCountryCode());
                    }
                    
                    if(!checkIfAddressExists())
                    {
                        createNewAddress(addressField.getText(), address2Field.getText(), getCityId(), postalCodeField.getText(), phoneField.getText());
                    }
                    
                    if (idField.getText().isEmpty())
                    {
                        createNewCustomer(nameField.getText(), getAddressId());
                    }
                    else
                    {
                        if (validateCityMatchesCountry()) {
                            updateCustomer(Integer.parseInt(idField.getText()), getAddressId(), getCityId());
                        }
                    }
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(Customer_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Customer_ScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }    
    
   private Boolean validateFields()
   {
       ArrayList<String> fields = new ArrayList<>();

       if (nameField.getText().isEmpty())
       {
           String field1 = "Customer Name";
           fields.add(field1);
       }
       if (addressField.getText().isEmpty())
       {
           String field1 = "Primary Address";
           fields.add(field1);
       }
       if (cityField.getText().isEmpty())
       {
           String field1 = "City";
           fields.add(field1);
       }
       if (phoneField.getText().isEmpty())
       {
           String field1 = "Phone #";
           fields.add(field1);
       }
       if (postalCodeField.getText().isEmpty())
       {
           String field1 = "Postal Code";
           fields.add(field1);
       }
       
       if(!fields.isEmpty())
       {
           String alertMessage = "The following fields cannot be left empty:\n";
             
           for (int i = 0; i < fields.size(); i++) 
           {
               alertMessage += fields.get(i) + "\n";
           }
       
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("ERROR");
            alert.setContentText(alertMessage);
            alert.showAndWait();
            return false;
       }
       return true;
   }
   
   private void setDefaultvalues()
    {
        countryComboBox.getSelectionModel().selectFirst();
        phoneField.setText("Only numbers and hyphens allowed");
        cityField.setText("New York");
        addressField.setText("123 Main St");
        address2Field.setText("not required");
        nameField.setText("John Doe");
                
    }
   
   private Boolean validateCityMatchesCountry()
   {
       City city = new City();
       Country country = new Country();
       
       for (int i = 0; i < cityList.size(); i++) {
           if (cityList.get(i).getCity().equals(cityField.getText()))
           {
               city = cityList.get(i);
           }
       }
       for (int i = 0; i < countryList.size(); i++) {
           if (countryList.get(i).getCountry().equals(countryComboBox.getValue().toString()))
           {
               country = countryList.get(i);
           }
       }
       
       if (city.getCountryID() != country.getCountryID())
       {
           System.out.println(city.getCountryID());
           System.out.println(country.getCountryID());
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("ERROR");
            alert.setContentText("Your city and country do not match");
            alert.showAndWait();
            return false;
       }
      return true; 
   }
   
   private Boolean validatePhoneNumber()
   {
       Pattern usrNamePtrn = Pattern.compile("^[0-9\\-]*$");       
       Matcher mtch = usrNamePtrn.matcher(phoneField.getText());
        if(mtch.matches()){
            return true;
        }
        Alert alert = new Alert(INFORMATION);
        alert.setHeaderText("ERROR");
        alert.setContentText("Your phone number is not valid.");
        alert.showAndWait();
        return false;
        
   }
   
   private Boolean checkIfCityExists(String city) throws SQLException
   {
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select city from city");
        
        while(rs.next())
        {
            String name = rs.getString("city");
            if (name.equals(city))
            {
                return true;
            }
        }
        return false;
   }
   
   private int getCityId() throws SQLException
   {
       String city = (String) cityField.getText();
       Statement stmt = conn.createStatement();
       ResultSet rs =  stmt.executeQuery("select * from city");
        
       while(rs.next())
       {
           if(rs.getString("city").equals(city))
           {
               return rs.getInt("cityId");
           }
       }
       return 0;       
   }
   
    private int getCountryCode() throws SQLException
    {
     String country = (String) countryComboBox.getValue();
     Statement stmt = conn.createStatement();
     ResultSet rs =  stmt.executeQuery("select * from country");

     while(rs.next())
     {
         if(rs.getString("country").equals(country))
         {
             return rs.getInt("countryId");
         }
     }
     return 0;
 }
      
    private Boolean checkIfAddressExists() throws SQLException
    {
       String address = (String) addressField.getText();
       String address2 = (String) address2Field.getText();
       String phone = (String) phoneField.getText();
       String postalCode = (String) postalCodeField.getText();
       
       Statement stmt = conn.createStatement();
       ResultSet rs =  stmt.executeQuery("select * from address");
        
       while(rs.next())
       {
           if(rs.getString("address").equals(address) &&
                   rs.getString("address2").equals(address2) &&
                   rs.getString("phone").equals(phone) &&
                   rs.getString("postalCode").equals(postalCode))
           {
               return true;
           }
       }
       return false;
    }
      
   private int getAddressId() throws SQLException
   {
       Boolean addressFound = false;
       String address = (String) addressField.getText();
       String address2 = (String) address2Field.getText();
       String phone = (String) phoneField.getText();
       String postalCode = (String) postalCodeField.getText();
       String city = (String) cityField.getText();
       String country = countryComboBox.getValue().toString();
       Statement stmt = conn.createStatement();
       ResultSet rs =  stmt.executeQuery(
               "select address.addressId, address.address, address.address2, address.phone, address.postalCode, city.city, country.country\n" +
                "from address join city\n" +
                "on address.cityId = city.cityId\n" +
                "join country on city.countryId = country.countryId;");
       while(rs.next())
       {
           if(rs.getString("address").equals(address) &&
                   rs.getString("address2").equals(address2) &&
                   rs.getString("phone").equals(phone) &&
                   rs.getString("postalCode").equals(postalCode) &&
                   rs.getString("city").equals(city) &&
                   rs.getString("country").equals(country))
           {
               addressFound = true;
               return rs.getInt("addressId");
           }

       }
       
       if (!addressFound)
       {
            createNewAddress(address, address2, getCityId(), postalCode, phone);
            rs =  stmt.executeQuery(
               "select address.addressId, address.address, address.address2, address.phone, address.postalCode, city.city, country.country\n" +
                "from address join city\n" +
                "on address.cityId = city.cityId\n" +
                "join country on city.countryId = country.countryId;");
            while(rs.next())
            {
                if(rs.getString("address").equals(address) &&
                        rs.getString("address2").equals(address2) &&
                        rs.getString("phone").equals(phone) &&
                        rs.getString("postalCode").equals(postalCode) &&
                        rs.getString("city").equals(city) &&
                        rs.getString("country").equals(country))
                {
                    addressFound = true;
                    return rs.getInt("addressId");
                }

            }
       }
       return 0;
   }
   
   private void createCity(String city, int countryId) throws SQLException
   {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       LocalDateTime currentTimeDate = LocalDateTime.now();
       String createDate = currentTimeDate.format(formatter);       
       String createdBy = AppointmentScheduler.currentSessionUser;
       Statement stmt = conn.createStatement();
       int rs =  stmt.executeUpdate("insert into city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) values"
               + " ('" + city + "', '" + countryId + "', '" + createDate + "', '" + createdBy + "', '" + createDate + "', '" + createdBy + "')");
   }
   
   private void createNewAddress(String address, String address2, int cityId, String postalCode, String phone) throws SQLException
   {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       LocalDateTime currentTimeDate = LocalDateTime.now();
       String createDate = currentTimeDate.format(formatter);       
       String createdBy = AppointmentScheduler.currentSessionUser;
       Statement stmt = conn.createStatement();
       int rs =  stmt.executeUpdate("insert into address (address, address2, cityId, postalCode, phone, createdBy, createDate, lastUpdate, lastUpdateBy)"
               + " values ('" + address + "', '" + address2 + "', '" + cityId + "', '" + postalCode + "', '" + phone + "', '" + createdBy + "', '" + createDate + "', '" + createDate + "', '" + createdBy + "')");

   }
   
   private void createNewCustomer(String customerName, int addressId) throws SQLException, IOException
   {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       LocalDateTime currentTimeDate = LocalDateTime.now();
       String createDate = currentTimeDate.format(formatter);       
       String createdBy = AppointmentScheduler.currentSessionUser;
       int active = 0;
       if (activeRadioBtn.isSelected())
       {
           active = 1;
       }
       Statement stmt = conn.createStatement();
       int rs =  stmt.executeUpdate("insert into customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)"
               + "values ('" + customerName + "', '" + addressId + "', '" + active + "', '" + createDate + "', '" + createdBy + "', '" + createDate + "', '" + createdBy + "')");

       Stage stage = (Stage) confirmBtn.getScene().getWindow();
       Parent root = FXMLLoader.load(getClass().getResource("Main_Screen.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
   }
   
   private void updateCustomer(int customerId, int addressId, int cityId) throws SQLException, IOException
   {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       LocalDateTime currentTimeDate = LocalDateTime.now();
       Customer customer = new Customer();
       
       for (int i = 0; i < customerList.size(); i++) {
           if(customerList.get(i).getCustomerID() == customerId)
               customer = customerList.get(i);
       }
       
       String customerName = nameField.getText();
       String lastUpdate = currentTimeDate.format(formatter); 
       String lastUpdateBy = AppointmentScheduler.currentSessionUser;
       String createDate = customer.getCreateDate();
       String createdBy = customer.getCreatedBy();
       
       int active = 0;
       if (activeRadioBtn.isSelected())
       {
           active = 1;
       }
       
       Statement stmt = conn.createStatement();
       int rs =  stmt.executeUpdate("update customer"
               + " set customerName = '" + customerName + "',"
               + " addressId = '" + addressId + "',"
               + " active = '" + active + "',"
               + " createDate = '" + createDate + "',"
               + " createdBy = '" + createdBy + "',"
               + " lastUpdate = '" + lastUpdate + "',"
               + " lastUpdateBy = '" + lastUpdateBy + "'"
               + " where customerId = '" + customerId + "';");
               
       Stage stage = (Stage) confirmBtn.getScene().getWindow();
       Parent root = FXMLLoader.load(getClass().getResource("Main_Screen.fxml"));
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.show();
   }
   
   private void populateCountryComboBox() throws SQLException
   {
       Statement stmt = conn.createStatement();
       ResultSet rs =  stmt.executeQuery("select * from country");
        
       while(rs.next())
       {
           String country = rs.getString("country");
           countryComboBox.getItems().add(country);
       }
   }
   
   public void setFieldsForUpdate(Customer customer, Address address, City city)
   {
       idField.setText(String.valueOf(customer.getCustomerID()));
       nameField.setText(customer.getCustomerName());
       addressField.setText(address.getAddress());
       cityField.setText(city.getCity());
       phoneField.setText(address.getPhone());
       address2Field.setText(address.getAddress2());
       postalCodeField.setText(address.getPostalCode());
       countryComboBox.getSelectionModel().select(city.getCountryID() - 1);
       if (customer.getActive() == 1)
       {
           activeRadioBtn.setSelected(true);
       }
       else
       {
           notActiveRadioBtn.setSelected(true);
       }       
   }
    
}
