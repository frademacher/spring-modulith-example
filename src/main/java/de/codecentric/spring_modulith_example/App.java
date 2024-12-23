package de.codecentric.spring_modulith_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@ConfigurationPropertiesScan
// This annotation enables to influence the module detection behavior of Spring Modulith. Using the sharedModules
// property, we instruct Spring Modulith to implicitly include the Shared module into all module integration tests,
// without the test classes having to explicitly specify it as a value for the extraIncludes property of
// @ApplicationModuleTest annotations.
@Modulithic(sharedModules = "shared")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
