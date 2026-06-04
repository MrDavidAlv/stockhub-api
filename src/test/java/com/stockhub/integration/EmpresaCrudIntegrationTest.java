package com.stockhub.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmpresaCrudIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll_isPublic_returnsList() throws Exception {
        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void create_withoutAuth_returns403() throws Exception {
        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nit":"700100100-9","nombre":"X","direccion":"y","telefono":"z"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@stockhub.local", roles = "ADMIN")
    void create_asAdmin_succeedsAndPersists() throws Exception {
        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nit":"700200200-1","nombre":"TestCo","direccion":"Cra 1","telefono":"3001234567"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nit").value("700200200-1"))
                .andExpect(jsonPath("$.nombre").value("TestCo"));

        mockMvc.perform(get("/api/empresas/700200200-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("TestCo"));
    }

    @Test
    @WithMockUser(username = "externo@stockhub.local", roles = "EXTERNO")
    void create_asExterno_returns403() throws Exception {
        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nit":"700300300-2","nombre":"X","direccion":"y","telefono":"z"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withInvalidNit_returns400() throws Exception {
        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nit":"","nombre":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.nit").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_nonExistent_returns404() throws Exception {
        mockMvc.perform(delete("/api/empresas/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
