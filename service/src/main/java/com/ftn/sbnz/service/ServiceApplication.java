package com.ftn.sbnz.service;

import java.util.stream.Collectors;

import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ftn.sbnz.model.DiagnosticReport;

@SpringBootApplication
public class ServiceApplication {
    private static final Logger log = LoggerFactory.getLogger(ServiceApplication.class);

    public static void main(String[] args) {
        System.setProperty("debug", "false");
        System.setProperty("logging.level.org.springframework", "INFO");
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    public KieBase kieBase() {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(
                ResourceFactory.newClassPathResource("rules/vehicle-diagnosis.drl", getClass().getClassLoader()),
                ResourceType.DRL);
        kieHelper.addResource(
                ResourceFactory.newClassPathResource("rules/backward/fault-causes.drl", getClass().getClassLoader()),
                ResourceType.DRL);

        Results results = kieHelper.verify();
        if (results.hasMessages(Message.Level.ERROR)) {
            String errors = results.getMessages(Message.Level.ERROR).stream()
                    .map(Message::toString)
                    .collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalStateException("Drools rule compilation failed:" + System.lineSeparator() + errors);
        }
        return kieHelper.build(EventProcessingOption.STREAM);
    }

    @Bean
    public KieBase cepKieBase() {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(
                ResourceFactory.newClassPathResource("rules/cep/sensor-trend-rules.drl", getClass().getClassLoader()),
                ResourceType.DRL);

        Results results = kieHelper.verify();
        if (results.hasMessages(Message.Level.ERROR)) {
            String errors = results.getMessages(Message.Level.ERROR).stream()
                    .map(Message::toString)
                    .collect(Collectors.joining(System.lineSeparator()));
            throw new IllegalStateException("CEP rule compilation failed:" + System.lineSeparator() + errors);
        }
        return kieHelper.build(EventProcessingOption.STREAM);
    }

    @Bean
    public CommandLineRunner runDemoOnStartup(DiagnosticService diagnosticService) {
        return args -> {
            DiagnosticReport report = diagnosticService.runDemo();
            log.info("\n{}", report.toConsoleString());
        };
    }
}
