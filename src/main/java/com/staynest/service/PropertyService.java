package com.staynest.service;

import com.staynest.dto.PropertyRequest;
import com.staynest.dto.PropertySearchRequest;
import com.staynest.entity.Property;
import com.staynest.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {

    Property createProperty(PropertyRequest request, List<MultipartFile> images, User host);

    Property updateProperty(Long propertyId, PropertyRequest request, List<MultipartFile> newImages, User host);

    void deleteProperty(Long propertyId, User host);

    Property getPropertyById(Long propertyId);

    Page<Property> search(PropertySearchRequest searchRequest);

    List<Property> getPropertiesByHost(User host);

    List<Property> getAllActiveProperties();

    /** Admin action: deactivate/reactivate a listing platform-wide. */
    void toggleActiveStatus(Long propertyId);

    List<Property> getTopBookedProperties();
}
