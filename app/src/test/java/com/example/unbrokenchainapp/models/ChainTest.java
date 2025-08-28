package com.example.unbrokenchainapp.models;

import junit.framework.TestCase;

import java.util.Date;

public class ChainTest extends TestCase {
    
    private Chain chain;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        chain = new Chain();
    }

    public void testDefaultConstructor() {
        assertNotNull("Chain object should be created", chain);
        assertTrue("Newly created chain should be active", chain.isActive());
        assertNotNull("Created time should be set", chain.getCreatedAt());
    }

    public void testParameterizedConstructor() {
        String name = "Test Habit";
        String description = "This is a test habit";
        Chain testChain = new Chain(name, description);
        
        assertEquals("Name should be set correctly", name, testChain.getName());
        assertEquals("Description should be set correctly", description, testChain.getDescription());
        assertTrue("Newly created chain should be active", testChain.isActive());
        assertNotNull("Created time should be set", testChain.getCreatedAt());
    }

    public void testIdGetterAndSetter() {
        long testId = 12345L;
        chain.setId(testId);
        assertEquals("ID should be set and retrieved correctly", testId, chain.getId());
    }

    public void testNameGetterAndSetter() {
        String testName = "Daily Exercise";
        chain.setName(testName);
        assertEquals("Name should be set and retrieved correctly", testName, chain.getName());
    }

    public void testDescriptionGetterAndSetter() {
        String testDescription = "Run for 30 minutes daily";
        chain.setDescription(testDescription);
        assertEquals("Description should be set and retrieved correctly", testDescription, chain.getDescription());
    }

    public void testCreatedAtGetterAndSetter() {
        Date testDate = new Date();
        chain.setCreatedAt(testDate);
        assertEquals("Created time should be set and retrieved correctly", testDate, chain.getCreatedAt());
    }

    public void testActiveGetterAndSetter() {
        // Test setting to inactive
        chain.setActive(false);
        assertFalse("Active status should be set to false correctly", chain.isActive());
        
        // Test setting to active
        chain.setActive(true);
        assertTrue("Active status should be set to true correctly", chain.isActive());
    }

    public void testCompleteChainObject() {
        long id = 1L;
        String name = "Learn Programming";
        String description = "Learn Java programming for 2 hours daily";
        Date createdAt = new Date();
        boolean isActive = true;
        
        chain.setId(id);
        chain.setName(name);
        chain.setDescription(description);
        chain.setCreatedAt(createdAt);
        chain.setActive(isActive);
        
        assertEquals("ID should be correct", id, chain.getId());
        assertEquals("Name should be correct", name, chain.getName());
        assertEquals("Description should be correct", description, chain.getDescription());
        assertEquals("Created time should be correct", createdAt, chain.getCreatedAt());
        assertEquals("Active status should be correct", isActive, chain.isActive());
    }
} 