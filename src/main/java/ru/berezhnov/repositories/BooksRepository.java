package ru.berezhnov.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.berezhnov.models.Book;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    Book findBookByTitleStartingWith(String title);
}
