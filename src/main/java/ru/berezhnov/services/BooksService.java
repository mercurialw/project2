package ru.berezhnov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.berezhnov.models.Book;
import ru.berezhnov.models.Person;
import ru.berezhnov.repositories.BooksRepository;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(boolean sorted) {
        if (sorted) return booksRepository.findAll(Sort.by("year"));
        return booksRepository.findAll();
    }

    public Book findOne(int id) {
        return booksRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Book book) {
        book.setLastModified(new Date());
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book book) {
        book.setId(id);
        book.setLastModified(new Date());
        booksRepository.save(book);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void giveBookToPerson(int bookId, Person person) {
        Book book = findOne(bookId);
        book.setOwner(person);
    }

    @Transactional
    public void takeBookFromPerson(int bookId) {
        Book book = findOne(bookId);
        book.getOwner().getBooks().remove(book);
        book.setOwner(null);
    }

    public List<Book> findWithPagination(int pageNumber, int size, boolean sorted) {
        if (sorted) return booksRepository.findAll(PageRequest.of(pageNumber, size, Sort.by("year")))
                .getContent();
        return booksRepository.findAll(PageRequest.of(pageNumber, size)).getContent();
    }

    public List<Book> findBooksByTitleStartingWith(String title) {
        return booksRepository.findByTitleStartingWith(title);
    }
}
