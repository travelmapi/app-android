package com.travelmapi.app.travelmapi_app;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class DateTimeDialogFragment extends DialogFragment {


    private OnDialogCompleteListener mListener;
    private int mFlag;
    private int mHour;
    private int mMinute;

    @BindView(R.id.fragment_date_time_date_picker)
    DatePicker mDate;

    @BindView(R.id.fragment_date_time_time_picker)
    TimePicker mTime;

    @BindView(R.id.fragment_date_time_button_next)
    Button mNextButton;

    @BindView(R.id.fragment_date_time_linear_layout_buttons)
    LinearLayout mButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_time_dialog, container, false);
        ButterKnife.bind(this, view);
        mHour = Calendar.getInstance().get(Calendar.HOUR);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);


        mTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
            }
        });
        return view;
    }

    public void setOnDateTimeSetListener(OnDialogCompleteListener listener){
        mListener = listener;
    }

    public void setFlag(int flag){
        mFlag = flag;
    }
    @OnClick(R.id.fragment_date_time_button_next)
    void nextClick(){
        mDate.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
        mButtons.setVisibility(View.VISIBLE);
        mTime.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.fragment_date_time_button_back)
    void backClick(){
        mDate.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
        mButtons.setVisibility(View.GONE);
        mTime.setVisibility(View.GONE);
    }

    @OnClick(R.id.fragment_date_time_button_finish)
    void finishClick(){
        //replace null with selected date
        Date date = null;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd:kk:mm:ss", Locale.US);
        String dateString = mDate.getYear() + "/" + (mDate.getMonth()+1) + "/" + mDate.getDayOfMonth() + ":" + mHour + ":" + mMinute + ":00" ;
        try {
            date = format.parse(dateString);
            if(mListener != null){
                mListener.dialogComplete(date , mFlag);
            }
            dismiss();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }




    public interface OnDialogCompleteListener {
        // TODO: Update argument type and name
        void dialogComplete(Date date, int flag);
    }
}
