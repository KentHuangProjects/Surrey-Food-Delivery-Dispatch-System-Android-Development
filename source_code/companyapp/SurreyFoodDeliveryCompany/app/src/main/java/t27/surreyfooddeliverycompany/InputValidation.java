package t27.surreyfooddeliverycompany;

import android.text.TextUtils;

/**
 * Created by danie on 2017-05-09.
 */

public class InputValidation {
    public static Boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches();
    }

    public static Boolean isValidName(String name) {
        if (name.length() > 0)
            return true;
        return false;
    }

    public static Boolean isValidAddress(String address) {
        if (address.length() > 0)
            return true;
        return false;
    }


    public static Boolean isSamePassword(String password1, String password2) {
        if (password1.compareTo(password2) == 0)
            return true;
        return false;
    }

    public static Boolean isWeakPassword(String password) {
        if (password.length() < 6)
            return true;
        return false;
    }

    public static Boolean isValidPhoneNumber(String number) {
        if (!TextUtils.isEmpty(number))
            return android.util.Patterns.PHONE.matcher(number).matches();
        return false;
    }

    public static Boolean isEmptyInput(String input) {
        if (input.length() == 0)
            return true;
        return false;
    }

    /**
     * @param time
     * @return true if time is between 0 - 60 minutes
     */
    public static Boolean isValidReadyTime(String time) {
        int minutes;
        try {
            minutes = Integer.parseInt(time);
        } catch (Exception e) {
            return false;
        }

        if (minutes >= 0 && minutes <= 60)
            return true;

        return false;
    }

    /**
     * @param amount
     * @return true if amount > 0 dollars
     */
    public static Boolean isValidAmount(String amount) {
        double dblAmount;
        try {
            dblAmount = Double.parseDouble(amount);
        } catch (Exception e) {
            return false;
        }
        if (dblAmount > 0)
            return true;
        return false;
    }

    public static Boolean isValidOrderDetail(String detail) {
        if (detail.length() > 0)
            return true;
        return false;
    }
}
