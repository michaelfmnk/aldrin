package com.michaelfmnk.aldrin.repositories;

import com.michaelfmnk.aldrin.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    boolean existsByCommentId(Integer id);

    Page<Comment> findByPostId(Integer id, Pageable pageable);

}
