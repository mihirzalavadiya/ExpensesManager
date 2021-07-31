package com.mihirzalawadiya.expensemanager.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.mihirzalawadiya.expensemanager.model.Category;
import com.mihirzalawadiya.expensemanager.model.TranDetail;
import com.mihirzalawadiya.expensemanager.model.PaymentMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper{
    private static String DB_PATH;
    private static String DB_NAME = "dailyexpense.db";
    private final Context myContext;
    private SQLiteDatabase myDataBase;

    // Contacts table name
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_TRAN = "tran_details";
    private static final String TABLE_INCOME = "income";
    private static final String TABLE_PAYMENT_MODE = "payment_mode";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH = "/data/data/" + myContext.getPackageName() + "/databases/";
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            //Toast.makeText(myContext, "DB can access", Toast.LENGTH_SHORT).show();
            //Log.v("DB Exists", "DB Exists");
            //do nothing - database already exist
        } else {
            //Toast.makeText(myContext, "DB can  NOT access", Toast.LENGTH_SHORT).show();
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        boolean checkDB = false;
        //SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbFile = new File(myPath);
            checkDB = dbFile.exists();
        } catch (SQLiteException e) {
            //database does't exist yet.
        }
        //Log.v("CHECKDB", checkDB + "  Got");
        return checkDB;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
       // Log.e("DATA", outFileName);
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        //Open the database
        //String myPath = DB_PATH + DB_NAME;
        String myPath = myContext.getDatabasePath(DB_NAME).getPath();
        if (myDataBase != null && myDataBase.isOpen()) {
            return;
        }
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Code for handling Category
     *
     */

    public double getIncome(){
        String selectQuery = "SELECT SUM(amount) FROM tran_details where type=0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        double income = 0;
        if (c.moveToFirst()) {
            income = c.getDouble(0);
        }
        db.close();
        return income;
    }
    public double getExpense(){
        String selectQuery = "SELECT SUM(amount) FROM tran_details where type=1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        double income = 0;
        if (c.moveToFirst()) {
            income = c.getDouble(0);
        }
        db.close();
        return income;
    }
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM category ORDER BY category_name ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                //Log.e("=====DATA",c.getString(1));
                Category category = new Category();
                category.setCategoryID(c.getInt(0));
                category.setCategoryName(c.getString(1));
                category.setCategoryType(c.getString(2));

                categories.add(category);

                // adding to tags list

            } while (c.moveToNext());
        }
        db.close();
        return categories;
    }

    public List<Category> getIncomeCategories() {
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM category where category_type='Income' ORDER BY category_name ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                //Log.e("=====DATA",c.getString(1));
                Category category = new Category();
                category.setCategoryID(c.getInt(0));
                category.setCategoryName(c.getString(1));
                category.setCategoryType(c.getString(2));

                categories.add(category);

                // adding to tags list

            } while (c.moveToNext());
        }
        db.close();
        return categories;
    }
    public List<Category> getExpenseCategories() {
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM category where category_type='Expense' ORDER BY category_name ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                //Log.e("=====DATA",c.getString(1));
                Category category = new Category();
                category.setCategoryID(c.getInt(0));
                category.setCategoryName(c.getString(1));
                category.setCategoryType(c.getString(2));

                categories.add(category);

                // adding to tags list

            } while (c.moveToNext());
        }
        db.close();
        return categories;
    }
    public int deleteCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        int status = db.delete(TABLE_CATEGORY, "category_id" + " = ?",
                new String[] { String.valueOf(category.getCategoryID()) });
        db.close();
        return status;
    }
    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("category_name", category.getCategoryName());
        values.put("category_type", category.getCategoryType());
        // updating row
        return db.update(TABLE_CATEGORY, values, "category_id" + " = ?",
                new String[] { String.valueOf(category.getCategoryID()) });
    }
    public long addCategory(Category c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID,p.getID());
        values.put("category_name",c.getCategoryName());
        values.put("category_type",c.getCategoryType());
        // Inserting Row
        long count = db.insert(TABLE_CATEGORY, null, values);
        db.close(); // Closing database connection
        return count;
    }

    /**
     * Methods for Payment Mode
     */

    public List<PaymentMode> getModes() {
        List<PaymentMode> modes = new ArrayList<PaymentMode>();
        String selectQuery = "SELECT * FROM payment_mode ORDER BY mode_name ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                //Log.e("=====DATA",c.getString(1));
                PaymentMode mode = new PaymentMode();
                mode.setModeID(c.getInt(0));
                mode.setModeName(c.getString(1));

                modes.add(mode);

            } while (c.moveToNext());
        }
        db.close();
        return modes;
    }

    public int deleteMode(PaymentMode mode){
        SQLiteDatabase db = this.getWritableDatabase();
        int status = db.delete(TABLE_PAYMENT_MODE, "mode_id" + " = ?",
                new String[] { String.valueOf(mode.getModeID()) });
        db.close();
        return status;
    }
    public int updateMode(PaymentMode mode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mode_name", mode.getModeName());
        // updating row
        return db.update(TABLE_PAYMENT_MODE, values, "mode_id" + " = ?",
                new String[] { String.valueOf(mode.getModeID()) });
    }
    public long addMode(PaymentMode p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID,p.getID());
        values.put("mode_name",p.getModeName());
        // Inserting Row
        long count = db.insert(TABLE_PAYMENT_MODE, null, values);
        db.close(); // Closing database connection
        return count;
    }

    /**
     * Methods for Transaction
     */

    public long addTransaction(TranDetail detail) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID,p.getID());
        values.put("income_date",detail.getIncomeDate());
        values.put("amount",detail.getAmount());
        values.put("description",detail.getDescription());
        values.put("category_id",detail.getCategoryID());
        values.put("mode_id",detail.getModeID());
        values.put("type",detail.getType());

        // Inserting Row
        long count = db.insert(TABLE_TRAN, null, values);
        db.close(); // Closing database connection
        return count;
    }
    public List<TranDetail> getTranDetails() {
        List<TranDetail> tranDetails = new ArrayList<TranDetail>();
        String selectQuery = "SELECT * FROM tran_details ORDER BY income_date ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                TranDetail temp = new TranDetail();
                temp.setId(c.getInt(0));
                temp.setIncomeDate(c.getString(1));
                temp.setAmount(c.getDouble(2));
                temp.setCategoryID(c.getInt(3));
                temp.setDescription(c.getString(4));
                temp.setModeID(c.getInt(5));
                temp.setType(c.getInt(6));
                tranDetails.add(temp);

            } while (c.moveToNext());
        }
        db.close();
        return tranDetails;
    }
    public int getCategoryCount(Category category) {
        int count =0;
        List<TranDetail> tranDetails = new ArrayList<TranDetail>();
        String selectQuery = "SELECT count(*) FROM tran_details where mode_id="+category.getCategoryID();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        db.close();
        return count;
    }
    public int getPaymentCount(PaymentMode mode) {
        int count =0;
        List<TranDetail> tranDetails = new ArrayList<TranDetail>();
        String selectQuery = "SELECT count(*) FROM tran_details where category_id="+mode.getModeID();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        db.close();
        return count;
    }
    public String getCategoryName(int id){
        String selectQuery = "SELECT category_name FROM category where category_id="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if(c.moveToFirst()){
            return c.getString(0);
        }
        else{
            return null;
        }

    }
    public TranDetail getTransaction(int id) {

        TranDetail temp = new TranDetail();
        String selectQuery = "SELECT * FROM tran_details where id="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {

                temp.setId(c.getInt(0));
                temp.setIncomeDate(c.getString(1));
                temp.setAmount(c.getDouble(2));
                temp.setCategoryID(c.getInt(3));
                temp.setDescription(c.getString(4));
                temp.setModeID(c.getInt(5));
                temp.setType(c.getInt(6));

        }
        db.close();
        return temp;
    }
    public int deleteTransaction(TranDetail detail){
        SQLiteDatabase db = this.getWritableDatabase();
        int status = db.delete(TABLE_TRAN, "id" + " = ?",
                new String[] { String.valueOf(detail.getId()) });
        db.close();
        return status;
    }
    public int updateTransaction(TranDetail detail) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID,p.getID());
        values.put("income_date",detail.getIncomeDate());
        values.put("amount",detail.getAmount());
        values.put("description",detail.getDescription());
        values.put("category_id",detail.getCategoryID());
        values.put("mode_id",detail.getModeID());
        values.put("type",detail.getType());

        // updating row
        return db.update(TABLE_TRAN, values, "id" + " = ?",
                new String[] { String.valueOf(detail.getId()) });
    }
    public double incomeSource(int id){
        //String selectQuery = "SELECT DISTINCT c.category_name, t.category_id, sum(t.amount) FROM tran_details t, category c where type=0 and t.category_id = c.category_id";
        //String selectQuery = "SELECT category_id, sum(amount) FROM tran_details GROUP BY category_id";
        double amount = 0.0;
        String selectQuery = "SELECT sum(amount) FROM tran_details where category_id="+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c.moveToFirst()){
            //do{
                amount = c.getDouble(0);
                //Log.d("=====",c.getDouble(0)+"");
                //Log.d("=====",c.getString(1));
                //Log.d("=====",c.getString(2));
            //}while (c.moveToNext());
        }
        return amount;
    }
}
