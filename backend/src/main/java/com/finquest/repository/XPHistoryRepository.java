package com.finquest.repository;
import com.finquest.model.XPHistory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface XPHistoryRepository extends JpaRepository<XPHistory, Long> {
    long countByUserId(Long userId);
}
