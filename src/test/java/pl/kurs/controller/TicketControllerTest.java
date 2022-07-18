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
import pl.kurs.errors.ViolationAlreadyExistsException;
import pl.kurs.model.command.CreateTicketCommand;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateTicketCommand;
import pl.kurs.model.dto.TicketDto;
import pl.kurs.model.entity.Person;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("no-liquibase")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TicketControllerTest {

    @Autowired
    private MockMvc postman;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PersonRepository personRepository;

    private Person person1, person2;

    @BeforeEach
    void init() {
        person1 = Person.builder()
                .pesel("03282009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .licenseSuspended(false)
                .build();
        person2 = Person.builder()
                .pesel("04282009610")
                .name("Agnieszka")
                .surname("Sobczak")
                .email("moderntoking9@gmail.com")
                .licenseSuspended(false)
                .build();

        personRepository.saveAndFlush(person1);
        personRepository.saveAndFlush(person2);
    }

    @Test
    @WithMockUser(username = "user")
    void shouldThrowViolationAlreadyExistsException() throws Exception {
        String saveTicket1 = objectMapper.writeValueAsString(
                CreateTicketCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .personPesel(person1.getPesel())
                        .series("AB")
                        .payed(false)
                        .build());
        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(2000)
                        .points(15)
                        .type(ViolationType.HIGH_SPEED)
                        .build());

        //add ticket
        String firstTicket = postman.perform(MockMvcRequestBuilders.post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveTicket1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TicketDto ticketDto1 = objectMapper.readValue(firstTicket, TicketDto.class);

        //add first violation
        postman.perform(MockMvcRequestBuilders.post("/tickets/" + ticketDto1.getId() + "/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isOk());

        //try to add the same violation to ticket
        postman.perform(MockMvcRequestBuilders.post("/tickets/" + ticketDto1.getId() + "/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ViolationAlreadyExistsException))
                .andExpect(jsonPath("violationType").value("High speed"))
                .andExpect(jsonPath("points").value(15))
                .andExpect(jsonPath("payment").value(2000));

    }

    @Test
    @WithMockUser(username = "user")
    void shouldThrowTooManyPointsInThePreviousYearException() throws Exception {
        String saveTicket1 = objectMapper.writeValueAsString(
                CreateTicketCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .personPesel(person1.getPesel())
                        .series("AB")
                        .payed(false)
                        .build());
        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(2000)
                        .points(15)
                        .type(ViolationType.HIGH_SPEED)
                        .build());
        String saveViolation2 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(1000)
                        .points(12)
                        .type(ViolationType.RED_LIGHT)
                        .build());

        String saveViolation3 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(4000)
                        .points(8)
                        .type(ViolationType.DRIFTING)
                        .build());

        //add ticket
        String firstTicket = postman.perform(MockMvcRequestBuilders.post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saveTicket1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TicketDto ticketDto1 = objectMapper.readValue(firstTicket, TicketDto.class);

        //add first violation
        postman.perform(MockMvcRequestBuilders.post("/tickets/" + ticketDto1.getId() + "/violations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saveViolation1))
                .andExpect(status().isOk());

        Optional<Person> person = personRepository.findByPesel(person1.getPesel());

        postman.perform(MockMvcRequestBuilders.get("/people/" + person.get().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("licenseSuspended").value(false));

        //try to add second violation and person will have license suspended
        postman.perform(MockMvcRequestBuilders.post("/tickets/" + ticketDto1.getId() + "/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation2))
                .andExpect(status().isOk());

        //person should have license suspended
        postman.perform(MockMvcRequestBuilders.get("/people/" + person.get().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("licenseSuspended").value(true));

        //try to add third violation but person has already too many points
        postman.perform(MockMvcRequestBuilders.post("/tickets/" + ticketDto1.getId() + "/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation3))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof TooManyPointsInPreviousYearException))
                .andExpect(jsonPath("msg").value("Too many points already"))
                .andExpect(jsonPath("points").value(27))
                .andExpect(jsonPath("personPesel").value(person1.getPesel()));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldAddTicketWithOneViolation() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateTicketCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .personPesel(person2.getPesel())
                        .payed(true)
                        .series("AB")
                        .build());

        String saveViolation1 = objectMapper.writeValueAsString(
                CreateViolationCommand.builder()
                        .payment(1500)
                        .points(10)
                        .type(ViolationType.COLLISION)
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TicketDto ticket = objectMapper.readValue(content, TicketDto.class);

        //add first violation
        postman.perform(MockMvcRequestBuilders.post("/tickets/" + ticket.getId() + "/violations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveViolation1))
                .andExpect(status().isOk());

        postman.perform(MockMvcRequestBuilders.get("/tickets/" + ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("totalPoints").value(10))
                .andExpect(jsonPath("totalPayment").value(1500))
                .andExpect(jsonPath("version").value(0))
                .andExpect(jsonPath("series").value("AB"))
                .andExpect(jsonPath("personPesel").value("04282009610"))
                .andExpect(jsonPath("deleted").value(false))
                .andExpect(jsonPath("payed").value(true));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotAddTicketBecauseOfIncorrectParameters() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateTicketCommand.builder()
                        .date(LocalDateTime.now().plusDays(2))
                        .personPesel("123")
                        .series("DUPA")
                        .payed(false)
                        .build());

        postman.perform(MockMvcRequestBuilders.post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].error", containsInAnyOrder("DATE_FROM_FUTURE",  "PESEL_NOT_VALID", "VALUE_NOT_ALLOWED")))
                .andExpect(jsonPath("$[*].field", containsInAnyOrder("date", "personPesel", "series")));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldDeleteTicket() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateTicketCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .personPesel(person2.getPesel())
                        .payed(true)
                        .series("AB")
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TicketDto ticket = objectMapper.readValue(content, TicketDto.class);

        postman.perform(MockMvcRequestBuilders.delete("/tickets/" + ticket.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldEditTicket() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateTicketCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .personPesel(person2.getPesel())
                        .payed(false)
                        .series("AB")
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TicketDto ticket = objectMapper.readValue(content, TicketDto.class);

        String editJson = objectMapper.writeValueAsString(
                UpdateTicketCommand.builder()
                        .version(0)
                        .payed(true)
                        .personPesel(person1.getPesel())
                        .build());

        postman.perform(MockMvcRequestBuilders.put("/tickets/" + ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value(1))
                .andExpect(jsonPath("series").value("AB"))
                .andExpect(jsonPath("personPesel").value(person1.getPesel()))
                .andExpect(jsonPath("payed").value(true));
    }

    @Test
    @WithMockUser(username = "user")
    void shouldNotEditTicketBecauseOfMissingPersonWithSuchPesel() throws Exception {
        String saveJson = objectMapper.writeValueAsString(
                CreateTicketCommand.builder()
                        .date(LocalDateTime.now().minusHours(1))
                        .personPesel(person2.getPesel())
                        .payed(false)
                        .series("AB")
                        .build());

        String content = postman.perform(MockMvcRequestBuilders.post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TicketDto ticket = objectMapper.readValue(content, TicketDto.class);

        String editJson = objectMapper.writeValueAsString(
                UpdateTicketCommand.builder()
                        .version(0)
                        .payed(true)
                        .personPesel("05212009610")
                        .build());

        postman.perform(MockMvcRequestBuilders.put("/tickets/" + ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(jsonPath("entityName").value("Person"))
                .andExpect(jsonPath("entityKey").value("05212009610"));
    }


}