package com.paysys.indoMojaloopMarchant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.paysys.indoMojaloopMarchant.Fragment.BaseFragment;
import com.paysys.indoMojaloopMarchant.Fragment.ConfirmPaymentFragment;
import com.paysys.indoMojaloopMarchant.Fragment.HomeFragment;
import com.paysys.indoMojaloopMarchant.Fragment.PrepareOderFragment;
import com.paysys.indoMojaloopMarchant.Fragment.SplashFragment;

import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.dialog.CancelDialog;
import com.paysys.indoMojaloopMarchant.interfaces.DrawerLocker;
import com.paysys.indoMojaloopMarchant.interfaces.OnBackPressedListener;
import com.paysys.indoMojaloopMarchant.model.SendParams.BalanceInquiryPrram;
import com.paysys.indoMojaloopMarchant.model.SendParams.generateQrParm;
import com.paysys.indoMojaloopMarchant.utils.Constants;
import com.paysys.indoMojaloopMarchant.utils.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.paysys.indoMojaloopMarchant.utils.UtilMethod;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

import static com.paysys.indoMojaloopMarchant.utils.Constants.*;

public class MainDrawerActivity extends AppCompatActivity implements DrawerLocker,CancelDialog.YesDialogCallback {

    DrawerLayout drawer;
    Toolbar toolbar;
    public FirebaseAuth mAuth;
    private boolean isPaused;
    HorizontalScrollView tablayout;
    public TextView drawer_userName,drawer_accountNumber,toolbar_username;
    ImageButton btnLogout;
    private CancelDialog cancelDlg;
    public String FcmToken;
    public BalanceInquiryPrram inquiryPram = new BalanceInquiryPrram();
    public generateQrParm QRPram = new generateQrParm();
    private OnBackPressedListener onBackPressedListener;
    private Locale myLocale;
//    public String NotifyData;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

//        Crashlytics.getInstance();
//        IsCustomerLogin();

        mAuth = FirebaseAuth.getInstance();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        str_IMEI = UtilMethod.getDeviceId(this);
        Constants.deviceLanguage = UtilMethod.getLangFromSharedPref(this);
        setSupportActionBar(toolbar);

        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        btnLogout = findViewById(R.id.btnLogout);
        tablayout = findViewById(R.id.tablayout);
        toolbar_username = findViewById(R.id.toolbar_username);

        TextView option1 = findViewById(R.id.btnOption1);
        TextView option2 = findViewById(R.id.btnOption2);
        TextView option3 = findViewById(R.id.btnOption3);
        TextView option4 = findViewById(R.id.btnOption4);
        TextView btnP2P = findViewById(R.id.btnP2P);

        option1.setOnClickListener(new onClickListener());
        option2.setOnClickListener(new onClickListener());
        option3.setOnClickListener(new onClickListener());
        option4.setOnClickListener(new onClickListener());
        btnP2P.setOnClickListener(new onClickListener());
        btnLogout.setOnClickListener(new onClickListener());
//        NotifyData = getIntent().getStringExtra("NotifyData");

     /*   Log.d("XXX" + NotifyData);
        if (NotifyData != null){
            HomeFragment homeFragment = new HomeFragment();
            homeFragment.reqObj(NotifyData);
            addDockableFragment(homeFragment);
        }
*/

        ////generate Fcm Token/////
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainDrawerActivity.this, "getInstanceId failed"+ task.getException(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FcmToken = task.getResult().getToken();
                        Log.d(FcmToken);
                    }
                });
        try {

            Constants.deviceDetails = UtilMethod.getDeviceDetails();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SplashFragment())
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out).commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer_userName = mNavigationView.getHeaderView(0).findViewById(R.id.drawer_username);
        drawer_accountNumber = mNavigationView.getHeaderView(0).findViewById(R.id.drawer_accountNumber);
        loadLocale1();
    }
    @Override
    public void yesSelected(int dlgType) {
        switch (dlgType) {
            case 0:
                cancelDlg.dismiss();
                logOutUser();
                break;
        }
    }
