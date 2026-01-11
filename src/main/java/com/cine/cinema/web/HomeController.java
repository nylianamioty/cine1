package com.cine.cinema.web;

import com.cine.cinema.repository.FilmRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final FilmRepository filmRepository;

    public HomeController(FilmRepository filmRepository) { this.filmRepository = filmRepository; }

    @GetMapping({"/", "/films"})
    public String index(Model model) {
        model.addAttribute("films", filmRepository.findAll());
        return "index";
    }
}
