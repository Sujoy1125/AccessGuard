package com.accessguard.accessguard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@org.springframework.test.context.TestExecutionListeners(listeners = {
        org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
        org.springframework.test.context.support.DirtiesContextTestExecutionListener.class,
        org.springframework.test.context.transaction.TransactionalTestExecutionListener.class }, inheritListeners = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTests {
    @LocalServerPort
    int port;

    RestClient client() {
        return RestClient.create("http://localhost:" + port);
    }

    Map<?, ?> post(String route, Map<String, Object> body) {
        var result = client().post().uri("/api/" + route).contentType(MediaType.APPLICATION_JSON).body(body).retrieve()
                .toEntity(Map.class);
        assertThat(result.getStatusCode().value()).isEqualTo(201);
        return result.getBody();
    }

    Map<?, ?> get(String route) {
        return client().get().uri("/api/" + route).retrieve().body(Map.class);
    }

    @Test
    void allEntitiesSupportCrudAndMappings() {
        Map<String, Object> employee = new LinkedHashMap<>(Map.of("name", "API Test", "email",
                UUID.randomUUID() + "@example.com", "department", "IT", "status", "ACTIVE"));
        String emp = post("employees", employee).get("id").toString();
        var system = Map.<String, Object>of("name", "Git", "ownerUserId", emp, "category", "SAAS");
        String sys = post("systems", system).get("id").toString();
        var device = Map.<String, Object>of("employeeId", emp, "assetType", "LAPTOP", "returnStatus", "PENDING");
        String asset = post("device-assets", device).get("id").toString();
        var grant = Map.<String, Object>of("employeeId", emp, "systemId", sys, "grantedBy", emp, "grantedAt",
                "2026-10-08T10:00:00", "accessLevel", "READ", "deviceAssetId", asset);
        String grantId = post("access-grants", grant).get("id").toString();
        var flag = Map.<String, Object>of("employeeId", emp, "sourceSystem", "Git", "activityType", "EXPORT",
                "detectedAt", "2026-10-08T10:00:00", "detectedBy", emp, "reviewStatus", "PENDING");
        String flagId = post("high-risk-activity-flags", flag).get("id").toString();
        var off = Map.<String, Object>of("employeeId", emp, "initiatedBy", emp, "initiatedAt", "2026-10-08T10:00:00",
                "targetCompletionDate", "2026-10-09", "status", "OPEN", "triggerFlagId", flagId);
        String caseId = post("offboarding-cases", off).get("id").toString();
        var task = Map.<String, Object>of("offboardingCaseId", caseId, "systemId", sys, "assignedOwnerId", emp,
                "status", "PENDING");
        String taskId = post("revocation-tasks", task).get("id").toString();
        var ack = Map.<String, Object>of("offboardingCaseId", caseId, "employeeId", emp, "acknowledgedAt",
                "2026-10-08T11:00:00", "statementVersion", "v1");
        String ackId = post("data-acknowledgments", ack).get("id").toString();
        assertThatThrownBy(() -> post("data-acknowledgments", ack))
                .isInstanceOf(HttpClientErrorException.Conflict.class);
        var escalation = Map.<String, Object>of("revocationTaskId", taskId, "escalatedTo", emp, "escalatedAt",
                "2026-10-08T12:00:00", "reason", "Overdue");
        String escId = post("escalation-logs", escalation).get("id").toString();
        var audit = Map.<String, Object>of("periodStart", "2026-10-01", "periodEnd", "2026-10-08", "totalOffboardings",
                4, "retainedAssetCount", 3, "overdueCasesCount", 2);
        String auditId = post("audit-snapshots", audit).get("id").toString();
        var rows = List.of(new Object[] { "employees", emp, employee }, new Object[] { "systems", sys, system },
                new Object[] { "device-assets", asset, device }, new Object[] { "access-grants", grantId, grant },
                new Object[] { "high-risk-activity-flags", flagId, flag },
                new Object[] { "offboarding-cases", caseId, off }, new Object[] { "revocation-tasks", taskId, task },
                new Object[] { "data-acknowledgments", ackId, ack },
                new Object[] { "escalation-logs", escId, escalation },
                new Object[] { "audit-snapshots", auditId, audit });
        for (var row : rows) {
            String route = row[0] + "/" + row[1];
            assertThat(get(route).get("id")).isEqualTo(row[1]);
            assertThat(client().get().uri("/api/" + row[0]).retrieve().body(List.class)).isNotEmpty();
            assertThat(client().put().uri("/api/" + route).contentType(MediaType.APPLICATION_JSON).body(row[2])
                    .retrieve().toEntity(Map.class).getStatusCode().value()).isEqualTo(200);
            assertThat(client().patch().uri("/api/" + route).contentType(MediaType.APPLICATION_JSON).body(Map.of())
                    .retrieve().toEntity(Map.class).getStatusCode().value()).isEqualTo(200);
        }
        var patched = client().patch().uri("/api/audit-snapshots/" + auditId).contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("avgRevocationTimeHrs", 2.5)).retrieve().body(Map.class);
        assertThat(patched.get("totalOffboardings")).isEqualTo(4);
        assertThat(patched.get("retainedAssetCount")).isEqualTo(3);
        assertThatThrownBy(() -> client().patch().uri("/api/employees/" + emp).contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", "bad")).retrieve().toBodilessEntity())
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
        assertThatThrownBy(() -> client().patch().uri("/api/employees/" + emp).contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("id", emp)).retrieve().toBodilessEntity())
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
        assertThatThrownBy(() -> post("device-assets",
                Map.of("employeeId", UUID.randomUUID(), "assetType", "LAPTOP", "returnStatus", "PENDING")))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
        assertThatThrownBy(() -> client().delete().uri("/api/employees/" + emp).retrieve().toBodilessEntity())
                .isInstanceOf(HttpClientErrorException.Conflict.class);
        assertThatThrownBy(() -> client().delete().uri("/api/systems/" + sys).retrieve().toBodilessEntity())
                .isInstanceOf(HttpClientErrorException.Conflict.class);
        assertThat(get("access-grants/" + grantId).get("employeeId")).isEqualTo(emp);
        assertThat(client().get().uri("/v3/api-docs").retrieve().body(String.class)).contains("/api/offboarding-cases",
                "/api/revocation-tasks");
        for (String route : List.of("escalation-logs/" + escId, "data-acknowledgments/" + ackId,
                "revocation-tasks/" + taskId, "offboarding-cases/" + caseId, "high-risk-activity-flags/" + flagId,
                "access-grants/" + grantId, "device-assets/" + asset, "systems/" + sys, "audit-snapshots/" + auditId,
                "employees/" + emp)) {
            assertThat(client().delete().uri("/api/" + route).retrieve().toBodilessEntity().getStatusCode().value())
                    .isEqualTo(204);
            assertThatThrownBy(() -> get(route)).isInstanceOf(HttpClientErrorException.NotFound.class);
        }
    }
}
