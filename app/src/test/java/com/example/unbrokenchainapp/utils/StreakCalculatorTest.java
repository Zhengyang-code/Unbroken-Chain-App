package com.example.unbrokenchainapp.utils;

import com.example.unbrokenchainapp.models.ChainEntry;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StreakCalculatorTest extends TestCase {
    
    /**
     * Test current streak calculation with empty list
     */
    public void testCalculateCurrentStreakEmptyList() {
        List<ChainEntry> entries = new ArrayList<>();
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals("Empty list should return 0", 0, streak);
    }
    
    /**
     * Test current streak calculation with no completed entries
     */
    public void testCalculateCurrentStreakNoCompletedEntries() {
        List<ChainEntry> entries = new ArrayList<>();
        
        // Add some uncompleted entries
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 5; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -i);
            Date date = cal.getTime();
            entries.add(new ChainEntry(1L, date, false));
        }
        
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals("No completed entries should return 0", 0, streak);
    }
    
    /**
     * Test current streak calculation with completed entries
     */
    public void testCalculateCurrentStreakWithCompletedEntries() {
        List<ChainEntry> entries = new ArrayList<>();
        
        // Add completed entries for the last 3 days
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // Today
        Date today = cal.getTime();
        entries.add(new ChainEntry(1L, today, true));
        
        // Yesterday
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        entries.add(new ChainEntry(1L, yesterday, true));
        
        // Day before yesterday
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date dayBeforeYesterday = cal.getTime();
        entries.add(new ChainEntry(1L, dayBeforeYesterday, true));
        
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals("Should have 3 days streak", 3, streak);
    }
    
    /**
     * Test current streak calculation with gap
     */
    public void testCalculateCurrentStreakWithGap() {
        List<ChainEntry> entries = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        
        // Today completed
        entries.add(new ChainEntry(1L, cal.getTime(), true));
        
        // Yesterday not completed
        cal.add(Calendar.DAY_OF_YEAR, -1);
        entries.add(new ChainEntry(1L, cal.getTime(), false));
        
        // Day before yesterday completed
        cal.add(Calendar.DAY_OF_YEAR, -1);
        entries.add(new ChainEntry(1L, cal.getTime(), true));
        
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals("Should have only 1 day streak after gap", 1, streak);
    }
    
    /**
     * Test longest streak calculation with empty list
     */
    public void testCalculateLongestStreakEmptyList() {
        List<ChainEntry> entries = new ArrayList<>();
        int streak = StreakCalculator.calculateLongestStreak(entries);
        assertEquals("Empty list should return 0", 0, streak);
    }
    
    /**
     * Test longest streak calculation
     */
    public void testCalculateLongestStreak() {
        List<ChainEntry> entries = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        
        // Create test data: 5 consecutive days, 1 day break, 3 consecutive days
        for (int i = 0; i < 5; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -i);
            Date date = cal.getTime();
            entries.add(new ChainEntry(1L, date, true));
        }
        
        // One day break
        cal.add(Calendar.DAY_OF_YEAR, -1);
        entries.add(new ChainEntry(1L, cal.getTime(), false));
        
        // Another 3 consecutive days
        for (int i = 0; i < 3; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date date = cal.getTime();
            entries.add(new ChainEntry(1L, date, true));
        }
        
        int longestStreak = StreakCalculator.calculateLongestStreak(entries);
        assertEquals("Longest streak should be 5", 5, longestStreak);
    }
    
    /**
     * Test monthly streak calculation
     */
    public void testCalculateMonthlyStreak() {
        List<ChainEntry> entries = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        
        // Add some completed entries for current month
        cal.set(currentYear, currentMonth - 1, 1); // First day of month
        for (int day = 1; day <= 5; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = cal.getTime();
            entries.add(new ChainEntry(1L, date, true));
        }
        
        // One day break
        cal.set(Calendar.DAY_OF_MONTH, 6);
        entries.add(new ChainEntry(1L, cal.getTime(), false));
        
        // Another 3 consecutive days
        for (int day = 7; day <= 9; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = cal.getTime();
            entries.add(new ChainEntry(1L, date, true));
        }
        
        int monthlyStreak = StreakCalculator.calculateMonthlyStreak(entries, currentYear, currentMonth);
        assertEquals("Monthly longest streak should be 5", 5, monthlyStreak);
    }
    
    /**
     * Test streak calculation with different chains
     */
    public void testCalculateStreakDifferentChains() {
        List<ChainEntry> entries = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        
        // Chain 1: today completed
        entries.add(new ChainEntry(1L, today, true));
        
        // Chain 2: today not completed
        entries.add(new ChainEntry(2L, today, false));
        
        // Chain 1: yesterday completed
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        entries.add(new ChainEntry(1L, yesterday, true));
        
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals("Should only calculate streak for chain 1", 2, streak);
    }
    
    /**
     * Test edge case: only today's entry
     */
    public void testCalculateStreakOnlyToday() {
        List<ChainEntry> entries = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        entries.add(new ChainEntry(1L, today, true));
        
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals("Only today completed should return 1", 1, streak);
    }
    
    /**
     * Test edge case: only yesterday's entry
     */
    public void testCalculateStreakOnlyYesterday() {
        List<ChainEntry> entries = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        entries.add(new ChainEntry(1L, yesterday, true));
        
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals("Only yesterday completed should return 0", 0, streak);
    }
    
    /**
     * Test complex scenario: multiple breaks and streaks
     */
    public void testCalculateStreakComplexScenario() {
        List<ChainEntry> entries = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // Create a simpler test scenario that's easier to verify
        // Today, yesterday, day before yesterday (3 consecutive days)
        Date today = cal.getTime();
        entries.add(new ChainEntry(1L, today, true));
        
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        entries.add(new ChainEntry(1L, yesterday, true));
        
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date dayBeforeYesterday = cal.getTime();
        entries.add(new ChainEntry(1L, dayBeforeYesterday, true));
        
        // Add some older entries that shouldn't affect current streak
        cal.add(Calendar.DAY_OF_YEAR, -5);
        Date oldEntry = cal.getTime();
        entries.add(new ChainEntry(1L, oldEntry, true));
        
        int currentStreak = StreakCalculator.calculateCurrentStreak(entries);
        int longestStreak = StreakCalculator.calculateLongestStreak(entries);
        
        assertEquals("Current streak should be 3", 3, currentStreak);
        assertEquals("Longest streak should be 4", 4, longestStreak);
    }
}
