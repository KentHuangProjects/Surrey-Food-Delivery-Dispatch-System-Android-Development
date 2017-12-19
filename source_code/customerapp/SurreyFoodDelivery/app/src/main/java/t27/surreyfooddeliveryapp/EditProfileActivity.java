package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import t27.surreyfooddeliveryapp.LocalOrders.CheckConnection;
import t27.surreyfooddeliveryapp.objectstodb.Customer;

public class EditProfileActivity extends AppCompatActivity {

    Customer cur_customer = null;
    SharedPreferences shar_pre;
    String name;
    String phone;
    String address;
    String address_detail;
    EditText name_et;
    EditText phone_et;
    EditText address_et;
    EditText address_detail_et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //get back the customer object
        shar_pre = getApplicationContext().getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shar_pre.getString("userObject", null);

        //log in as customer or restaurant
        //or logged in before
        if (json == null)
            return;

        cur_customer = gson.fromJson(json, Customer.class);

        name = cur_customer.getName();
        phone = cur_customer.getNumber();
        address = cur_customer.getAddress();
        address_detail = cur_customer.getAddressDetail();

        name_et = (EditText) findViewById(R.id.edit_name);
        phone_et = (EditText) findViewById(R.id.edit_number);
        address_et = (EditText) findViewById(R.id.edit_address);
        address_detail_et = (EditText) findViewById(R.id.edit_address_detail);

        name_et.setText(name);
        phone_et.setText(phone);
        address_et.setText(address);
        address_detail_et.setText(address_detail);
    }

    public void logo_click(View view) {
        Intent newIntent = new Intent(this, HomeActivity.class);
        startActivity(newIntent);
    }

    public void save_edit_pro_click(View view) {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference currentUserRef = mDatabaseRef.child("users").child(uid);

        final String name_updated = name_et.getText().toString();
        final String phone_updated = phone_et.getText().toString();
        final String address_updated = address_et.getText().toString();
        final String address_detail_updated = address_detail_et.getText().toString();


        if (!InputValidation.isValidName(name_updated)) {
            name_et.setError("Please enter a valid name");
            Toast.makeText(EditProfileActivity.this, "Please enter a name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidPhoneNumber(phone_updated)){
            phone_et.setError("Please enter a valid phone number");
            Toast.makeText(EditProfileActivity.this, "Please enter a valid phone number",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!CheckConnection.isOnline(EditProfileActivity.this)) {
            Toast.makeText(EditProfileActivity.this, "No Network",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //-----update db
        Map<String, Object> userInfoUpdates = new HashMap<String, Object>();
        userInfoUpdates.put("name", name_updated);
        userInfoUpdates.put("number", phone_updated);
        userInfoUpdates.put("address", address_updated);
        userInfoUpdates.put("addressDetail", address_detail_updated);
        currentUserRef.updateChildren(userInfoUpdates);
        //end-----update db

        currentUserRef.updateChildren(userInfoUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    Toast.makeText(EditProfileActivity.this, databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();

                } else {
                    System.out.println("Data saved successfully.");

                    //----modify the object in sharedPreference
                    shar_pre = getApplicationContext().getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
                    Gson gson = new Gson();
                    String json = shar_pre.getString("userObject", null);

                    //log in as customer or restaurant
                    //or logged in before
                    if (json != null) {
                        Customer cur_customer = gson.fromJson(json, Customer.class);
                        cur_customer.setName(name_updated);
                        cur_customer.setNumber(phone_updated);
                        cur_customer.setAddress(address_updated);
                        cur_customer.setAddressDetail(address_detail_updated);

                        //store back to sharedpre
                        SharedPreferences.Editor prefsEditor = shar_pre.edit();
                        gson = new Gson();
                        json = gson.toJson(cur_customer);
                        prefsEditor.putString("userObject", json);
                        prefsEditor.apply();
                        //end----store back
                    }
                    //end ------modify the object in sharedPreference

                    Toast.makeText(EditProfileActivity.this, "Profile updated",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });
    }
}
