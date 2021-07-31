package com.mihirzalawadiya.expensemanager;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.adapter.DetailsAdapter;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.TranDetail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllDetailActivity extends AppCompatActivity {

    private DataBaseHelper dbHelper;
    private RecyclerView mViewDetails;
    private DetailsAdapter mDetailsAdapter;
    private List<TranDetail> mTranDetails, details;
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerYear;
    private List<String> listYear;
    private Button buttonSearch, buttonClear;
    private LinearLayout layoutSearch, layoutBalance;
    private TextView textIncome, textExpense, textBalance;
    private boolean isSearchEnable = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setTitle("All Transaction");
        setSupportActionBar(toolbar);
        dbHelper = new DataBaseHelper(this);
        mTranDetails = dbHelper.getTranDetails();

        mSpinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        mSpinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonClear = (Button) findViewById(R.id.buttonClear);
        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
        layoutBalance = (LinearLayout) findViewById(R.id.layoutBalance);
        textIncome = (TextView) findViewById(R.id.textIncome);
        textExpense = (TextView) findViewById(R.id.textExpense);
        textBalance = (TextView) findViewById(R.id.textBalance);
        buttonClear.setVisibility(View.GONE);

        details = new ArrayList<>();

        double income = dbHelper.getIncome();
        double expense = dbHelper.getExpense();
        textIncome.setText(String.format("%.2f", income));
        textExpense.setText(String.format("%.2f", expense));
        textBalance.setText(String.format("%.2f", (income - expense)));
        listYear = new ArrayList<>();
        for (TranDetail detail : mTranDetails) {
            String dateTemp = detail.getIncomeDate();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date dtt = df.parse(dateTemp);
                Calendar cTemp = Calendar.getInstance();
                cTemp.setTime(dtt);
                int year = cTemp.get(Calendar.YEAR);
                listYear.add(String.valueOf(year));
                //Log.d("===",year+"   "+dateTemp);

            } catch (ParseException p) {
                //Log.d("===",p.getMessage());
            }
        }
        Set<String> hs = new HashSet<>(listYear);
        listYear.clear();
        listYear.addAll(hs);
        Collections.sort(listYear);

        //Set Current month and year to spinner
        Calendar calendar = Calendar.getInstance();
        if (listYear.size() == 0) {
            int year = calendar.get(Calendar.YEAR);
            listYear.add(String.valueOf(year));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner, listYear);
        mSpinnerYear.setAdapter(dataAdapter);
        mSpinnerMonth.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner, getResources().getStringArray(R.array.month_arrays)));
        mSpinnerMonth.setSelection(calendar.get(Calendar.MONTH));


        mViewDetails = (RecyclerView) findViewById(R.id.recyclerDetails);
        mViewDetails.setLayoutManager(new LinearLayoutManager(this));
        mDetailsAdapter = new DetailsAdapter(mTranDetails, R.layout.list_all_tran, getApplicationContext());
        mViewDetails.setAdapter(mDetailsAdapter);
        mViewDetails.setVisibility(View.GONE);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchEnable = true;
                details.clear();
                String month = (String) mSpinnerMonth.getSelectedItem();
                String year = (String) mSpinnerYear.getSelectedItem();

                String months[] = getResources().getStringArray(R.array.month_arrays);
                List<String> listMonth = new ArrayList<>();
                for (String s : months) {
                    listMonth.add(s);
                }
                int monthFind = listMonth.indexOf(month);
                int yearFind = Integer.parseInt(year);


                for (TranDetail temp : mTranDetails) {
                    String dateTemp = temp.getIncomeDate();
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date dtt = df.parse(dateTemp);
                        Calendar cTemp = Calendar.getInstance();
                        cTemp.setTime(dtt);
                        int yearTemp = cTemp.get(Calendar.YEAR);
                        int monthTemp = cTemp.get(Calendar.MONTH);
                        if (yearFind == yearTemp && monthFind == monthTemp) {
                            details.add(temp);
                        }
                        //Log.d("===",year+"   "+dateTemp);

                    } catch (ParseException p) {
                        //Log.d("===",p.getMessage());
                    }

                }
                if (details.size() == 0) {
                    Toast.makeText(AllDetailActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
                } else {
                    buttonClear.setVisibility(View.VISIBLE);
                    mViewDetails.setVisibility(View.VISIBLE);
                    layoutBalance.setVisibility(View.GONE);
                    layoutSearch.setVisibility(View.GONE);
                    mDetailsAdapter = new DetailsAdapter(details, R.layout.list_all_tran, getApplicationContext());
                    mViewDetails.setAdapter(mDetailsAdapter);
                }

                //Toast.makeText(AllDetailActivity.this, details.size()+"", Toast.LENGTH_SHORT).show();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //mSpinnerMonth.setSelection(0);
                //mSpinnerYear.setSelection(0);
                mViewDetails.setVisibility(View.GONE);
                buttonClear.setVisibility(View.GONE);
                layoutBalance.setVisibility(View.VISIBLE);
                layoutSearch.setVisibility(View.VISIBLE);
                details.clear();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isSearchEnable) {
            isSearchEnable = false;
            clearDataToDefault();
        } else {
            super.onBackPressed();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        //clearDataToDefault();
        //Log.d("===","Resume Called");
        //mTranDetails.clear();
       /* buttonClear.setVisibility(View.GONE);
        mViewDetails.setVisibility(View.GONE);
        layoutBalance.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.VISIBLE);
        double income = dbHelper.getIncome();
        double expense = dbHelper.getExpense();
        textIncome.setText(String.format("%.2f", income));
        textExpense.setText(String.format("%.2f", expense));
        textBalance.setText(String.format("%.2f", (income - expense)));
        mTranDetails = dbHelper.getTranDetails();*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("===","Restart Called");

        clearDataToDefault();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearDataToDefault() {
        isSearchEnable = false;
        buttonClear.setVisibility(View.GONE);
        mViewDetails.setVisibility(View.GONE);
        layoutBalance.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.VISIBLE);
        /*mSpinnerMonth.setSelection(0);
        mSpinnerYear.setSelection(0);*/
        details.clear();
        double income = dbHelper.getIncome();
        double expense = dbHelper.getExpense();
        textIncome.setText(String.format("%.2f", income));
        textExpense.setText(String.format("%.2f", expense));
        textBalance.setText(String.format("%.2f", (income - expense)));
        mTranDetails = dbHelper.getTranDetails();
    }
}
