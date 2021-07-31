package com.mihirzalawadiya.expensemanager.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.R;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.PaymentMode;

import java.util.List;


public class PaymentModeAdapter extends RecyclerView.Adapter<PaymentModeAdapter.PaymentViewHolder> {

    private List<PaymentMode> mPaymetModes;
    private int rowLayout;
    private Context context;
    private DataBaseHelper dbHelper;

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout PaymentLayout;
        TextView paymentTitle;
        ImageView mImageEdit;
        ImageView mImageDelete;

        public PaymentViewHolder(View v) {
            super(v);
            PaymentLayout = (LinearLayout) v.findViewById(R.id.layout_payment);
            paymentTitle = (TextView) v.findViewById(R.id.textPaymentName);
            mImageDelete = (ImageView) v.findViewById(R.id.imageDelete);
            mImageEdit = (ImageView) v.findViewById(R.id.imageEdit);
        }
    }

    public PaymentModeAdapter(List<PaymentMode> modes, int rowLayout, Context context) {
        this.mPaymetModes = modes;
        this.rowLayout = rowLayout;
        this.context = context;
        dbHelper = new DataBaseHelper(context);
    }

    @Override
    public PaymentModeAdapter.PaymentViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (rowLayout, parent, false);
        return new PaymentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PaymentViewHolder holder, final int position) {
        holder.paymentTitle.setText(mPaymetModes.get(position).getModeName());

        holder.mImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.mode_dialog, null);
                dialogBuilder.setView(dialogView);

                final TextView textTitle = (TextView) dialogView.findViewById(R.id.textTitle);
                final EditText editModeName = (EditText) dialogView.findViewById(R.id.editMode);
                final Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmit);
                final PaymentMode mode = mPaymetModes.get(position);

                textTitle.setText("Update Mode");
                editModeName.setText(mode.getModeName());
                //dialogBuilder.show();
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();


                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strMode = editModeName.getText().toString().trim();
                        if (strMode.equals("")) {
                            editModeName.setError("Enter Mode Name");
                        } else {
                            boolean status = false;
                            List<PaymentMode> modes = dbHelper.getModes();
                            for (PaymentMode mode : modes) {
                                if (mode.getModeName().equalsIgnoreCase(strMode)) {
                                    status = true;
                                    break;
                                }
                            }
                            if (status) {
                                Toast.makeText(context, "Payment Mode already exists!", Toast.LENGTH_SHORT).show();
                            } else {
                                mode.setModeName(strMode);

                                dbHelper.updateMode(mode);
                                updateData();
                                alertDialog.dismiss();
                            }
                        }

                    }
                });
            }
        });
        holder.mImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());

                builder.setTitle("Are you sure you want to delete?");
                //builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        int count = dbHelper.getPaymentCount(mPaymetModes.get(position));

                        if (count > 0) {
                            Toast.makeText(context, "You are not allowed to delete this payment mode, because transaction exists on this payment mode.", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else {
                            int status = dbHelper.deleteMode(mPaymetModes.get(position));
                            if (status > 0) {
                                removeItem(position);
                                updateData();
                                Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Delete Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPaymetModes.size();
    }

    public void removeItem(int position) {
        mPaymetModes.remove(position);
        notifyItemRemoved(position);
        //notifyDataSetChanged();
    }

    public void updateData() {

        mPaymetModes.clear();
        mPaymetModes.addAll(dbHelper.getModes());
        notifyDataSetChanged();
    }
}
