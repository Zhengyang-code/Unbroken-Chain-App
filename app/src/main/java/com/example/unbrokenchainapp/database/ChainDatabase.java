package com.example.unbrokenchainapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.unbrokenchainapp.models.Chain;
import com.example.unbrokenchainapp.models.ChainEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChainDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ChainDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_CHAINS = "chains";
    private static final String TABLE_ENTRIES = "entries";

    // Common column names
    private static final String KEY_ID = "id";

    // Chains table column names
    private static final String KEY_CHAIN_NAME = "name";
    private static final String KEY_CHAIN_DESCRIPTION = "description";
    private static final String KEY_CHAIN_CREATED_AT = "created_at";
    private static final String KEY_CHAIN_IS_ACTIVE = "is_active";

    // Entries table column names
    private static final String KEY_ENTRY_CHAIN_ID = "chain_id";
    private static final String KEY_ENTRY_DATE = "date";
    private static final String KEY_ENTRY_COMPLETED = "completed";

    // Create table statements
    private static final String CREATE_TABLE_CHAINS = "CREATE TABLE " + TABLE_CHAINS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CHAIN_NAME + " TEXT,"
            + KEY_CHAIN_DESCRIPTION + " TEXT,"
            + KEY_CHAIN_CREATED_AT + " TEXT,"
            + KEY_CHAIN_IS_ACTIVE + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_ENTRIES = "CREATE TABLE " + TABLE_ENTRIES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ENTRY_CHAIN_ID + " INTEGER,"
            + KEY_ENTRY_DATE + " TEXT,"
            + KEY_ENTRY_COMPLETED + " INTEGER,"
            + "FOREIGN KEY(" + KEY_ENTRY_CHAIN_ID + ") REFERENCES " + TABLE_CHAINS + "(" + KEY_ID + ")"
            + ")";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public ChainDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHAINS);
        db.execSQL(CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAINS);
        onCreate(db);
    }

    // Chain operations
    public long addChain(Chain chain) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHAIN_NAME, chain.getName());
        values.put(KEY_CHAIN_DESCRIPTION, chain.getDescription());
        values.put(KEY_CHAIN_CREATED_AT, dateFormat.format(chain.getCreatedAt()));
        values.put(KEY_CHAIN_IS_ACTIVE, chain.isActive() ? 1 : 0);

        long id = db.insert(TABLE_CHAINS, null, values);
        db.close();
        return id;
    }

    public List<Chain> getAllChains() {
        List<Chain> chains = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CHAINS + " WHERE " + KEY_CHAIN_IS_ACTIVE + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Chain chain = new Chain();
                chain.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                chain.setName(cursor.getString(cursor.getColumnIndex(KEY_CHAIN_NAME)));
                chain.setDescription(cursor.getString(cursor.getColumnIndex(KEY_CHAIN_DESCRIPTION)));
                try {
                    chain.setCreatedAt(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_CHAIN_CREATED_AT))));
                } catch (Exception e) {
                    chain.setCreatedAt(new Date());
                }
                chain.setActive(cursor.getInt(cursor.getColumnIndex(KEY_CHAIN_IS_ACTIVE)) == 1);
                chains.add(chain);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return chains;
    }

    // Entry operations
    public long addEntry(ChainEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ENTRY_CHAIN_ID, entry.getChainId());
        values.put(KEY_ENTRY_DATE, dateFormat.format(entry.getDate()));
        values.put(KEY_ENTRY_COMPLETED, entry.isCompleted() ? 1 : 0);

        long id = db.insert(TABLE_ENTRIES, null, values);
        db.close();
        return id;
    }

    public void updateEntry(ChainEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ENTRY_COMPLETED, entry.isCompleted() ? 1 : 0);

        db.update(TABLE_ENTRIES, values, KEY_ID + " = ?", new String[]{String.valueOf(entry.getId())});
        db.close();
    }

    public ChainEntry getEntry(long chainId, Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES + " WHERE " + KEY_ENTRY_CHAIN_ID + " = ? AND " + KEY_ENTRY_DATE + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(chainId), dateFormat.format(date)});

        ChainEntry entry = null;
        if (cursor.moveToFirst()) {
            entry = new ChainEntry();
            entry.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            entry.setChainId(cursor.getLong(cursor.getColumnIndex(KEY_ENTRY_CHAIN_ID)));
            try {
                entry.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_ENTRY_DATE))));
            } catch (Exception e) {
                entry.setDate(date);
            }
            entry.setCompleted(cursor.getInt(cursor.getColumnIndex(KEY_ENTRY_COMPLETED)) == 1);
        }

        cursor.close();
        db.close();
        return entry;
    }

    public List<ChainEntry> getEntriesForMonth(long chainId, int year, int month) {
        List<ChainEntry> entries = new ArrayList<>();
        String monthStr = String.format(Locale.getDefault(), "%04d-%02d", year, month);
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES + " WHERE " + KEY_ENTRY_CHAIN_ID + " = ? AND " + KEY_ENTRY_DATE + " LIKE ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(chainId), monthStr + "%"});

        if (cursor.moveToFirst()) {
            do {
                ChainEntry entry = new ChainEntry();
                entry.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                entry.setChainId(cursor.getLong(cursor.getColumnIndex(KEY_ENTRY_CHAIN_ID)));
                try {
                    entry.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_ENTRY_DATE))));
                } catch (Exception e) {
                    // Skip invalid entries
                    continue;
                }
                entry.setCompleted(cursor.getInt(cursor.getColumnIndex(KEY_ENTRY_COMPLETED)) == 1);
                entries.add(entry);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entries;
    }

    public void toggleEntry(long chainId, Date date) {
        ChainEntry entry = getEntry(chainId, date);
        if (entry == null) {
            entry = new ChainEntry(chainId, date, true);
            addEntry(entry);
        } else {
            entry.setCompleted(!entry.isCompleted());
            updateEntry(entry);
        }
    }

    public boolean deleteChain(long chainId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("chains", "id = ?", new String[]{String.valueOf(chainId)});
        return rows > 0;
    }

    public boolean updateChain(Chain chain) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", chain.getName());
        values.put("description", chain.getDescription());
        int rows = db.update("chains", values, "id = ?", new String[]{String.valueOf(chain.getId())});
        return rows > 0;
    }
} 