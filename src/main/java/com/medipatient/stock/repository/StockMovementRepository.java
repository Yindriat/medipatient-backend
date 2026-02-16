package com.medipatient.stock.repository;

import com.medipatient.stock.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    List<StockMovement> findByProductId(UUID productId);

    Page<StockMovement> findByProductId(UUID productId, Pageable pageable);

    List<StockMovement> findByUserId(UUID userId);

    Page<StockMovement> findByUserId(UUID userId, Pageable pageable);

    List<StockMovement> findByMovementType(StockMovement.MovementType movementType);

    Page<StockMovement> findByMovementType(StockMovement.MovementType movementType, Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE " +
           "sm.createdAt BETWEEN :startDate AND :endDate")
    Page<StockMovement> findByCreatedAtBetween(@Param("startDate") ZonedDateTime startDate,
                                               @Param("endDate") ZonedDateTime endDate,
                                               Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE " +
           "(:productId IS NULL OR sm.product.id = :productId) AND " +
           "(:movementType IS NULL OR sm.movementType = :movementType) AND " +
           "(:userId IS NULL OR sm.user.id = :userId) AND " +
           "(:dateFrom IS NULL OR sm.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR sm.createdAt <= :dateTo)")
    Page<StockMovement> findWithFilters(@Param("productId") UUID productId,
                                        @Param("movementType") StockMovement.MovementType movementType,
                                        @Param("userId") UUID userId,
                                        @Param("dateFrom") ZonedDateTime dateFrom,
                                        @Param("dateTo") ZonedDateTime dateTo,
                                        Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE " +
           "sm.product.id = :productId " +
           "ORDER BY sm.createdAt DESC")
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(@Param("productId") UUID productId);

    @Query("SELECT sm FROM StockMovement sm WHERE " +
           "LOWER(sm.reason) LIKE LOWER(CONCAT('%', :reason, '%'))")
    Page<StockMovement> findByReasonContainingIgnoreCase(@Param("reason") String reason, Pageable pageable);

    @Query("SELECT COUNT(sm) FROM StockMovement sm WHERE sm.movementType = :movementType")
    long countByMovementType(@Param("movementType") StockMovement.MovementType movementType);

    @Query("SELECT COUNT(sm) FROM StockMovement sm WHERE sm.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    @Query("SELECT COUNT(sm) FROM StockMovement sm WHERE sm.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    @Query("SELECT sm.movementType, COUNT(sm) FROM StockMovement sm GROUP BY sm.movementType")
    List<Object[]> countByMovementTypeGrouped();

    @Query(value = "SELECT DATE(sm.created_at), sm.movement_type, SUM(sm.quantity) FROM stock_movements sm " +
           "WHERE sm.created_at >= :startDate " +
           "GROUP BY DATE(sm.created_at), sm.movement_type " +
           "ORDER BY DATE(sm.created_at)", nativeQuery = true)
    List<Object[]> getMovementStatisticsByDate(@Param("startDate") ZonedDateTime startDate);

    @Query("SELECT sm.product.name, SUM(CASE WHEN sm.movementType = 'ENTRÉE' THEN sm.quantity ELSE -sm.quantity END) " +
           "FROM StockMovement sm " +
           "WHERE sm.createdAt >= :startDate " +
           "GROUP BY sm.product.id, sm.product.name " +
           "ORDER BY sm.product.name")
    List<Object[]> getNetMovementsByProduct(@Param("startDate") ZonedDateTime startDate);

    @Query(value = "SELECT * FROM stock_movements sm WHERE " +
           "DATE(sm.created_at) = CURRENT_DATE " +
           "ORDER BY sm.created_at DESC", nativeQuery = true)
    List<StockMovement> findTodaysMovements();

    @Query("SELECT SUM(sm.quantity) FROM StockMovement sm WHERE " +
           "sm.product.id = :productId AND " +
           "sm.movementType = :movementType AND " +
           "sm.createdAt >= :startDate")
    Long getTotalQuantityByProductAndTypeAfterDate(@Param("productId") UUID productId,
                                                   @Param("movementType") StockMovement.MovementType movementType,
                                                   @Param("startDate") ZonedDateTime startDate);
}