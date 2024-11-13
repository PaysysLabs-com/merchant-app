package com.paysys.indoMojaloopMarchant.Fragment.Login;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paysys.indoMojaloopMarchant.Fragment.BaseFragment;
import com.paysys.indoMojaloopMarchant.Fragment.HomeFragment;
import com.paysys.indoMojaloopMarchant.Fragment.registration.RegistrationFragment;
import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.api.MojaloopService;
import com.paysys.indoMojaloopMarchant.interfaces.DrawerLocker;
import com.paysys.indoMojaloopMarchant.model.Request.CreditUnionListRequest;
import com.paysys.indoMojaloopMarchant.model.Respose.AuthenticateResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.CreditUnionListRespose;
import com.paysys.indoMojaloopMarchant.model.Respose.GenericResponse;
import com.paysys.indoMojaloopMarchant.utils.Log;
import com.google.android.material.textfield.TextInputEditText;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paysys.indoMojaloopMarchant.utils.Constants.MULTI_LOGIN;
import static com.paysys.indoMojaloopMarchant.utils.Constants.PROCESSED_OK;
import static com.paysys.indoMojaloopMarchant.utils.Constants.SESSION_EXPIRED;
import static com.paysys.indoMojaloopMarchant.utils.Constants.SESSION_INVALID;
import static com.paysys.indoMojaloopMarchant.utils.Constants.deviceDetails;
import static com.paysys.indoMojaloopMarchant.utils.Constants.str_IMEI;
import static com.paysys.indoMojaloopMarchant.utils.UtilMethod.getConnectivityMessage;


public class LoginFragment extends BaseFragment {

    private View rootView;
    private Call<GenericResponse<AuthenticateResponse>> callback;
    private Call<GenericResponse<CreditUnionListRespose>> callbackCreditUnionList;

