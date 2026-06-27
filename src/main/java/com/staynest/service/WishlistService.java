package com.staynest.service;

import com.staynest.entity.User;
import com.staynest.entity.Wishlist;

import java.util.List;

public interface WishlistService {

    Wishlist addToWishlist(Long propertyId, User user);

    void removeFromWishlist(Long propertyId, User user);

    List<Wishlist> getWishlistByUser(User user);

    boolean isPropertyWishlisted(Long propertyId, User user);
}
