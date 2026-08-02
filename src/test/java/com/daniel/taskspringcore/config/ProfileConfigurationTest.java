package com.daniel.taskspringcore.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;

class ProfileConfigurationTest {

    private static final List<String> PROFILES = List.of("local", "dev", "stg", "prod");

    @ParameterizedTest
    @ValueSource(strings = {"local", "dev", "stg", "prod"})
    void everyProfileDefinesItsOwnDatasourceAndDdlPolicy(String profile) throws IOException {
        Properties properties = load(profile);

        assertThat(properties.getProperty("spring.datasource.url")).as("datasource url").isNotBlank();
        assertThat(properties.getProperty("spring.jpa.hibernate.ddl-auto")).as("ddl-auto").isNotBlank();
        assertThat(properties.getProperty("management.metrics.tags.environment")).isEqualTo(profile);
    }

    @Test
    void noTwoProfilesShareADatabaseUrl() throws IOException {
        List<String> urls = urls();

        assertThat(urls).doesNotHaveDuplicates();
    }

    @Test
    void onlyTheLocalProfileUsesAnInMemoryDatabase() throws IOException {
        assertThat(load("local").getProperty("spring.datasource.url")).startsWith("jdbc:h2:mem:");

        for (String profile : List.of("dev", "stg", "prod")) {
            assertThat(load(profile).getProperty("spring.datasource.url"))
                    .as("%s datasource", profile)
                    .startsWith("jdbc:postgresql://");
        }
    }

    @Test
    void managedEnvironmentsValidateTheSchemaInsteadOfMutatingIt() throws IOException {
        assertThat(load("stg").getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
        assertThat(load("prod").getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
        assertThat(load("prod").getProperty("spring.sql.init.mode")).isEqualTo("never");
    }

    @Test
    void productionCredentialsHaveNoFallbackDefaults() throws IOException {
        Properties prod = load("prod");

        assertThat(prod.getProperty("spring.datasource.username")).isEqualTo("${DB_USERNAME}");
        assertThat(prod.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
        assertThat(prod.getProperty("spring.datasource.url")).contains("${DB_HOST}");
    }

    @Test
    void productionDoesNotExposeDiagnosticEndpointsOrHealthDetails() throws IOException {
        Properties prod = load("prod");

        assertThat(prod.getProperty("management.endpoints.web.exposure.include"))
                .doesNotContain("env")
                .doesNotContain("beans")
                .doesNotContain("loggers")
                .contains("prometheus");
        assertThat(prod.getProperty("management.endpoint.health.show-details")).isEqualTo("never");
    }

    @Test
    void productionDoesNotPublishTheApiDocumentation() throws IOException {
        Properties prod = load("prod");

        assertThat(prod.getProperty("springdoc.api-docs.enabled")).isEqualTo("false");
        assertThat(prod.getProperty("springdoc.swagger-ui.enabled")).isEqualTo("false");
    }

    @Test
    void everyProfileExposesPrometheusExceptTheOnesThatOnlyNeedHealth() throws IOException {
        for (String profile : List.of("dev", "stg", "prod")) {
            assertThat(load(profile).getProperty("management.endpoints.web.exposure.include"))
                    .as("%s exposure", profile)
                    .contains("prometheus");
        }
        assertThat(load("local").getProperty("management.endpoints.web.exposure.include")).isEqualTo("*");
    }

    private List<String> urls() throws IOException {
        List<String> urls = PROFILES.stream()
                .map(profile -> {
                    try {
                        return load(profile).getProperty("spring.datasource.url");
                    } catch (IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                })
                .collect(Collectors.toList());
        assertThat(urls).hasSize(PROFILES.size()).doesNotContainNull();
        return urls;
    }

    private Properties load(String profile) throws IOException {
        Properties properties = new Properties();
        try (var input = new ClassPathResource("application-" + profile + ".properties").getInputStream()) {
            properties.load(input);
        }
        return properties;
    }
}