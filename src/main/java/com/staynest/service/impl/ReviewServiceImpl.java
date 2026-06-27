package com.staynest.service.impl;

import com.staynest.dto.ReviewRequest;
import com.staynest.entity.Property;
import com.staynest.entity.Review;
import com.staynest.entity.User;
import com.staynest.exception.DuplicateResourceException;
import com.staynest.exception.ResourceNotFoundException;
import com.staynest.exception.UnauthorizedActionException;
import com.staynest.repository.PropertyRepository;
import com.staynest.repository.ReviewRepository;
import com.staynest.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public Review addReview(Long propertyId, ReviewRequest request, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        // One review per guest per property - prevents review spamming/bombing.
        if (reviewRepository.findByPropertyAndUser(property, user).isPresent()) {
            throw new DuplicateResourceException("You have already reviewed this property. You can edit your existing review instead.");
        }

        Review review = new Review();
        review.setProperty(property);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(Long reviewId, ReviewRequest request, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You can only edit your own reviews.");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Owner of the review OR an admin may delete it.
        boolean isOwner = review.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().name().equals("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("You do not have permission to delete this review.");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<Review> getReviewsForProperty(Property property) {
        return reviewRepository.findByPropertyOrderByCreatedAtDesc(property);
    }

    @Override
    public double getAverageRating(Long propertyId) {
        Double avg = reviewRepository.findAverageRatingByPropertyId(propertyId);
        return avg == null ? 0.0 : Math.round(avg * 10) / 10.0; // round to 1 decimal place
    }
}
