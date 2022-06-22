package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import pl.kurs.errors.EntityNotFoundException;
import pl.kurs.errors.TooManyPointsInPreviousYearException;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateViolationCommand;
import pl.kurs.model.dto.ViolationDto;
import pl.kurs.model.entity.Person;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.repository.PersonRepository;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("no-liquibase")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ViolationControllerTest {

    @Autowired
    private MockMvc postman;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void init() {
        Person person1 = Person.builder()
                .pesel("01292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .isLicenseSuspended(false)
                .build();
        Person person2 = Person.builder()
                .pesel("03282009610")
                .name("Agnieszka")
                .surname("Sobczak")
                .email("moderntoking9@gmail.com")
                .isLicenseSuspended(false)
                .build();

        personRepository.saveAndFlush(person1);
        personRepository.saveAndFlush(person2);
    }

    @Test
    @WithMockUser(username = "user")
    void shouldThrowTooManyPointsInThePreviousYearException() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .payment(1500)
                        .points(15)
                        .personPesel("03282009610")
                        .type(ViolationType.COLLISION)
                        .build());

        String saveJson2 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusDays(1))
                        .payment(5000)
                        .points(15)
                        .personPesel("03282009610")
                        .type(ViolationType.DRIFTING)
                        .build());

        String saveJson3 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusDays(1))
                        .payment(3000)
                        .points(12)
                        .personPesel("03282009610")
                        .type(ViolationType.RED_LIGHT)
                        .build());

        //add first violation
        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated());

        Optional<Person> person = personRepository.findByPesel("03282009610");

        postman.perform(MockMvcRequestBuilders.get("/people/" + person.get().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("licenseSuspended").value(false));

        //try to add second violation and person will have license suspended
        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson2))
                .andExpect(status().isCreated());

        //person should have license suspended
        postman.perform(MockMvcRequestBuilders.get("/people/" + person.get().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("licenseSuspended").value(true));

        //try to add third violation but person has already too many points
        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson3))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof TooManyPointsInPreviousYearException))
                .andExpect(jsonPath("msg").value("Too many points already"))
                .andExpect(jsonPath("points").value(30))
                .andExpect(jsonPath("personPesel").value("03282009610"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldAddTrafficViolation() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .payment(1500)
                        .points(10)
                        .personPesel("01292009610")
                        .type(ViolationType.COLLISION)
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ViolationDto violation = objectMapper.readValue(content, ViolationDto.class);

        postman.perform(MockMvcRequestBuilders.get("/violations/" + violation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("points").value(10))
                .andExpect(jsonPath("payment").value(1500))
                .andExpect(jsonPath("version").value(0))
                .andExpect(jsonPath("personPesel").value("01292009610"))
                .andExpect(jsonPath("deleted").value(false))
                .andExpect(jsonPath("type").value("Collision"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotAddTrafficViolationBecauseOfIncorrectParameters() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .payment(1500)
                        .points(10)
                        .personPesel("01292009610")
                        .type(ViolationType.COLLISION)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated());

        String incorrectJson = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().plusDays(2))
                        .payment(10000)
                        .points(20)
                        .personPesel("")
                        .type(null)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incorrectJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].error", containsInAnyOrder("DATE_FROM_FUTURE", "TOO_MANY_POINTS", "PAYMENT_TOO_HIGH", "PESEL_NOT_EMPTY", "VIOLATION_TYPE_NOT_FOUND")))
                .andExpect(jsonPath("$[*].field", containsInAnyOrder("date", "points", "payment", "personPesel", "type")));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldDeleteTrafficViolation() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .payment(1500)
                        .points(10)
                        .personPesel("01292009610")
                        .type(ViolationType.COLLISION)
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ViolationDto violation = objectMapper.readValue(content, ViolationDto.class);

        postman.perform(MockMvcRequestBuilders.delete("/violations/" + violation.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldEditTrafficViolation() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .payment(1500)
                        .points(10)
                        .personPesel("01292009610")
                        .type(ViolationType.COLLISION)
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ViolationDto violation = objectMapper.readValue(content, ViolationDto.class);

        String editJson = objectMapper.writeValueAsString(
                UpdateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(2))
                        .payment(500)
                        .points(6)
                        .type(ViolationType.TECHNICAL_CONDITION)
                        .personPesel("03282009610")
                        .version(0)
                        .build());

        postman.perform(MockMvcRequestBuilders.put("/violations/" + violation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("points").value(6))
                .andExpect(jsonPath("payment").value(500))
                .andExpect(jsonPath("version").value(1))
                .andExpect(jsonPath("personPesel").value("03282009610"))
                .andExpect(jsonPath("deleted").value(false))
                .andExpect(jsonPath("type").value("Technical condition"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotEditTrafficViolationBecauseOfMissingPersonWithSuchPesel() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .payment(1500)
                        .points(10)
                        .personPesel("01292009610")
                        .type(ViolationType.COLLISION)
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ViolationDto violation = objectMapper.readValue(content, ViolationDto.class);

        String editJson = objectMapper.writeValueAsString(
                UpdateViolationCommand.builder()
                        .date(LocalDateTime.now().minusHours(2))
                        .payment(1200)
                        .points(14)
                        .type(ViolationType.TECHNICAL_CONDITION)
                        .personPesel("04292009610")
                        .version(0)
                        .build());

        postman.perform(MockMvcRequestBuilders.put("/violations/" + violation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(jsonPath("entityName").value("Person"))
                .andExpect(jsonPath("entityKey").value("04292009610"));
    }


}