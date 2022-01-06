package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.SalleController;
import fr.semifir.apicinema.dtos.salle.SalleDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.services.SalleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SalleController.class)
public class SalleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalleService service;

    @Test
    public void testFindAllSalle() throws Exception{
        this.mockMvc.perform(get("/salles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindOneSalleWhereWrongIdOrInexistant() throws Exception {
        this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindOneSalle() throws Exception {
        SalleDTO salleDTO = this.salleDTO();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(salleDTO));
        MvcResult result = this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SalleDTO body = json.fromJson(
                result.getResponse().getContentAsString(),
                SalleDTO.class
        );
        Assertions.assertEquals(body.getNumDeSalle(), this.salleDTO().getNumDeSalle());
        Assertions.assertEquals(body.getId(), this.salleDTO().getId());
    }

    @Test
    public void testSaveSalle() throws Exception {
        SalleDTO salleDTO = this.salleDTO();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(salleDTO);
        this.mockMvc.perform(post("/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateSalle() throws Exception {
        SalleDTO salleDTO = this.salleDTO();
        SalleDTO salleDTOUpdate = this.salleDTOUpdate();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(salleDTO));
        MvcResult result = this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SalleDTO body = json.fromJson(result.getResponse().getContentAsString(), SalleDTO.class);
        BDDMockito.when(service.save(any(Salle.class)))
                .thenReturn(salleDTOUpdate);
        body.setNbrPlace(123);
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                        .andExpect(status().isOk())
                        .andReturn();
        SalleDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), SalleDTO.class);
        Assertions.assertEquals(finalBody.getNbrPlace(), this.salleDTOUpdate().getNbrPlace());
    }

    @Test
    public void testDeleteSalle() throws Exception {
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(this.salleDTO());
        this.mockMvc.perform(delete("/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isOk());
    }

    private SalleDTO salleDTO() {
        return new SalleDTO(
                "1",
                3443,
                2213,
                new Cinema()
        );
    }
    private SalleDTO salleDTOUpdate() {
        return new SalleDTO(
                "1",
                1234,
                123,
                new Cinema()
        );
    }
}
