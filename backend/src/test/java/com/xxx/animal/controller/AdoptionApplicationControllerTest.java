package com.xxx.animal.controller;

import com.xxx.animal.entity.AdoptionApplication;
import com.xxx.animal.entity.Animal;
import com.xxx.animal.entity.User;
import com.xxx.animal.service.AdoptionApplicationService;
import com.xxx.animal.service.AnimalService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AdoptionApplicationControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private AdoptionApplicationService adoptionApplicationService;

    private MockHttpSession adminSession;
    private Long testAnimalId;
    private Long testApplicationId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        // Admin session
        User admin = new User();
        admin.setId(1L);
        admin.setRole("ADMIN");
        adminSession = new MockHttpSession();
        adminSession.setAttribute("loginUser", admin);

        // Create test pet
        Animal pet = new Animal();
        pet.setName("小白");
        pet.setAnimalNo("TEST-XB-" + System.currentTimeMillis());
        pet.setCategory("DOG");
        pet.setSex("FEMALE");
        pet.setStatus(1);
        animalService.save(pet);
        testAnimalId = pet.getId();

        // Create test application for that pet
        AdoptionApplication app = new AdoptionApplication();
        app.setAnimalId(testAnimalId);
        app.setApplicantName("测试审核员");
        app.setPhone("13800000000");
        app.setAddress("北京市测试街道");
        app.setStatus(0);
        adoptionApplicationService.save(app);
        testApplicationId = app.getId();
    }

    @AfterEach
    void tearDown() {
        if (testApplicationId != null) {
            adoptionApplicationService.removeById(testApplicationId);
        }
        if (testAnimalId != null) {
            animalService.removeById(testAnimalId);
        }
    }

    @Test
    void shouldReturnAnimalNameInListResponse() throws Exception {
        mockMvc.perform(get("/api/admin/adoption-applications")
                        .session(adminSession)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].animalName").value("小白"));
    }
}
