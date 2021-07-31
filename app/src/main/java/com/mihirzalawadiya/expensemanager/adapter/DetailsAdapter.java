package com.mihirzalawadiya.expensemanager.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mihirzalawadiya.expensemanager.R;
import com.mihirzalawadiya.expensemanager.ViewExpenseActivity;
import com.mihirzalawadiya.expensemanager.ViewIncomeActivity;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.TranDetail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    private List<TranDetail> mTranDetails;
    private int rowLayout;
    private Context context;
    private DataBaseHelper dbHelper;
    public static class DetailsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout detailLayout;
        TextView textDate;
        TextView textAmount;
        TextView textCategory;
        TextView textView;

        public DetailsViewHolder(View v) {
            super(v);
            detailLayout = (LinearLayout) v.findViewById(R.id.layout_details);
            textDate = (TextView) v.findViewById(R.id.textDate);
            textAmount = (TextView) v.findViewById(R.id.textAmount);
            textCategory = (TextView) v.findViewById(R.id.textCategoryName);
            textView = (TextView) v.findViewById(R.id.textView);
        }
    }

    public DetailsAdapter(List<TranDetail> details, int rowLayout, Context context) {
        this.mTranDetails = details;
        this.rowLayout = rowLayout;
        this.context = context;
        dbHelper = new DataBaseHelper(context);
    }

    @Override
    public DetailsAdapter.DetailsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (rowLayout, parent, false);
        return new DetailsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(DetailsViewHolder holder, final int position) {
        final TranDetail detail = mTranDetails.get(position);


        if(detail.getType()==0){
            holder.textDate.setTextColor(context.getResources().getColor(R.color.darkblue));
            holder.textAmount.setTextColor(context.getResources().getColor(R.color.darkblue));
            holder.textCategory.setTextColor(context.getResources().getColor(R.color.darkblue));


        }else{
            holder.textDate.setTextColor(context.getResources().getColor(R.color.darkorange));
            holder.textAmount.setTextColor(context.getResources().getColor(R.color.darkorange));
            holder.textCategory.setTextColor(context.getResources().getColor(R.color.darkorange));
        }
        String dateTemp = detail.getIncomeDate();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        int tempDate = 0;
        try {
            Date dtt = df.parse(dateTemp);
            Calendar cTemp = Calendar.getInstance();
            cTemp.setTime(dtt);
            tempDate = cTemp.get(Calendar.DATE);

        }catch (ParseException p){
            //Log.d("===",p.getMessage());
        }
        holder.textDate.setText(tempDate+"");
        holder.textAmount.setText(String.format("%.2f", detail.getAmount()));
        String cat_name = dbHelper.getCategoryName(detail.getCategoryID());
        if(cat_name == null){
            holder.textCategory.setText("NA");
        }else{
            holder.textCategory.setText(cat_name);
        }

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detail.getType()==0){
                    Intent intent = new Intent(context, ViewIncomeActivity.class);
                    intent.putExtra("tran_id",detail.getId());
                    view.getContext().startActivity(intent);
                    /*Context context = view.getContext();
                    Activity activity = (Activity)context;
                    activity.startActivityForResult(intent,111);*/

                }else{
                    Intent intent = new Intent(context, ViewExpenseActivity.class);
                    intent.putExtra("tran_id",detail.getId());
                    view.getContext().startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mTranDetails.size();
    }

    public void removeItem(int position) {
        mTranDetails.remove(position);
        notifyItemRemoved(position);
        //notifyDataSetChanged();
    }
    public void updateData() {

        mTranDetails.clear();
        mTranDetails.addAll(dbHelper.getTranDetails());
        notifyDataSetChanged();
    }
}
