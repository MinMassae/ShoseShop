package com.example.shoseshop.repository;

import com.example.shoseshop.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByType(String type, Pageable pageable);
}
