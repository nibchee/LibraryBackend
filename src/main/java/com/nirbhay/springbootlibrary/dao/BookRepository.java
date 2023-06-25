package com.nirbhay.springbootlibrary.dao;

import com.nirbhay.springbootlibrary.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