/*
    public void IsCustomerLogin(){
        sharedPreferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("LOGIN_RESPONSE_SHARED_PREF_KEY","" );
        if (!json.equals("")){

            Details authenticateResponse = (new Gson()).fromJson(json, Details.class);
            inquiryPram.setAccountNumber(authenticateResponse.getAccountNumber());
            inquiryPram.setUserName(authenticateResponse.getUsername());
            inquiryPram.setInstitutionCode(authenticateResponse.getInstitutionCode());

            QRPram.setAlias_Type(authenticateResponse.getAliasType());
            QRPram.setAlias(authenticateResponse.getAlias());
        }
    }
*/
@Override
protected void onPause() {
    isPaused = true;
    super.onPause();
}

    public boolean isPaused(){
        return isPaused;
    }

    @Override
    protected void onResume() {
        isPaused = false;
        super.onResume();
    }
    private class onClickListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnOption1:
                    addDockableFragment(new ConfirmPaymentFragment());
                    break;

                case R.id.btnOption2:
                    addDockableFragment(new ConfirmPaymentFragment());
                    break;

                case R.id.btnOption3:
                    addDockableFragment(new ConfirmPaymentFragment());
                    break;

                case R.id.btnP2P:
                    addDockableFragment(new PrepareOderFragment());
                    break;

                case R.id.btnOption4:
                    addDockableFragment(new ConfirmPaymentFragment());
                    break;

                case R.id.btnLogout:
                  logout();
                    break;
            }
        }
    }

    public void drawerSectionItems(View view){
        drawer.closeDrawer(GravityCompat.START);
        switch (view.getId()){
            case R.id.drawer_tvHome:
               addDockableFragment(new HomeFragment());
                break;

            case R.id.drawer_PrepareOder:
                addDockableFragment(new PrepareOderFragment());
                break;

            case R.id.drawer_tvCashOut:
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_tvOtherService:
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_tvPayment:
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_tvLogout:
               logout();
                break;
        }
    }

    @Override
    protected void onPostResume(){
        super.onPostResume();
        if (ACTIVITY2 ) {
//            addDockableFragment(new HomeFragment());
            emptyStackUptil("HomeFragment");
            Constants.ACTIVITY2=false;

        }
    }
    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public void loadLocale1() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    public void
    savedlangInPreferences(String lang){
        String langPref = "Language";
        SharedPreferences preferences = getSharedPreferences( getPackageName() + "_preferences", MODE_PRIVATE);
        preferences.edit().putString(langPref,lang).commit();
        changeLang(lang);
        restartActivity();
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void getTabLayout(){
        tablayout.setVisibility(View.VISIBLE);
    }

    public void hideTabLayout(){
        tablayout.setVisibility(View.GONE);
    }

    public void addDockableFragment(BaseFragment frag ) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String fragmentName=frag.toString().substring(0, frag.toString().indexOf("{"));
        transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_from_right).
                addToBackStack(fragmentName).replace(R.id.container, frag).commitAllowingStateLoss();
        logviewbackStack();
    }

    public void logviewbackStack() {

        FragmentManager fm = getSupportFragmentManager();
        for(int i = fm.getBackStackEntryCount()-1; i>= 0; i--){
            Log.v("entry of : " + fm.getBackStackEntryAt(i).getName() + " at " + i);
        }
    }

    public void popBack() {
        FragmentManager fm = getSupportFragmentManager();
        Log.d(" popping back : count :" + fm.getBackStackEntryCount());
        if (fm.getBackStackEntryCount() - 1 > 0)
            fm.popBackStack();
    }

    public void hideSoftKeyboard() {
        try{
            InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch(Exception e){
        }
    }

    @Override
    public void setDrawerLocked(boolean shouldLock) {
        if(shouldLock){
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toolbar.setVisibility(View.GONE);
        }else{
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    public void logOutUser(){
        emptyStackUptil("LoginFragment");
//        sharedPreferences.edit().remove("LOGIN_RESPONSE_SHARED_PREF_KEY").apply();
    }

    public  void logout(){
        cancelDlg = new CancelDialog();
        cancelDlg.setParams(MainDrawerActivity.this, getString(R.string.logout), 0);
        cancelDlg.setCallback(MainDrawerActivity.this);
        cancelDlg.show(getSupportFragmentManager(), "OkDialog");
    }

    public void emptyStackUptil(String fragmentName) {
        FragmentManager fm = getSupportFragmentManager();
        int i =  fm.getBackStackEntryCount()-1;
        if(i>=0){
            while(!fm.getBackStackEntryAt(i).getName().equals(fragmentName)){
                Log.v("removing : " + fm.getBackStackEntryAt(i).getName());
                fm.popBackStack();
                i--;
            }
        }
    }
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
    public void removeOnBackPressedListener(){
        this.onBackPressedListener = null;
    }
    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();

        drawer = findViewById(R.id.drawer_layout);
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentCount > 1) {
                getFragmentManager().popBackStack();
            } else {
                finish();
            }
        }
    }
}