package com.okhara.rating_system.model.rating;

import com.okhara.rating_system.model.auth.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ratings")
@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private AppUser seller;

    private BigDecimal rating;

    private Long sumOfGrades;

    private Long commentsCount;
}
