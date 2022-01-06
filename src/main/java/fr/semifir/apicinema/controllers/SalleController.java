package fr.semifir.apicinema.controllers;

import fr.semifir.apicinema.dtos.salle.SalleDTO;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.exceptions.NotFoundException;
import fr.semifir.apicinema.services.SalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("salles")
public class SalleController {

    @Autowired
    SalleService service;

    @GetMapping
    public List<SalleDTO> findAll() {
        return this.service.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<SalleDTO> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(this.service.findByID(id).get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @PostMapping
    public ResponseEntity<SalleDTO> save(@RequestBody Salle salle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(salle));
    }

    @PutMapping
    public SalleDTO update(@RequestBody Salle salle) {
        return this.service.save(salle);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody Salle salle) {
        this.service.delete(salle);
        return ResponseEntity.ok(true);
    }
}
