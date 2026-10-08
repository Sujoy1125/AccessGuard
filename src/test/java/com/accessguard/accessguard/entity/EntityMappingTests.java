package com.accessguard.accessguard.entity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@org.springframework.test.context.TestExecutionListeners(listeners = {
        org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
        org.springframework.test.context.support.DirtiesContextTestExecutionListener.class,
        org.springframework.test.context.transaction.TransactionalTestExecutionListener.class }, inheritListeners = false)
@SpringBootTest
@Transactional
class EntityMappingTests {

    @Autowired
    private EntityManager entityManager;

    @Test
    void requiredJpaRelationshipsAreDeclared() throws Exception {
        assertThat(field(DataAcknowledgment.class, "offboardingCase")
                .isAnnotationPresent(jakarta.persistence.OneToOne.class)).isTrue();
        assertThat(field(DataAcknowledgment.class, "employee").isAnnotationPresent(jakarta.persistence.ManyToOne.class))
                .isTrue();
        assertThat(
                field(HighRiskActivityFlag.class, "employee").isAnnotationPresent(jakarta.persistence.ManyToOne.class))
                .isTrue();
        assertThat(
                field(HighRiskActivityFlag.class, "reviewer").isAnnotationPresent(jakarta.persistence.ManyToOne.class))
                .isTrue();
        assertThat(
                field(EscalationLog.class, "revocationTask").isAnnotationPresent(jakarta.persistence.ManyToOne.class))
                .isTrue();
        assertThat(field(EscalationLog.class, "escalatedToEmployee")
                .isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
        assertThat(AuditSnapshot.class.getDeclaredFields())
                .noneMatch(f -> f.isAnnotationPresent(jakarta.persistence.ManyToOne.class)
                        || f.isAnnotationPresent(jakarta.persistence.OneToOne.class)
                        || f.isAnnotationPresent(jakarta.persistence.OneToMany.class));
    }

    @Test
    void acknowledgmentPersistsForeignKeysAndKeepsIdApi() {
        Employee employee = new Employee();
        employee.setName("Mapping Test");
        employee.setEmail("mapping@example.com");
        employee.setDepartment("IT");
        employee.setStatus(EmployeeStatus.ACTIVE);
        entityManager.persist(employee);
        UUID employeeId = employee.getId();
        OffboardingCase offboardingCase = new OffboardingCase();
        offboardingCase.setEmployeeId(employeeId);
        offboardingCase.setInitiatedBy(employeeId);
        offboardingCase.setInitiatedAt(LocalDateTime.now());
        offboardingCase.setTargetCompletionDate(java.time.LocalDate.now().plusDays(1));
        offboardingCase.setStatus("OPEN");
        entityManager.persist(offboardingCase);
        UUID caseId = offboardingCase.getId();

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
        assertThat(stored.getEmployee().getName()).isEqualTo("Mapping Test");
        assertThat(stored.getOffboardingCase().getEmployeeId()).isEqualTo(employeeId);
        assertThat(entityManager.find(OffboardingCase.class, caseId).getDataAcknowledgment().getId())
                .isEqualTo(stored.getId());
    }

    private Field field(Class<?> type, String name) throws NoSuchFieldException {
        return type.getDeclaredField(name);
    }

    @Test
    void grantTaskAndFlagMappingsLoadFromPersistedForeignKeys() {
        Employee employee = new Employee();
        employee.setName("Owner");
        employee.setEmail("owner@example.com");
        employee.setDepartment("IT");
        employee.setStatus(EmployeeStatus.ACTIVE);
        entityManager.persist(employee);
        SystemEntity system = new SystemEntity();
        system.setName("Git");
        system.setOwnerUserId(employee.getId());
        system.setCategory(SystemCategory.SAAS);
        entityManager.persist(system);
        DeviceAsset asset = new DeviceAsset();
        asset.setEmployeeId(employee.getId());
        asset.setAssetType(AssetType.LAPTOP);
        asset.setReturnStatus("PENDING");
        entityManager.persist(asset);
        AccessGrantEntity grant = new AccessGrantEntity();
        grant.setEmployeeId(employee.getId());
        grant.setSystemId(system.getId());
        grant.setGrantedBy(employee.getId());
        grant.setGrantedAt(LocalDateTime.now());
        grant.setAccessLevel("READ");
        grant.setDeviceAssetId(asset.getId());
        entityManager.persist(grant);
        HighRiskActivityFlag flag = new HighRiskActivityFlag();
        flag.setEmployeeId(employee.getId());
        flag.setDetectedBy(employee.getId());
        flag.setSourceSystem("Git");
        flag.setActivityType(ActivityType.EXPORT);
        flag.setDetectedAt(LocalDateTime.now());
        entityManager.persist(flag);
        OffboardingCase off = new OffboardingCase();
        off.setEmployeeId(employee.getId());
        off.setInitiatedBy(employee.getId());
        off.setInitiatedAt(LocalDateTime.now());
        off.setTargetCompletionDate(java.time.LocalDate.now().plusDays(1));
        off.setStatus("OPEN");
        off.setTriggerFlagId(flag.getId());
        entityManager.persist(off);
        RevocationTask task = new RevocationTask();
        task.setOffboardingCaseId(off.getId());
        task.setSystemId(system.getId());
        task.setAssignedOwnerId(employee.getId());
        task.setStatus("PENDING");
        entityManager.persist(task);
        entityManager.flush();
        entityManager.clear();
        Employee loaded = entityManager.find(Employee.class, employee.getId());
        assertThat(loaded.getDeviceAssets()).hasSize(1);
        assertThat(loaded.getGrantedSystems()).extracting(SystemEntity::getId).containsExactly(system.getId());
        assertThat(entityManager.find(SystemEntity.class, system.getId()).getGrantedEmployees())
                .extracting(Employee::getId).containsExactly(employee.getId());
        assertThat(entityManager.find(DeviceAsset.class, asset.getId()).getAccessGrants()).hasSize(1);
        assertThat(entityManager.find(HighRiskActivityFlag.class, flag.getId()).getOffboardingCases()).hasSize(1);
        assertThat(entityManager.find(OffboardingCase.class, off.getId()).getRevocationTasks()).hasSize(1);
        assertThat(entityManager.find(RevocationTask.class, task.getId()).getAssignedOwner().getId())
                .isEqualTo(employee.getId());
    }
}
