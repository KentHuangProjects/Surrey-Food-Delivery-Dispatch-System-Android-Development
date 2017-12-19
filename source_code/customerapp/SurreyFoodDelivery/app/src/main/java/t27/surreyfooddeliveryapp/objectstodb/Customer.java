package t27.surreyfooddeliveryapp.objectstodb;

import java.util.HashMap;

import com.google.firebase.database.ServerValue;

public class Customer {
    private String accountID;
    private String accountType;
    private String email;

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

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    private String password;
    private String name;
    private String number;
    private String address;
    private String addressDetail;
    private HashMap<String, Object> timestampCreated;

    public Customer(String accountID, String accountType, String email, String password, String name, String number, String address, String addressDetail) {
        this.accountID = accountID;
        this.accountType = accountType;
        this.email = email;
        this.password = password;
        this.name = name;
        this.number = number;
        this.address = address;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
        this.addressDetail = addressDetail;
    }


    public Customer() {
    }

    public String getAccountID() {
        return accountID;
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


    public String getAddressDetail() {
        return addressDetail;
    }
}
