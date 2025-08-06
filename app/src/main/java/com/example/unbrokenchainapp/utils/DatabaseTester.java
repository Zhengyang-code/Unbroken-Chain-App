package com.example.unbrokenchainapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.unbrokenchainapp.database.ChainDatabase;
import com.example.unbrokenchainapp.models.Chain;
import com.example.unbrokenchainapp.models.ChainEntry;

import java.util.Date;
import java.util.List;

public class DatabaseTester {
    
    private static final String TAG = "DatabaseTester";
    private ChainDatabase database;
    
    public DatabaseTester(Context context) {
        this.database = new ChainDatabase(context);
    }
    
    public void runTests() {
        Log.d(TAG, "Starting database tests...");
        
        try {
            // Test 1: Create a test chain
            Chain testChain = new Chain("Test Habit", "A test habit for debugging");
            long chainId = database.addChain(testChain);
            Log.d(TAG, "Test chain created with ID: " + chainId);
            
            // Test 2: Retrieve all chains
            List<Chain> chains = database.getAllChains();
            Log.d(TAG, "Retrieved " + chains.size() + " chains");
            
            // Test 3: Add some test entries
            Date today = new Date();
            ChainEntry entry1 = new ChainEntry(chainId, today, true);
            long entryId1 = database.addEntry(entry1);
            Log.d(TAG, "Test entry 1 created with ID: " + entryId1);
            
            // Test 4: Get entries for current month
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH) + 1;
            
            List<ChainEntry> entries = database.getEntriesForMonth(chainId, year, month);
            Log.d(TAG, "Retrieved " + entries.size() + " entries for month " + month + "/" + year);
            
            // Test 5: Test streak calculation
            int currentStreak = StreakCalculator.calculateCurrentStreak(entries);
            int longestStreak = StreakCalculator.calculateLongestStreak(entries);
            Log.d(TAG, "Current streak: " + currentStreak + ", Longest streak: " + longestStreak);
            
            Log.d(TAG, "All database tests passed successfully!");
            
        } catch (Exception e) {
            Log.e(TAG, "Database test failed", e);
        }
    }
    
    public void cleanup() {
        if (database != null) {
            database.close();
        }
    }
} 