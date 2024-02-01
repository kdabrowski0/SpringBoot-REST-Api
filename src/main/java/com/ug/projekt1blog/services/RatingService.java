package com.ug.projekt1blog.services;

import com.ug.projekt1blog.models.Comment;
import com.ug.projekt1blog.models.Rating;
import com.ug.projekt1blog.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    public List<Rating> getAllPostRatingByPostId(Long id) {return ratingRepository.findAllByPostId(id); }

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Optional<Rating> getRatingByAccountIdAndPostId(Long userId, Long postId) {
        return ratingRepository.findByAccountIdAndPostId(userId, postId);
    }

}
