package com.nirbhay.springbootlibrary.dao;

import com.nirbhay.springbootlibrary.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
}
