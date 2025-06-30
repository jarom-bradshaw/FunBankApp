package com.jarom.funbankapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Main application test suite that runs all backend tests automatically.
 * This class serves as the entry point for running the complete test suite.
 */
@SpringBootTest
@ActiveProfiles("test")
class FunBankAppApplicationTests {

	@Test
	void contextLoads() {
		// This test ensures the Spring application context loads without errors
		// It validates that all beans are properly configured and can be instantiated
	}
}
