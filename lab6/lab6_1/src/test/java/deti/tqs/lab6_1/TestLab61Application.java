package deti.tqs.lab6_1;

import org.springframework.boot.SpringApplication;

public class TestLab61Application {

	public static void main(String[] args) {
		SpringApplication.from(Lab61Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
