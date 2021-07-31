package com.mihirzalawadiya.expensemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.adapter.CategoryAdapter;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.Category;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    List<Category> mCategories;
    RecyclerView mViewCategories;
    DataBaseHelper dbHelper;
    CategoryAdapter mCategoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setTitle("Category List");
        setSupportActionBar(toolbar);

        //Database COde
        dbHelper = new DataBaseHelper(this);
        mCategories = dbHelper.getCategories();

        mViewCategories = (RecyclerView)findViewById(R.id.recyclerCatogery);
        mViewCategories.setLayoutManager(new LinearLayoutManager(this));
        mCategoryAdapter = new CategoryAdapter(mCategories, R.layout.list_category, getApplicationContext());
        mViewCategories.setAdapter(mCategoryAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.category_dialog, null);
                dialogBuilder.setView(dialogView);

                final TextView textTitle = (TextView)dialogView.findViewById(R.id.textTitle);
                final EditText editCategoryName = (EditText) dialogView.findViewById(R.id.editCategory);
                final RadioButton rbIncome = (RadioButton)dialogView.findViewById(R.id.radioIncome);
                final RadioButton rbExpense = (RadioButton)dialogView.findViewById(R.id.radioExpense);
                final Button buttonSubmit = (Button)dialogView.findViewById(R.id.buttonSubmit);

                textTitle.setText("Insert Category");
                rbIncome.setChecked(true);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Category temp = new Category();
                        String strCatName = editCategoryName.getText().toString().trim();
                        if(strCatName.equals("")){
                            editCategoryName.setError("Please enter category name");
                        }else {
                            boolean status = false;
                            List<Category> tempCat = dbHelper.getCategories();
                            for (Category category : tempCat){
                                if(category.getCategoryName().equalsIgnoreCase(strCatName)){
                                    status = true;
                                    break;
                                }
                            }
                            if(status){
                                Toast.makeText(CategoryActivity.this, "Category already exists!", Toast.LENGTH_SHORT).show();
                            }else {
                                temp.setCategoryName(strCatName);
                                if (rbExpense.isChecked()) {
                                    temp.setCategoryType("Expense");
                                } else {
                                    temp.setCategoryType("Income");
                                }

                                dbHelper.addCategory(temp);
                                mCategories = dbHelper.getCategories();
                                mCategoryAdapter.updateData();
                                alertDialog.dismiss();
                                Toast.makeText(CategoryActivity.this, "Category Added", Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                });
            }
        });
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
