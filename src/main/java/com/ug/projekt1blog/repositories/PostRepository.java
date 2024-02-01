package com.ug.projekt1blog.repositories;
import com.ug.projekt1blog.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE %?1%")
    List<Post> findByTitleLike(String title);

}
