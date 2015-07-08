package demo;

import config.ModuleDefinition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.bus.runner.EnableMessageBus;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableMessageBus
@ComponentScan(basePackageClasses = ModuleDefinition.class)
public class SinkApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SinkApplication.class, args);
	}

}
