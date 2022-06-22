package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.kurs.model.command.CreatePersonCommand;
import pl.kurs.model.command.UpdatePersonCommand;
import pl.kurs.model.dto.PersonDto;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("no-liquibase")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PersonControllerTest {

    @Autowired
    private MockMvc postman;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user")
    void shouldAddPerson() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreatePersonCommand.builder()
                        .pesel("01292009610")
                        .name("Adam")
                        .surname("Graczyk")
                        .email("moderntoking7@gmail.com")
                        .isLicenseSuspended(false)
                        .build());
        String content = postman.perform(MockMvcRequestBuilders.post("/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDto personDto = objectMapper.readValue(content, PersonDto.class);

        postman.perform(MockMvcRequestBuilders.get("/people/" + personDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Adam"))
                .andExpect(jsonPath("surname").value("Graczyk"))
                .andExpect(jsonPath("version").value(0))
                .andExpect(jsonPath("pesel").value("01292009610"))
                .andExpect(jsonPath("email").value("moderntoking7@gmail.com"))
                .andExpect(jsonPath("licenseSuspended").value("false"));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotAddPersonBecauseOfNotUniquePeselAndIncorrectParameters() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreatePersonCommand.builder()
                        .pesel("03272009610")
                        .name("Ada")
                        .surname("Michalak")
                        .email("moderntoking7@gmail.com")
                        .isLicenseSuspended(false)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/people")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(saveJson))
                .andExpect(status().isCreated());

        String incorrectJson = objectMapper.writeValueAsString(
                CreatePersonCommand.builder()
                        .pesel("03272009610")
                        .name("")
                        .surname("")
                        .email("moderntoking7@gmail.com")
                        .isLicenseSuspended(false)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(incorrectJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].error", containsInAnyOrder("PESEL_NOT_UNIQUE", "NAME_NOT_EMPTY", "SURNAME_NOT_EMPTY", "EMAIL_NOT_UNIQUE_OR_INVALID")))
                .andExpect(jsonPath("$[*].field", containsInAnyOrder("email", "name", "surname", "pesel")));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldDeletePerson() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreatePersonCommand.builder()
                        .pesel("03272009610")
                        .name("Ada")
                        .surname("Michalak")
                        .email("moderntoking7@gmail.com")
                        .isLicenseSuspended(false)
                        .build());

        String saveContent = postman.perform(MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDto person = objectMapper.readValue(saveContent, PersonDto.class);

        String content = postman.perform(MockMvcRequestBuilders.get("/people/" + person.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDto personDto = objectMapper.readValue(content, PersonDto.class);

        postman.perform(MockMvcRequestBuilders.delete("/people/" + personDto.getId()))
                .andExpect(status().isNoContent());

        postman.perform(MockMvcRequestBuilders.get("/people/" + personDto.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(jsonPath("entityName").value("Person"))
                .andExpect(jsonPath("entityKey").value(personDto.getId()));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldEditPerson() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreatePersonCommand.builder()
                        .pesel("03272009610")
                        .name("Ada")
                        .surname("Michalak")
                        .email("moderntoking7@gmail.com")
                        .isLicenseSuspended(false)
                        .build());

        String saveContent = postman.perform(MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDto personDto = objectMapper.readValue(saveContent, PersonDto.class);

        String editJson = objectMapper.writeValueAsString(
                UpdatePersonCommand.builder()
                        .name("Agnieszka")
                        .surname("Sobczak")
                        .email("aga123@gmail.com")
                        .isLicenseSuspended(true)
                        .version(0)
                        .build());
        postman.perform(MockMvcRequestBuilders.put("/people/" + personDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Agnieszka"))
                .andExpect(jsonPath("surname").value("Sobczak"))
                .andExpect(jsonPath("email").value("aga123@gmail.com"))
                .andExpect(jsonPath("licenseSuspended").value(true))
                .andExpect(jsonPath("version").value(1));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotEditPerson() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreatePersonCommand.builder()
                        .pesel("03272009610")
                        .name("Ada")
                        .surname("Michalak")
                        .email("moderntoking7@gmail.com")
                        .isLicenseSuspended(false)
                        .build());

        String saveContent = postman.perform(MockMvcRequestBuilders.post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDto personDto = objectMapper.readValue(saveContent, PersonDto.class);

        String editJson = objectMapper.writeValueAsString(
                UpdatePersonCommand.builder()
                        .name("")
                        .surname("")
                        .email("ag3@#%*gmail.com")
                        .isLicenseSuspended(true)
                        .version(null)
                        .build());

        postman.perform(MockMvcRequestBuilders.put("/people/" + personDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].error", containsInAnyOrder("VERSION_NOT_EMPTY", "NAME_NOT_EMPTY", "SURNAME_NOT_EMPTY", "EMAIL_NOT_UNIQUE_OR_INVALID")))
                .andExpect(jsonPath("$[*].field", containsInAnyOrder("version", "name", "surname", "email")));
    }


}