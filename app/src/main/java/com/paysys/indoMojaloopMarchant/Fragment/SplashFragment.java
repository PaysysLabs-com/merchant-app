package com.paysys.indoMojaloopMarchant.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.paysys.indoMojaloopMarchant.Fragment.Login.LoginFragment;
import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.interfaces.DrawerLocker;

public class SplashFragment extends BaseFragment {
    View rootView;

    private long SplashTimer = 1500;
    private Handler hndSplash;
    private Runnable runSplash;

    public SplashFragment() {

    }@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ((DrawerLocker)getActivity()).setDrawerLocked(true);
        View rootView = inflater.inflate(R.layout.splash_screen, container, false);
        hndSplash = new Handler();
        runSplash = new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && !getMainDrawerActivity().isPaused()) {

                    getMainDrawerActivity().addDockableFragment(new LoginFragment());

                }
            }
        };
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        hndSplash.postDelayed(runSplash, SplashTimer);
    }

    @Override
    public void doBack() {
        hndSplash.removeCallbacks(null);
        getActivity().finish();
    }


    @Override
    public void onPause() {
        hndSplash.removeCallbacks(null);
        super.onPause();
    }

    @Override
    public void onStop() {
        hndSplash.removeCallbacks(null);
        getMainDrawerActivity().removeOnBackPressedListener();
        super.onStop();
    }


}

