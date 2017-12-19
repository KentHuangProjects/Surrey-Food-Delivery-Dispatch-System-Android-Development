package LocalOrders;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import objectstodb.Order;
import t27.surreyfooddeliverycompany.InputValidation;
import t27.surreyfooddeliverycompany.R;

/**
 * Created by Kent on 2017-05-22.
 */

public class PlaceNewOrder {
    private
    String TAG = "PlaceNewOrder";

    public static void rest_new(View dialogView, Context context) {
        //get EditTexts from xml
        EditText restaurant_name_et = (EditText) dialogView.findViewById(R.id.restaurant_name_edittext);
        EditText restaurant_phone_et = (EditText) dialogView.findViewById(R.id.restaurant_phone_edittext);
        EditText restaurant_email_et = (EditText) dialogView.findViewById(R.id.restaurant_email_edittext);
        EditText restaurant_address_et = (EditText) dialogView.findViewById(R.id.restaurant_address_edittext);
        EditText ready_time_et = (EditText) dialogView.findViewById(R.id.restaurant_estimatedTime_edittext);
        EditText total_amount_et = (EditText) dialogView.findViewById(R.id.restaurant_totalAmount_edittext);
        EditText customer_name_et = (EditText) dialogView.findViewById(R.id.restaurant_custname_edittext);
        EditText customer_phone_et = (EditText) dialogView.findViewById(R.id.restaurant_custphone_edittext);
        EditText customer_address_et = (EditText) dialogView.findViewById(R.id.restaurant_custlocation_edittext);
        EditText customer_address_detail_et = (EditText) dialogView.findViewById(R.id.restaurant_custlocation_detail_edittext);
        EditText order_detail_et = (EditText) dialogView.findViewById(R.id.cust_order_detail_edittext);
        RadioGroup preferred_payment_method_RadioGroup = (RadioGroup) dialogView.findViewById(R.id.preferred_payment_radioGroup);
        //get input strings from EditTexts
        String restaurant_name = restaurant_name_et.getText().toString();
        String restaurant_phone = restaurant_phone_et.getText().toString();
        String restaurant_email = restaurant_email_et.getText().toString();
        String restaurant_address = restaurant_address_et.getText().toString();
        String  ready_time = ready_time_et.getText().toString();
        String total_amount = total_amount_et.getText().toString();
        String customer_name = customer_name_et.getText().toString();
        String customer_phone = customer_phone_et.getText().toString();
        String customer_address = customer_address_et.getText().toString();
        String customer_address_detail = customer_address_detail_et.getText().toString();
        String order_detail = order_detail_et.getText().toString();
        RadioButton preferred_payment_method_RadioButton = (RadioButton) dialogView.findViewById(preferred_payment_method_RadioGroup.getCheckedRadioButtonId());
        String preferred_payment_method = preferred_payment_method_RadioButton.getText().toString();

        //input validations
        if (!InputValidation.isValidName(restaurant_name)) {
            restaurant_name_et.setError("invalid name");
            Toast.makeText(context, "Please enter a valid restaurant name.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidPhoneNumber(restaurant_phone)) {
            restaurant_phone_et.setError("invalid phone number");
            Toast.makeText(context, "Please enter a valid restaurant phone number.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidAddress(restaurant_address)) {
            restaurant_address_et.setError("invalid address");
            Toast.makeText(context, "Please enter a valid pick-up location.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidReadyTime(ready_time)) {
            ready_time_et.setError("");
            Toast.makeText(context, "Please enter an estimate ready time (0-60 minutes).",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidAmount(total_amount)) {
            total_amount_et.setError("");
            Toast.makeText(context, "Please enter a valid total amount.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidName(customer_name)) {
            customer_name_et.setError("invalid name");
            Toast.makeText(context, "Please enter a valid customer name.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidPhoneNumber(customer_phone)) {
            customer_phone_et.setError("invalid phone number");
            Toast.makeText(context, "Please enter a valid customer phone number.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidAddress(customer_address)) {
            customer_address_et.setError("invalid address");
            Toast.makeText(context, "Please enter a valid drop-off location.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!InputValidation.isValidOrderDetail(order_detail)) {
            order_detail_et.setError("invalid order detail");
            Toast.makeText(context, "Please enter order details.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!CheckConnection.isOnline(context)) {
            Toast.makeText(context, "No Network",
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
                preferred_payment_method,
        context);
    }

    private static void requestDriver(String restaurant_name,
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
                                      String preferred_payment_method,
                                      final Context context) {

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = mDatabaseRef.child("order").push();
        String orderUid = orderRef.getKey();
        //get the token for notification to store in the order in db
        Order newOrder = new Order(orderUid,
                null,
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

        final Order newOrderSaved = newOrder;

        orderRef.setValue(newOrder, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Order could not be saved " + databaseError.getMessage());
                } else {



                    System.out.println("Order successfully.");

                    //if validations passed, display a success toast
                    Toast.makeText(context, "Driver request sent.",
                            Toast.LENGTH_SHORT).show();



                }
            }
        });
    }

    public static void cust_new(View dialogView, Context context) {
        //get EditTexts from xml
        EditText name_et = (EditText)dialogView.findViewById(R.id.cust_order_name_edittext);
        EditText  phone_et = (EditText)dialogView.findViewById(R.id.cust_order_phone_edittext);
        EditText  email_et = (EditText)dialogView.findViewById(R.id.cust_order_email_edittext);
        EditText address_et = (EditText)dialogView.findViewById(R.id.cust_order_address_edittext);
        EditText address_detail_et = (EditText)dialogView.findViewById(R.id.cust_order_address_detail_edittext);
        EditText order_detail_et = (EditText)dialogView.findViewById(R.id.cust_order_detail_edittext);
        RadioGroup preferred_payment_method_RadioGroup = (RadioGroup) dialogView.findViewById(R.id.preferred_payment_radioGroup);

        //get input strings from EditTexts
        String name = name_et.getText().toString();
        String phone = phone_et.getText().toString();
        String email = email_et.getText().toString();
        String address = address_et.getText().toString();
        String address_detail = address_detail_et.getText().toString();
        String order_detail = order_detail_et.getText().toString();
        RadioButton preferred_payment_method_RadioButton = (RadioButton) dialogView.findViewById(preferred_payment_method_RadioGroup.getCheckedRadioButtonId());
        String preferred_payment_method = preferred_payment_method_RadioButton.getText().toString();


        //input validations
        if (!InputValidation.isValidName(name)) {
            name_et.setError("invalid name");
            Toast.makeText(context, "Please enter a valid name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!InputValidation.isValidPhoneNumber(phone)){
            phone_et.setError("invalid phone number");
            Toast.makeText(context, "Please enter a valid phone number",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!InputValidation.isValidAddress(address)){
            address_et.setError("invalid address");
            Toast.makeText(context, "Please enter a valid address",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!InputValidation.isValidOrderDetail(order_detail)){
            order_detail_et.setError("invalid order detail");
            Toast.makeText(context, "Please enter order details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!CheckConnection.isOnline(context)) {
            Toast.makeText(context, "No Network",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        //place order
        placeOrder(null,
                name,
                phone,
                email,
                address,
                address_detail,
                order_detail,
                preferred_payment_method,
                context);
    }

    //TODO send order info to dispatcher/db
    private static void placeOrder(String token,
                                   String name,
                                   String phone,
                                   String email,
                                   String address,
                                   String address_detail,
                                   String order_detail,
                                   String preferred_payment_method,
                                   final Context context){

        //-------------order initialization-------------------------------------------------

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("order").push();
        String orderUid = orderRef.getKey();
        Order newOrder = new Order( orderUid,
                token,
                "customer",
                name,
                phone,
                address,
                order_detail,
                preferred_payment_method,
                "pending");

        newOrder.setDropoff_email(email);
        newOrder.setDropoff_address_detail(address_detail);

        //-------------------------------------------------------
        final Order newOrderSaved = newOrder;

   /*     //email for saving order into sharedPreference
        final String finalLoginEmail = (loginEmail==null)?"guest":loginEmail;*/

        orderRef.setValue(newOrder, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Order could not be saved " + databaseError.getMessage());
                } else {
                    //Log.d(TAG, "onComplete: " + CachedOrderPrefrence.getOrdersJs(getApplicationContext(),
                    //finalLoginEmail));
                    System.out.println("Order successfully.");

                    //if validations passed, display a success toast
                    Toast.makeText(context, "Order successfully placed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*String notification_token,
        String orderType,
        String drop_cust_name,
        String drop_phone,
        String drop_address,
        String order_detail,
        String payment_method,
        String state*/
    }
}
