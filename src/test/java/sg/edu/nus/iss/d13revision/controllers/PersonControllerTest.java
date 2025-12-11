package sg.edu.nus.iss.d13revision.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sg.edu.nus.iss.d13revision.models.Person;
import sg.edu.nus.iss.d13revision.services.PersonService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private List<Person> personList;

    @BeforeEach
    void setUp() {
        personList = new ArrayList<>();
        personList.add(new Person("John", "Doe"));
        personList.add(new Person("Jane", "Doe"));
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/person/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    public void testGetAllPersons() throws Exception {
        when(personService.getPersons()).thenReturn(personList);
        mockMvc.perform(get("/person/testRetrieve"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    public void testPersonList() throws Exception {
        when(personService.getPersons()).thenReturn(personList);
        mockMvc.perform(get("/person/personList"))
                .andExpect(status().isOk())
                .andExpect(view().name("personList"))
                .andExpect(model().attribute("persons", personList));
    }

    @Test
    public void testShowAddPersonPage() throws Exception {
        mockMvc.perform(get("/person/addPerson"))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("personForm"));
    }

    @Test
    public void testSavePersonSuccess() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                        .param("firstName", "Peter")
                        .param("lastName", "Jones"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));
    }

    @Test
    public void testSavePersonFailure() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                        .param("firstName", "")
                        .param("lastName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void testPersonToEdit() throws Exception {
        Person person = new Person("123", "Test", "User");
        mockMvc.perform(post("/person/personToEdit")
                        .flashAttr("per", person))
                .andExpect(status().isOk())
                .andExpect(view().name("editPerson"))
                .andExpect(model().attribute("per", person));
    }

    @Test
    public void testPersonEdit() throws Exception {
        mockMvc.perform(post("/person/personEdit")
                        .param("id", "123")
                        .param("firstName", "Updated")
                        .param("lastName", "User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));
    }

    @Test
    public void testPersonDelete() throws Exception {
        mockMvc.perform(post("/person/personDelete")
                        .param("id", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));
    }
}
