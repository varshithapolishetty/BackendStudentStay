package com.studentstay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentstay.dto.ApiDtos;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentStayApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUserAndFetchPublicProperties() throws Exception {
        ApiDtos.RegisterRequest request = new ApiDtos.RegisterRequest();
        request.setFullName("Test Student");
        request.setUsername("student#25");
        request.setEmail("student@example.com");
        request.setPassword("Secret123");
        request.setPhone("9876543210");
        request.setRole("STUDENT");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"student#25","password":"Secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("student#25"));

        request.setEmail("another@example.com");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Username already exists. Please try another username."));

        request.setUsername("another#student1");
        request.setEmail("third@example.com");
        request.setPassword("");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/properties")
                        .param("city", "Hyderabad")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void bootstrapAdministratorCanSignInWithAdminIdAndAccessAdminDashboardData() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();
        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void studentDemoBookingPersistsAcrossRequestsAndCanBeCancelled() throws Exception {
        ApiDtos.RegisterRequest student = new ApiDtos.RegisterRequest();
        student.setFullName("Booking Test Student");
        student.setUsername("booking@1");
        student.setEmail("booking-test@example.com");
        student.setPassword("123456");
        student.setPhone("9876543210");
        String studentResponse = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String studentToken = objectMapper.readTree(studentResponse).get("token").asText();

        ApiDtos.DemoBookingRequest booking = new ApiDtos.DemoBookingRequest();
        booking.setPgId(1L);
        booking.setPgName("Test Hostel");
        booking.setLocation("Hyderabad");
        booking.setCategory("men");
        booking.setSharing("DOUBLE");
        booking.setRent(new java.math.BigDecimal("8500"));
        booking.setStudentName("Booking Test Student");
        booking.setPlace("Warangal");
        booking.setPhone("9876543210");
        booking.setMoveInDate(LocalDate.now().plusDays(1));
        String bookingResponse = mockMvc.perform(post("/api/v1/demo-bookings")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.studentEmail").value("booking-test@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long bookingId = objectMapper.readTree(bookingResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1/demo-bookings/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pgName").value("Test Hostel"));

        String adminResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"admin123"}
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String adminToken = objectMapper.readTree(adminResponse).get("token").asText();
        mockMvc.perform(get("/api/v1/admin/demo-bookings")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentEmail").value("booking-test@example.com"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/v1/demo-bookings/{id}/cancel", bookingId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void registrationAcceptsNumericPassword123456() throws Exception {
        ApiDtos.RegisterRequest request = new ApiDtos.RegisterRequest();
        request.setFullName("Weak Password Student");
        request.setUsername("weak@1");
        request.setEmail("weak-password@example.com");
        request.setPassword("123456");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"weak@1","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("weak@1"));
    }

    @Test
    void usernameValidationRequiresLettersNumbersAndExactlyOneAllowedSpecialCharacter() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ApiDtos.RegisterRequest request = new ApiDtos.RegisterRequest();

        for (String username : List.of("a@1", "varsha@123", "student#25", "abc_123", "user-456", "a1.b", "A1$")) {
            request.setUsername(username);
            assertTrue(validator.validateProperty(request, "username").isEmpty(), username);
        }

        for (String username : List.of("student123", "varsha@abc", "user@@123", "user!123", "user name1", "ab@1#")) {
            request.setUsername(username);
            assertFalse(validator.validateProperty(request, "username").isEmpty(), username);
        }
    }

    @Test
    void registrationPasswordMustNotBeBlank() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ApiDtos.RegisterRequest request = new ApiDtos.RegisterRequest();

        for (String password : List.of("x", "123", "123456", "abc12!", "Abc123")) {
            request.setPassword(password);
            assertTrue(validator.validateProperty(request, "password").isEmpty(), password);
        }

        for (String password : List.of("", "   ")) {
            request.setPassword(password);
            assertFalse(validator.validateProperty(request, "password").isEmpty(), password);
        }
    }
}
