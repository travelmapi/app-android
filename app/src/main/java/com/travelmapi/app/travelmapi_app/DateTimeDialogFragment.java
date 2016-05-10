package com.travelmapi.app.travelmapi_app;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
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
    private Date mDefaultDate;
    public static final String DATE_FORMAT ="yyyy/MM/dd kk:mm:ss";

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
        getDialog().setCanceledOnTouchOutside(true);
        ButterKnife.bind(this, view);
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);


        mTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
            }
        });
        Calendar cal = Calendar.getInstance();
        if(mDefaultDate != null) {
            cal.setTime(mDefaultDate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTime.setMinute(cal.get(Calendar.MINUTE));
                mTime.setHour(cal.get(Calendar.HOUR_OF_DAY));
            } else {
                mTime.setCurrentMinute(cal.get(Calendar.MINUTE));
                mTime.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            }
            mDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
        return view;
    }

    public void setOnDateTimeSetListener(OnDialogCompleteListener listener){
        mListener = listener;
    }

    /**
     *TODO: change calendar to default to selected date
     * @param date the intital date to be shown.
     */
    public void setDate(Date date){
        if(date == null){
            return;
        }
        mDefaultDate = date;

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
        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String dateString = mDate.getYear() + "/" + (mDate.getMonth()+1) + "/" + mDate.getDayOfMonth() + " " + mHour + ":" + mMinute + ":00" ;
        try {
            mDefaultDate = format.parse(dateString);
            if(mListener != null){
                mListener.dialogComplete(mDefaultDate , mFlag);
            }
            dismiss();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    public interface OnDialogCompleteListener {
        void dialogComplete(Date date, int flag);
    }
}
