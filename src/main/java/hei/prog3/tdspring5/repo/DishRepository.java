package hei.prog3.tdspring5.repo;

import hei.prog3.tdspring5.entity.CategoryEnum;
import hei.prog3.tdspring5.entity.Dish;
import hei.prog3.tdspring5.entity.Ingredient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DishRepository {

    private DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Dish> findAll() {
        String sql = """
                SELECT d.id AS dish_id, 
                       d.name AS dish_name, 
                       d.selling_price, 
                       i.id AS ingredient_id, 
                       i.name AS ingredient_name, 
                       i.category AS ingredient_category, 
                       i.price AS ingredient_price
                FROM dish d
                LEFT JOIN dish_ingredient di ON di.id_dish = d.id
                LEFT JOIN ingredient i ON i.id = di.id_ingredient
                ORDER BY d.id, i.id
                """;

        Map<Integer, Dish> dishMap = new LinkedHashMap<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Récupérer les informations sur le plat
                int dishId = resultSet.getInt("dish_id");
                Dish dish = dishMap.computeIfAbsent(dishId, id -> {
                    Dish newDish = new Dish();
                    newDish.setId(id);
                    try {
                        newDish.setName(resultSet.getString("dish_name"));
                        newDish.setSellingPrice(resultSet.getDouble("selling_price"));
                        newDish.setIngredients(new ArrayList<>());
                    } catch (SQLException e) {
                        throw new RuntimeException("Erreur lors de la récupération des données du plat", e);
                    }
                    return newDish;
                });

                // Récupérer les informations sur les ingrédients, si disponibles
                int ingredientId = resultSet.getInt("ingredient_id");
                if (!resultSet.wasNull()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(ingredientId);
                    ingredient.setName(resultSet.getString("ingredient_name"));
                    ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("ingredient_category")));
                    ingredient.setPrice(resultSet.getDouble("ingredient_price"));
                    dish.getIngredients().add(ingredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SQL", e);
        }

        return new ArrayList<>(dishMap.values());
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM dish WHERE name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de l'existence d'un plat", e);
        }

        return false;
    }

    public Dish save(Dish dish) {
        String sql = "INSERT INTO dish (name, selling_price) VALUES (?, ?) RETURNING id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, dish.getName());
            statement.setDouble(2, dish.getSellingPrice());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dish.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion d'un plat dans la base", e);
        }

        return dish;
    }

    public List<Dish> saveAll(List<Dish> dishes) {
        List<Dish> savedDishes = new ArrayList<>();

        for (Dish dish : dishes) {
            savedDishes.add(this.save(dish));
        }

        return savedDishes;
    }

}