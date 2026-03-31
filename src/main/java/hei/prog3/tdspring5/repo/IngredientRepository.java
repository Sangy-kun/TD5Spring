package hei.prog3.tdspring5.repo;

import hei.prog3.tdspring5.entity.CategoryEnum;
import hei.prog3.tdspring5.entity.Ingredient;
import hei.prog3.tdspring5.entity.StockValue;
import hei.prog3.tdspring5.entity.Unit;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> findAll() {
        String sql = "SELECT id, name, category, price FROM ingredient ORDER BY id";
        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ingredients.add(mapIngredient(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }

        return ingredients;
    }

    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT id, name, category, price FROM ingredient WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapIngredient(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'ingrédient avec id=" + id, e);
        }

        return Optional.empty();
    }

    public Optional<StockValue> getStockValueAt(Integer ingredientId, Instant at, Unit unit) {
        String sql = """
                SELECT COALESCE(SUM(CASE WHEN type = 'OUT' THEN quantity * -1 ELSE quantity END), 0) AS current_quantity
                FROM stock_movement
                WHERE id_ingredient = ?
                  AND creation_datetime <= ?
                  AND unit = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ingredientId);
            statement.setTimestamp(2, Timestamp.from(at));
            statement.setString(3, unit.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double quantity = resultSet.getDouble("current_quantity");
                    return Optional.of(new StockValue(unit, quantity));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du stock de l'ingrédient id=" + ingredientId, e);
        }

        return Optional.empty();
    }

    private Ingredient mapIngredient(ResultSet resultSet) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(resultSet.getInt("id"));
        ingredient.setName(resultSet.getString("name"));
        ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));
        ingredient.setPrice(resultSet.getDouble("price"));
        return ingredient;
    }
}