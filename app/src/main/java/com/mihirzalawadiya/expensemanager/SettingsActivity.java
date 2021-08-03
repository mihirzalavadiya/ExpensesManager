package com.mihirzalawadiya.expensemanager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.helper.AlarmReceiver;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvActionBarTitle;
    private TextView tvReminderTime;
    private Calendar calendar;
    SharedPreferences sharedPref;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initControls();
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        tvReminderTime = findViewById(R.id.tv_remider_time);

        sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        int hour = sharedPref.getInt("hour", 0);
        int minute = sharedPref.getInt("minute", 0);
        int seconds = sharedPref.getInt("second", 0);
        tvReminderTime.setText(String.format("%02d", hour)
                + ":" + String.format("%02d", minute));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, seconds);


        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView tvVersionName = (TextView) findViewById(R.id.tvVersionName);
            tvVersionName.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setReminderForExpense(View view) {

        // Use the current time as the default values for the picker
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view1, hourOfDay, minute1) -> {

                    Toast.makeText(SettingsActivity.this,"Reminder set for "+String.format("%02d", hourOfDay)
                            + ":" + String.format("%02d", minute1),Toast.LENGTH_LONG).show();

                    tvReminderTime.setText(String.format("%02d", hourOfDay)
                            + ":" + String.format("%02d", minute1));
                    //tvReminderTime.setText(hourOfDay + ":" + minute);

                    Calendar temp = Calendar.getInstance();
                    temp.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    temp.set(Calendar.MINUTE, minute1);


                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("hour",temp.get(Calendar.HOUR_OF_DAY));
                    editor.putInt("minute",temp.get(Calendar.MINUTE));
                    editor.putInt("second",temp.get(Calendar.SECOND));
                    editor.putBoolean("status",true);
                    editor.apply();

                    Intent intent1 = new Intent(SettingsActivity.this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager) SettingsActivity.this.getSystemService(ALARM_SERVICE);
                    assert am != null;
                    am.setRepeating(AlarmManager.RTC_WAKEUP, temp.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                }, hour, minute, true);
        timePickerDialog.show();


    }

    public void OnCategory(View view) {
        startActivity(new Intent(SettingsActivity.this, CategoryActivity.class));
    }

    public void OnMode(View view) {
        startActivity(new Intent(SettingsActivity.this, PaymentModeActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
