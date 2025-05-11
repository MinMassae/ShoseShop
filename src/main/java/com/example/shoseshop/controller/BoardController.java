package com.example.shoseshop.controller;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.entity.Board;
import com.example.shoseshop.service.BoardService;
import com.example.shoseshop.jwtoken.JwtToken;
import com.example.shoseshop.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final JwtToken jwtToken;

    // 게시판 목록 (기본은 おしらせ)
    @GetMapping
    public String boardList(@RequestParam(defaultValue = "notice") String type,
                            @RequestParam(defaultValue = "0") int page,
                            HttpServletRequest request,
                            Model model) {
        Page<Board> boardPage = boardService.getBoardsByType(type, PageRequest.of(page, 10));
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("type", type);
        model.addAttribute("page", page);

        // ✅ 로그인 여부 및 권한 확인
        String token = jwtToken.resolveToken(request);
        boolean isAdmin = false;
        boolean isUser = false;

        if (token != null && !token.isEmpty()) {
            try {
                String userId = jwtToken.getUserIdFromToken(token);
                Member member = memberRepository.findByUserId(userId).orElse(null);

                if (member != null) {
                    if ("ADMIN".equalsIgnoreCase(member.getRole())) {
                        isAdmin = true;
                    } else {
                        isUser = true;
                    }
                }
            } catch (Exception e) {
            }
        }

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isUser", isUser);

        return "board/board_list";
    }

    // 게시글 상세보기
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {
        Board board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        return "board/board_detail";
    }

    // 게시글 작성 폼
    @GetMapping("/write")
    public String boardWriteForm(@RequestParam String type,
                                 HttpServletRequest request,
                                 Model model) {
        String token = jwtToken.resolveToken(request);
        String userId = null;

        if (token != null && !token.isEmpty()) {
            userId = jwtToken.getUserIdFromToken(token);
        }

        Member member = userId != null ? memberRepository.findByUserId(userId).orElse(null) : null;

        // 관리자만 おしらせ 작성 가능
        if ("notice".equals(type)) {
            if (member == null || !"ADMIN".equalsIgnoreCase(member.getRole())) {
                return "redirect:/board?type=notice";
            }
        }

        // Q&A는 로그인 사용자만 작성 가능
        if ("qna".equals(type)) {
            if (member == null) {
                return "redirect:/login";
            }
        }

        model.addAttribute("type", type);
        return "board/board_write";
    }

    // 게시글 작성
    @PostMapping("/write")
    public String boardWrite(@ModelAttribute Board board,
                             HttpServletRequest request) {
        String token = jwtToken.resolveToken(request);
        String userId = null;

        if (token != null && !token.isEmpty()) {
            userId = jwtToken.getUserIdFromToken(token);
        }

        Member member = userId != null ? memberRepository.findByUserId(userId).orElse(null) : null;

        if (member == null) {
            return "redirect:/login";
        }

        // 관리자만 おしらせ 작성 가능
        if ("notice".equals(board.getType()) && !"ADMIN".equalsIgnoreCase(member.getRole())) {
            return "redirect:/board?type=notice";
        }

        board.setWriter(userId);
        boardService.save(board);
        return "redirect:/board?type=" + board.getType();
    }

    // 게시글 삭제 (관리자만 가능)
    @PostMapping("/delete/{id}")
    public String boardDelete(@PathVariable Long id,
                              @RequestParam String type,
                              HttpServletRequest request) {
        String token = jwtToken.resolveToken(request);
        String userId = null;

        if (token != null && !token.isEmpty()) {
            userId = jwtToken.getUserIdFromToken(token);
        }

        Member member = userId != null ? memberRepository.findByUserId(userId).orElse(null) : null;

        if (member == null || !"ADMIN".equalsIgnoreCase(member.getRole())) {
            return "redirect:/board?type=" + type;
        }

        boardService.deleteById(id);
        return "redirect:/board?type=" + type;
    }
}
