package com.okhara.rating_system.model.rating;

import com.okhara.rating_system.model.auth.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private AppUser seller;

    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Builder.Default
    private Long sumOfGrades = 0L;

    @Builder.Default
    private Long commentsCount = 0L;


    private BigDecimal calculateAverageRating() {
        if (commentsCount == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(sumOfGrades).divide(BigDecimal.valueOf(commentsCount), 2, RoundingMode.HALF_UP);
    }

    public void addGrade(Byte grade) {
        sumOfGrades += grade;
        commentsCount++;
        rating = calculateAverageRating();
    }

    public void removeGrade(Byte grade) {
        sumOfGrades -= grade;
        commentsCount = Math.max(0, commentsCount - 1);
        rating = calculateAverageRating();
    }
}
