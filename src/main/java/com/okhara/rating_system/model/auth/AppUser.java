package com.okhara.rating_system.model.auth;

import com.okhara.rating_system.model.rating.Rating;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "app_users", schema = "user_schema")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 16)
    private String nickname;

    private String password;

    private String email;

    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<RoleType> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Rating rating = null;

    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;


    @PrePersist
    public void initRating() {
        if (rating == null) {
            rating = new Rating();
            rating.setSeller(this);
        }
    }
}
