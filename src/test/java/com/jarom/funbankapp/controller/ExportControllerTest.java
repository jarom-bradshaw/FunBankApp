package com.jarom.funbankapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.ExportRequest;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeAll
    public void registerUserOnce() throws Exception {
        String registerJson = "{" +
                "\"username\":\"exportuser\"," +
                "\"email\":\"exportuser@example.com\"," +
                "\"password\":\"Export123!\"," +
                "\"firstName\":\"Export\"," +
                "\"lastName\":\"User\"}";
        try {
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerJson))
                    .andExpect(status().isOk());
        } catch (Exception ignored) {}
    }

    @BeforeEach
    public void setup() throws Exception {
        // Login and get JWT token
        String loginJson = "{" +
                "\"email\":\"exportuser@example.com\"," +
                "\"password\":\"Export123!\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        int tokenStart = response.indexOf("token\":\"") + 8;
        int tokenEnd = response.indexOf('"', tokenStart);
        jwtToken = response.substring(tokenStart, tokenEnd);
    }

    @Test
    public void testExportTransactionsCSV() throws Exception {
        ExportRequest request = new ExportRequest();
        request.setExportType("transactions");
        request.setFormat("CSV");
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/export/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andReturn();
        // Optionally, parse jobId and check file later
    }

    @Test
    public void testExportDebtsPDF() throws Exception {
        ExportRequest request = new ExportRequest();
        request.setExportType("debts");
        request.setFormat("PDF");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/export/debts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testExportBudgetsExcel() throws Exception {
        ExportRequest request = new ExportRequest();
        request.setExportType("budgets");
        request.setFormat("EXCEL");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/export/budgets")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testExportGoalsJSON() throws Exception {
        ExportRequest request = new ExportRequest();
        request.setExportType("goals");
        request.setFormat("JSON");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/export/goals")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testListExportJobs() throws Exception {
        mockMvc.perform(get("/api/export/jobs")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void testJobManagementEndpoints() throws Exception {
        // Create a job
        ExportRequest request = new ExportRequest();
        request.setExportType("transactions");
        request.setFormat("CSV");
        String json = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(post("/api/export/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Long jobId = objectMapper.readTree(response).get("data").get("id").asLong();

        // Get job status
        mockMvc.perform(get("/api/export/jobs/" + jobId + "/status")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        // Cancel job (should fail since job is already completed)
        mockMvc.perform(delete("/api/export/jobs/" + jobId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    public void testDownloadExportFile() throws Exception {
        // Create a job
        ExportRequest request = new ExportRequest();
        request.setExportType("transactions");
        request.setFormat("CSV");
        String json = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(post("/api/export/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Long jobId = objectMapper.readTree(response).get("data").get("id").asLong();

        // Wait for async job to complete (simulate)
        Thread.sleep(2000);

        // Download file
        MvcResult downloadResult = mockMvc.perform(get("/api/export/jobs/" + jobId + "/download")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();
        byte[] fileBytes = downloadResult.getResponse().getContentAsByteArray();
        assertThat(fileBytes.length).isGreaterThan(0);
    }
} 