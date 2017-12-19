package LocalOrders;

import java.util.ArrayList;
import java.util.HashMap;

import objectstodb.Order;

/**
 * Created by Kent on 2017-05-15.
 */

public class newAndInProgressFun {
    public static ArrayList<Order> getNewOrdersFromMap(HashMap<String,Order> map) {
        ArrayList<Order> newOrders = new ArrayList<Order>();

        for (Order one : map.values()) {
            if (one.getState().equals("pending")) {
                newOrders.add(one);
            }
        }
        return newOrders;
    }

    public static ArrayList<Order> getInProgressOrdersFromMap(HashMap<String,Order> map) {
        ArrayList<Order> newOrders = new ArrayList<Order>();

        for (Order one : map.values()) {
            if (!one.getState().equals("pending") && !one.getState().equals("confirmed")) {
                newOrders.add(one);
            }
        }
        return newOrders;
    }
    
}
