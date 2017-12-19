package LocalOrders;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import objectstodb.Order;

/**
 * Created by Kent on 2017-05-15.
 */

//order adapter for showing orders in listview
public class NewOrderAdapter extends ArrayAdapter<Order> {
    private int listlayoutxml;
    private int layoutid1;
    private int layoutid2;
    private int layoutid3;

    public NewOrderAdapter(Context context, ArrayList<Order> orders,
                        int listlayoutxml,
                        int layoutid1,
                        int layoutid2,
                           int layoutid3
                        ) {
        super(context, 0, orders);
        this.listlayoutxml = listlayoutxml;
        this.layoutid1 = layoutid1;
        this.layoutid2 = layoutid2;
        this.layoutid3 = layoutid3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Order order = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(listlayoutxml, parent, false);
        }
        // Lookup view for data population
        TextView tvType = (TextView) convertView.findViewById(layoutid1);
        TextView tvDetail = (TextView) convertView.findViewById(layoutid2);
        TextView tvStatus = (TextView) convertView.findViewById(layoutid3);

        // Populate the data into the template view using the data object
        String description;
        String status;
        String type;
        if (order != null) {
            type  = "Order Type: <font color=\"red\">"+order.getOrderType();
            description= order.orderDetail_dispatcher_display();
            status = "Status: <font color=\"red\">" + order.getState() + "</font><br>" +
                    order.getTimeStamp();
            tvType.setText(Html.fromHtml(type), TextView.BufferType.SPANNABLE);
            tvDetail.setText(description);
            tvStatus.setText(Html.fromHtml(status), TextView.BufferType.SPANNABLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
