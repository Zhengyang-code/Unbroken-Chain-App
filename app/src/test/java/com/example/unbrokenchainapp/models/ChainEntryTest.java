package com.example.unbrokenchainapp.models;

import junit.framework.TestCase;

import java.util.Date;

public class ChainEntryTest extends TestCase {
    
    private ChainEntry entry;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        entry = new ChainEntry();
    }
    
    /**
     * Test default constructor
     */
    public void testDefaultConstructor() {
        assertNotNull("ChainEntry object should be created", entry);
        assertEquals("Default ID should be 0", 0, entry.getId());
        assertEquals("Default chainId should be 0", 0, entry.getChainId());
        assertNull("Default date should be null", entry.getDate());
        assertFalse("Default completed status should be false", entry.isCompleted());
    }
    
    /**
     * Test parameterized constructor
     */
    public void testParameterizedConstructor() {
        long chainId = 1L;
        Date date = new Date();
        boolean completed = true;
        
        ChainEntry testEntry = new ChainEntry(chainId, date, completed);
        
        assertEquals("chainId should be set correctly", chainId, testEntry.getChainId());
        assertEquals("Date should be set correctly", date, testEntry.getDate());
        assertEquals("Completed status should be set correctly", completed, testEntry.isCompleted());
    }
    
    /**
     * Test ID getter and setter
     */
    public void testIdGetterAndSetter() {
        long testId = 12345L;
        entry.setId(testId);
        assertEquals("ID should be set and retrieved correctly", testId, entry.getId());
    }
    
    /**
     * Test chainId getter and setter
     */
    public void testChainIdGetterAndSetter() {
        long testChainId = 67890L;
        entry.setChainId(testChainId);
        assertEquals("chainId should be set and retrieved correctly", testChainId, entry.getChainId());
    }
    
    /**
     * Test date getter and setter
     */
    public void testDateGetterAndSetter() {
        Date testDate = new Date();
        entry.setDate(testDate);
        assertEquals("Date should be set and retrieved correctly", testDate, entry.getDate());
    }
    
    /**
     * Test completed status getter and setter
     */
    public void testCompletedGetterAndSetter() {
        // Test setting to completed
        entry.setCompleted(true);
        assertTrue("Completed status should be set to true correctly", entry.isCompleted());
        
        // Test setting to not completed
        entry.setCompleted(false);
        assertFalse("Completed status should be set to false correctly", entry.isCompleted());
    }
    
    /**
     * Test complete object creation and property setting
     */
    public void testCompleteChainEntryObject() {
        long id = 1L;
        long chainId = 2L;
        Date date = new Date();
        boolean completed = true;
        
        entry.setId(id);
        entry.setChainId(chainId);
        entry.setDate(date);
        entry.setCompleted(completed);
        
        assertEquals("ID should be correct", id, entry.getId());
        assertEquals("chainId should be correct", chainId, entry.getChainId());
        assertEquals("Date should be correct", date, entry.getDate());
        assertEquals("Completed status should be correct", completed, entry.isCompleted());
    }
    
    /**
     * Test entries with different dates
     */
    public void testDifferentDates() {
        Date today = new Date();
        Date yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000); // Yesterday
        Date tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000); // Tomorrow
        
        ChainEntry entry1 = new ChainEntry(1L, today, true);
        ChainEntry entry2 = new ChainEntry(1L, yesterday, false);
        ChainEntry entry3 = new ChainEntry(1L, tomorrow, true);
        
        assertEquals("Today's entry date should be correct", today, entry1.getDate());
        assertEquals("Yesterday's entry date should be correct", yesterday, entry2.getDate());
        assertEquals("Tomorrow's entry date should be correct", tomorrow, entry3.getDate());
        
        assertTrue("Today's entry should be marked as completed", entry1.isCompleted());
        assertFalse("Yesterday's entry should be marked as not completed", entry2.isCompleted());
        assertTrue("Tomorrow's entry should be marked as completed", entry3.isCompleted());
    }
    
    /**
     * Test entries for different chains
     */
    public void testDifferentChains() {
        Date date = new Date();
        
        ChainEntry entry1 = new ChainEntry(1L, date, true);
        ChainEntry entry2 = new ChainEntry(2L, date, false);
        ChainEntry entry3 = new ChainEntry(3L, date, true);
        
        assertEquals("First entry's chainId should be correct", 1L, entry1.getChainId());
        assertEquals("Second entry's chainId should be correct", 2L, entry2.getChainId());
        assertEquals("Third entry's chainId should be correct", 3L, entry3.getChainId());
        
        assertTrue("First entry should be marked as completed", entry1.isCompleted());
        assertFalse("Second entry should be marked as not completed", entry2.isCompleted());
        assertTrue("Third entry should be marked as completed", entry3.isCompleted());
    }
} 