package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.FilmController;
import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.entities.Film;
import fr.semifir.apicinema.entities.Seance;
import fr.semifir.apicinema.services.FilmService;
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

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService service;

    @Test
    public void testFindAllFilm() throws Exception{
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk()) // On check si le code retour et 200
                .andExpect(jsonPath("$").isEmpty()); // On verifie si le tableau est vide
    }

    @Test
    public void testFindOneFilmWhereWrongIdOrInexistant() throws Exception {
        // On execute une request sur /films/1
        // grâce à la methode "perfom" de mockMvc
        this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isNotFound()); // On vérifie si le status est bien une 404
    }

    @Test
    public void testFindOneFilm() throws Exception {
        FilmDTO filmDTO = this.filmDTO();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(filmDTO));
        MvcResult result = this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        FilmDTO body = json.fromJson(
                result.getResponse().getContentAsString(),
                FilmDTO.class
        );
        Assertions.assertEquals(body.getNom(), this.filmDTO().getNom());
        Assertions.assertEquals(body.getId(), this.filmDTO().getId());
    }

    @Test
    public void testSaveFilm() throws Exception {
        FilmDTO filmDTO = this.filmDTO();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(filmDTO);
        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateFilm() throws Exception {
        FilmDTO filmDTO = this.filmDTO();
        FilmDTO filmDTOUpdate = this.filmDTOUpdate();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(filmDTO));
        MvcResult result = this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        FilmDTO body = json.fromJson(result.getResponse().getContentAsString(), FilmDTO.class);
        BDDMockito.when(service.save(any(Film.class)))
                .thenReturn(filmDTOUpdate);
        body.setNom("Majestic");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                        .andExpect(status().isOk())
                        .andReturn();
        FilmDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), FilmDTO.class);
        Assertions.assertEquals(finalBody.getNom(), this.filmDTOUpdate().getNom());
    }

    @Test
    public void testDeleteFilm() throws Exception {
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(this.filmDTO());
        this.mockMvc.perform(delete("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isOk());
    }

    private FilmDTO filmDTO() {
        return new FilmDTO(
                "1",
                "Spiderman",
                2.34F,
                new Seance()
        );
    }
    private FilmDTO filmDTOUpdate() {
        return new FilmDTO(
                "1",
                "TouloulouFilm",
                2.34F,
                new Seance()
        );
    }
}
