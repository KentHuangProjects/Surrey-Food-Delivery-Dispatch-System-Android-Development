package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import t27.surreyfooddeliveryapp.LocalOrders.CachedOrderPrefrence;
import t27.surreyfooddeliveryapp.LocalOrders.CheckConnection;
import t27.surreyfooddeliveryapp.objectstodb.Customer;
import t27.surreyfooddeliveryapp.objectstodb.Order;

public class RequestDriverActivity extends BaseActivity {
    private String TAG = "RequestDriverActivity";
    SharedPreferences shared_preference;

    //input fields - restaurant info
    private String restaurant_name;
    private String restaurant_phone;
    private String restaurant_email;
    private String restaurant_address;
    private String ready_time;
    private String total_amount;

    //input fields - customer info
    private String customer_name;
    private String customer_phone;
    private String customer_address;
    private String customer_address_detail;
    private String order_detail;
    private String preferred_payment_method;

    //xml input fields - restaurant
    private EditText restaurant_name_et;
    private EditText restaurant_phone_et;
    private EditText restaurant_email_et;
    private EditText restaurant_address_et;
    private EditText ready_time_et;
    private EditText total_amount_et;

    //xml input fields - customer
    private EditText customer_name_et;
    private EditText customer_phone_et;
    private EditText customer_address_et;
    private EditText customer_address_detail_et;
    private EditText order_detail_et;
    private RadioGroup preferred_payment_method_RadioGroup;
    private RadioButton preferred_payment_method_RadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);

        //get EditTexts from xml
        restaurant_name_et = (EditText) findViewById(R.id.restaurant_name_edittext);
        restaurant_phone_et = (EditText) findViewById(R.id.restaurant_phone_edittext);
        restaurant_email_et = (EditText) findViewById(R.id.restaurant_email_edittext);
        restaurant_address_et = (EditText) findViewById(R.id.restaurant_address_edittext);
        ready_time_et = (EditText) findViewById(R.id.restaurant_estimatedTime_edittext);
        total_amount_et = (EditText) findViewById(R.id.restaurant_totalAmount_edittext);
        customer_name_et = (EditText) findViewById(R.id.restaurant_custname_edittext);
        customer_phone_et = (EditText) findViewById(R.id.restaurant_custphone_edittext);
        customer_address_et = (EditText) findViewById(R.id.restaurant_custlocation_edittext);
        customer_address_detail_et = (EditText) findViewById(R.id.restaurant_custlocation_detail_edittext);
        order_detail_et = (EditText) findViewById(R.id.cust_order_detail_edittext);
        preferred_payment_method_RadioGroup = (RadioGroup) findViewById(R.id.preferred_payment_radioGroup);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //get back the customer object
        shared_preference = getApplicationContext().getSharedPreferences(getString(R.string.User_info),
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared_preference.getString("userObject", null);
        if (json != null) {
            Customer cur_customer = gson.fromJson(json, Customer.class);
            String cur_name = cur_customer.getName();
            String cur_address = cur_customer.getAddress();
            String cur_address_detail = cur_customer.getAddressDetail();
            String cur_email = cur_customer.getEmail();
            String cur_phone = cur_customer.getNumber();

            restaurant_name_et.setText(cur_name);
            restaurant_phone_et.setText(cur_phone);
            restaurant_email_et.setText(cur_email);
            restaurant_address_et.setText(cur_address);
        }
    }

    public void logo_click(View view) {
        Intent newIntent = new Intent(this, HomeActivity.class);
        startActivity(newIntent);
    }

    public void restaurant_order_click(View view) {
        //get input strings from EditTexts
        restaurant_name = restaurant_name_et.getText().toString();
        restaurant_phone = restaurant_phone_et.getText().toString();
        restaurant_email = restaurant_email_et.getText().toString();
        restaurant_address = restaurant_address_et.getText().toString();
        ready_time = ready_time_et.getText().toString();
        total_amount = total_amount_et.getText().toString();
        customer_name = customer_name_et.getText().toString();
        customer_phone = customer_phone_et.getText().toString();
        customer_address = customer_address_et.getText().toString();
        customer_address_detail = customer_address_detail_et.getText().toString();
        order_detail = order_detail_et.getText().toString();
        preferred_payment_method_RadioButton = (RadioButton) findViewById(
                preferred_payment_method_RadioGroup.getCheckedRadioButtonId());
        preferred_payment_method = preferred_payment_method_RadioButton.getText().toString();

        //input validations
        if (!InputValidation.isValidName(restaurant_name)) {
            restaurant_name_et.setError("invalid name");
            Toast.makeText(RequestDriverActivity.this, "Please enter a valid restaurant name.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidPhoneNumber(restaurant_phone)) {
            restaurant_phone_et.setError("invalid phone number");
            Toast.makeText(RequestDriverActivity.this, "Please enter a valid restaurant phone number.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidAddress(restaurant_address)) {
            restaurant_address_et.setError("invalid address");
            Toast.makeText(RequestDriverActivity.this, "Please enter a valid pick-up location.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidReadyTime(ready_time)) {
            ready_time_et.setError("");
            Toast.makeText(RequestDriverActivity.this, "Please enter an estimate ready time (0-60 minutes).",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidAmount(total_amount)) {
            total_amount_et.setError("");
            Toast.makeText(RequestDriverActivity.this, "Please enter a valid total amount.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidName(customer_name)) {
            customer_name_et.setError("invalid name");
            Toast.makeText(RequestDriverActivity.this, "Please enter a valid customer name.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidPhoneNumber(customer_phone)) {
            customer_phone_et.setError("invalid phone number");
            Toast.makeText(RequestDriverActivity.this, "Please enter a valid customer phone number.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidAddress(customer_address)) {
            customer_address_et.setError("invalid address");
            Toast.makeText(RequestDriverActivity.this, "Please enter a valid drop-off location.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidOrderDetail(order_detail)) {
            order_detail_et.setError("invalid order detail");
            Toast.makeText(RequestDriverActivity.this, "Please enter order details.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!CheckConnection.isOnline(RequestDriverActivity.this)) {
            Toast.makeText(RequestDriverActivity.this, "No Network",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //send driver request
        requestDriver(restaurant_name,
                restaurant_phone,
                restaurant_email,
                restaurant_address,
                ready_time,
                total_amount,
                customer_name,
                customer_phone,
                customer_address,
                customer_address_detail,
                order_detail,
                preferred_payment_method);
    }

    //TODO send order info to dispatcher/db
    private void requestDriver(String restaurant_name,
                               String restaurant_phone,
                               String restaurant_email,
                               String restaurant_address,
                               String ready_time,
                               String total_amount,
                               String customer_name,
                               String customer_phone,
                               String customer_address,
                               String customer_address_detail,
                               String order_detail,
                               String preferred_payment_method) {

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = mDatabaseRef.child("order").push();
        String orderUid = orderRef.getKey();
        //get the token for notification to store in the order in db
        SharedPreferences tokenPre = getApplicationContext().getSharedPreferences("notifToken",
                Context.MODE_PRIVATE);
        String token = tokenPre.getString("token", null);
        //Log.d("tokenM",token);
        Order newOrder = new Order(orderUid,
                token,
                "restaurant",
                customer_name,
                customer_phone,
                customer_address,
                order_detail,
                preferred_payment_method,
                "pending");
        newOrder.setRest_name(restaurant_name);
        newOrder.setRest_phone(restaurant_phone);
        newOrder.setRest_email(restaurant_email);
        newOrder.setRest_address(restaurant_address);
        newOrder.setRest_ready_min(ready_time);
        newOrder.setCust_total(total_amount);
        newOrder.setDropoff_address_detail(customer_address_detail);

        //wont be null because rest has to login
        final String loginEmail = shared_preference
                .getString("curEmail", null);

        newOrder.setEmail_Account(loginEmail);
        final Order newOrderSaved = newOrder;

        showProgressDialog();


        orderRef.setValue(newOrder, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Order could not be saved " + databaseError.getMessage());
                } else {
                    Log.d(TAG, "onComplete: " + CachedOrderPrefrence.getOrdersJs(
                            getApplicationContext(), loginEmail));
                    System.out.println("Order successfully.");

                    //if validations passed, display a success toast
                    Toast.makeText(RequestDriverActivity.this, "Driver request sent.",
                            Toast.LENGTH_SHORT).show();

                    //go to currentOrders activity
                    Intent from_cust_order_to_current_order = new Intent(
                            RequestDriverActivity.this, CurrentOrderActivity.class);
                    from_cust_order_to_current_order.putExtra("caller_activity",
                            "CustomerOrderActivity");
                    finish();
                    startActivity(from_cust_order_to_current_order);
                }

                hideProgressDialog();
            }
        });


    }
}
