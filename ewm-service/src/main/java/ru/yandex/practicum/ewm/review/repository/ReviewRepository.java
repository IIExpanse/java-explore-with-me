package ru.yandex.practicum.ewm.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.review.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByEventIdOrderByCreated(long id, Pageable pageable);
}
