package kz.iitu.hello.web.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.iitu.hello.security.JwtUtil;
import kz.iitu.hello.service.CourseService;
import kz.iitu.hello.web.dto.form.CourseFormDto;
import kz.iitu.hello.web.validations.CourseFormValidator;
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
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = CoursesRestController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class CoursesRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @MockBean
    private CourseFormValidator courseFormValidator;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void read_returnsStatus200() throws Exception {
        when(courseService.search(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void create_withValidBody_returnsStatus201() throws Exception {
        CourseFormDto form = new CourseFormDto();
        form.setCourseName("Algorithms");
        form.setCredits(3);
        form.setMaxStudents(30);
        form.setTeacherId(1L);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated());
    }

    @Test
    void create_whenStudentCountExceedsMax_returnsStatus400() throws Exception {
        doAnswer(inv -> {
            BindingResult br = inv.getArgument(1);
            br.rejectValue("studentIds", "tooMany", "Number of selected students exceeds the maximum allowed");
            return null;
        }).when(courseFormValidator).validate(any(CourseFormDto.class), any(), any());

        CourseFormDto form = new CourseFormDto();
        form.setCourseName("Algorithms");
        form.setCredits(3);
        form.setMaxStudents(2);
        form.setTeacherId(1L);
        form.setStudentIds(List.of(1L, 2L, 3L));

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returnsStatus204() throws Exception {
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }
}
