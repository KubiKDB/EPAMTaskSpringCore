package com.daniel.taskspringcore.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TransactionLoggingFilterTest {

    @Test
    void masksPasswordSoItNeverReachesTheLogs() {
        assertThat(TransactionLoggingFilter.maskSensitive("username=john&password=s3cret"))
                .isEqualTo("username=john&password=***");
    }

    @Test
    void masksRegardlessOfCase() {
        assertThat(TransactionLoggingFilter.maskSensitive("PassWord=s3cret"))
                .isEqualTo("PassWord=***");
    }

    @Test
    void masksEveryCredentialBearingParameter() {
        assertThat(TransactionLoggingFilter.maskSensitive("oldPassword=a&newPassword=b&token=c"))
                .isEqualTo("oldPassword=***&newPassword=***&token=***");
    }

    @Test
    void leavesHarmlessParametersIntact() {
        assertThat(TransactionLoggingFilter.maskSensitive("periodFrom=2026-01-01&trainerName=Anna"))
                .isEqualTo("periodFrom=2026-01-01&trainerName=Anna");
    }

    @Test
    void handlesNullAndBlankQueryStrings() {
        assertThat(TransactionLoggingFilter.maskSensitive(null)).isNull();
        assertThat(TransactionLoggingFilter.maskSensitive("")).isEmpty();
    }
}
