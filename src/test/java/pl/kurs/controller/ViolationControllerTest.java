package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.errors.ViolationAlreadyExistsException;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateViolationCommand;
import pl.kurs.model.dto.ViolationDto;
import pl.kurs.model.enums.ViolationType;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("no-liquibase")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ViolationControllerTest {

    @Autowired
    private MockMvc postman;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(username = "user")
    void shouldAddViolation() throws Exception {
        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(1500)
                        .points(10)
                        .type(ViolationType.COLLISION)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("points").value(10))
                .andExpect(jsonPath("payment").value(1500))
                .andExpect(jsonPath("type").value("Collision"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotAddViolationBecauseOfIncorrectParameters() throws Exception {
        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(6000)
                        .points(16)
                        .type(ViolationType.DRIFTING)
                        .build());

        String response = postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertTrue(response.contains("VIOLATION_PAYMENT_BESIDE_RANGE"));
        Assertions.assertTrue(response.contains("VIOLATION_POINTS_BESIDE_RANGE"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotAddViolationAndThrowViolationAlreadyExistsException() throws Exception {
        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(5000)
                        .points(8)
                        .type(ViolationType.DRIFTING)
                        .build());

        //add new violation
        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isCreated());

        //try to add the same violation
        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ViolationAlreadyExistsException))
                .andExpect(jsonPath("violationType").value("Drifting"))
                .andExpect(jsonPath("points").value(8))
                .andExpect(jsonPath("payment").value(5000));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldDeleteViolation() throws Exception {
        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(5000)
                        .points(8)
                        .type(ViolationType.DRIFTING)
                        .build());

        //add new violation
        String content = postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ViolationDto violationDto = objectMapper.readValue(content, ViolationDto.class);

        postman.perform(MockMvcRequestBuilders.delete("/violations/" + violationDto.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldEditViolation() throws Exception {
        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(5000)
                        .points(8)
                        .type(ViolationType.DRIFTING)
                        .build());

        //add new violation
        String content = postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ViolationDto violationDto = objectMapper.readValue(content, ViolationDto.class);

        String editViolation = objectMapper.writeValueAsString(
                UpdateViolationCommand.builder()
                        .type(ViolationType.LIGHTS)
                        .points(4)
                        .payment(1000)
                        .build());

        postman.perform(MockMvcRequestBuilders.put("/violations/" + violationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editViolation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("type").value("Lights"))
                .andExpect(jsonPath("points").value(4))
                .andExpect(jsonPath("payment").value(1000));
    }


}