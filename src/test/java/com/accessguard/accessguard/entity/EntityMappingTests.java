package com.accessguard.accessguard.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EntityMappingTests {

    @Autowired
    private EntityManager entityManager;

    @Test
    void requiredJpaRelationshipsAreDeclared() throws Exception {
        assertThat(field(DataAcknowledgment.class, "offboardingCase").isAnnotationPresent(jakarta.persistence.OneToOne.class)).isTrue();
        assertThat(field(DataAcknowledgment.class, "employee").isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
        assertThat(field(HighRiskActivityFlag.class, "employee").isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
        assertThat(field(HighRiskActivityFlag.class, "reviewer").isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
        assertThat(field(EscalationLog.class, "revocationTask").isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
        assertThat(field(EscalationLog.class, "escalatedToEmployee").isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
        assertThat(AuditSnapshot.class.getDeclaredFields()).noneMatch(f -> f.isAnnotationPresent(jakarta.persistence.ManyToOne.class)
                || f.isAnnotationPresent(jakarta.persistence.OneToOne.class)
                || f.isAnnotationPresent(jakarta.persistence.OneToMany.class));
    }

    @Test
    void acknowledgmentPersistsForeignKeysAndKeepsIdApi() {
        UUID employeeId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        entityManager.persist(Employee.reference(employeeId));
        entityManager.persist(OffboardingCase.reference(caseId));

        DataAcknowledgment acknowledgment = new DataAcknowledgment();
        acknowledgment.setEmployeeId(employeeId);
        acknowledgment.setOffboardingCaseId(caseId);
        acknowledgment.setAcknowledgedAt(LocalDateTime.now());
        acknowledgment.setStatementVersion("v1");
        entityManager.persist(acknowledgment);
        entityManager.flush();
        entityManager.clear();

        DataAcknowledgment stored = entityManager.find(DataAcknowledgment.class, acknowledgment.getId());
        assertThat(stored.getEmployeeId()).isEqualTo(employeeId);
        assertThat(stored.getOffboardingCaseId()).isEqualTo(caseId);
    }

    private Field field(Class<?> type, String name) throws NoSuchFieldException {
        return type.getDeclaredField(name);
    }
}
