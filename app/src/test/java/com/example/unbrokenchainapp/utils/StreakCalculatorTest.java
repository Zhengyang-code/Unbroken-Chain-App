package com.example.unbrokenchainapp.utils;

import com.example.unbrokenchainapp.models.ChainEntry;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StreakCalculatorTest {

    private List<ChainEntry> entries;
    private SimpleDateFormat sdf;

    @Before
    public void setUp() {
        // Initialize an empty list of entries and date formatter
        entries = new ArrayList<>();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    // Test: Three consecutive check-ins should return a streak of 3 days
    @Test
    public void testCalculateCurrentStreak_withConsecutiveDays() throws Exception {
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-22"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-23"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-24"), true));

        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals(3, streak); // Should return a streak of 3 consecutive days
    }

    // Test: Check-in records with gaps should not count as consecutive
    @Test
    public void testCalculateCurrentStreak_withGapsInDates() throws Exception {
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-22"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-24"), true)); // Skipped 23rd

        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals(1, streak); // Should only count 1 day
    }

    // Test: Calculate longest streak, should return the longest streak of consecutive days
    @Test
    public void testCalculateLongestStreak() throws Exception {
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-20"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-21"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-22"), false)); // Break in streak
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-23"), true));

        int streak = StreakCalculator.calculateLongestStreak(entries);
        assertEquals(2, streak); // Longest streak should be 2 days
    }

    // Test: Calculate the longest streak for a specific month
    @Test
    public void testCalculateMonthlyStreak() throws Exception {
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-01"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-02"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-03"), false)); // Break in streak
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-04"), true));
        entries.add(new ChainEntry(1L, sdf.parse("2025-08-05"), true));

        int streak = StreakCalculator.calculateMonthlyStreak(entries, 2025, 8);
        assertEquals(2, streak); // Should return 2-day streak
    }

    // Test: Empty entry list should return 0 for current streak
    @Test
    public void testCalculateCurrentStreak_emptyList() {
        int streak = StreakCalculator.calculateCurrentStreak(entries);
        assertEquals(0, streak);
    }

    // Test: Empty entry list should return 0 for longest streak
    @Test
    public void testCalculateLongestStreak_emptyList() {
        int streak = StreakCalculator.calculateLongestStreak(entries);
        assertEquals(0, streak);
    }

    // Test: Empty entry list should return 0 for monthly streak
    @Test
    public void testCalculateMonthlyStreak_emptyList() {
        int streak = StreakCalculator.calculateMonthlyStreak(entries, 2025, 8);
        assertEquals(0, streak);
    }
}
