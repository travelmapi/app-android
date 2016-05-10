package com.travelmapi.app.travelmapi_app;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditDateDialogFragment extends DialogFragment {
    public static final int FLAG_START = 0;
    public static final int FLAG_END = 1;


  public interface DialogCompleteListener{
      public void onDialogComplete(int flag);
  }

    DialogCompleteListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_date_dialog, container, false);
        ButterKnife.bind(this, view);
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    public void setOnDialogCompleteListener(DialogCompleteListener listener){
        mListener = listener;
    }

    @OnClick(R.id.fragment_edit_date_edit_start)
    void startClick(){
        if(mListener != null){
            mListener.onDialogComplete(FLAG_START);
        }
        dismiss();
    }

    @OnClick(R.id.fragment_edit_date_edit_end)
    void endClick(){
        if(mListener != null){
            mListener.onDialogComplete(FLAG_END);
        }
        dismiss();
    }

    @OnClick(R.id.fragment_edit_date_cancel)
    void cancelClick(){
        dismiss();
    }

}
