package ru.berezhnov.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.berezhnov.models.Book;
import ru.berezhnov.models.Person;
import ru.berezhnov.services.BooksService;
import ru.berezhnov.services.PeopleService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String booksPage(@RequestParam(name = "page", defaultValue = "-1") int pageNumber,
                            @RequestParam(name = "books_per_page", defaultValue = "100") int size,
                            @RequestParam(name = "sort_by_year", defaultValue = "false") boolean sortByYear,
                            Model model) {
        List<Book> books = null;
        if (pageNumber < 0) {
            if (sortByYear) books = booksService.findAllSorted();
            else books = booksService.findAll();
        } else {
            if (sortByYear) books = booksService.findAllPagination(pageNumber, size, true);
            else books = booksService.findAllPagination(pageNumber, size, false);
        }
        model.addAttribute("books", books);
        return "books/index";
    }

    @GetMapping("/new")
    public String newBookPage(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String save(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }
        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }

    /* TO-DO: change uncorrected validation in situation when edit book with empty fields -_- */
    @PatchMapping("/{id}")
    public String updateBook(@ModelAttribute("book") @Valid Book book, @PathVariable("id") int id,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        booksService.update(id, book);
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String showBook(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        Book book = booksService.findOne(id);
        model.addAttribute("book", book);
        Optional<Person> owner = Optional.ofNullable(book.getOwner());
        if(owner.isPresent()) {
            model.addAttribute("owner", owner.get());
        } else {
            model.addAttribute("people", peopleService.findAll());
        }
        return "books/show";
    }

    @PatchMapping("/{id}/give")
    public String giveBook(@PathVariable("id") int bookId, @ModelAttribute("person") Person person) {
        booksService.giveBookToPerson(bookId, person);
        return "redirect:/books/" + bookId;
    }

    @PatchMapping("/{id}/take")
    public String takeBook(@PathVariable("id") int bookId) {
        booksService.takeBookFromPerson(bookId);
        return "redirect:/books/" + bookId;
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        booksService.delete(id);

        return "redirect:/books";
    }

    @GetMapping("/search")
    public String search(@ModelAttribute(name = "proxyBook") Book proxyBook,
                         Model model) {
        Book book = null;
        String title = proxyBook.getTitle();
        if (title != null && !title.isEmpty()) {
            book = booksService.findBookByTitleStartingWith(title);
            model.addAttribute("searchAttemptTried", true);
        }
        if (book != null) {
            model.addAttribute("book", book);
            model.addAttribute("owner", book.getOwner());
        }
        return "books/search";
    }
}
