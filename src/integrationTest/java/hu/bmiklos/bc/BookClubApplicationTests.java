package hu.bmiklos.bc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class BookClubApplicationTests {

	@Test
	void contextLoads() {
		// if the Spring context cannot be loaded, the test will fail with an exception
	}
}
