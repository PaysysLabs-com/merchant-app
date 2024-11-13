package com.paysys.indoMojaloopMarchant.Fragment;

import android.os.Bundle;

import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.paysys.indoMojaloopMarchant.Activity.MainDrawerActivity;
import com.paysys.indoMojaloopMarchant.api.MojaLoopApi;
import com.paysys.indoMojaloopMarchant.api.MojaLoopPublicApi;
import com.paysys.indoMojaloopMarchant.api.MojaloopService;
import com.paysys.indoMojaloopMarchant.interfaces.OnBackPressedListener;
import com.paysys.indoMojaloopMarchant.utils.Log;


public abstract class BaseFragment extends Fragment implements OnBackPressedListener {

    protected MojaLoopApi service = MojaloopService.getApi();
    protected MojaLoopPublicApi publicService = MojaloopService.getPublicApi();

    public BaseFragment(){
    }

    public MainDrawerActivity getMainDrawerActivity(){
        return (MainDrawerActivity) getActivity();
    }

    @Override
    public void doBack() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Log.d(" basefrag on create");
    }
}
