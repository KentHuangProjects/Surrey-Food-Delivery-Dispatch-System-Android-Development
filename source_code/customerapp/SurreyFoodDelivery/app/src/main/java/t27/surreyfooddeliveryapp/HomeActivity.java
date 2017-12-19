package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;

import t27.surreyfooddeliveryapp.objectstodb.Customer;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences shar_pre;
    ImageView icon_profile;
    Button order_btn;
    String account_type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        order_btn = (Button) findViewById(R.id.button_customer_order);
        icon_profile = (ImageView) findViewById(R.id.profile_icon);


        //get back the customer object
        shar_pre = getApplicationContext().getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shar_pre.getString("userObject", null);

        //log in as customer or restaurant
        //or logged in before
        if(json!=null) {
            Customer cur_customer = gson.fromJson(json, Customer.class);
            //get type
            account_type = cur_customer.getAccountType();
            //set button order text based on type(restaurant or customer)
            if(account_type.equals("customer")||account_type.equals("Customer")) {
                order_btn.setText(getString(R.string.customer_order_btn_text));
                icon_profile.setImageResource(R.drawable.customericon);
            } else if(account_type.equals("restaurant")||account_type.equals("Restaurant")) {
                order_btn.setText(getString(R.string.resta_order_btn_text));
                icon_profile.setImageResource(R.drawable.restauranticon);
            }
        }
    }

    public void order_click(View view) {
        if(account_type==null||account_type.equals("customer")||account_type.equals("Customer")) {
            Intent fromHomeToCustomerOrder = new Intent(this, CustomerOrderActivity.class);
            startActivity(fromHomeToCustomerOrder);
        } else if(account_type.equals("restaurant")||account_type.equals("Restaurant")) {
            Intent fromHomeToRequestDriver = new Intent(this, RequestDriverActivity.class);
            startActivity(fromHomeToRequestDriver);
        }
    }

    public void profile_click(View view) {
        //TODO distiguish guest and customer and restaurant

        Intent fromHomeToProfile = new Intent(this,ProfileActivity.class);
        startActivity(fromHomeToProfile);
    }
}
