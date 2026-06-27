package com.staynest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PropertyImage entity - represents ONE photo belonging to a Property.
 * A Property can have many images (gallery), hence this is its own
 * table rather than a single "imageUrl" column on Property.
 */
@Entity
@Table(name = "property_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * We store only the file PATH/URL here (e.g. "/uploads/abc123.jpg"),
     * never the raw image bytes. Storing binary files inside a relational
     * database is an anti-pattern - it bloats the DB and slows down
     * backups. The actual file lives on disk (or, in production, in
     * cloud storage like AWS S3); the database just points to it.
     */
    @Column(nullable = false, length = 255)
    private String imageUrl;

    /**
     * Marks one image as the "cover photo" shown on search result cards.
     */
    @Column(nullable = false)
    private boolean isCover = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}
