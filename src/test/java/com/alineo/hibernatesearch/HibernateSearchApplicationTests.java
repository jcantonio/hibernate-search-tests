package com.alineo.hibernatesearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
class HibernateSearchApplicationTests {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	BookSearch bookSearch;

	@Autowired
	TransactionTemplate transactionTemplate;

	@Test
	void crudTest() {

		UUID bookId = UUID.randomUUID();
		transactionTemplate.execute(ignored -> {
			Book book = new Book();
			book.setId(bookId);
			book.setTitle("space");
			book.setGenre("Sci-Fi");
			bookRepository.save(book);
			return null;
		});
		transactionTemplate.execute(ignored -> {
			List<Book> books = bookSearch.getByTitle("space");
			print(books);
			// actual: result books size = 1
			// OK: size = 1
			assertEquals(books.get(0).getTitle(), "space");
			return null;
		});
		transactionTemplate.execute(ignored -> {
			Book book = bookRepository.findById(bookId).get();
			book.setTitle("2001: Space Odyssey");
			book.setGenre("Sci-Fi");
			bookRepository.save(book);
			return null;
		});
		transactionTemplate.execute(ignored -> {
			List<Book> books = bookSearch.getByTitle("space");
			print(books);
			// actual: result books size = 1 with title = "space"
			// KO: should have been size size = 1 with title = "2001: Space Odyssey"
			assertEquals(books.get(0).getTitle(), "2001: Space Odyssey");

			return null;
		});
		transactionTemplate.execute(ignored -> {
			Book book = bookRepository.findById(bookId).get();
			book.setTitle("2010: Odyssey Two");
			book.setGenre("Sci-Fi");
			bookRepository.save(book);
			return null;
		});
		transactionTemplate.execute(ignored -> {
			List<Book> books = bookSearch.getByTitle("space");
			print(books);
			// actual: size = 0
			// OK: size = 0
			return null;
		});
		transactionTemplate.execute(ignored -> {
			List<Book> books = bookSearch.getByTitle("2001: Space Odyssey");
			print(books);
			// actual: books size = 1 with title = "2001: Space Odyssey"
			// KO: should have been "2010: Odyssey Two"
			assertEquals(books.get(0).getTitle(), "2010: Odyssey Two");
			return null;
		});
		transactionTemplate.execute(ignored -> {
			Book book = bookRepository.findById(bookId).get();
			bookRepository.delete(book);
			return null;
		});
	}

	private void print(List<Book> books) {
		System.out.println("----- Size: " + books.size());
		if (books.size() > 0) {
			System.out.println("------- Title: " + books.get(0).toString());
		}
	}
}
