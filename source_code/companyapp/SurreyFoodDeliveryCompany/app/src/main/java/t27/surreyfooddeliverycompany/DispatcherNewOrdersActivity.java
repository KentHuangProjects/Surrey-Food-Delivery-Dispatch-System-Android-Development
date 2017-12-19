package t27.surreyfooddeliverycompany;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import LocalOrders.CheckConnection;
import LocalOrders.InProgressAdapter;
import LocalOrders.NewOrderAdapter;
import LocalOrders.newAndInProgressFun;
import objectstodb.Account;
import objectstodb.Order;

import static LocalOrders.PlaceNewOrder.cust_new;
import static LocalOrders.PlaceNewOrder.rest_new;

public class DispatcherNewOrdersActivity extends BaseActivity {
    private Intent intent;
    private TabHost tabHost;
    private DatabaseReference mDatabaseRef;
    private ListView listview;
    private TextView name;
    private TextView address;
    private TextView email;
    private TextView phoneNumber;
    private Order order;
    private AlertDialog alert;

    //all the order records grabbed from db when this activity is loaded the first time
    private List<Order> orders_list;
    private HashMap<String,Order> map_uid_to_order;
    //two lists in two tabs
    private ArrayList<Order> newOrder_list;
    private ArrayList<Order> inProgress_list;

    private NewOrderAdapter new_ordersAdapter;
    private InProgressAdapter inprogress_orderAdapter;

    private boolean initialOrdersLoad;

    private FirebaseListAdapter<Account> driversAdapter;

