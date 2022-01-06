package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.SeanceController;
import fr.semifir.apicinema.dtos.seance.SeanceDTO;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.entities.Seance;
import fr.semifir.apicinema.services.SeanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SeanceController.class)
public class SeanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeanceService service;

    @Test
    public void testFindAllSalle() throws Exception{
        this.mockMvc.perform(get("/seances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindOneSalleWhereWrongIdOrInexistant() throws Exception {
        this.mockMvc.perform(get("/seances/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindOneSalle() throws Exception {
        SeanceDTO salleDTO = this.seanceDTO();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(salleDTO));
        MvcResult result = this.mockMvc.perform(get("/seances/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SeanceDTO body = json.fromJson(
                result.getResponse().getContentAsString(),
                SeanceDTO.class
        );
        Assertions.assertEquals(body.getId(), this.seanceDTO().getId());
    }

    @Test
    public void testSaveSalle() throws Exception {
        SeanceDTO salleDTO = this.seanceDTO();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(salleDTO);
        this.mockMvc.perform(post("/seances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateSalle() throws Exception {
        SeanceDTO seanceDTO = this.seanceDTO();
        SeanceDTO seanceDTOUpdate = this.seanceDTOUpdate();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(seanceDTO));
        MvcResult result = this.mockMvc.perform(get("/seances/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        SeanceDTO body = json.fromJson(result.getResponse().getContentAsString(), SeanceDTO.class);
        BDDMockito.when(service.save(any(Seance.class)))
                .thenReturn(seanceDTOUpdate);
        body.setId("2");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/seances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                        .andExpect(status().isOk())
                        .andReturn();
        SeanceDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), SeanceDTO.class);
        Assertions.assertEquals(finalBody.getId(), this.seanceDTOUpdate().getId());
    }

    @Test
    public void testDeleteSalle() throws Exception {
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(this.seanceDTO());
        this.mockMvc.perform(delete("/seances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isOk());
    }

    private SeanceDTO seanceDTO() {
        return new SeanceDTO(
                "1",
                new Date(),
                new Salle()
        );
    }
    private SeanceDTO seanceDTOUpdate() {
        return new SeanceDTO(
                "2",
                new Date(),
                new Salle()
        );
    }
}
