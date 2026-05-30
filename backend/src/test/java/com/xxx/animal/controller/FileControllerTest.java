package com.xxx.animal.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileControllerTest {

    private MockMvc mockMvc;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Override user.dir so files land in the temp directory
        System.setProperty("user.dir", tempDir.toString());
        mockMvc = MockMvcBuilders.standaloneSetup(new FileController()).build();
    }

    @Test
    void shouldUploadToActivitiesFolder() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file)
                        .param("folder", "activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data", containsString("/uploads/activities/")));
    }

    @Test
    void shouldUploadToAnimalsFolder() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "pet image".getBytes()
        );

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file)
                        .param("folder", "animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data", containsString("/uploads/animals/")));
    }

    @Test
    void shouldRejectMissingFolder() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file))
                .andExpect(status().isBadRequest());
    }
}
