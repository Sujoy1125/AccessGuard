package com.accessguard.accessguard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@org.springframework.test.context.TestExecutionListeners(listeners = {
		org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
		org.springframework.test.context.support.DirtiesContextTestExecutionListener.class,
		org.springframework.test.context.transaction.TransactionalTestExecutionListener.class }, inheritListeners = false)
@SpringBootTest
class AccessguardApplicationTests {

	@Test
	void contextLoads() {
	}

}