    private ListView neworderListView;
    private ListView inprogressListView;
    private ChildEventListener childAddedListener;
    private Query queryOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher_new_orders);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        listview = (ListView) findViewById(R.id.drivers_list);
        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.edit_address);
        email = (TextView) findViewById(R.id.email);
        phoneNumber = (TextView) findViewById(R.id.phone);

        neworderListView = (ListView) findViewById(R.id.new_order_list);
        inprogressListView =(ListView)findViewById(R.id.inprogress_order_list);

        setTabs(tabHost);
        Query queryDrivers = mDatabaseRef.child("driver").orderByChild("status").equalTo("online");

        driversAdapter = new FirebaseListAdapter<Account>(
                DispatcherNewOrdersActivity.this, Account.class,
                R.layout.dispatcher_drivers_list, queryDrivers) {
            @Override
            protected void populateView(View view, Account account, int i) {
                TextView text = (TextView) view.findViewById(R.id.driver);

                //letter image
                ImageView image = (ImageView) view.findViewById(R.id.image_view);
                String driverDetails;

                if (account.getStatus().compareTo("online") == 0) {

                    ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                    // generate random color
                    int color = generator.getColor(account.getName().substring(0,1));
                    //set the first letter as the image content
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(account.getName().substring(0,1), color);
                    //set the image
                    image.setImageDrawable(drawable);

                    driverDetails = account.getName() + "\n# taken order(s): " +
                            account.getIdle() + "\nPhone Number: " + account.getNumber();
                    text.setText(driverDetails);
                }
            }
        };

        listview.setAdapter(driversAdapter);
        setProfileInfo();

        //new order page and in-progress page
        map_uid_to_order = new HashMap<String,Order>();
        initialOrdersLoad = false;

        //-----------start---------------------get orders from db and merge them with local records
        queryOrders = mDatabaseRef.child("order");

        queryOrders.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //after the initial is loaded
                if(initialOrdersLoad) {
                    Order neworder = dataSnapshot.getValue(Order.class);
                    //add new order to the new order list ui
                    map_uid_to_order.put(neworder.getOrderUid(),neworder);
                    newOrder_list.add(neworder);
                    // Sorting
                    sortList(newOrder_list);


                    new_ordersAdapter.notifyDataSetChanged();

                    childAddedListener = this;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Order changeorder = dataSnapshot.getValue(Order.class);
                String driv = changeorder.getDriverUID();
                String changedState = changeorder.getState();
                String orderUID = changeorder.getOrderUid();

                /*if(dataSnapshot.exists()) {
                    return;
                }


                */
                if(changedState.equals("processing")&&driv!=null) {
                    Iterator<Order> it = newOrder_list.iterator();
                    while (it.hasNext()) {
                        if (it.next().getOrderUid().equals(orderUID)) {
                            Log.d("DispatcherAct", "onChildChanged: remove old item " +orderUID+"from inProgress_list" );
                            it.remove();
                            new_ordersAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } else if(changedState.equals("delivering")) {
                    Iterator<Order> it = inProgress_list.iterator();
                    while (it.hasNext()) {
                        if (it.next().getOrderUid().equals(orderUID)) {
                            Log.d("DispatcherAct", "onChildChanged: remove old item " +orderUID+"from inProgress_list" );
                            it.remove();
                            break;
                        }
                    }
                } else if(changedState.equals("finished")) {
                    Iterator<Order> it = inProgress_list.iterator();
                    while (it.hasNext()) {
                        if (it.next().getOrderUid().equals(orderUID)) {
                            Log.d("DispatcherAct", "onChildChanged: remove old item " +orderUID+"from inProgress_list" );
                            it.remove();
                            break;
                        }
                    }
                } else if(changedState.equals("confirmed")) {
                    Iterator<Order> it = inProgress_list.iterator();
                    while (it.hasNext()) {
                        if (it.next().getOrderUid().equals(orderUID)) {
                            Log.d("DispatcherAct", "onChildChanged: remove old item " +orderUID+"from inProgress_list" );
                            it.remove();
                            break;
                        }
                    }

                    inprogress_orderAdapter.notifyDataSetChanged();
                    return;
                } else {
                    return;
                }

                inProgress_list.add(changeorder);

                // Sorting
                sortList(inProgress_list);
                inprogress_orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Order removeorder = dataSnapshot.getValue(Order.class);
                String removeState = removeorder.getState();
                String orderUID = removeorder.getOrderUid();
                if(removeState.equals("finished")) {
                    Iterator<Order> it = inProgress_list.iterator();
                    while (it.hasNext()) {
                        if (it.next().getOrderUid().equals(orderUID)) {
                            Log.d("DispatcherAct", "onChildRemoved: remove old item " +orderUID+"from inProgress_list" );
                            it.remove();
                            break;
                        }
                    }
                    inProgress_list.add(removeorder);
                    // Sorting
                    sortList(inProgress_list);
                    inprogress_orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        queryOrders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String,Order>> type_orders_list =
                        new GenericTypeIndicator<HashMap<String,Order>>() {};
                map_uid_to_order = dataSnapshot.getValue(type_orders_list);

                if(map_uid_to_order==null) {
                    map_uid_to_order = new HashMap<String, Order>();
                }
                //store as a map

                /*//get current email for saving orders to sharedPreference
                String curEmail = getApplicationContext().getSharedPreferences(
                        getString(R.string.user_preference), Context.MODE_PRIVATE).getString("curEmail",null);
                //store orders as hashMap
                //merge with the old ones
                CachedOrderPrefrence.saveOrderMapToAppByEmail(getApplicationContext(),curEmail,map_uid_to_order);*/
                //get the orders in correct state
                //map_uid_to_order.putAll(CachedOrderPrefrence.getOrderByEmail(getApplicationContext(),curEmail)) ;


                newOrder_list = newAndInProgressFun.getNewOrdersFromMap(map_uid_to_order);
                inProgress_list = newAndInProgressFun.getInProgressOrdersFromMap(map_uid_to_order);
                // Sorting new orders
                sortList(newOrder_list);
                sortList(inProgress_list);

                new_ordersAdapter = new NewOrderAdapter(DispatcherNewOrdersActivity.this,newOrder_list,R.layout.new_order_item_layout,
                                                        R.id.tvType,R.id.tvDetail,R.id.tvStatus);
                inprogress_orderAdapter = new InProgressAdapter(DispatcherNewOrdersActivity.this,inProgress_list,R.layout.in_progress_item_layout,
                        R.id.tvType,R.id.tvDetail,R.id.tvStatus);
                neworderListView.setAdapter(new_ordersAdapter);
                inprogressListView.setAdapter(inprogress_orderAdapter);
                initialOrdersLoad = true;
                neworderListView.setOnItemClickListener(new NewOrderItemOnClickListener());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DispatcherNewOrdersActivity.this,"Fail to load orders",Toast.LENGTH_LONG).show();
            }
        });
        //--------------end------get orders from db and merge them with local records

        inprogressListView.setOnItemClickListener(new InProgressItemOnClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void setTabColor(TabHost tabhost) {
        int i = 0;

        for(i = 0; i<tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }

        // Change colour of the selected tab
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundColor(Color.parseColor("#DBE4FB"));
    }

    private void setTabs(TabHost tabhost) {
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Tab1").setIndicator(null,
                ResourcesCompat.getDrawable(getResources(),
                        R.drawable.orders_btn, null))
                .setContent(R.id.orders_btn);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Tab2").setIndicator(null,
                ResourcesCompat.getDrawable(getResources(),
                        R.drawable.inprogress_btn, null))
                .setContent(R.id.inprogress_btn);
        final TabHost.TabSpec tab3 = tabHost.newTabSpec("Tab3").setIndicator(null,
                ResourcesCompat.getDrawable(getResources(),
                        R.drawable.drivers_btn, null))
                .setContent(R.id.drivers_btn);
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Tab4").setIndicator(null,
                ResourcesCompat.getDrawable(getResources(),
                        R.drawable.profile_btn, null))
                .setContent(R.id.profile_btn);
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
                setTabColor(tabHost);
            }
        });

        setTabColor(tabHost);
    }

    private void setProfileInfo() {
        SharedPreferences userPreference = getApplicationContext().getSharedPreferences(
                getString(R.string.user_preference), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreference.getString("userObject", null);
        if (json != null) {
            Account account = gson.fromJson(json, Account.class);
            String accountName = account.getName();
            String accountAddress = account.getAddress();
            String accountEmail = account.getEmail();
            String accountPhone = account.getNumber();

            name.setText(accountName);
            address.setText(accountAddress);
            email.setText(accountEmail);
            phoneNumber.setText(accountPhone);
        }
    }

    public void SignOut(View view) {
        SharedPreferences preferences = getSharedPreferences(getString(
                R.string.user_preference), Context.MODE_PRIVATE);

            String tok = FirebaseInstanceId
                    .getInstance().getToken();
        mDatabaseRef.child("dispatch_token").child(tok).removeValue();
            Log.d("profileAct", "signOut: token:" + tok);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void mapButton (View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void newCustOrderButton(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(DispatcherNewOrdersActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.new_cust_order_dialog, null);
        builderSingle.setView(dialogView);

        builderSingle.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if(!CheckConnection.isOnline(DispatcherNewOrdersActivity.this)) {
                    Toast.makeText(DispatcherNewOrdersActivity.this, "Sorry, No network", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                cust_new(dialogView,getBaseContext());
                //Toast.makeText(getBaseContext(), "New order added. (not working yet)", Toast.LENGTH_SHORT).show();

            }
        });

        builderSingle.show();
    }


    public void newRestOrderButton(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(DispatcherNewOrdersActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.new_order_dialog, null);
        builderSingle.setView(dialogView);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.first_popup_tv);
        String title_for_popup = "Create new order:";
        tvTitle.setText(title_for_popup);


        builderSingle.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if(!CheckConnection.isOnline(DispatcherNewOrdersActivity.this)) {
                    Toast.makeText(DispatcherNewOrdersActivity.this, "Sorry, No network", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                rest_new(dialogView,getBaseContext());
                //Toast.makeText(getBaseContext(), "New order added. (not working yet)", Toast.LENGTH_SHORT).show();

            }
        });

        builderSingle.show();


    }

    private void sortList(ArrayList<Order> order_list) {
        Collections.sort(order_list, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if(o1.getTimestampCreated()==null||o2.getTimestampCreated()==null) {
                    return 1;
                }
                Long otime1 = o1.getDateCreatedLong();
                Long otime2 = o2.getDateCreatedLong();
                return otime1.compareTo(otime2);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //detach
        //queryOrders.removeEventListener(childAddedListener);
    }

    private class InProgressItemOnClickListener implements  AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            order = (Order) parent.getItemAtPosition(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(DispatcherNewOrdersActivity.this);
            LayoutInflater inflater = DispatcherNewOrdersActivity.this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.in_progress_order_dialog, null);
            builder.setView(dialogView);

            alert = builder.create();
            alert.show();
        }
    }

    //onclick for new order items
    private class NewOrderItemOnClickListener implements AdapterView.OnItemClickListener {
        private Order selectedOrder;
        private AlertDialog outdia;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedOrder = (Order)parent.getItemAtPosition(position);

            // driver list to be selected
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(DispatcherNewOrdersActivity.this);

            //builderSingle.setIcon(R.drawable.ic_launcher);
            //builderSingle.setTitle("Select One driver:");

            //layout of the first dialog
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.first_popup_in_new_order_tab, null);
            builderSingle.setView(dialogView);
            TextView tvTitle = (TextView) dialogView.findViewById(R.id.first_popup_tv);
            String title_for_popup = "Select a driver:";
            tvTitle.setText(title_for_popup);
            //----------end-----------layout of the first dialog

            ListView listDrivers = (ListView) dialogView.findViewById(R.id.first_popup_lv);
            listDrivers.setAdapter(driversAdapter);

            //when a driver is selected, pops up another page for confirmation
            listDrivers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Account selectedDriver = (Account)parent.getItemAtPosition(position);

                    final AlertDialog.Builder builderInner = new AlertDialog.Builder(DispatcherNewOrdersActivity.this);
                    //inner dialog layout
                    LayoutInflater inflater = getLayoutInflater();
                    View innerdialogView = inflater.inflate(R.layout.order_assign_confirmation_popup, null);
                    builderInner.setView(innerdialogView);
                    TextView driver_Title = (TextView) innerdialogView.findViewById(R.id.tv_title_for_driver);
                    TextView orderTitle = (TextView) innerdialogView.findViewById(R.id.tv_title_for_order);
                    //end-------inner dialog layout



                    //TODO change ui for confirmation driver
                    //-------------------------------------------------------setting driver info---
                    TextView text = (TextView) innerdialogView.findViewById(R.id.driver);
                    //letter image
                    ImageView image = (ImageView) innerdialogView.findViewById(R.id.image_view);

                    String driverDetails;

                        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                        // generate random color
                        int color = generator.getColor(selectedDriver.getName().substring(0,1));
                        //set the first letter as the image content
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRound(selectedDriver.getName().substring(0,1), color);
                        //set the image
                        image.setImageDrawable(drawable);

                        driverDetails = selectedDriver.getName() + "\nStatus: " +
                                selectedDriver.getIdle() + "\nPhone Number: " + selectedDriver.getNumber();
                        text.setText(driverDetails);

                        //---------------------------------------end setting driver info

                    //TODO change ui for confirmation order
                    //----------------------setting order info -------------------------------
                    // Lookup view for data population
                    TextView tvType = (TextView) innerdialogView.findViewById(R.id.tvType);
                    TextView tvDetail = (TextView) innerdialogView.findViewById(R.id.tvDetail);
                    TextView tvStatus = (TextView) innerdialogView.findViewById(R.id.tvStatus);

                    // Populate the data into the template view using the data object
                    String description;
                    String status;
                    String type;
                    if (selectedOrder != null) {
                        type  = "Order Type: <font color=\"red\">"+selectedOrder.getOrderType();
                        description= selectedOrder.orderDetail_dispatcher_display();
                        status = "Status: <font color=\"red\">" + selectedOrder.getState() + "</font><br>" +
                                selectedOrder.getTimeStamp();
                        tvType.setText(Html.fromHtml(type), TextView.BufferType.SPANNABLE);
                        tvDetail.setText(description);
                        tvStatus.setText(Html.fromHtml(status), TextView.BufferType.SPANNABLE);
                    }
                    //----------end------------------setting order info

                    builderInner.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            if(!CheckConnection.isOnline(DispatcherNewOrdersActivity.this)) {
                                Toast.makeText(DispatcherNewOrdersActivity.this, "Sorry, No network", Toast.LENGTH_LONG).show();
                                outdia.dismiss();
                                dialog.dismiss();
                                return;
                            }

                            //Confirm assigning to the selected driver
                            Map<String, Object> orderUpdate = new HashMap<String, Object>();
                            orderUpdate.put("order/"+selectedOrder.getOrderUid()+"/state", "processing");
                            orderUpdate.put("order/"+selectedOrder.getOrderUid()+"/driverUID", selectedDriver.getAccountUID());
                            mDatabaseRef.updateChildren(orderUpdate, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Toast.makeText(DispatcherNewOrdersActivity.this, "Order assigned", Toast.LENGTH_LONG).show();
                                        /*//move the order from new list to in-progress list
                                        selectedOrder.setDriverUID(selectedDriver.getAccountUID());
                                        selectedOrder.setState("processing");
                                        newOrder_list.remove(selectedOrder);
                                        inProgress_list.add(selectedOrder);
                                        new_ordersAdapter.notifyDataSetChanged();
                                        inprogress_orderAdapter.notifyDataSetChanged();*/
                                        outdia.dismiss();
                                        dialog.dismiss();
                                    }else {
                                        Toast.makeText(DispatcherNewOrdersActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    });
//                    builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            outdia.dismiss();
//                            dialog.dismiss();
//
//                        }
//                    });
                    builderInner.create().show();

                }
            });
            builderSingle.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            /*builderSingle.setAdapter(driversAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Account driver = driversAdapter.getItem(which);


                }
            });*/
            outdia = builderSingle.create();
            outdia.show();
        }
    }

    public void confirm(View view) {
        //check network first
        if(!CheckConnection.isOnline(DispatcherNewOrdersActivity.this)) {
            Toast.makeText(DispatcherNewOrdersActivity.this, "Sorry, No network", Toast.LENGTH_LONG).show();
            return;
        }

        mDatabaseRef.child("order").child(order.getOrderUid()).child("state").setValue("confirmed");
        alert.dismiss();
    }

    public void cancel(View view) {
        alert.dismiss();
    }

}
