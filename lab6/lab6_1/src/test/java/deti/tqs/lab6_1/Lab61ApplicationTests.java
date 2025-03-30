package deti.tqs.lab6_1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Lab61ApplicationTests {

	@Test
	void contextLoads() {
	}

}
