package com.mihirzalawadiya.expensemanager;

import android.app.AlertDialog;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.Category;
import com.mihirzalawadiya.expensemanager.model.PaymentMode;
import com.mihirzalawadiya.expensemanager.model.TranDetail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private DataBaseHelper dbHelper;
    private List<Category> mCategories;
    private List<PaymentMode> mModes;
    private Spinner mSpinnerCategory;
    private Spinner mSpinnerMode;
    private TextView mTextDate;
    private EditText mEditAMount;
    private EditText mEditDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setTitle("Add Expense");
        setSupportActionBar(toolbar);
        dbHelper = new DataBaseHelper(this);
        mCategories = dbHelper.getExpenseCategories();
        mModes = dbHelper.getModes();

        mSpinnerCategory = (Spinner)findViewById(R.id.spinnerCategory);
        mSpinnerMode = (Spinner)findViewById(R.id.spinnerMode);
        mTextDate = (TextView) findViewById(R.id.textDate);
        mEditAMount = (EditText) findViewById(R.id.editAmount);
        mEditDescription = (EditText) findViewById(R.id.editDescription);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate= formatter.format(date);
        mTextDate.setText(strDate);

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<Category>(this,
                R.layout.spinner, mCategories);
        mSpinnerCategory.setAdapter(dataAdapter);

        ArrayAdapter<PaymentMode> dataAdapterMode = new ArrayAdapter<PaymentMode>(this,
                R.layout.spinner, mModes);
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMode.setAdapter(dataAdapterMode);

        if(mCategories.size() == 0){
            mSpinnerCategory.setVisibility(View.GONE);
        }else if(mModes.size() == 0){
            mSpinnerMode.setVisibility(View.GONE);
        }
        mTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.category_date, null);
                dialogBuilder.setView(dialogView);

                final DatePicker picker = (DatePicker)dialogView.findViewById(R.id.dateIncome);
                Button cancle = (Button)dialogView.findViewById(R.id.buttonCancel);
                Button ok = (Button)dialogView.findViewById(R.id.buttonOK);

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
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR,picker.getYear());
                        calendar.set(Calendar.MONTH,picker.getMonth());
                        calendar.set(Calendar.DATE,picker.getDayOfMonth());

                        Date d = calendar.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate= formatter.format(d);
                        mTextDate.setText(strDate);
                        alertDialog.dismiss();
                    }
                });


            }
        });
    }
    public void onSaveExpense(View view){

        if(mCategories.size() == 0){
            Toast.makeText(ExpenseActivity.this,"Please add category from setting",Toast.LENGTH_LONG).show();
            return;
        }else if(mModes.size() == 0){
            Toast.makeText(ExpenseActivity.this,"Please add payment mode from setting",Toast.LENGTH_LONG).show();
            return;
        }

        String strAmount = mEditAMount.getText().toString().trim();
        if(strAmount.equals("")){
            mEditAMount.setError("Please Enter Amount");
        }else {
            TranDetail detail = new TranDetail();
            detail.setAmount(Double.valueOf(mEditAMount.getText().toString()));
            detail.setDescription(mEditDescription.getText().toString());
            detail.setIncomeDate(mTextDate.getText().toString());
            Category cTemp = (Category) mSpinnerCategory.getSelectedItem();
            detail.setCategoryID(cTemp.getCategoryID());
            PaymentMode pTemp = (PaymentMode) mSpinnerMode.getSelectedItem();
            detail.setModeID(pTemp.getModeID());
            detail.setType(1);
            long count = dbHelper.addTransaction(detail);
            if (count > 0) {
                mEditDescription.setText("");
                mEditAMount.setText("");
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = formatter.format(date);
                mTextDate.setText(strDate);

                Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT).show();
            }
        }
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
