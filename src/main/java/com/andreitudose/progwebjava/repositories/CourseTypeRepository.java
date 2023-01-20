package com.andreitudose.progwebjava.repositories;

import com.andreitudose.progwebjava.model.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTypeRepository extends JpaRepository<CourseType, Integer> {
}
