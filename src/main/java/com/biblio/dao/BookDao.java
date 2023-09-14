package com.biblio.dao;

import com.biblio.app.Enums.Language;
import com.biblio.app.Enums.LostStatus;
import com.biblio.app.Models.*;
import com.biblio.libs.Model;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public final class BookDao extends Model {

    private Book book = null;

    public BookDao() {
        super("books", new String[]{"isbn"});
        this.book = new Book();
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try {
            String query = "SELECT DISTINCT" +
                    "       b.isbn," +
                    "       b.quantities," +
                    "       b.pages," +
                    "       b.title," +
                    "       b.edition," +
                    "       b.language," +
                    "       b.description," +
                    "       a.id AS author_id," +
                    "       a.first_name AS author_firstName," +
                    "       a.last_name AS author_lastName," +
                    "       c.category," +
                    "       c.description AS category_description " +
                    "FROM books b " +
                    "LEFT JOIN books_authors ba ON b.isbn = ba.book " +
                    "LEFT JOIN authors a ON ba.author = a.id " +
                    "LEFT JOIN categories_books cb ON b.isbn = cb.book " +
                    "LEFT JOIN categories c ON cb.category = c.id " +
                    "WHERE b.delete_at IS NULL " +
                    "ORDER BY b.isbn;";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            executeAndWrapQuery(books, preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();

        try {
            String query = "SELECT DISTINCT" +
                    "       b.isbn," +
                    "       b.quantities," +
                    "       b.pages," +
                    "       b.title," +
                    "       b.edition," +
                    "       b.language," +
                    "       b.description," +
                    "       a.id AS author_id," +
                    "       a.first_name AS author_firstName," +
                    "       a.last_name AS author_lastName," +
                    "       c.category," +
                    "       c.description AS category_description " +
                    "FROM books b " +
                    "LEFT JOIN books_authors ba ON b.isbn = ba.book " +
                    "LEFT JOIN authors a ON ba.author = a.id " +
                    "LEFT JOIN categories_books cb ON b.isbn = cb.book " +
                    "LEFT JOIN categories c ON cb.category = c.id " +
                    "LEFT JOIN borrowed_books bb ON b.isbn = bb.book " +
                    "LEFT JOIN lost_books lb ON b.isbn = lb.book " +
                    "WHERE b.delete_at IS NULL " +
                    "AND (" +
                    "    (bb.return_date IS NULL AND lb.actual_statut IS NULL) OR " +
                    "    (bb.return_date IS NULL AND lb.actual_statut = 'still_lost')" +
                    ") " +
                    "GROUP BY b.isbn, b.quantities " +
                    "HAVING SUM(CASE WHEN bb.return_date IS NULL THEN 1 ELSE 0 END) < b.quantities " +
                    "ORDER BY b.isbn;";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            executeAndWrapQuery(availableBooks, preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableBooks;
    }

    public List<Book> getLostBooks() {
        List<Book> lostBooks = new ArrayList<>();

        try {
            String query = "SELECT DISTINCT" +
                    "       b.isbn," +
                    "       b.quantities," +
                    "       b.pages," +
                    "       b.title," +
                    "       b.edition," +
                    "       b.language," +
                    "       b.description," +
                    "       a.id AS author_id," +
                    "       a.first_name AS author_firstName," +
                    "       a.last_name AS author_lastName," +
                    "       c.category," +
                    "       c.description AS category_description " +
                    "FROM books b " +
                    "LEFT JOIN books_authors ba ON b.isbn = ba.book " +
                    "LEFT JOIN authors a ON ba.author = a.id " +
                    "LEFT JOIN categories_books cb ON b.isbn = cb.book " +
                    "LEFT JOIN categories c ON cb.category = c.id " +
                    "INNER JOIN lost_books lb ON b.isbn = lb.book " +
                    "WHERE b.delete_at IS NULL " +
                    "AND lb.actual_statut = " + LostStatus.STILL_LOST + " " +
                    "ORDER BY b.isbn;";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            executeAndWrapQuery(lostBooks, preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lostBooks;
    }

    public List<Book> search(String keywords) {
        List<Book> books = new ArrayList<>();

        try {
            String query = "SELECT DISTINCT" +
                    "       b.isbn," +
                    "       b.quantities," +
                    "       b.pages," +
                    "       b.title," +
                    "       b.edition," +
                    "       b.language," +
                    "       b.description," +
                    "       a.id AS author_id," +
                    "       a.first_name AS author_firstName," +
                    "       a.last_name AS author_lastName," +
                    "       c.category," +
                    "       c.description AS category_description " +
                    "FROM books b " +
                    "LEFT JOIN books_authors ba ON b.isbn = ba.book " +
                    "LEFT JOIN authors a ON ba.author = a.id " +
                    "LEFT JOIN categories_books cb ON b.isbn = cb.book " +
                    "LEFT JOIN categories c ON cb.category = c.id " +
                    "WHERE b.delete_at IS NULL " +
                    "AND (b.isbn LIKE ? OR b.title LIKE ? OR b.description LIKE ? OR a.first_name LIKE ? OR a.last_name LIKE ? OR c.category LIKE ?) " +
                    "ORDER BY b.isbn;";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            preparedStatement.setString(1, keywords);
            preparedStatement.setString(2, "%" + keywords + "%");
            preparedStatement.setString(3, "%" + keywords + "%");
            preparedStatement.setString(4, "%" + keywords + "%");
            preparedStatement.setString(5, "%" + keywords + "%");
            preparedStatement.setString(6, "%" + keywords + "%");

            executeAndWrapQuery(books, preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public List<Book> searchInAvailableBooks(String keywords) {
        List<Book> availableBooks = new ArrayList<>();

        try {
            String query = "SELECT DISTINCT" +
                    "       b.isbn," +
                    "       b.quantities," +
                    "       b.pages," +
                    "       b.title," +
                    "       b.edition," +
                    "       b.language," +
                    "       b.description," +
                    "       a.id AS author_id," +
                    "       a.first_name AS author_firstName," +
                    "       a.last_name AS author_lastName," +
                    "       c.category," +
                    "       c.description AS category_description " +
                    "FROM books b " +
                    "LEFT JOIN books_authors ba ON b.isbn = ba.book " +
                    "LEFT JOIN authors a ON ba.author = a.id " +
                    "LEFT JOIN categories_books cb ON b.isbn = cb.book " +
                    "LEFT JOIN categories c ON cb.category = c.id " +
                    "LEFT JOIN borrowed_books bb ON b.isbn = bb.book " +
                    "LEFT JOIN lost_books lb ON b.isbn = lb.book " +
                    "WHERE b.delete_at IS NULL " +
                    "AND (b.isbn LIKE ? OR b.title LIKE ? OR b.description LIKE ? OR a.first_name LIKE ? OR a.last_name LIKE ? OR c.category LIKE ?) " +
                    "AND (" +
                    "    (bb.return_date IS NULL AND lb.actual_statut IS NULL) OR " +
                    "    (bb.return_date IS NULL AND lb.actual_statut = 'still_lost')" +
                    ") " +
                    "ORDER BY b.isbn;";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            preparedStatement.setString(1, keywords);
            preparedStatement.setString(2, "%" + keywords + "%");
            preparedStatement.setString(3, "%" + keywords + "%");
            preparedStatement.setString(4, "%" + keywords + "%");
            preparedStatement.setString(5, "%" + keywords + "%");
            preparedStatement.setString(6, "%" + keywords + "%");

            executeAndWrapQuery(availableBooks, preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableBooks;
    }

    public Book insert(String isbn, String title, String description, Language lang, int quantity, int pages, String edition, String[] authorIds, String[] categoryIds) throws SQLException {
        this.beginTransaction();
        try {
            this.book.setBook(
                    isbn,
                    quantity,
                    pages,
                    title,
                    edition,
                    Language.valueOf(String.valueOf(lang)),
                    description
            );

            Map<String, String> bookData = this.book.getBook();
            String bookId = super.create(bookData);

            if (bookId != null) {
                if (insertAuthorsForBook(bookId, authorIds)) {
                    this.rollbackTransaction();
                    return null;
                }

                if (insertCategoriesForBook(bookId, categoryIds)) {
                    this.rollbackTransaction();
                    return null;
                }
            } else {
                this.rollbackTransaction();
                return null;
            }

            this.commitTransaction();
            return getBookWithDetails(isbn);

        } catch (Exception e) {
            this.rollbackTransaction();
            e.printStackTrace();
            return null;
        }
    }

    public Book update(String isbn, String title, String description, Language lang, int quantity, int pages, String edition, String[] authorIds, String[] categoryIds) throws SQLException {
        this.beginTransaction();
        try {
            this.book.setBook(
                    isbn,
                    quantity,
                    pages,
                    title,
                    edition,
                    Language.valueOf(String.valueOf(lang)),
                    description
            );

            boolean updated = super.update(this.book.getBook(), new String[]{isbn});

            if (updated) {
                if (!deleteAuthorsForBook(isbn)) {
                    this.rollbackTransaction();
                    return null;
                }

                if (insertAuthorsForBook(isbn, authorIds)) {
                    this.rollbackTransaction();
                    return null;
                }

                if (!deleteCategoriesForBook(isbn)) {
                    this.rollbackTransaction();
                    return null;
                }

                if (insertCategoriesForBook(isbn, categoryIds)) {
                    this.rollbackTransaction();
                    return null;
                }
            } else {
                this.rollbackTransaction();
                return null;
            }

            this.commitTransaction();
            return getBookWithDetails(isbn);

        } catch (Exception e) {
            this.rollbackTransaction();
            e.printStackTrace();
            return null;
        }
    }

    public boolean delete(String isbn) {
        return super.softDelete(new String[]{isbn});
    }

    public Book find(String isbn) {
        Book book = null;

        try {
            String query = "SELECT DISTINCT" +
                    "       b.isbn," +
                    "       b.quantities," +
                    "       b.pages," +
                    "       b.title," +
                    "       b.edition," +
                    "       b.language," +
                    "       b.description," +
                    "       a.id AS author_id," +
                    "       a.first_name AS author_firstName," +
                    "       a.last_name AS author_lastName," +
                    "       c.category," +
                    "       c.description AS category_description " +
                    "FROM books b " +
                    "LEFT JOIN books_authors ba ON b.isbn = ba.book " +
                    "LEFT JOIN authors a ON ba.author = a.id " +
                    "LEFT JOIN categories_books cb ON b.isbn = cb.book " +
                    "LEFT JOIN categories c ON cb.category = c.id " +
                    "WHERE b.delete_at IS NULL " +
                    "AND b.isbn LIKE ? " +
                    "LIMIT 1";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            preparedStatement.setString(1, isbn);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                book = new Book();

                book.setBook(
                        resultSet.getString("isbn"),
                        resultSet.getInt("quantities"),
                        resultSet.getInt("pages"),
                        resultSet.getString("title"),
                        resultSet.getString("edition"),
                        Language.valueOf(resultSet.getString("language")),
                        resultSet.getString("description")
                );

                List<Author> authors = new ArrayList<>();
                List<Category> categories = new ArrayList<>();

                do {
                    int authorId = resultSet.getInt("author_id");
                    String authorFirstName = resultSet.getString("author_firstName");
                    String authorLastName = resultSet.getString("author_lastName");

                    if (authorId != 0 && authorFirstName != null && authorLastName != null) {
                        Author author = new Author();
                        author.setAuthor(
                                authorId,
                                authorFirstName,
                                authorLastName
                        );
                        authors.add(author);
                    }

                    String categoryStr = resultSet.getString("category");
                    String categoryDescription = resultSet.getString("category_description");

                    if (categoryStr != null) {
                        Category category = new Category();
                        category.setCategory(
                                categoryStr,
                                categoryDescription
                        );
                        categories.add(category);
                    }
                } while (resultSet.next());

                book.hasAuthors(authors);
                book.hasCategories(categories);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return book;
    }

    public boolean existsBookQuantity(String isbn) {
        try {
            String query = "SELECT DISTINCT COUNT(*), b.quantities " +
                    "FROM `books` b " +
                    "LEFT JOIN `borrowed_books` bb ON b.isbn = bb.book AND bb.return_date IS NULL " +
                    "LEFT JOIN `lost_books` lb ON b.isbn = lb.book " +
                    "WHERE b.isbn = ? AND (" +
                    "    (bb.return_date IS NULL AND lb.actual_statut IS NULL) OR " +
                    "    (bb.return_date IS NULL AND lb.actual_statut = 'still_lost')" +
                    ");";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            preparedStatement.setString(1, isbn);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int totalQuantity = resultSet.getInt(2);
                int availableQuantity = resultSet.getInt(1);

                return availableQuantity > 0 && availableQuantity < totalQuantity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void executeAndWrapQuery(List<Book> books, PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();

        String currentIsbn = null;
        Book currentBook = null;
        List<Author> authors = new ArrayList<>();
        List<Category> categories = new ArrayList<>();

        while (resultSet.next()) {
            String isbn = resultSet.getString("isbn");

            if (currentIsbn == null || !currentIsbn.equals(isbn)) {

                if (currentBook != null) {
                    currentBook.hasAuthors(authors);
                    currentBook.hasCategories(categories);
                    books.add(currentBook);
                }

                currentBook = new Book();
                currentBook.setBook(
                        isbn,
                        resultSet.getInt("quantities"),
                        resultSet.getInt("pages"),
                        resultSet.getString("title"),
                        resultSet.getString("edition"),
                        Language.valueOf(resultSet.getString("language")),
                        resultSet.getString("description")
                );

                authors = new ArrayList<>();
                categories = new ArrayList<>();
                currentIsbn = isbn;
            }

            int authorId = resultSet.getInt("author_id");
            String authorFirstName = resultSet.getString("author_firstName");
            String authorLastName = resultSet.getString("author_lastName");

            if (authorId != 0 && authorFirstName != null && authorLastName != null) {
                Author author = new Author();
                author.setAuthor(
                        authorId,
                        authorFirstName,
                        authorLastName
                );
                if (!authors.contains(author)) {
                    authors.add(author);
                }
            }

            String categoryStr = resultSet.getString("category");
            String categoryDescription = resultSet.getString("category_description");

            if (categoryStr != null) {
                Category category = new Category();
                category.setCategory(
                        categoryStr,
                        categoryDescription
                );
                if (!categories.contains(category)) {
                    categories.add(category);
                }
            }
        }

        if (currentBook != null) {
            currentBook.hasAuthors(authors);
            currentBook.hasCategories(categories);
            books.add(currentBook);
        }
    }

    public Book getBookWithDetails(String isbn) {
        boolean flag = true;
        try {
            String query = "SELECT b.isbn, b.quantities, b.pages, b.title, b.edition, b.language, b.description,a.id, a.first_name, a.last_name, c.category, c.description c_description " +
                    "FROM books b " +
                    "LEFT JOIN books_authors ba ON b.isbn = ba.book " +
                    "LEFT JOIN authors a ON ba.author = a.id " +
                    "LEFT JOIN categories_books cb ON b.isbn = cb.book " +
                    "LEFT JOIN categories c ON cb.category = c.id " +
                    "WHERE b.isbn = ? AND b.delete_at IS NULL ";

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, this.book.getIsbn());

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Author> authors = new ArrayList<>();
            List<Category> categories = new ArrayList<>();

            while (resultSet.next()) {
                if (flag) {
                    this.book.setBook(
                            resultSet.getString("isbn"),
                            resultSet.getInt("quantities"),
                            resultSet.getInt("pages"),
                            resultSet.getString("title"),
                            resultSet.getString("edition"),
                            Language.valueOf(resultSet.getString("language")),
                            resultSet.getString("description")
                    );
                    flag = false;
                }

                int authorId = resultSet.getInt("id");
                String authorFirstName = resultSet.getString("first_name");
                String authorLastName = resultSet.getString("last_name");

                if (authorFirstName != null && authorLastName != null && authorId != 0) {
                    Author author = new Author();
                    author.setAuthor(
                            authorId,
                            authorFirstName,
                            authorLastName
                    );
                    authors.add(author);
                }

                int categoryId = resultSet.getInt("id");
                String categoryStr = resultSet.getString("category");
                String categoryDescription = resultSet.getString("c_description");

                if (categoryStr != null) {
                    Category category = new Category();
                    category.setCategory(
                            categoryId,
                            categoryStr,
                            categoryDescription
                    );
                    categories.add(category);
                }
            }

            this.book.hasAuthors(authors);
            this.book.hasCategories(categories);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.book;
    }

    public boolean save() throws SQLException {
        this.book.setIsbn(this.create(this.book.getBook()));
        return this.book.getIsbn() != null;
    }

    public Book read() {
        Map<String, String> Book = super.read(new String[]{this.book.getIsbn()});

        System.out.println(Book.toString());

        this.book.setBook(
                Book.get("isbn"),
                Integer.parseInt(Book.get("quantities")),
                Integer.parseInt(Book.get("pages")),
                Book.get("title"),
                Book.get("edition"),
                Language.valueOf(Book.get("language")),
                Book.get("description")
        );

        return this.book;
    }

    private boolean insertAuthorsForBook(String bookId, String[] authorIds) throws Exception {
        try (BooksAuthorsDao authorBookDao = new BooksAuthorsDao()) {
            for (String authorId : authorIds) {
                Map<String, String> authorBook = new HashMap<>();
                authorBook.put("author", String.valueOf(authorId));
                authorBook.put("book", bookId);
                if (authorBookDao.create(authorBook) == null) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean insertCategoriesForBook(String bookId, String[] categoryIds) throws Exception {
        try (CategorieBookDao categorieBookDao = new CategorieBookDao()) {
            for (String categoryId : categoryIds) {
                Map<String, String> categorieBookData = new HashMap<>();
                categorieBookData.put("category", String.valueOf(categoryId));
                categorieBookData.put("book", bookId);
                if (categorieBookDao.create(categorieBookData) == null) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean deleteAuthorsForBook(String isbn) {
        try (BooksAuthorsDao authorBookDao = new BooksAuthorsDao()) {
            return authorBookDao.delete(new String[]{isbn});
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteCategoriesForBook(String isbn) {
        try (CategorieBookDao categorieBookDao = new CategorieBookDao()) {
            return categorieBookDao.delete(new String[]{isbn});
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalBooks() {
        try {
            String query = "SELECT COUNT(*) FROM books WHERE delete_at IS NULL";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalLostBooks() {
        try {
            String query = "SELECT COUNT(*) FROM books WHERE delete_at IS NULL " +
                    "AND isbn IN (SELECT book FROM lost_books WHERE actual_statut = 'still_lost')";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalBorrowedBooks() {
        try {
            String query = "SELECT COUNT(*) FROM borrowed_books";
//            " WHERE delete_at IS NULL " +
//                    "AND isbn IN (SELECT book FROM borrowed_books WHERE return_date IS NULL)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalAvailableBooks() {
        try {
            String query = "SELECT COUNT(*) FROM books WHERE delete_at IS NULL " +
                    "AND isbn NOT IN (SELECT book FROM borrowed_books WHERE return_date IS NULL) " +
                    "AND isbn NOT IN (SELECT book FROM lost_books WHERE actual_statut = 'still_lost')";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalBorrowedNotReturnedBooks() {
        try {
            String query = "SELECT COUNT(*) FROM books WHERE delete_at IS NULL " +
                    "AND isbn IN (SELECT book FROM borrowed_books WHERE return_date IS NULL) " +
                    "AND isbn NOT IN (SELECT book FROM lost_books WHERE actual_statut = 'still_lost')";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalStock() {
        try {
            String query = "SELECT SUM(b.quantities) FROM books b " +
                    "WHERE b.delete_at IS NULL " +
                    "AND b.isbn NOT IN (SELECT book FROM lost_books WHERE actual_statut = 'still_lost')";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalReturnedBooks() {
        try {
            String query = "SELECT COUNT(*) FROM borrowed_books WHERE return_date IS NOT NULL;";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalAuthors() {
        try {
            String query = "SELECT COUNT(*) FROM authors;";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalCategories() {
        try {
            String query = "SELECT COUNT(*) FROM categories;";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the total number of users in the database.
     *
     * @return The total number of users.
     */
    public int getTotalUsers() {
        try {
            String query = "SELECT COUNT(*) FROM users;";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the total number of users who have borrowed books that have not been returned in the database.
     *
     * @return The total number of users with borrowed books not returned.
     */
    public int getTotalBorrowedUsers() {
        try {
            String query = "SELECT COUNT(DISTINCT u.cnie) " +
                    "FROM users u " +
                    "INNER JOIN borrowed_books bb ON u.cnie = bb.user " +
                    "WHERE bb.return_date IS NULL;";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the total number of users who have not borrowed any books in the database.
     *
     * @return The total number of users with no borrowed books.
     */
    public int getTotalAvailableUsers() {
        try {
            String query = "SELECT COUNT(*) " +
                    "FROM users u " +
                    "WHERE u.cnie NOT IN (SELECT DISTINCT bb.user FROM borrowed_books bb);";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
