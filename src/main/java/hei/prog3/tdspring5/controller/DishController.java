package hei.prog3.tdspring5.controller;

import hei.prog3.tdspring5.dto.DishCreationRequest;
import hei.prog3.tdspring5.entity.Dish;
import hei.prog3.tdspring5.repo.DishRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository dishRepository;

    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @GetMapping
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Dish> createDishes(@RequestBody List<DishCreationRequest> dishRequests) {
        try {
            // Vérification de l'existence des plats par leur nom
            for (DishCreationRequest request : dishRequests) {
                if (dishRepository.existsByName(request.getName())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Dish.name=" + request.getName() + " already exists"
                    );
                }
            }

            // Conversion des requêtes en objets Dish
            List<Dish> dishesToSave = dishRequests.stream()
                    .map(request -> new Dish(null, request.getName(), request.getSellingPrice(), null))
                    .collect(Collectors.toList());

            // Sauvegarde des plats et retour des objets persistés
            return dishRepository.saveAll(dishesToSave);

        } catch (ResponseStatusException e) {
            throw e; // Garde les exceptions prévues telles qu'elles
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Une erreur est survenue lors de la création des plats",
                    e
            );
        }
    }



}