package com.m.sofiane.go4lunch.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.m.sofiane.go4lunch.R;
import com.m.sofiane.go4lunch.services.notificationService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

/**
 * created by Sofiane M. 23/04/2020
 */

public class SettingsFragment extends DialogFragment {

    public SettingsFragment() {
        // Empty constructor required for DialogFragment
    }


    @BindView(R.id.switchNotif)
    Switch mSwitch;

    @BindView(R.id.ClockNotif)
    TimePicker picker;

    @BindView(R.id.buttonTime)
    Button mButtonTime;

    final Fragment mapFragment = new MapFragment();

    int h;
    int m;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);
        uploadToolbar(view);
        ButterKnife.bind(this, view);
        initSwitch();
    //    initButton();
     //   uploadBototmBr(view);
        return view;
    }

    private void uploadToolbar(View view) {
        TextView mTitleText = (TextView) getActivity().findViewById(R.id.toolbar_title);
        mTitleText.setText("My Settings");
    }

    private void uploadBototmBr(View view) {
        BottomNavigationView mBottomNavigationView = getActivity().findViewById(R.id.activity_main_bottom_navigation);
        mBottomNavigationView.setVisibility(mBottomNavigationView.GONE);
        BottomNavigationView mBmNaViewForDrawer = view.findViewById(R.id.drawer_bottom_navigation);
        mBmNaViewForDrawer.setVisibility(view.VISIBLE);

        mBmNaViewForDrawer.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                mBmNaViewForDrawer.setItemIconTintList(ColorStateList.valueOf(Color.parseColor("#ff4444")));
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, mapFragment)
                        .addToBackStack(null)
                        .commit();
                System.out.println("BACK = " + "To the future");
            } return true;
        });
    }

    @SuppressLint("ResourceAsColor")
    private void initButton() {
        picker.setVisibility(View.INVISIBLE);
        mButtonTime.setVisibility(View.INVISIBLE);

        mButtonTime.setOnClickListener(v -> {
            int hour, minute;
            String am_pm;
            if (Build.VERSION.SDK_INT >= 23) {
                h = picker.getHour();
                m = picker.getMinute();
            } else {
                h = picker.getCurrentHour();
                m = picker.getCurrentMinute();
            }
            if (h > 12) {
                am_pm = "PM";
                h = h - 12;
            } else {
                am_pm = "AM";
            }
            String mTime = "Selected Date: " + h + ":" + m + " " + am_pm;
            Toast.makeText(getContext(), mTime, Toast.LENGTH_SHORT).show();
        });
    }


    @SuppressLint("ResourceAsColor")
    public void initSwitch() {

        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Toast.makeText(getActivity(), "Switch in action", Toast.LENGTH_SHORT).show();
                    setAlarm();
                    picker.setVisibility(View.VISIBLE);
                    mButtonTime.setVisibility(View.VISIBLE);
                }

            } else {
                WorkManager.getInstance().cancelAllWork();
                Toast.makeText(getActivity(), "OFF", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAlarm() {

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);


        Intent notificationIntent = new Intent(getActivity(), notificationService.class);

        PendingIntent broadcast = PendingIntent.getBroadcast(getActivity(), 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);
    }

}


