package com.company.platform.repository;

import com.company.platform.entity.Complaint;
import com.company.platform.entity.ComplaintStatus;
import com.company.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByCitizen(User citizen);

    List<Complaint> findByAssignedAgent(User agent);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByCategoryId(Long categoryId);

    long countByStatus(ComplaintStatus status);

    @Query("SELECT c FROM Complaint c ORDER BY c.createdAt DESC")
    List<Complaint> findRecent();

    @Query("SELECT c.category.name, COUNT(c) FROM Complaint c GROUP BY c.category.name")
    List<Object[]> countGroupedByCategory();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR c.status = :status)")
    List<Complaint> search(@Param("keyword") String keyword, @Param("status") ComplaintStatus status);
}
