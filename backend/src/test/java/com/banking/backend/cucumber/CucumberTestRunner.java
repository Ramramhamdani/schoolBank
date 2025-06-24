package com.banking.backend.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.banking.backend.cucumber.steps",   // Step definitions
                "com.banking.backend.cucumber.config"   // Configuration classes
        },
        plugin = {
                "pretty",
        },
        tags = "not @ignore"
)
public class CucumberTestRunner {
}