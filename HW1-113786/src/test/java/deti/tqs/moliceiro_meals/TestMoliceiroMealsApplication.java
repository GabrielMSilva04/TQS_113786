package deti.tqs.moliceiro_meals;

import org.springframework.boot.SpringApplication;

public class TestMoliceiroMealsApplication {

	public static void main(String[] args) {
		SpringApplication.from(MoliceiroMealsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
