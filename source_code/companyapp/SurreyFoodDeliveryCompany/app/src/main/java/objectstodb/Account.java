package objectstodb;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Kent on 2017-05-04.
 */

public class Account {
    //for drivers
    private String status;
    private String idle;
    private String nofToken;
    public Double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public Double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    private double lat;
    private double lng;
    private String accountUID;
    private String accountType;
    private String email;
    private String password;
    private String name;
    private String number;
    private String address;
    private HashMap<String, Object> timestampCreated;

    public Account(String status, String idle, String accountType, String email, String password, String name, String number, String address,String accountUID) {
        this.status = status;
        this.idle = idle;
        this.accountType = accountType;
        this.email = email;
        this.password = password;
        this.name = name;
        this.number = number;
        this.address = address;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
        this.accountUID = accountUID;
    }


    public Account() {
    }

    public String getNofToken() {
        return nofToken;
    }

    public void setNofToken(String nofToken) {
        this.nofToken = nofToken;
    }

    public String getStatus() {
        return status;
    }

    public String getIdle() {
        return idle;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public String getAccountUID() {
        return accountUID;
    }

    public void setAccountUID(String accountUID) {
        this.accountUID = accountUID;
    }
}
