package LocalOrders;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import objectstodb.Order;


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

    public static void saveOrderMapToAppByEmail(Context ApplicationContext,String email, HashMap<String,Order> newones) {
        HashMap<String,Order> orders_al;

        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        String orders_js = sp.getString("orders", null);
        Gson gson = new Gson();

        //first time
        if (orders_js == null) {
            orders_al = new HashMap<String,Order>();
        } else {
            //not first time
            orders_al = gson.fromJson(orders_js, new TypeToken<HashMap<String,Order>>() {
            }.getType());
        }

        orders_al.putAll(newones);
        SharedPreferences.Editor prefsEditor = sp.edit();
        String newjson = gson.toJson(orders_al);
        prefsEditor.putString("orders", newjson);
        prefsEditor.apply();
    }

    /*
    * Save the order locally under the email account
    * */
    public static void saveOrderToAppByEmail(Context ApplicationContext,String email, Order newone) {
        HashMap<String,Order> orders_al;

        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        String orders_js = sp.getString("orders", null);
        Gson gson = new Gson();

        //first time
        if (orders_js == null) {
            orders_al = new HashMap<String,Order>();
        } else {
            //not first time
            orders_al = gson.fromJson(orders_js, new TypeToken<HashMap<String,Order>>() {
            }.getType());
        }

        orders_al.put(newone.getOrderUid(),newone);
        SharedPreferences.Editor prefsEditor = sp.edit();
        String newjson = gson.toJson(orders_al);
        prefsEditor.putString("orders", newjson);
        prefsEditor.apply();
    }



    /*
    * get order arraylist of the email account from SharedPreference
    * */
    public static HashMap<String,Order> getOrderByEmail(Context ApplicationContext,String email) {
        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        String orders_js = sp.getString("orders",null);
        Gson gson = new Gson();

        HashMap<String,Order> orders_al = gson.fromJson(orders_js, new TypeToken<HashMap<String,Order>>() {
        }.getType());

        if(orders_al == null) {
            orders_al = new HashMap<String,Order>();
        }

        return orders_al;
    }

    //return true when it removes
    //return false when it fails
    public static boolean removeOrderByEmail(Context ApplicationContext,String email, Order removeone) {
        HashMap<String,Order> orders_al;

        SharedPreferences sp = getLocalRecordPreByEmail(ApplicationContext,email);
        String orders_js = sp.getString("orders", null);
        Gson gson = new Gson();
        if(orders_js == null)
            return false;

        orders_al = gson.fromJson(orders_js, new TypeToken<HashMap<String,Order>>() {
        }.getType());

        if(orders_al.containsKey(removeone.getOrderUid())) {
            orders_al.remove(removeone.getOrderUid());
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


