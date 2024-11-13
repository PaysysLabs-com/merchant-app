package com.paysys.indoMojaloopMarchant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.paysys.indoMojaloopMarchant.Activity.MainDrawerActivity;


import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.model.StepsHeader;
import com.paysys.indoMojaloopMarchant.BuildConfig;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class UtilMethod {
    private static final String LOGIN_RESPONSE_SHARED_PREF_KEY = "LOGIN_RESPONSE_SHARED_PREF_KEY";

   /* private static final String ACCOUNT_NUMBER_SHARED_PREF_KEY = "ACCOUNT_NUMBER_SHARED_PREF_KEY";
    private static final String INSTITUTION_CODE_SHARED_PREF_KEY = "INSTITUTION_CODE_SHARED_PREF_KEY";
    private static final String USER_NAME_SHARED_PREF_KEY = "USER_NAME_SHARED_PREF_KEY";
    private static final String ALIAS_SHARED_PREF_KEY = "ALIAS_SHARED_PREF_KEY";
    private static final String ALIAS_TYPE_SHARED_PREF_KEY = "ALIAS_TYPE_SHARED_PREF_KEY";
*/
    public static ViewFlipper addRightTransitonAnimationToView(Context context, View view){
        ((ViewFlipper)view).setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right));
        ((ViewFlipper)view).setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_from_right));
        return (ViewFlipper) view;
    }

    public static ViewFlipper addLeftTransitonAnimationToView(Context context,View view){
        ((ViewFlipper)view).setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_from_left));
        ((ViewFlipper)view).setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_from_left));
        return (ViewFlipper) view;
    }

    public static void showToast(final String message, final MainDrawerActivity activityObj) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activityObj, message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public static String getLangFromSharedPref(MainDrawerActivity activityObj) {
        SharedPreferences preferences = activityObj.getSharedPreferences( activityObj.getPackageName() + "_preferences", MODE_PRIVATE);
        return preferences.getString("Language","");
    }

    public static DeviceDetails getDeviceDetails(){
        DeviceDetails deviceObj = new DeviceDetails();
        deviceObj.setDeviceOrigin("Android");
        deviceObj.setDeviceOS(Build.VERSION.RELEASE);
        deviceObj.setDeviceModel(Build.MANUFACTURER + " " + Build.MODEL);
        deviceObj.setDeviceVersion(BuildConfig.VERSION_NAME);
        return deviceObj;
    }

    public static String getConnectivityMessage(Throwable throwable){
        if(throwable !=null){
            String msg = throwable.getMessage();
            if (msg != null){
                if(msg.contains("Unable to resolve host") || msg.contains("Connection timed out") || msg.contains("failed to connect"))
                    return "Unable to establish connectivity, Please check your internet connection.";
                else
                    return msg;
            }else
                return "Request Timed Out , Please try again.";
        }else
            return "Request Timed Out, Please try again.";
    }

    public static StepsHeader steps_header(int mCurrentScreen) {
        switch (mCurrentScreen) {
            case 1:
                return new StepsHeader("1", "Personal Details", "Create Account");

            case 2:
                return new StepsHeader("2", "Bank Account", "Create Account");

            case 3:
                return new StepsHeader("3", "User Data", "Create Account");

            case 4:
                return new StepsHeader("4", "OTP confirmation", "Create Account");

            case 5:
                return new StepsHeader("", "", "Terms & Conditions");
        }
        return null;
    }

    public static StepsHeader steps_header_in(int mCurrentScreen) {
        switch (mCurrentScreen) {
            case 1:
                return new StepsHeader("1", "Data pribadi", "Buat Akun");

            case 2:
                return new StepsHeader("2", "Akun bank", "Buat Akun");

            case 3:
                return new StepsHeader("3", "Data pengguna", "Buat Akun");

            case 4:
                return new StepsHeader("4", "Konfirmasi OTP", "Buat Akun");

            case 5:
                return new StepsHeader("", "", "Syarat & Ketentuan");
        }
        return null;
    }

    public static String getDeviceId(MainDrawerActivity activityObj) {
        String deviceIMEI = Settings.Secure.getString(activityObj.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.i(" deviceIMEI: " + deviceIMEI);

        if(deviceIMEI == null){
            WifiManager manager = (WifiManager) activityObj.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            deviceIMEI =  info.getMacAddress();
        }
        Log.i(" deviceIMEI: " + deviceIMEI);
        return  deviceIMEI;
    }

  /*  public static void saveloginRespose(String accountNumber, String institutionCode, String username, String alias, String aliasType, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(LOGIN_RESPONSE_SHARED_PREF_KEY, accountNumber);
        edit.putString(INSTITUTION_CODE_SHARED_PREF_KEY, institutionCode);
        edit.putString(USER_NAME_SHARED_PREF_KEY, username);
        edit.putString(ALIAS_SHARED_PREF_KEY, alias);
        edit.putString(ALIAS_TYPE_SHARED_PREF_KEY, aliasType);

        edit.commit();
    }*/
  public static void saveloginRespose(String loginResponse, Context context) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor edit = prefs.edit();
      edit.putString(LOGIN_RESPONSE_SHARED_PREF_KEY, loginResponse);
      edit.commit();
  }

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(LOGIN_RESPONSE_SHARED_PREF_KEY, 0);
    }


    public static String getFormattedAmountWithLBL(String amount) {
        // Return unformatted amount if there's a parsing issue
        return "$ " + amount;

    }

    public static String getFormattedAmountWithLBLForEdittext(String amount) {
        try {
            // Remove non-numeric characters except decimal point
            double parsedAmount = Double.parseDouble(amount.replaceAll("[^\\d.]", ""));

            // Set up DecimalFormat with the desired pattern and locale
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
            df.applyPattern("#,##0.00"); // Ensures thousands separators and two decimal places

            // Format the parsed amount
            return  df.format(parsedAmount);

        } catch (NumberFormatException e) {
            // Return original input if parsing fails
            return amount;
        }
    }

    public static long parseFormattedAmount(String amountString){
        if (amountString == null || amountString.trim().isEmpty()) {
            // Return a default value if the input is empty or null
            return 0;
        }

        NumberFormat nf = NumberFormat.getInstance(Locale.US); // or your specific locale
        Number parsedNumber = null; // Parse into a Number
        try {
            parsedNumber = nf.parse(amountString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return parsedNumber.longValue(); // Convert to long if necessary
    }
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }
    public static char[] VALID_CHARACTERS = "123456879".toCharArray();

    public static String csRandomAlphaNumericString(int numChars) {
        SecureRandom srand = new SecureRandom();
        Random rand = new Random();
        char[] buff = new char[numChars];

        for (int i = 0; i < numChars; ++i) {
            if ((i % 10) == 0) {
                rand.setSeed(srand.nextLong());
            }
            buff[i] = VALID_CHARACTERS[rand.nextInt(VALID_CHARACTERS.length)];
        }
        Log.d("Generated password: " + new String(buff));
        return new String(buff);
    }

}
