package com.alineo.hibernatesearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HibernateSearchApplicationTests {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	BookSearch bookSearch;

	@Test
	void crudTest() {
		Book book = new Book();
		book.setId(UUID.randomUUID());
		book.setTitle("space");
		book.setGenre("Sci-Fi");
		bookRepository.save(book);
		List<Book> books = bookSearch.getByTitle("space");
		// actual: result books size = 0
		// should have been size = 1

		book.setTitle("2001: Space Odyssey");
		book.setGenre("Sci-Fi");
		bookRepository.save(book);
		books = bookSearch.getByTitle("space");
		print(books);
		// actual: result books size = 1 with title = "space"
		// KO: should have been size size = 1 with title = "2001: Space Odyssey"

		book.setTitle("2010: Odyssey Two");
		book.setGenre("Sci-Fi");
		bookRepository.save(book);
		books = bookSearch.getByTitle("space");
		print(books);
		// actual: size = 0
		// OK: size = 0
		books = bookSearch.getByTitle("2001: Space Odyssey");
		print(books);
		// actual: books size = 1 with title = "2001: Space Odyssey"
		// KO: should have been "2010: Odyssey Two"

		bookRepository.delete(book);

		assertEquals(books.get(0).getTitle(), "2010: Odyssey Two");
	}

	private void print(List<Book> books) {
		System.out.println("----- Size: " + books.size());
		if (books.size() > 0) {
			System.out.println("------- Title: " + books.get(0).toString());
		}
	}
}
