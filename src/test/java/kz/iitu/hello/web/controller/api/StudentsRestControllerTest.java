package kz.iitu.hello.web.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.iitu.hello.security.JwtUtil;
import kz.iitu.hello.service.StudentService;
import kz.iitu.hello.web.dto.form.StudentFormDto;
import kz.iitu.hello.web.validations.StudentFormValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = StudentsRestController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class StudentsRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentFormValidator studentFormValidator;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void read_returnsStatus200() throws Exception {
        when(studentService.search(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk());
    }

    @Test
    void create_withValidBody_returnsStatus201() throws Exception {
        StudentFormDto form = new StudentFormDto();
        form.setStudentName("John Doe");
        form.setAge(20);
        form.setGpa(3.5);
        form.setGroupName("CS-101");
        form.setUserId(1L);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated());
    }

    @Test
    void create_withMissingRequiredFields_returnsStatus400() throws Exception {
        StudentFormDto form = new StudentFormDto();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returnsStatus204() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }
}
