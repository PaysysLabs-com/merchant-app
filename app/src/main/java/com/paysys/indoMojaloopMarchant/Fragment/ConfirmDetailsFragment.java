package com.paysys.indoMojaloopMarchant.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.dialog.SuccessfulCheckDialog;

public class ConfirmDetailsFragment extends BaseFragment implements   SuccessfulCheckDialog.OKDialogCallback {

    View rootView;
    private SuccessfulCheckDialog notifDlg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.confirm_details_layout, container, false);
        initView(rootView);
        return rootView;
    }

    public  void  initView( View rootView){
        this.rootView  = rootView;

        Button btn_sendDetails = rootView.findViewById(R.id.btn_sendDetails);
        Button btn_addToPayee = rootView.findViewById(R.id.btnAddToPayee);

        btn_sendDetails.setOnClickListener(new ConfirmDetailsFragment.onClickListener());
        btn_addToPayee.setOnClickListener(new ConfirmDetailsFragment.onClickListener());

    }

    @Override
    public void okSelected(int dlgType) {
        switch (dlgType){
            case 0:
                notifDlg.dismiss();
                getMainDrawerActivity().addDockableFragment(new HomeFragment());
                getMainDrawerActivity().hideTabLayout();
                break;
            case 1:
                notifDlg.dismiss();
                break;
        }
    }

    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sendDetails:
                    notifDlg = new SuccessfulCheckDialog();
                    notifDlg.setParams(getMainDrawerActivity(),  getString(R.string.transfer_seccessfull), 0, getString(R.string.congratulations),"Ok");
                    notifDlg.setCallback(ConfirmDetailsFragment.this);
                    notifDlg.show(getFragmentManager(), "OkDialog");
                break;
                case R.id.btnAddToPayee:
                    Toast.makeText(getActivity(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    private void onBackButtonPressed() {
        getMainDrawerActivity().popBack();
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
