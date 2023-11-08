package com.smart.dao;

import com.smart.entities.Contact;
import com.smart.entities.User;
import jakarta.persistence.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    //pagination...


    @Query("from Contact as c where c.user.id =:userId")
    //currentPage-page
    //Contact Per Page - 5
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pePageable);

    //search
    @Query("SELECT c FROM Contact c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keywords, '%')) AND c.user = :user")
    public List<Contact> findByNameContainingAndUser(String keywords, User user);

}