    private String password,username;
    private MaterialEditText etUsername;
    private TextInputEditText etPassword;
    private TextView LangUnderLine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.login_layout, container, false);

        initView(rootView);
        return rootView;
    }
    public  void  initView( View rootView){

        this.rootView  = rootView;
        ((DrawerLocker)getActivity()).setDrawerLocked(true);

         etUsername = rootView.findViewById(R.id.et_userName);

        etPassword = rootView.findViewById(R.id.et_password);
        Button btnSigIn = rootView.findViewById(R.id.btn_login);
        Button btnSigUp = rootView.findViewById(R.id.btn_signUp);

        LangUnderLine = rootView.findViewById(R.id.lbl_lang2);
        TextView btn_forgot = rootView.findViewById(R.id.tv_forgot);
        
        LangUnderLine.setPaintFlags(LangUnderLine.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btn_forgot.setPaintFlags(LangUnderLine.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        resetFields();
        LangUnderLine.setOnClickListener(new LoginFragment.onClickListener());
        btnSigIn.setOnClickListener(new LoginFragment.onClickListener());
        btnSigUp.setOnClickListener(new LoginFragment.onClickListener());
    }

    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
                    credential();
                    getMainDrawerActivity().hideSoftKeyboard();
                    if(isValidateSuccess()) {
                        initiateRequestForLogin();
                    }
                    break;

                case R.id.btn_signUp:
                    initiateRequestToCreditUnionList();
                    break;

                case R.id.lbl_lang2:
                    if(LangUnderLine.getText().toString().equals("English"))
                        getMainDrawerActivity().savedlangInPreferences("en");
                    else
                        getMainDrawerActivity().savedlangInPreferences("in");
                    break;
            }
        }
    }
    private void credential() {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
    }

    private void initiateRequestForLogin() {

        final ProgressDialog progressDlg = new ProgressDialog(getMainDrawerActivity());
        progressDlg.setMessage( getString(R.string.loading));
        progressDlg.show();

        String pwd = password;
        pwd = Base64.encodeToString(pwd.getBytes(), Base64.NO_WRAP);

        byte[] data = Base64.decode(pwd, Base64.DEFAULT);
        String passwordText = "";
        try {
            passwordText= new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        callback = publicService.authenticate(username,passwordText,str_IMEI,deviceDetails.getDeviceVersion(),"ANDROID","MOBILE_APP","Merchant",getMainDrawerActivity().FcmToken);

        callback.enqueue(new Callback<GenericResponse<AuthenticateResponse>>(){
            @Override
            public void onResponse(Call<GenericResponse<AuthenticateResponse>> call, Response<GenericResponse<AuthenticateResponse>> response) {
                progressDlg.dismiss();
                if(response.body()!=null)
                    if(response.body().getResponseCode().equals(PROCESSED_OK)){
                        loginSuccessfulHandler(response.body().getData());
                      /*  /////////save in preferences/////
                        Gson gson = new Gson();
                        UtilMethod.saveloginRespose(gson.toJson(response.body().getData().getDetails()),getActivity());*/
                        ///inquiry Parm
                        getMainDrawerActivity().inquiryPram.setAccountNumber(response.body().getData().getDetails().getAccountNumber());
                        getMainDrawerActivity().inquiryPram.setInstitutionCode(response.body().getData().getDetails().getInstitutionCode());
                        getMainDrawerActivity().inquiryPram.setUserName(response.body().getData().getDetails().getUsername());
                        ///qr parm
                        getMainDrawerActivity().QRPram.setAlias(response.body().getData().getDetails().getAlias());
                        getMainDrawerActivity().QRPram.setAlias_Type(response.body().getData().getDetails().getAliasType());

                    } else {
                        switch (response.body().getResponseCode()) {
                            case SESSION_EXPIRED:
                            case MULTI_LOGIN:
                            case SESSION_INVALID:
                                getMainDrawerActivity().logOutUser();
                                break;
                        }
                        loginUnSuccessfulHandler(response.body().getResponseDescription());
                    }
            }
            @Override
            public void onFailure(Call<GenericResponse<AuthenticateResponse>> call, Throwable t) {
                progressDlg.dismiss();
                loginFailureUnSuccessfulHandler(getConnectivityMessage(t));
                Toast.makeText(getMainDrawerActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginSuccessfulHandler(AuthenticateResponse authRespObj) {

        Log.d(" response ok  " + authRespObj);
        MojaloopService.updateServiceWithToken(authRespObj.getToken());
        HomeFragment fragObj = new HomeFragment();
        getMainDrawerActivity().addDockableFragment(fragObj);
        resetFields();
        }

    private void resetFields() {
        etUsername.setText("");
        etPassword.setText("");
    }

    private void loginUnSuccessfulHandler(String responseDesc ) {

        Toast.makeText(getMainDrawerActivity(), responseDesc, Toast.LENGTH_SHORT).show();
    }
    private void loginFailureUnSuccessfulHandler(String responseDesc) {
        Toast.makeText(getMainDrawerActivity(), responseDesc, Toast.LENGTH_SHORT).show();
        resetFields();
    }
    private boolean isValidateSuccess() {
        if (etUsername.getText().length() < 1 || etPassword.getText().length() < 1)
        {
            Toast.makeText(getMainDrawerActivity(),getString(R.string.username_pass_validation),Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(etUsername.getText().length() < 5){

            Toast.makeText(getMainDrawerActivity(),getString(R.string.invalid_user_length),Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if (etPassword.getText().length() < 6){

            Toast.makeText(getMainDrawerActivity(),getString(R.string.invalid_pass_length),Toast.LENGTH_SHORT).show();

            return  false;
        }

        return true;
    }
    private void initiateRequestToCreditUnionList() {
        final ProgressDialog progressDlg = new ProgressDialog(getMainDrawerActivity());
        progressDlg.show();

        CreditUnionListRequest requestObj = new CreditUnionListRequest("indonesia");

        callbackCreditUnionList = publicService.creditUnionList(requestObj);
        callbackCreditUnionList.enqueue(new Callback<GenericResponse<CreditUnionListRespose>>() {
            @Override
            public void onResponse(Call<GenericResponse<CreditUnionListRespose>> call, Response<GenericResponse<CreditUnionListRespose>> response) {
                progressDlg.dismiss();
                if(response.body()!=null && response.body().getData() != null )
                    if(response.body().getResponseCode().equals(PROCESSED_OK)){

                        creditUnionSuccessfulHandler(response.body().getData());
                    } else{
                        switch (response.body().getResponseCode()) {
                            case SESSION_EXPIRED:
                            case MULTI_LOGIN:
                            case SESSION_INVALID:
                                getMainDrawerActivity().logOutUser();
                                break;
                        }
                        creditUnionUnSuccessfulHandler(response.body().getResponseDescription());
                    }else {
                    creditUnionUnSuccessfulHandler(response.body().getResponseDescription());
                }
            }
            @Override
            public void onFailure(Call<GenericResponse<CreditUnionListRespose>> call, Throwable t) {
                progressDlg.dismiss();
                creditUnionUnSuccessfulHandler(getConnectivityMessage(t));
            }
        });
    }
    private void creditUnionUnSuccessfulHandler(String responseDesc) {
        Toast.makeText(getMainDrawerActivity(),responseDesc , Toast.LENGTH_SHORT).show();
    }
    private void creditUnionSuccessfulHandler(CreditUnionListRespose creditUnionListRespose) {
        RegistrationFragment reqObj = new RegistrationFragment();
        reqObj.setRequestObj(creditUnionListRespose);
        getMainDrawerActivity().addDockableFragment(reqObj);
        getMainDrawerActivity().hideSoftKeyboard();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainDrawerActivity().setOnBackPressedListener(this);
    }

}
