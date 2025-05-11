package com.example.shoseshop.service;

import com.example.shoseshop.entity.Board;
import com.example.shoseshop.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시글 저장
    public Board save(Board board) {
        return boardRepository.save(board);
    }

    // 타입별 게시글 목록 조회 (notice, qna)
    public Page<Board> getBoardsByType(String type, Pageable pageable) {
        return boardRepository.findByType(type, pageable);
    }

    // ID로 게시글 조회
    public Board getBoardById(Long id) {
        return boardRepository.findById(id).orElse(null);
    }

    // 게시글 삭제
    public void deleteById(Long id) {
        boardRepository.deleteById(id);
    }
}
