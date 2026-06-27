package com.staynest.controller;

import com.staynest.dto.PropertySearchRequest;
import com.staynest.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PropertyService propertyService;

    /**
     * The landing page shows a handful of active listings as a teaser,
     * plus the search bar. We reuse propertyService.search() with an
     * empty PropertySearchRequest (no filters set), so the homepage and
     * the dedicated search results page share the exact same query
     * logic instead of two different code paths that could drift apart.
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        PropertySearchRequest emptySearch = new PropertySearchRequest();
        model.addAttribute("properties", propertyService.search(emptySearch).getContent());
        model.addAttribute("searchRequest", emptySearch);
        return "index";
    }
}
