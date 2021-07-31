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
import android.widget.TextView;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.adapter.PaymentModeAdapter;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.PaymentMode;

import java.util.List;

public class PaymentModeActivity extends AppCompatActivity {

    List<PaymentMode> mPaymentModes;
    RecyclerView mViewModes;
    DataBaseHelper dbHelper;
    PaymentModeAdapter mModeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setTitle("Payment Mode List");
        setSupportActionBar(toolbar);

        //Database COde
        dbHelper = new DataBaseHelper(this);
        mPaymentModes= dbHelper.getModes();

        mViewModes = (RecyclerView)findViewById(R.id.recyclerMode);
        mViewModes.setLayoutManager(new LinearLayoutManager(this));
        mModeAdapter = new PaymentModeAdapter(mPaymentModes, R.layout.list_payment_mode, getApplicationContext());
        mViewModes.setAdapter(mModeAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.mode_dialog, null);
                dialogBuilder.setView(dialogView);

                final TextView textTitle = (TextView)dialogView.findViewById(R.id.textTitle);
                final EditText editModeName = (EditText) dialogView.findViewById(R.id.editMode);
                final Button buttonSubmit = (Button)dialogView.findViewById(R.id.buttonSubmit);

                textTitle.setText("Insert Mode");

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PaymentMode temp = new PaymentMode();
                        String strMode = editModeName.getText().toString().trim();
                        if(strMode.equals("")){
                            editModeName.setError("Enter Mode Name");
                        }else {
                            boolean status = false;
                            for (PaymentMode mode : mPaymentModes){
                                if(mode.getModeName().equalsIgnoreCase(strMode)){
                                    status = true;
                                    break;
                                }
                            }
                            if(status){
                                Toast.makeText(PaymentModeActivity.this, "Payment Mode already exists!", Toast.LENGTH_SHORT).show();
                            }else {
                                temp.setModeName(strMode);
                                dbHelper.addMode(temp);
                                mPaymentModes = dbHelper.getModes();
                                mModeAdapter.updateData();
                                alertDialog.dismiss();
                                Toast.makeText(PaymentModeActivity.this, "Payment Added", Toast.LENGTH_SHORT).show();
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
