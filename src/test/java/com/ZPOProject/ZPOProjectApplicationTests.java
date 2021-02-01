package com.ZPOProject;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ZPOProject.models.Project;
import com.ZPOProject.service.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin")
public class ZPOProjectApplicationTests {
    private final String apiPath = "/api/projects";
    @MockBean
    private ProjectService mockProjectService;
    @Autowired
    private MockMvc mockMvc;
    private JacksonTester<Project> jacksonTester;

    @Test
    public void getProjects() throws Exception {
        Project projekt = new Project(1, "Nazwa1", "Opis1", LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        Page<Project> page = new PageImpl<>(Collections.singletonList(projekt));
        when(mockProjectService.getProjects(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(apiPath).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]").exists())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].projectId").value(projekt.getProjectId()))
                .andExpect(jsonPath("$.content[0].name").value(projekt.getName()));

        verify(mockProjectService, times(1)).getProjects(any(Pageable.class));
        verifyNoMoreInteractions(mockProjectService);
    }

    @Test
    public void getProject() throws Exception {
        Project projekt = new Project(2, "Nazwa2", "Opis2", LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        when(mockProjectService.getProject(projekt.getProjectId())).thenReturn(Optional.of(projekt));

        mockMvc.perform(get(apiPath + "/{projectId}", projekt.getProjectId()).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(projekt.getProjectId()))
                .andExpect(jsonPath("$.name").value(projekt.getName()));
        verify(mockProjectService, times(1)).getProject(projekt.getProjectId());
        verifyNoMoreInteractions(mockProjectService);
    }

    @Test
    public void createProject() throws Exception {
        Project projekt = new Project(null, "Nazwa3", "Opis3", null, LocalDate.of(2020, 6, 7));
        String jsonProjekt = jacksonTester.write(projekt).getJson();
        projekt.setProjectId(3);
        when(mockProjectService.setProject(any(Project.class))).thenReturn(projekt);
        mockMvc.perform(post(apiPath).content(jsonProjekt).contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString(apiPath + "/" + projekt.getProjectId())));
    }

    @Test
    public void createProjectEmptyName() throws Exception {
        Project projekt = new Project(null, "", "Opis4", null, LocalDate.of(2020, 6, 7));
        MvcResult result = mockMvc.perform(post(apiPath)
                .content(jacksonTester.write(projekt).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(mockProjectService, times(0)).setProject(any(Project.class));
        Exception exception = result.getResolvedException();
        assertNotNull(exception);
        assertTrue(exception instanceof MethodArgumentNotValidException);
        System.out.println(exception.getMessage());
    }

    @Test
    public void updateProjekt() throws Exception {
        Project projekt = new Project(5, "Nazwa5", "Opis5", LocalDateTime.now(), LocalDate.of(2020, 6, 7));
        String jsonProjekt = jacksonTester.write(projekt).getJson();
        when(mockProjectService.getProject(projekt.getProjectId())).thenReturn(Optional.of(projekt));
        when(mockProjectService.setProject(any(Project.class))).thenReturn(projekt);
        mockMvc.perform(put(apiPath + "/{projectId}", projekt.getProjectId()).content(jsonProjekt).contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mockProjectService, times(1)).getProject(projekt.getProjectId());
        verify(mockProjectService, times(1)).setProject(any(Project.class));
        verifyNoMoreInteractions(mockProjectService);
    }

    /*** Test sprawdza czy żądanie o danych parametrach stronicowania i sortowania* spowoduje przekazanie do serwisu odpowiedniego obiektu Pageable, wcześniej* wstrzykniętego do parametru wejściowego metody kontrolera*/
    @Test
    public void getProjectsAndVerifyPageableParams() throws Exception {
        Integer page = 5;
        Integer size = 15;
        String sortProperty = "name";
        String sortDirection = "desc";
        mockMvc.perform(get(apiPath).param("page", page.toString()).param("size", size.toString()).param("sort", String.format("%s,%s", sortProperty, sortDirection)))
                .andExpect(status().isOk());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(mockProjectService, times(1)).getProjects(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();
        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());
        assertEquals(sortProperty, pageable.getSort().getOrderFor(sortProperty).getProperty());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor(sortProperty).getDirection());
    }

    @BeforeEach
    public void before(TestInfo testInfo) {
        System.out.printf("--METODA -> %s%n", testInfo.getTestMethod().get().getName());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, mapper);
    }

    @AfterEach
    public void after(TestInfo testInfo) {
        System.out.printf("<-KONIEC --%s%n", testInfo.getTestMethod().get().getName());
    }
}