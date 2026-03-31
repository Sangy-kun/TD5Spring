package hei.prog3.tdspring5.controller;


import hei.prog3.tdspring5.entity.Ingredient;
import hei.prog3.tdspring5.entity.StockValue;
import hei.prog3.tdspring5.entity.Unit;
import hei.prog3.tdspring5.repo.IngredientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    public IngredientController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Ingredient getIngredientById(@PathVariable Integer id) {
        return ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient.id=" + id + " is not found"));
    }

    @GetMapping("/{id}/stock")
    public StockValue getIngredientStockValue(
            @PathVariable Integer id,
            @RequestParam(required = false) Instant at,
            @RequestParam(required = false) Unit unit
    ) {
        if (at == null || unit == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Either mandatory query parameter 'at' or 'unit' is not provided.");
        }

        ingredientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient.id=" + id + " is not found"));

        return ingredientRepository.getStockValueAt(id, at, unit).orElse(new StockValue(unit, 0));
    }
}