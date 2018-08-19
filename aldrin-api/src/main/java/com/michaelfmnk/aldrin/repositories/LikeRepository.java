package com.michaelfmnk.aldrin.repositories;

import com.michaelfmnk.aldrin.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Like.LikePK> {
}
