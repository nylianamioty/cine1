package com.cine.cinema.web;

import com.cine.cinema.model.Film;
import com.cine.cinema.repository.FilmRepository;
import com.cine.cinema.repository.SeanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmRepository filmRepository;
    private final SeanceRepository seanceRepository;

    public FilmController(FilmRepository filmRepository, SeanceRepository seanceRepository) {
        this.filmRepository = filmRepository;
        this.seanceRepository = seanceRepository;
    }

    @GetMapping("/{id}")
    public String showFilm(@PathVariable Long id, Model model) {
        Film film = filmRepository.findById(id).orElse(null);
        if (film == null) {
            log.info("Film with id {} not found", id);
            return "index";
        }
        // use native query to be explicit: SELECT * FROM seance WHERE idfilm = :id
        var seances = seanceRepository.findByFilmIdNative(id);
        log.info("Rendering film {} (id={}), seances count={} (native)", film.getTitre(), id, seances == null ? 0 : seances.size());
        model.addAttribute("film", film);
        model.addAttribute("seances", seances);
        return "film";
    }

    @org.springframework.web.bind.annotation.GetMapping("/{id}/seances.json")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.List<com.cine.cinema.model.Seance> seancesJson(@PathVariable Long id) {
        return seanceRepository.findByFilmIdNative(id);
    }
}
