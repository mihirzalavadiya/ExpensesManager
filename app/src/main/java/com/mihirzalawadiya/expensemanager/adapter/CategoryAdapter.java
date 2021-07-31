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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mihirzalawadiya.expensemanager.R;
import com.mihirzalawadiya.expensemanager.helper.DataBaseHelper;
import com.mihirzalawadiya.expensemanager.model.Category;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> mCategories;
    private int rowLayout;
    private Context context;
    private DataBaseHelper dbHelper;
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout categoryLayout;
        TextView categoryTitle;
        ImageView mImageEdit;
        ImageView mImageDelete;

        public CategoryViewHolder(View v) {
            super(v);
            categoryLayout = (LinearLayout) v.findViewById(R.id.layout_category);
            categoryTitle = (TextView) v.findViewById(R.id.textCategoryName);
            mImageDelete = (ImageView) v.findViewById(R.id.imageDelete);
            mImageEdit = (ImageView) v.findViewById(R.id.imageEdit);
        }
    }

    public CategoryAdapter(List<Category> categories, int rowLayout, Context context) {
        this.mCategories = categories;
        this.rowLayout = rowLayout;
        this.context = context;
        dbHelper = new DataBaseHelper(context);
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (rowLayout, parent, false);
        return new CategoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        holder.categoryTitle.setText(mCategories.get(position).getCategoryName());

        holder.mImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.category_dialog, null);
                dialogBuilder.setView(dialogView);

                final TextView textTitle = (TextView)dialogView.findViewById(R.id.textTitle);
                final EditText editCategoryName = (EditText) dialogView.findViewById(R.id.editCategory);
                final RadioButton rbIncome = (RadioButton)dialogView.findViewById(R.id.radioIncome);
                final RadioButton rbExpense = (RadioButton)dialogView.findViewById(R.id.radioExpense);
                final Button buttonSubmit = (Button)dialogView.findViewById(R.id.buttonSubmit);
                final Category category = mCategories.get(position);
                if(category.getCategoryType().equalsIgnoreCase("income")){
                    rbIncome.setChecked(true);
                }else {
                    rbExpense.setChecked(true);
                }

                textTitle.setText("Update Category");
                editCategoryName.setText(category.getCategoryName());
                //dialogBuilder.show();
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();


                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                                Toast.makeText(context, "Category already exists!", Toast.LENGTH_SHORT).show();
                            }else {
                                category.setCategoryName(strCatName);
                                if(rbExpense.isChecked()){
                                    category.setCategoryType("Expense");
                                }else {
                                    category.setCategoryType("Income");
                                }
                                dbHelper.updateCategory(category);
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
                            int count = dbHelper.getCategoryCount(mCategories.get(position));

                            if(count>0){
                                Toast.makeText(context, "You are not allowed to delete this category, because transaction exists on this category.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }else {
                                int status = dbHelper.deleteCategory(mCategories.get(position));
                                if(status > 0){
                                    removeItem(position);
                                    updateData();
                                    Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();
                                }else {
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
        return mCategories.size();
    }

    public void removeItem(int position) {
        mCategories.remove(position);
        notifyItemRemoved(position);
        //notifyDataSetChanged();
    }
    public void updateData() {

        mCategories.clear();
        mCategories.addAll(dbHelper.getCategories());
        notifyDataSetChanged();
    }
}
