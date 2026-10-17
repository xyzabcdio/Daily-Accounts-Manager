package com.dailyaccounts.manager;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DB extends SQLiteOpenHelper {
    public DB(Context c) { super(c, "daily_accounts.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE accounts(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,type TEXT NOT NULL,opening REAL NOT NULL DEFAULT 0)");
        db.execSQL("CREATE TABLE tx(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT NOT NULL,type TEXT NOT NULL,from_id INTEGER,to_id INTEGER,amount REAL NOT NULL,source TEXT,category TEXT,particular TEXT,remark TEXT,created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX tx_date_idx ON tx(date)");
        ContentValues v=new ContentValues(); v.put("name","Cash"); v.put("type","CASH"); v.put("opening",0); db.insert("accounts",null,v);
        v=new ContentValues(); v.put("name","UPI"); v.put("type","UPI"); v.put("opening",0); db.insert("accounts",null,v);
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {}
    public long addAccount(String name,String type,double opening) {
        ContentValues v=new ContentValues(); v.put("name",name.trim()); v.put("type",type); v.put("opening",opening); return getWritableDatabase().insert("accounts",null,v);
    }
    public Cursor accounts(){ return getReadableDatabase().rawQuery("SELECT id,name,type,opening FROM accounts ORDER BY id",null); }
    public long addTx(String date,String type,Long from,Long to,double amount,String source,String category,String particular,String remark) {
        if(amount<=0) return -1;
        SQLiteDatabase d=getWritableDatabase(); ContentValues v=new ContentValues(); v.put("date",date); v.put("type",type);
        if(from==null) v.putNull("from_id"); else v.put("from_id",from);
        if(to==null) v.putNull("to_id"); else v.put("to_id",to);
        v.put("amount",amount); v.put("source",source); v.put("category",category); v.put("particular",particular); v.put("remark",remark); v.put("created_at",System.currentTimeMillis());
        return d.insert("tx",null,v);
    }
    public double balance(long id) {
        Cursor c=getReadableDatabase().rawQuery("SELECT (SELECT opening FROM accounts WHERE id=?)+COALESCE((SELECT SUM(amount) FROM tx WHERE to_id=? AND type IN ('INCOME','TRANSFER')),0)-COALESCE((SELECT SUM(amount) FROM tx WHERE from_id=? AND type IN ('EXPENSE','TRANSFER')),0)",new String[]{""+id,""+id,""+id});
        double x=0;if(c.moveToFirst())x=c.getDouble(0);c.close();return x;
    }
    public double[] totals(String period) {
        String today=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
        String month=today.substring(0,7);
        String clause=period.equals("TODAY")?"date=?":"date LIKE ?"; String arg=period.equals("TODAY")?today:month+"%";
        Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(CASE WHEN type='INCOME' THEN amount ELSE 0 END),0),COALESCE(SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END),0) FROM tx WHERE "+clause,new String[]{arg});
        double[] a={0,0};if(c.moveToFirst()){a[0]=c.getDouble(0);a[1]=c.getDouble(1);}c.close();return a;
    }
    public Cursor recent(){return getReadableDatabase().rawQuery("SELECT date,type,amount,COALESCE(source,category,''),COALESCE(particular,''),COALESCE(remark,'') FROM tx ORDER BY date DESC,id DESC LIMIT 100",null);}
    public Cursor report(){return getReadableDatabase().rawQuery("SELECT date,type,amount,COALESCE(source,category,''),COALESCE(particular,''),COALESCE(remark,'') FROM tx ORDER BY date DESC,id DESC",null);}
}
