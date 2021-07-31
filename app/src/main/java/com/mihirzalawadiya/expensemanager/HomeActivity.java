package com.mihirzalawadiya.expensemanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.helper.AlarmReceiver;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private DataBaseHelper dataBaseHelper;
    private ArrayList<PieEntry> entries;
    PieChart mChart;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);

        dataBaseHelper = new DataBaseHelper(getApplicationContext());
        try {
            dataBaseHelper.createDataBase();
        }catch (Exception e){
            //Log.e("DB CREATE",e.getMessage());
            Toast.makeText(this,"Not Able to Create DB!",Toast.LENGTH_SHORT).show();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Expenses Manager");
        setSupportActionBar(toolbar);


        if(!preferences.getBoolean("status",false)){
            //Set Data for notification
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("hour",calendar.get(Calendar.HOUR_OF_DAY));
            editor.putInt("minute",calendar.get(Calendar.MINUTE));
            editor.putInt("second",calendar.get(Calendar.SECOND));
            editor.apply();

            setNotification();
        }





        mChart = (PieChart)findViewById(R.id.chart1);
        mChart.setDescription("Expenses Manager");
        entries = new ArrayList<>();

        fillChartData();
        /*double income = dataBaseHelper.getIncome();
        double expense = dataBaseHelper.getExpense();
        entries.add(new PieEntry((float) (income -expense),"Balance",0));
        entries.add(new PieEntry((float) expense,"Expense",0));*/


    }

    private SpannableString generateCenterSpannableText(String msg) {

        SpannableString s = new SpannableString(msg);
        s.setSpan(new RelativeSizeSpan(1.4f), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(.9f), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() , s.length(), 0);
        return s;
    }

    public void onSettings(View view){
        startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
    }
    public void onAddIncome(View view){
        startActivity(new Intent(HomeActivity.this,IncomeActivity.class));
    }
    public void onAddExpense(View view){
        startActivity(new Intent(HomeActivity.this,ExpenseActivity.class));
    }
    public void onAllTran(View view){
        startActivity(new Intent(HomeActivity.this,AllDetailActivity.class));
    }
    public void onShare(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "To-Do Expenses");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "https://www.linkedin.com/in/mihirzalavadiya");
        sendIntent.setType("text/plain");
        view.getContext().startActivity(Intent.createChooser(sendIntent,"Share this app using"));
    }
    public void onReport(View view){
        startActivity(new Intent(HomeActivity.this,ReportActivity.class));
    }
    @Override
    protected void onResume() {
        super.onResume();
        //fillChartData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fillChartData();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.exit_dialog, null);
        dialogBuilder.setView(dialogView);

        Button cancle = (Button)dialogView.findViewById(R.id.buttonNo);
        Button ok = (Button)dialogView.findViewById(R.id.buttonYes);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                HomeActivity.super.onBackPressed();
                finish();

            }
        });
    }

    private void fillChartData(){

        double income = dataBaseHelper.getIncome();
        mChart.setCenterText(generateCenterSpannableText("Total Income\n"+income));
        double expense = dataBaseHelper.getExpense();
        entries.clear();
        double difference = income -expense;
        ArrayList<Integer> colors = new ArrayList<Integer>();
        if(difference <= 0){
            //entries.add(new PieEntry((float) 0,"Balance",0));
        }else {
            colors.add(getResources().getColor(R.color.light_blue_A200));
            entries.add(new PieEntry((float) (income -expense),"Balance",0));

        }
        entries.add(new PieEntry((float) expense,"Expense",0));


        colors.add(getResources().getColor(R.color.chocolate));



        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(8f);
        l.setYEntrySpace(1f);
        l.setYOffset(2f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        //mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);
        mChart.animateY(2000, Easing.EasingOption.EaseInOutQuad);


        mChart.setDrawHoleEnabled(true);
        //mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setHoleColor(getResources().getColor(R.color.DarkGray));


        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        mChart.highlightValue(null);
        mChart.invalidate();

    }
    private void setNotification(){

        int hour = preferences.getInt("hour", 0);
        int minute = preferences.getInt("minute", 0);
        int seconds = preferences.getInt("second", 0);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("status",true).apply();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,seconds);

        Intent intent1 = new Intent(HomeActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) HomeActivity.this.getSystemService(ALARM_SERVICE);
        assert am != null;
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
