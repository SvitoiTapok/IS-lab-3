package com.example.islab1.DBApi;

import com.example.islab1.Entities.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HumanRepository extends JpaRepository<Human, Integer> {

}
