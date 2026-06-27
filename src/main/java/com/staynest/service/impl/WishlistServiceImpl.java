package com.staynest.service.impl;

import com.staynest.entity.Property;
import com.staynest.entity.User;
import com.staynest.entity.Wishlist;
import com.staynest.exception.DuplicateResourceException;
import com.staynest.exception.ResourceNotFoundException;
import com.staynest.repository.PropertyRepository;
import com.staynest.repository.WishlistRepository;
import com.staynest.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public Wishlist addToWishlist(Long propertyId, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        if (wishlistRepository.existsByUserAndProperty(user, property)) {
            throw new DuplicateResourceException("This property is already in your wishlist.");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProperty(property);
        return wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long propertyId, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        wishlistRepository.deleteByUserAndProperty(user, property);
    }

    @Override
    public List<Wishlist> getWishlistByUser(User user) {
        return wishlistRepository.findByUser(user);
    }

    @Override
    public boolean isPropertyWishlisted(Long propertyId, User user) {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) return false;
        return wishlistRepository.existsByUserAndProperty(user, property);
    }
}
