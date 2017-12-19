package t27.surreyfooddeliveryapp.LocalOrders;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

import t27.surreyfooddeliveryapp.objectstodb.Order;

/**
 * Created by Kent on 2017-05-11.
 */

public class CachedOrderPrefrence {

    private CachedOrderPrefrence() {
    }

    //guest email:"guest"
    //get by email to distinguish different login users
    private static SharedPreferences getLocalRecordPreByEmail(Context ApplicationContext, String email) {
        return ApplicationContext.getSharedPreferences(email, Context.MODE_PRIVATE);
    }

    /*
    * Save the order locally under the email account
    * */
    public static void saveOrderToAppByEmail(Context ApplicationContext,String email, Order newone) {
        ArrayList<Order> orders_al;

        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        String orders_js = sp.getString("orders", null);
        Gson gson = new Gson();

        //first time
        if (orders_js == null) {
            orders_al = new ArrayList<Order>();
        } else {
            //not first time
            orders_al = gson.fromJson(orders_js, new TypeToken<ArrayList<Order>>() {
            }.getType());
        }

        orders_al.add(newone);
        SharedPreferences.Editor prefsEditor = sp.edit();
        String newjson = gson.toJson(orders_al);
        prefsEditor.putString("orders", newjson);
        prefsEditor.commit();
    }

    public static boolean updateOrderByEmail(Context ApplicationContext,String email,Order newone) {
        ArrayList<Order> orders_al;

        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext, email);
        String orders_js = sp.getString("orders", null);
        Gson gson = new Gson();

        //nothing in the email
        if (orders_js != null) {
            //try to update
            orders_al = gson.fromJson(orders_js, new TypeToken<ArrayList<Order>>() {
            }.getType());
            Log.d("upd", "updateOrderByEmail: hello" + newone.toString());
            if(orders_al.contains(newone)) {
                Collections.replaceAll(orders_al, newone, newone);
                SharedPreferences.Editor prefsEditor = sp.edit();
                String newjson = gson.toJson(orders_al);
                prefsEditor.putString("orders", newjson);
                prefsEditor.commit();
                Log.d("upd", "updateOrderByEmail: world" +newjson);
                return true;

            }
        }

        return false;
    }

    /*
    * get order arraylist of the email account from SharedPreference
    * */
    public static ArrayList<Order> getOrderByEmail(Context ApplicationContext,String email) {
        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        String orders_js = sp.getString("orders",null);
        Gson gson = new Gson();

        ArrayList<Order> orders_al = gson.fromJson(orders_js, new TypeToken<ArrayList<Order>>() {
        }.getType());

        if(orders_al == null) {
            orders_al = new ArrayList<Order>();
        }

        return orders_al;
    }

    //return true when it removes
    //return false when it fails
    public static boolean romoveOrderByEmail(Context ApplicationContext,String email, Order removeone) {
        ArrayList<Order> orders_al;

        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        String orders_js = sp.getString("orders", null);
        Gson gson = new Gson();
        if(orders_js == null)
            return false;

        orders_al = gson.fromJson(orders_js, new TypeToken<ArrayList<Order>>() {
        }.getType());
        if(orders_al.contains(removeone)) {
            orders_al.remove(removeone);
            //put the object back
            SharedPreferences.Editor prefsEditor = sp.edit();
            String newjson = gson.toJson(orders_al);
            prefsEditor.putString("orders", newjson);
            prefsEditor.apply();
            return true;
        }

        return false;
    }

    public static String getOrdersJs(Context ApplicationContext,String email) {
        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        return sp.getString("orders",null);
    }
}


