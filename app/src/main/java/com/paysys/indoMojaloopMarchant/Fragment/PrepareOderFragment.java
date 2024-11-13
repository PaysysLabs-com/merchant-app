package com.paysys.indoMojaloopMarchant.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.dialog.CancelDialog;
import com.paysys.indoMojaloopMarchant.interfaces.DrawerLocker;
import com.paysys.indoMojaloopMarchant.model.dropDownList;
import com.paysys.indoMojaloopMarchant.utils.Log;

import static com.paysys.indoMojaloopMarchant.utils.UtilMethod.addLeftTransitonAnimationToView;
import static com.paysys.indoMojaloopMarchant.utils.UtilMethod.addRightTransitonAnimationToView;
import static com.paysys.indoMojaloopMarchant.utils.UtilMethod.getFormattedAmountWithLBLForEdittext;
import static com.paysys.indoMojaloopMarchant.utils.UtilMethod.parseFormattedAmount;

public class PrepareOderFragment extends BaseFragment implements CancelDialog.YesDialogCallback{

    View rootView;
    TextView stepName;
    TextView stepCount;
    EditText et_amount;
    EditText et_notes;
    ViewFlipper viewFlipper;
    String Value;
    Bitmap bitmap ;
    ImageView iv_qr,iv_backbtn,iv_cancelbtn;
    public  int QRcodeWidth = 1000;
    private CancelDialog cancelDlg;
    int mCurrentScreen = 0;
    String Amount;
    Spinner sp_Select_Qrtype;
    String qrType;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.perpare_order_layout, container, false);
        initView(rootView);
        return rootView;
    }
    public  void  initView( View rootView) {
        ((DrawerLocker) getActivity()).setDrawerLocked(true);
        this.rootView = rootView;
        mCurrentScreen = 1;

        viewFlipper = rootView.findViewById(R.id.viewFlipper_PrepareOderSteps);
        Button btn_continue = rootView.findViewById(R.id.step1_btnNext);
        et_amount = rootView.findViewById(R.id.et_amount);
        et_notes = rootView.findViewById(R.id.et_Note);
        stepName = rootView.findViewById(R.id.stepName);
        stepCount = rootView.findViewById(R.id.stepCount);
        iv_qr = rootView.findViewById(R.id.iv_qr);
        sp_Select_Qrtype = rootView.findViewById(R.id.sp_qrType);
        iv_backbtn = rootView.findViewById(R.id.iv_backbtn);
        iv_cancelbtn = rootView.findViewById(R.id.iv_cancelbtn);

        iv_qr.setOnClickListener(new PrepareOderFragment.onClickListener());
        btn_continue.setOnClickListener(new PrepareOderFragment.onClickListener());
        iv_backbtn.setOnClickListener(new PrepareOderFragment.onClickListener());
        iv_cancelbtn.setOnClickListener(new PrepareOderFragment.onClickListener());

        sp_Select_Qrtype.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item,
                dropDownList.QRTypelist));

        sp_Select_Qrtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                qrType = dropDownList.QRTypelist[sp_Select_Qrtype.getSelectedItemPosition()];
                switch (qrType) {
                    case "DYNAMIC":
                        et_amount.setVisibility(View.VISIBLE);
                        et_notes.setVisibility(View.VISIBLE);
                        break;

                    case "STATIC":
                        et_amount.setVisibility(View.GONE);
                        et_notes.setVisibility(View.GONE);
                        break;
                }

                /*if (qrType.equals("DYNAMIC")) {
                    et_amount.setVisibility(View.VISIBLE);
                    et_notes.setVisibility(View.VISIBLE);
                }else{
                    et_amount.setVisibility(View.GONE);
                    et_notes.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        et_amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    // Format the amount when the EditText loses focus
                    String input = et_amount.getText().toString();
                    et_amount.setText(getFormattedAmountWithLBLForEdittext(input));
                }
            }
        });
    }

        @Override
    public void yesSelected(int dlgType){
        switch (dlgType){
            case 0:
                cancelDlg.dismiss();
                getMainDrawerActivity().popBack();
                break;
            case 1:
                cancelDlg.dismiss();
                break;
        }
    }
    private class onClickListener implements View.OnClickListener {
        @Override
            public void onClick(View view) {
            switch (view.getId()) {
                case R.id.step1_btnNext:
                    if(qrType.equals("Select QR type"))
                        Toast.makeText(getContext(),  getString(R.string.select_qr), Toast.LENGTH_SHORT).show();
                    else {
                        if(isValidate()) {
                            credential();
                            getMainDrawerActivity().hideSoftKeyboard();
                            try {
                                bitmap = TextToImageEncode(Value);
                                viewFlipper = addRightTransitonAnimationToView(getActivity(), viewFlipper);
                                viewFlipper.showNext();
                                mCurrentScreen = 2;
                                stepCount.setText(" 2");
                                stepName.setText( getString(R.string.generate_qr));
                                iv_qr.setImageBitmap(bitmap);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;

                case R.id.iv_backbtn:
                    doBack();
                    break;

             /*   case R.id.iv_qr:
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    break;*/
                case R.id.iv_cancelbtn:
                    cancelDlg = new CancelDialog();
                    cancelDlg.setParams(getMainDrawerActivity(), getString(R.string.quit_qr), 0);
                    cancelDlg.setCallback(PrepareOderFragment.this);
                    cancelDlg.show(getFragmentManager(), "OkDialog");
                    break;
            }
        }
    }

    private boolean isValidate(){

        if(qrType.equals("DYNAMIC")) {
            if (et_amount.getText().toString().equals("") || et_amount.getText().toString() == null) {
                Toast.makeText(getMainDrawerActivity(), getString(R.string.amount_validation), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
            BitMatrix bitMatrix;
            try {
                bitMatrix = new MultiFormatWriter().encode(
                        Value,
                        BarcodeFormat.DATA_MATRIX.QR_CODE,
                        QRcodeWidth, QRcodeWidth, null);
            } catch (IllegalArgumentException Illegalargumentexception) {
                return null;
            }
            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
            int color_black = getResources().getColor(R.color.black);
            int color_white = getResources().getColor(R.color.white);

            for (int y = 0; y < bitMatrixHeight; y++){
                int offset = y * bitMatrixWidth;
                for (int x = 0; x < bitMatrixWidth; x++){
                    pixels[offset + x] = bitMatrix.get(x, y) ? color_black : color_white;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, 1000, 0, 0, bitMatrixWidth, bitMatrixHeight);
            return bitmap;
    }
    private void credential(){

        Amount = String.valueOf(parseFormattedAmount(et_amount.getText().toString()));

        String Note = et_notes.getText().toString();
        String Alias = getMainDrawerActivity().QRPram.getAlias();
        String Aliastype =  getMainDrawerActivity().QRPram.getAlias_Type();
        if(Note.equals(""))
            Value = qrType+"/00"+Alias+"/01"+Aliastype+"/02"+Amount+"/03"+"N/A";
        else
            Value = qrType+"/00"+Alias+"/01"+Aliastype+"/02"+Amount+"/03"+Note;
//        Value = Alias+"/00"+Aliastype+"/01"+Amount+"/02"+Note;
    }
    private void onBackButtonPressed() {
        mCurrentScreen -= 1;
        Log.d(" vf mCurrentScreen " + mCurrentScreen);
        if(mCurrentScreen > 0){
            viewFlipper = addLeftTransitonAnimationToView(getActivity(),viewFlipper);
            viewFlipper.showPrevious();
            stepCount.setText(" 1");
            stepName.setText(getString(R.string.order_details));
//            setHeaderViewFlipper(UtilMethod.steps_header(mCurrentScreen));
        }else
            getMainDrawerActivity().popBack();
    }
    @Override
    public void doBack(){
        onBackButtonPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainDrawerActivity().setOnBackPressedListener(this);
    }
}