package com.andreitudose.progwebjava.repositories;

import com.andreitudose.progwebjava.model.Programme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface ProgrammeRepository extends JpaRepository<Programme, Integer> {

}
