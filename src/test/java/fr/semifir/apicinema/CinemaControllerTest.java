package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.CinemaController;
import fr.semifir.apicinema.dtos.cinema.CinemaDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.services.CinemaService;
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

@WebMvcTest(controllers = CinemaController.class)
public class CinemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CinemaService service;

    /**
     * On va tester la route qui permet de récuperer un tableau
     * de collaborateurs
     * @throws Exception
     */
    @Test
    public void testFindAllCinemas() throws Exception{
        this.mockMvc.perform(get("/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    /**
     * On vérifie si la route qui permet de retrouver un collabo
     * renvoi bien un 404 si on ne trouve pas de collabo
     * @throws Exception
     */
    @Test
    public void testFindOneCinemaWhereWrongIdOrInexistant() throws Exception {
        this.mockMvc.perform(get("/cinemas/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * On vérifie si la route qui permet de retrouver un collabo
     * renvoi bien un collabo
     * @throws Exception
     */
    @Test
    public void testFindOneCinema() throws Exception {
        CinemaDTO employeDTO = this.cinemaDTO();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(employeDTO));
        MvcResult result = this.mockMvc.perform(get("/cinemas/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CinemaDTO body = json.fromJson(
                result.getResponse().getContentAsString(),
                CinemaDTO.class
        );
        Assertions.assertEquals(body.getNom(), this.cinemaDTO().getNom());
        Assertions.assertEquals(body.getId(), this.cinemaDTO().getId());
    }

    @Test
    public void testSaveCinema() throws Exception {
        CinemaDTO employeDTO = this.cinemaDTO();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(employeDTO);
        this.mockMvc.perform(post("/cinemas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateCinema() throws Exception {
        CinemaDTO employeDTO = this.cinemaDTO();
        CinemaDTO employeDTOUpdated = this.cinemaDTOUpdate();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(employeDTO));
        MvcResult result = this.mockMvc.perform(get("/cinemas/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        CinemaDTO body = json.fromJson(result.getResponse().getContentAsString(), CinemaDTO.class);
        BDDMockito.when(service.save(any(Cinema.class)))
                .thenReturn(employeDTOUpdated); // On retourne un employeDTOUpdated
        body.setNom("Majestic");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/cinemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                        .andExpect(status().isOk())
                        .andReturn();
        CinemaDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), CinemaDTO.class);
        Assertions.assertEquals(finalBody.getNom(), this.cinemaDTOUpdate().getNom());
    }

    @Test
    public void testDeleteCinema() throws Exception {
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(this.cinemaDTO());
        this.mockMvc.perform(delete("/cinemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isOk());
    }

    private CinemaDTO cinemaDTO() {
        return new CinemaDTO(
                "1",
                "UGC"
        );
    }
    private CinemaDTO cinemaDTOUpdate() {
        return new CinemaDTO(
                "1",
                "Majestic"
        );
    }
}
