package fr.semifir.apicinema.services;

import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.entities.Film;
import fr.semifir.apicinema.entities.Film;
import fr.semifir.apicinema.exceptions.NotFoundException;
import fr.semifir.apicinema.repositories.FilmRepository;
import fr.semifir.apicinema.repositories.SeanceRepository;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class FilmService {

    FilmRepository repository;
    ModelMapper mapper;

    public FilmService(
            FilmRepository repository,
            ModelMapper mapper
            ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retour une liste de Film
     * @return List<Film>
     */
    public List<FilmDTO> findAll() {
        List<FilmDTO> filmDTOS = new ArrayList<>();
        this.repository.findAll().forEach(film -> {
            FilmDTO filmDTO = mapper.map(film, FilmDTO.class);
            filmDTOS.add(filmDTO);
        });
        return filmDTOS;
    }

    /**
     * Je récupère un film selon son ID
     * @param id
     * @return
     */
    public Optional<FilmDTO> findByID(String id) throws NoSuchElementException {
        Optional<Film> film = this.repository.findById(id);
        return Optional.of(mapper.map(film.get(), FilmDTO.class));
    }

    /**
     * Save & update un film
     * @param film
     * @return
     */
    public FilmDTO save(Film film) {
        return mapper.map(this.repository.save(film), FilmDTO.class);
    }

    /**
     * Je supprime mon film
     * @param film
     */
    public void delete(Film film) {
        this.repository.delete(film);
    }
}
