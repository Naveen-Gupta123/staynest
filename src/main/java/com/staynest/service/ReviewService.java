package com.staynest.service;

import com.staynest.dto.ReviewRequest;
import com.staynest.entity.Property;
import com.staynest.entity.Review;
import com.staynest.entity.User;

import java.util.List;

public interface ReviewService {

    Review addReview(Long propertyId, ReviewRequest request, User user);

    Review updateReview(Long reviewId, ReviewRequest request, User user);

    void deleteReview(Long reviewId, User user);

    List<Review> getReviewsForProperty(Property property);

    double getAverageRating(Long propertyId);
}
