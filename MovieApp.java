package Project_DB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MovieApp {
    public static void main(String[] args) {
        new MovieGUI();
    }
}

class MovieGUI extends JFrame {
    private JTextField titleField, directorField, yearField, genreField, ratingField, searchField;
    private JTextField rateMovieIdField, rateUserField, rateValueField;
    private JTextArea reviewArea, outputArea;
    private Connection conn;

    public MovieGUI() {
        setTitle("Movie Database");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        connectDB();

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Movie"));
        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Director:"));
        directorField = new JTextField();
        inputPanel.add(directorField);

        inputPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        inputPanel.add(yearField);

        inputPanel.add(new JLabel("Genre:"));
        genreField = new JTextField();
        inputPanel.add(genreField);

        inputPanel.add(new JLabel("Rating:"));
        ratingField = new JTextField();
        inputPanel.add(ratingField);

        JButton addButton = new JButton("Add Movie");
        addButton.addActionListener(e -> addMovie());
        inputPanel.add(addButton);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Movies"));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchMovies());
        searchPanel.add(searchButton);

        // Rate Movie Panel
        JPanel ratePanel = new JPanel(new GridLayout(5, 2));
        ratePanel.setBorder(BorderFactory.createTitledBorder("Rate a Movie"));
        ratePanel.add(new JLabel("Movie ID:"));
        rateMovieIdField = new JTextField();
        ratePanel.add(rateMovieIdField);
        ratePanel.add(new JLabel("Your Name:"));
        rateUserField = new JTextField();
        ratePanel.add(rateUserField);
        ratePanel.add(new JLabel("Rating (0-10):"));
        rateValueField = new JTextField();
        ratePanel.add(rateValueField);
        ratePanel.add(new JLabel("Review:"));
        reviewArea = new JTextArea(2, 20);
        ratePanel.add(new JScrollPane(reviewArea));
        JButton submitRatingButton = new JButton("Submit Rating");
        submitRatingButton.addActionListener(e -> submitRating());
        ratePanel.add(submitRatingButton);

        // Extras Panel
        JPanel extrasPanel = new JPanel();
        JButton showActorsButton = new JButton("Show Actors");
        showActorsButton.addActionListener(e -> showActors());
        JButton topRatedButton = new JButton("Top Recommendations");
        topRatedButton.addActionListener(e -> showTopRecommendations());
        extrasPanel.add(showActorsButton);
        extrasPanel.add(topRatedButton);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add components to frame
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(ratePanel, BorderLayout.NORTH);
        leftPanel.add(extrasPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        loadMovies();
        setVisible(true);
    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_db", "root", "kaleem6915&@!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private void loadMovies() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM movies");
            outputArea.setText("Movies:\n");
            while (rs.next()) {
                outputArea.append(
                    rs.getInt("movie_id") + " | " +
                    rs.getString("title") + " | " +
                    rs.getString("director") + " | " +
                    rs.getInt("release_year") + " | " +
                    rs.getString("genre") + " | " +
                    rs.getDouble("rating") + "\n"
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading movies: " + e.getMessage());
        }
    }

    private void addMovie() {
        try {
            String title = titleField.getText();
            String director = directorField.getText();
            int year = Integer.parseInt(yearField.getText());
            String genre = genreField.getText();
            double rating = Double.parseDouble(ratingField.getText());

            String sql = "INSERT INTO movies (title, director, release_year, genre, rating) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, director);
            pstmt.setInt(3, year);
            pstmt.setString(4, genre);
            pstmt.setDouble(5, rating);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Movie added successfully!");
            loadMovies();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding movie: " + e.getMessage());
        }
    }

    private void searchMovies() {
        try {
            String keyword = searchField.getText();
            String sql = "SELECT * FROM movies WHERE title LIKE ? OR director LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            outputArea.setText("Search Results:\n");
            while (rs.next()) {
                outputArea.append(
                    rs.getInt("movie_id") + " | " +
                    rs.getString("title") + " | " +
                    rs.getString("director") + " | " +
                    rs.getInt("release_year") + " | " +
                    rs.getString("genre") + " | " +
                    rs.getDouble("rating") + "\n"
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching movies: " + e.getMessage());
        }
    }

    private void submitRating() {
        try {
            int movieId = Integer.parseInt(rateMovieIdField.getText());
            String user = rateUserField.getText();
            double rating = Double.parseDouble(rateValueField.getText());
            String review = reviewArea.getText();

            String sql = "INSERT INTO user_ratings (movie_id, user_name, rating, review) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, movieId);
            pstmt.setString(2, user);
            pstmt.setDouble(3, rating);
            pstmt.setString(4, review);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Rating submitted!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error submitting rating: " + e.getMessage());
        }
    }

    private void showActors() {
        try {
            String movieTitle = JOptionPane.showInputDialog(this, "Enter movie title:");
            String sql = """
                SELECT a.name FROM actors a
                JOIN movie_actors ma ON a.actor_id = ma.actor_id
                JOIN movies m ON ma.movie_id = m.movie_id
                WHERE m.title LIKE ?
            """;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + movieTitle + "%");
            ResultSet rs = pstmt.executeQuery();

            outputArea.setText("Actors:\n");
            while (rs.next()) {
                outputArea.append("- " + rs.getString("name") + "\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error showing actors: " + e.getMessage());
        }
    }

    private void showTopRecommendations() {
        try {
            String genre = JOptionPane.showInputDialog(this, "Enter genre:");
            String sql = "SELECT title, AVG(rating) as avg_rating FROM movies " +
                         "JOIN user_ratings USING(movie_id) " +
                         "WHERE genre LIKE ? GROUP BY movie_id ORDER BY avg_rating DESC LIMIT 5";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + genre + "%");
            ResultSet rs = pstmt.executeQuery();

            outputArea.setText("Top Recommendations in " + genre + ":\n");
            while (rs.next()) {
                outputArea.append(rs.getString("title") + " | Rating: " + rs.getDouble("avg_rating") + "\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error showing recommendations: " + e.getMessage());
        }
    }
}
