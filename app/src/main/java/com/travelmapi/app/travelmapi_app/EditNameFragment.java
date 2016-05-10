package com.travelmapi.app.travelmapi_app;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditNameFragment extends DialogFragment {

    private OnFragmentCompleteListener mListener;

    public interface OnFragmentCompleteListener{
        public void fragmentComplete(String name);
    }

    @BindView(R.id.fragment_edit_name_edit_text)
    EditText mName;

    String tripName;

    public void setName(String name){
        tripName = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_name, container, false);
        ButterKnife.bind(this, view);
        getDialog().setCanceledOnTouchOutside(true);
        if(tripName != null){
            mName.setText(tripName);
        }
        return view;
    }

    public void setOnFragmentCompleteListener(OnFragmentCompleteListener listener){
        mListener = listener;
    }

    @OnClick(R.id.fragment_edit_name_button_cancel)
    void cancelClick(){
        dismiss();
    }

    @OnClick(R.id.fragment_edit_name_button_save)
    void saveClick(){
        if(mListener != null){
            mListener.fragmentComplete(mName.getText().toString());
        }
        dismiss();
    }
}
