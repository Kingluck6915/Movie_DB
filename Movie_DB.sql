
CREATE DATABASE movie_db;
USE movie_db;


CREATE TABLE movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    director VARCHAR(100),
    release_year INT,
    genre VARCHAR(50),
    rating DECIMAL(3,1)
);


INSERT INTO movies (title, director, release_year, genre, rating) VALUES
('Inception', 'Christopher Nolan', 2010, 'Sci-Fi', 8.8),
('The Dark Knight', 'Christopher Nolan', 2008, 'Action', 9.0),
('Interstellar', 'Christopher Nolan', 2014, 'Sci-Fi', 8.6);
-- Add actors table
CREATE TABLE actors (
    actor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    biography TEXT
);


CREATE TABLE movie_actors (
    movie_id INT,
    actor_id INT,
    PRIMARY KEY (movie_id, actor_id),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    FOREIGN KEY (actor_id) REFERENCES actors(actor_id)
);

CREATE TABLE user_ratings (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT,
    user_name VARCHAR(100),
    rating DECIMAL(2,1) CHECK (rating BETWEEN 0 AND 10),
    review TEXT
);
