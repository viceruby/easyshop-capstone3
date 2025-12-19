package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

// IGNORE: FOR C&P PURPOSES ONLY
// /* A */

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    /* GET ALL CATS */
    @Override
    public List<Category> getAllCategories()
    {
        String sql = "SELECT * FROM categories";
        try (var connection = getConnection();
             var statement = connection.prepareStatement(sql);
             var results = statement.executeQuery())
        {
            List<Category> categories = new java.util.ArrayList<>();
            while (results.next())
                {categories.add(mapRow(results));}
            return categories;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting categories.", e);
        }
    }

    /* GET CAT ID */
    @Override
    public Category getById(int categoryId)
    {
        String sql = "SELECT * FROM categories WHERE category_id = ?;";

        try (var connection = getConnection();
             var statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, categoryId);
            try (var results = statement.executeQuery())
            {
                    if (results.next())
                        return mapRow(results);
                    return null;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting category.", e);
        }
    }

    /* CREATE CAT */
    @Override
    public Category create(Category category)
    {
        String sql = """
                     INSERT INTO categories (name, description)
                     VALUES (?, ?);
                     """;

        try (var connection = getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys())
            {
                if (keys.next())
                {
                    int newId = keys.getInt(1);
                    return getById(newId);
                }
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error creating category", e);
        }
    }

    /* UPDATE CAT */
    @Override
    public void update(int categoryId, Category category)
    {
        String sql = """
                     UPDATE categories
                     SET name = ?, description = ?
                     WHERE category_id = ?;
                     """;

        try (var connection = getConnection();
             var statement = connection.prepareStatement(sql))
        {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    /* DELETE CAT */
    @Override
    public void delete(int categoryId)
    {
        String sql = "DELETE FROM categories WHERE category_id = ?;";

        try (var connection = getConnection();
             var statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, categoryId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
