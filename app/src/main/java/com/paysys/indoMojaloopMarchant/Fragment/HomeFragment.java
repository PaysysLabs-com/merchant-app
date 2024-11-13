package com.paysys.indoMojaloopMarchant.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.dialog.OKDialog;
import com.paysys.indoMojaloopMarchant.interfaces.DrawerLocker;
import com.paysys.indoMojaloopMarchant.model.Request.BalanceInquiryRequest;
import com.paysys.indoMojaloopMarchant.model.Respose.BalanceInquiryResponse;
import com.paysys.indoMojaloopMarchant.model.Respose.GenericResponse;
import com.paysys.indoMojaloopMarchant.model.SendParams.BalanceInquiryPrram;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paysys.indoMojaloopMarchant.utils.Constants.MULTI_LOGIN;
import static com.paysys.indoMojaloopMarchant.utils.Constants.PROCESSED_OK;
import static com.paysys.indoMojaloopMarchant.utils.Constants.SESSION_EXPIRED;
import static com.paysys.indoMojaloopMarchant.utils.Constants.SESSION_INVALID;
import static com.paysys.indoMojaloopMarchant.utils.UtilMethod.getConnectivityMessage;
import static com.paysys.indoMojaloopMarchant.utils.UtilMethod.getFormattedAmountWithLBL;

public class HomeFragment extends BaseFragment implements OKDialog.OKDialogCallback{

    View rootView;
    private TextView tv_amount;
    Button btnPreperOrder,btnCashOut,btnPayment,btnOtherServices,btnTopUp;
    TextView tv_actualBalance;
    OKDialog cancelDlg;


    private Call<GenericResponse<BalanceInquiryResponse>> callbackBalanceInquiry;
    private String NotifyData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_layout, container, false);

        initView(rootView);
        return rootView;
    }

    public void initView( View rootView) {

        this.rootView = rootView;
        ((DrawerLocker) getActivity()).setDrawerLocked(false);
        getMainDrawerActivity().hideTabLayout();

        tv_amount= rootView.findViewById(R.id.tv_Amount);
        btnPreperOrder = rootView.findViewById(R.id.btn_preperOder);
        btnCashOut= rootView.findViewById(R.id.btn_CashOut);
        btnPayment = rootView.findViewById(R.id.btn_Payment);
        btnOtherServices= rootView.findViewById(R.id.btn_other_Services);
        btnTopUp= rootView.findViewById(R.id.btn_topup);
        tv_actualBalance= rootView.findViewById(R.id.tv_actualBalance);

        btnPreperOrder.setOnClickListener(new HomeFragment.onClickListener());
        btnTopUp.setOnClickListener(new HomeFragment.onClickListener());
        btnOtherServices.setOnClickListener(new HomeFragment.onClickListener());
        btnPayment.setOnClickListener(new HomeFragment.onClickListener());
        btnCashOut.setOnClickListener(new HomeFragment.onClickListener());

        getMainDrawerActivity().drawer_accountNumber.setText(getMainDrawerActivity().inquiryPram.getAccountNumber());

        getMainDrawerActivity().drawer_userName.setText("Alias: "+getMainDrawerActivity().QRPram.getAlias());
        getMainDrawerActivity().toolbar_username.setText(getMainDrawerActivity().inquiryPram.getUserName());

        /*if(NotifyData != null){
            notifDlg = new SuccessfulCheckDialog();
            notifDlg.setParams(getMainDrawerActivity(), getString(R.string.successfully_reg),1 , getString(R.string.congratulations),getString(R.string.logintext));
            notifDlg.setCallback(HomeFragment.this);
            notifDlg.show(getFragmentManager(), "OkDialog");
        }*/
/////call for balance Inqueiry//////
        initiateRequestforBalanceInquiry();
    }

    public void reqObj(String notifyData) {
        NotifyData = notifyData;
    }

    @Override
    public void okSelected(int dlgType) {
        switch (dlgType) {
            case 0:
                cancelDlg.dismiss();
                getMainDrawerActivity().logOutUser();
                break;
            case 1:
                try {
                    cancelDlg.dismiss();
                    if (getMainDrawerActivity() != null){
                        getMainDrawerActivity().logOutUser();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
        }
    }
    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_preperOder:
                   getMainDrawerActivity().addDockableFragment(new PrepareOderFragment());
                    break;
                case R.id.btn_CashOut:
                    Toast.makeText(getActivity(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_Payment:
                    Toast.makeText(getActivity(),  getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_other_Services:
                    Toast.makeText(getActivity(),  getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_topup:
                    Toast.makeText(getActivity(),  getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    ///////////// Set 3 Check User Avabaility////////////
    private void initiateRequestforBalanceInquiry(){

        final ProgressDialog progressDlg = new ProgressDialog(getMainDrawerActivity());
        progressDlg.setMessage("Logging...");
        progressDlg.show();

        final BalanceInquiryRequest requestObj = new BalanceInquiryRequest
                (getMainDrawerActivity().inquiryPram.getInstitutionCode(),getMainDrawerActivity().inquiryPram.getAccountNumber());

        callbackBalanceInquiry = service.balanceInquiry(requestObj);

        callbackBalanceInquiry.enqueue(new Callback<GenericResponse<BalanceInquiryResponse>>(){
            @Override
            public void onResponse(Call<GenericResponse<BalanceInquiryResponse>> call, Response<GenericResponse<BalanceInquiryResponse>> response) {
                progressDlg.dismiss();
                if(response.body()!=null || response.body().getData()!= null)
                    if(response.body().getResponseCode().equals(PROCESSED_OK)){
                      BalanceInquiryPrram inquiryPrram = new BalanceInquiryPrram();
                      inquiryPrram.setAvailableBalance(response.body().getData().getAvailableBalance());
                      tv_amount.setText(String.valueOf(getFormattedAmountWithLBL(response.body().getData().getAvailableBalance())));
                        tv_actualBalance.setText(String.valueOf(getFormattedAmountWithLBL(response.body().getData().getActualBalance())));

                    } else {
                        switch (response.body().getResponseCode()){
                            case SESSION_EXPIRED:
                            case MULTI_LOGIN:
                            case SESSION_INVALID:
                                getMainDrawerActivity().logOutUser();
                                break;
                        }
                        validateUserUnSuccessfulFaliureHandler(response.body().getResponseDescription());
                    }
            }
            @Override
            public void onFailure(Call<GenericResponse<BalanceInquiryResponse>> call, Throwable t) {
                progressDlg.dismiss();
                validateUserUnSuccessfulFaliureHandler(getConnectivityMessage(t));
            }
        });
    }

    private void validateUserUnSuccessfulFaliureHandler(String message){
        cancelDlg = new OKDialog();
        cancelDlg.setParams(getMainDrawerActivity(),message,1);
        cancelDlg.setCallback(this);
        cancelDlg.show(getFragmentManager(), "OkDialog");
    }

    private void CheckUsernameUnSuccessfulHandler(String responseDesc) {
        cancelDlg = new OKDialog();
        cancelDlg.setParams(getMainDrawerActivity(),responseDesc,1);
        cancelDlg.setCallback(this);
        cancelDlg.show(getFragmentManager(), "OkDialog");
    }

    private void onBackButtonPressed() {
        getMainDrawerActivity().logout();
    }

    @Override
    public void doBack() {
        onBackButtonPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainDrawerActivity().setOnBackPressedListener(this);
    }
}
