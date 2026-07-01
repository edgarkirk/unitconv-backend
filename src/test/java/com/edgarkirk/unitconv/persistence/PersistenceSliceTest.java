package com.edgarkirk.unitconv.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PersistenceSliceTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void should_not_registerJpaRepositories_when_applicationIsStateless() {
        assertThat(applicationContext.getBeanNamesForType(org.springframework.data.jpa.repository.JpaRepository.class)).isEmpty();
    }
}
