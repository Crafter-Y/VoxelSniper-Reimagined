package com.github.kevindagame.updateChecker;

import junit.framework.TestCase;

import static org.junit.Assert.assertThrows;

public class UpdateCheckerTest extends TestCase {

    public void testCompareVersions() {
        var checker = new UpdateChecker();
        assertTrue(checker.compareVersions("1.0.0", "0.0.0"));
        assertTrue(checker.compareVersions("1.0.0", "0.0.1"));
        assertTrue(checker.compareVersions("1.0.0", "0.1.0"));
        assertFalse(checker.compareVersions("1.0.0", "1.0.0"));
        assertFalse(checker.compareVersions("1.0.0", "1.0.1"));
        assertFalse(checker.compareVersions("1.0.0", "1.1.0"));
        assertFalse(checker.compareVersions("1.0.0", "2.0.0"));
        assertFalse(checker.compareVersions("1.0.0", "2.0.1.beta"));
    }
}