package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import t27.surreyfooddeliveryapp.objectstodb.Customer;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences shar_pre;
    TextView name_tv;
    TextView address_tv;
    TextView address_detail_tv;
    TextView email_tv;
    TextView phone_tv;
    Button editProfile_btn;
    Button changePass_btn;
    Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get input fields
        name_tv = (TextView) findViewById(R.id.name);
        address_tv = (TextView) findViewById(R.id.address);
        address_detail_tv = (TextView) findViewById(R.id.address_detail);
        email_tv = (TextView) findViewById(R.id.email);
        phone_tv = (TextView) findViewById(R.id.phone);
        editProfile_btn = (Button) findViewById(R.id.edit_profile);
        changePass_btn = (Button) findViewById(R.id.change_password);
        logout_btn = (Button) findViewById(R.id.logout);


    }

    @Override
    protected void onStart() {
        super.onStart();

        //get back the customer object
        shar_pre = getApplicationContext().getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shar_pre.getString("userObject", null);
        if (json != null) {
            Customer cur_customer = gson.fromJson(json, Customer.class);
            String cur_name = cur_customer.getName();
            String cur_address = cur_customer.getAddress();
            String cur_address_detail = cur_customer.getAddressDetail();
            String cur_email = cur_customer.getEmail();
            String cur_phone = cur_customer.getNumber();

            editProfile_btn.setEnabled(true);
            changePass_btn.setEnabled(true);
            editProfile_btn.getBackground().setColorFilter(null);
            changePass_btn.getBackground().setColorFilter(null);

            name_tv.setText(cur_name);
            address_tv.setText(cur_address);
            address_detail_tv.setText(cur_address_detail);
            email_tv.setText(cur_email);
            phone_tv.setText(cur_phone);
        } else {
            //logout_btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            editProfile_btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            changePass_btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

            editProfile_btn.setTextColor(Color.GRAY);
            changePass_btn.setTextColor(Color.GRAY);
            //logout_btn.setTextColor(Color.GRAY);

            editProfile_btn.setEnabled(false);
            changePass_btn.setEnabled(false);
        }

    }

    public void editProfile_click(View view) {
        Intent from_profile_to_edit_pro = new Intent(this, EditProfileActivity.class);
        startActivity(from_profile_to_edit_pro);
    }

    public void changePassword_click(View view) {
        Intent from_profile_to_chagng_pass = new Intent(this, ChangePasswordActivity.class);
        startActivity(from_profile_to_chagng_pass);
    }

    public void logout_click(View view) {
        //login out function
        logout_fun();

        Intent from_profile_to_logout = new Intent(this, WelcomeActivity.class);
        from_profile_to_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(from_profile_to_logout);
    }

    public void current_order_click(View view) {
        Intent from_profile_to_currentorder = new Intent(this, CurrentOrderActivity.class);
        from_profile_to_currentorder.putExtra("caller_activity", "ProfileActivity");
        startActivity(from_profile_to_currentorder);
    }

    private void logout_fun() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        SharedPreferences userInfo_Prefs = getApplicationContext().getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = userInfo_Prefs.edit();
        prefsEditor.putString("userUID", null);
        prefsEditor.putString("userObject", null);
        prefsEditor.putString("curEmail",null);
        prefsEditor.apply();
    }
}
