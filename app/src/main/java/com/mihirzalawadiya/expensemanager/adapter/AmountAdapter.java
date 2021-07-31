package com.mihirzalawadiya.expensemanager.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mihirzalawadiya.expensemanager.R;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.AmountData;

import java.util.List;


public class AmountAdapter extends RecyclerView.Adapter<AmountAdapter.PaymentViewHolder> {

    private List<AmountData> mAmountData;
    private int rowLayout;
    private Context context;
    private DataBaseHelper dbHelper;
    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sourceLayout;
        TextView paymentTitle;
        TextView paymentAmount;

        public PaymentViewHolder(View v) {
            super(v);
            sourceLayout = (LinearLayout) v.findViewById(R.id.layout_source);
            paymentTitle = (TextView) v.findViewById(R.id.textCategoryName);
            paymentAmount = (TextView) v.findViewById(R.id.textAmount);
        }
    }

    public AmountAdapter(List<AmountData> data, int rowLayout, Context context) {
        this.mAmountData = data;
        this.rowLayout = rowLayout;
        this.context = context;
        dbHelper = new DataBaseHelper(context);
    }

    @Override
    public AmountAdapter.PaymentViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (rowLayout, parent, false);
        return new PaymentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PaymentViewHolder holder, final int position) {
        holder.paymentTitle.setText(mAmountData.get(position).getCategoty_name());
        holder.paymentAmount.setText(String.format("%.2f",mAmountData.get(position).getAmount()));

    }

    @Override
    public int getItemCount() {
        return mAmountData.size();
    }

}
