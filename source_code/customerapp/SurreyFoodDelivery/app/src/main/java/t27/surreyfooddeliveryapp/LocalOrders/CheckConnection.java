package t27.surreyfooddeliveryapp.LocalOrders;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Kent on 2017-05-16.
 */

public class CheckConnection {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
