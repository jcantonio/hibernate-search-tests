package com.alineo.hibernatesearch;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Component;

@Component
public class BookSearch {

    final EntityManager entityManager;

    public BookSearch(final EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();

    }

    public List<Book> getByTitle(String text) {
        SearchSession searchSession = Search.session(entityManager);
        SearchResult<Book> searchResult = searchSession.search(Book.class)
                .where(f -> f.match().field("title").matching(text)).fetch(100);
        return searchResult.hits();
    }

    public List<Book> searchByTitle(String text) {
        SearchSession searchSession = Search.session(entityManager);
        String searchPattern = text + "*";
        SearchResult<Book> searchResult = searchSession.search(Book.class)
                .where(f -> f.wildcard().field("title").matching(searchPattern)).fetch(100);
        return searchResult.hits();
    }

}
