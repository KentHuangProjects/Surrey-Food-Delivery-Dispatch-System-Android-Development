package t27.surreyfooddeliveryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import t27.surreyfooddeliveryapp.objectstodb.Order;

public class CurrentOrderActivity extends AppCompatActivity {
    SharedPreferences shar_pre;
    //ImageView icon_profile;
    String account_type = null;
    private ListView orderList;
    private DatabaseReference mDatabaseRef;
    private String cur_email;
    private String notif_tok;
    private Query query;
    private HashMap<String,Order> map_uid_to_order;
    ArrayList<Order> order_list;
    private OrderAdapter ordersAdapter;
    //private ArrayList<Order> orders;
    //private HashMap<DatabaseReference,ValueEventListener> mapOfRefToOrderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);

        //Intent_get_it = getIntent();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //listener arrayList to detach listeners in onstop
        //mapOfRefToOrderListener = new HashMap<DatabaseReference,ValueEventListener>();
        orderList = (ListView) findViewById(R.id.order_detail);
        shar_pre = getApplicationContext().getSharedPreferences(getString(R.string.User_info), Context.MODE_PRIVATE);
        cur_email = shar_pre
                .getString("curEmail",null);
        notif_tok = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
                /*//icon_profile = (ImageView) findViewById(R.id.profile_icon);
                //get back the customer object

                Gson gson = new Gson();
                String json = shar_pre.getString("userObject", null);

                //log in as customer or restaurant
                //or logged in before
                if (json != null) {
                    Customer cur_customer = gson.fromJson(json, Customer.class);
                    //get type
                    account_type = cur_customer.getAccountType();
                    //set button order text based on type(restaurant or customer)
*//*            if (account_type.equals("customer")) {
                icon_profile.setImageResource(R.drawable.customericon);
            } else if (account_type.equals("restaurant")) {
                icon_profile.setImageResource(R.drawable.restauranticon);
            }*//*

                }

                cur_email = shar_pre
                        .getString("curEmail",null);
                //if not logged in, it is a guest
                cur_email = (cur_email==null)?"guest":cur_email;
                orders = CachedOrderPrefrence.getOrderByEmail(getApplicationContext(),cur_email);

                //new order goes first
                Collections.reverse(orders);*/
            if(cur_email!=null) {
                //logged in user
                query = mDatabaseRef.child("order").orderByChild("email_Account")
                        .equalTo(cur_email);
            } else {
                //guest
                query = mDatabaseRef.child("order").orderByChild("guest_notiToken")
                        .equalTo("guest_" + notif_tok);
            }
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<HashMap<String,Order>> type_orders_list =
                            new GenericTypeIndicator<HashMap<String,Order>>() {};
                    map_uid_to_order = dataSnapshot.getValue(type_orders_list);

                    if(map_uid_to_order==null) {
                        map_uid_to_order = new HashMap<String, Order>();
                    }

                    order_list = new ArrayList<Order>(map_uid_to_order.values());
                    sortList(order_list);
                    ordersAdapter = new OrderAdapter(CurrentOrderActivity.this,order_list);

                    // Set The Adapter
                    orderList.setAdapter(ordersAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



                //Log.d("numorder", "onStart: " + orders.size());

                //Log.d("prints", "onStart: "+ orders);

                //db references related to orders stored before
                /*for (Order oneorder :orders) {
                    DatabaseReference ref = mDatabaseRef.child("order").child(oneorder.getOrderUid()).child("state");
                    ValueEventListener valueListener = new OrderChangeListener(oneorder);
                    //listening to data base for state changes
                    ref.addValueEventListener(valueListener);

                    //keep a copy to listener arrayList for detaching later in onstop
                    mapOfRefToOrderListener.put(ref,valueListener);
                }*/








    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*for (Map.Entry<DatabaseReference, ValueEventListener> entry : mapOfRefToOrderListener.entrySet())
        {
            //detach listener

            DatabaseReference re = entry.getKey();
            ValueEventListener vl = entry.getValue();
            re.removeEventListener(vl);
        }*/

    }

/*    public void logo_click(View view) {
        Intent newIntent = new Intent(this, HomeActivity.class);
        startActivity(newIntent);
    }

    public void profile_click(View view) {
        //TODO distiguish guest and customer and restaurant
        Intent fromCurrentOrderToProfile = new Intent(this, ProfileActivity.class);
        startActivity(fromCurrentOrderToProfile);
    }*/

    //order adapter for showing orders in listview
    public class OrderAdapter extends ArrayAdapter<Order> {
        public OrderAdapter(Context context, ArrayList<Order> orders) {
            super(context, 0, orders);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Order order = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item_of_list, parent, false);
            }
            // Lookup view for data population
            TextView tvDetail = (TextView) convertView.findViewById(R.id.tvDetail);
            TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
            // Populate the data into the template view using the data object
            String description;
            String status;
            if (order != null) {
                description= "Order Detail: <font color=\"red\">"+order.getOrder_detail() + "</font>";
                status = "Status: <font color=\"red\">" + order.getState() + "</font>";
                tvDetail.setText(Html.fromHtml(description), TextView.BufferType.SPANNABLE);
                tvStatus.setText(Html.fromHtml(status), TextView.BufferType.SPANNABLE);
            }

            // Return the completed view to render on screen
            return convertView;
        }
    }

/*
    public class OrderChangeListener implements ValueEventListener {

        Order oneorder;

        public OrderChangeListener(Order order) {
            this.oneorder = order;
        }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String orderState = dataSnapshot.getValue(String.class);
                        oneorder.setState(orderState);
                if(orderState == null) {
                    oneorder.setState("finished");
                }
                //real time change to ui

                new Thread(new Runnable() {
                    public void run() {

                        CachedOrderPrefrence.updateOrderByEmail(getApplicationContext(),cur_email,oneorder);

                        //function below :to updatee the ordersAdapter of the list view

                        //runOnUiThread();
                        CurrentOrderActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ordersAdapter.notifyDataSetChanged();
                                orderList.invalidateViews();
                                ;
                            }
                        });
                    }
                }).start();




            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CurrentOrderActivity.this,"Fail to update",Toast.LENGTH_SHORT).show();
            }
    }

*/
    private void sortList(ArrayList<Order> order_list) {
        Collections.sort(order_list, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if(o1.getTimestampCreated()==null||o2.getTimestampCreated()==null) {
                    return 1;
                }
                Long otime1 = o1.getDateCreatedLong();
                Long otime2 = o2.getDateCreatedLong();
                return otime2.compareTo(otime1);
            }
        });
    }
}
