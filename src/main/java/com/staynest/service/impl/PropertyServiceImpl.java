package com.staynest.service.impl;

import com.staynest.dto.PropertyRequest;
import com.staynest.dto.PropertySearchRequest;
import com.staynest.entity.Property;
import com.staynest.entity.PropertyImage;
import com.staynest.entity.PropertyType;
import com.staynest.entity.User;
import com.staynest.exception.ResourceNotFoundException;
import com.staynest.exception.UnauthorizedActionException;
import com.staynest.repository.PropertyRepository;
import com.staynest.service.PropertyService;
import com.staynest.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final FileStorageUtil fileStorageUtil;

    private static final int PAGE_SIZE = 12;

    @Override
    @Transactional
    public Property createProperty(PropertyRequest request, List<MultipartFile> images, User host) {
        Property property = new Property();
        mapRequestToEntity(request, property);
        property.setHost(host);
        property.setActive(true);

        // Save first so the Property has an ID before we attach images to it.
        Property saved = propertyRepository.save(property);

        attachImages(images, saved);

        return saved;
    }

    @Override
    @Transactional
    public Property updateProperty(Long propertyId, PropertyRequest request, List<MultipartFile> newImages, User host) {
        Property property = getPropertyById(propertyId);

        // ---- OWNERSHIP CHECK ----
        // This is exactly the kind of rule that a URL-pattern security
        // rule (hasRole("HOST")) CANNOT enforce - it only knows the
        // user IS a host, not WHICH listings belong to them. We must
        // check it explicitly here in the service layer.
        if (!property.getHost().getId().equals(host.getId())) {
            throw new UnauthorizedActionException("You do not have permission to edit this property.");
        }

        mapRequestToEntity(request, property);
        attachImages(newImages, property);

        return propertyRepository.save(property);
    }

    @Override
    @Transactional
    public void deleteProperty(Long propertyId, User host) {
        Property property = getPropertyById(propertyId);

        if (!property.getHost().getId().equals(host.getId())) {
            throw new UnauthorizedActionException("You do not have permission to delete this property.");
        }

        propertyRepository.delete(property);
    }

    @Override
    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
    }

    @Override
    public Page<Property> search(PropertySearchRequest req) {
        Pageable pageable = PageRequest.of(req.getPage(), PAGE_SIZE, Sort.by("createdAt").descending());

        PropertyType type = null;
        if (req.getType() != null && !req.getType().isBlank()) {
            try {
                type = PropertyType.valueOf(req.getType().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // invalid/unknown type string -> treat as "no filter" rather than erroring out
            }
        }

        return propertyRepository.searchProperties(
                blankToNull(req.getCity()),
                blankToNull(req.getState()),
                blankToNull(req.getCountry()),
                req.getMinPrice(),
                req.getMaxPrice(),
                req.getBedrooms(),
                req.getGuests(),
                type,
                pageable
        );
    }

    @Override
    public List<Property> getPropertiesByHost(User host) {
        return propertyRepository.findByHost(host);
    }

    @Override
    public List<Property> getAllActiveProperties() {
        return propertyRepository.findByActiveTrue();
    }

    @Override
    @Transactional
    public void toggleActiveStatus(Long propertyId) {
        Property property = getPropertyById(propertyId);
        property.setActive(!property.isActive());
        propertyRepository.save(property);
    }

    @Override
    public List<Property> getTopBookedProperties() {
        return propertyRepository.findTopBookedProperties();
    }

    // ---------------- private helpers ----------------

    private void mapRequestToEntity(PropertyRequest request, Property property) {
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPricePerNight(request.getPricePerNight());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setCountry(request.getCountry());
        property.setType(PropertyType.valueOf(request.getType().toUpperCase()));
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setMaxGuests(request.getMaxGuests());
        property.setAmenities(request.getAmenities());
    }

    private void attachImages(List<MultipartFile> images, Property property) {
        if (images == null || images.isEmpty()) {
            return;
        }
        boolean hasCoverAlready = property.getImages().stream().anyMatch(PropertyImage::isCover);

        for (MultipartFile file : images) {
            if (file.isEmpty()) continue;
            String url = fileStorageUtil.storeFile(file);
            if (url == null) continue;

            PropertyImage image = new PropertyImage();
            image.setImageUrl(url);
            image.setProperty(property);
            // first image ever uploaded becomes the cover photo automatically
            image.setCover(!hasCoverAlready && property.getImages().isEmpty());
            property.getImages().add(image);
            hasCoverAlready = true;
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
