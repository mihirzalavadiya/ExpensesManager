package com.mihirzalawadiya.expensemanager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mihirzalawadiya.expensemanager.adapter.AmountAdapter;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.AmountData;
import com.mihirzalawadiya.expensemanager.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private DataBaseHelper dbHelper;
    private List<Category> categories,incCategories;
    private List<AmountData> expenseList,incomeList;
    private RecyclerView mViewIncome;
    private RecyclerView mViewExpense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);

        mViewExpense = (RecyclerView)findViewById(R.id.viewExpense);
        mViewIncome = (RecyclerView)findViewById(R.id.viewIncome);

        dbHelper = new DataBaseHelper(this);

        //Expense Details
        categories = dbHelper.getExpenseCategories();
        expenseList= new ArrayList<>();
        for(Category category: categories){
            double amount =  dbHelper.incomeSource(category.getCategoryID());

            if(amount != 0.0){
                AmountData data = new AmountData();
                data.setCategoty_name(category.getCategoryName());
                data.setAmount(amount);
                expenseList.add(data);
            }
        }

        //Income Details
        incCategories = dbHelper.getIncomeCategories();
        incomeList= new ArrayList<>();
        for(Category category: incCategories){
            double amount =  dbHelper.incomeSource(category.getCategoryID());

            if(amount != 0.0){
                AmountData data = new AmountData();
                data.setCategoty_name(category.getCategoryName());
                data.setAmount(amount);
                incomeList.add(data);
            }
        }

        mViewIncome.setLayoutManager(new LinearLayoutManager(this));
        mViewIncome.setAdapter(new AmountAdapter(incomeList,R.layout.list_amount,getApplicationContext()));

        mViewExpense.setLayoutManager(new LinearLayoutManager(this));
        mViewExpense.setAdapter(new AmountAdapter(expenseList,R.layout.list_amount,getApplicationContext()));
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
