/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import static Model.Customer.customerList;
import static Model.User.userList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Simon
 */
public class Appointment {
    
    public static ArrayList<Appointment> appointmentList = new ArrayList<>();
    private int appointmentID;
    private int customerID;    
    private String startTime;
    private String endTime;
    private String createDate;    
    private String createdBy;
    private String lastUpdate;
    private String lastUpdateBy;
    private String type;
    private int userID;
    private String userName;
    private String customerName;
    
    public Appointment(int appointmentID, int customerID, String startTime, String endTime, String createDate, String createdBy, String lastUpdate, String lastUpdateBy, String type, int userID, String userName, String customerName) {
        this.appointmentID = appointmentID;
        this.customerID = customerID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
        this.type = type;
        this.userID = userID;
        this.userName = userName;
        this.customerName = customerName;
    }

    public Appointment(int appointmentID, int customerID, String startTime, String endTime, String createDate, String createdBy, String lastUpdate, String lastUpdateBy, String type, int userID) {
        this.appointmentID = appointmentID;
        this.customerID = customerID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
        this.type = type;
        this.userID = userID;
    }

    public Appointment(String startTime, String endTime, String type, String customerName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.customerName = customerName;
    }
    
    
    
    

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
}
