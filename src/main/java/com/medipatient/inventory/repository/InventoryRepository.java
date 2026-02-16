package com.medipatient.inventory.repository;

import com.medipatient.inventory.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    List<Inventory> findByCategory(Inventory.Category category);

    Page<Inventory> findByCategory(Inventory.Category category, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Inventory> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:supplier IS NULL OR LOWER(i.supplier) LIKE LOWER(CONCAT('%', :supplier, '%'))) AND " +
           "(:search IS NULL OR " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Inventory> findWithFilters(@Param("category") Inventory.Category category,
                                    @Param("supplier") String supplier,
                                    @Param("search") String search,
                                    Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.minStock")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.minStock")
    Page<Inventory> findLowStockItems(Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE " +
           "i.expiryDate IS NOT NULL AND " +
           "i.expiryDate < :today")
    List<Inventory> findExpiredItems(@Param("today") LocalDate today);

    @Query("SELECT i FROM Inventory i WHERE " +
           "i.expiryDate IS NOT NULL AND " +
           "i.expiryDate < :today")
    Page<Inventory> findExpiredItems(@Param("today") LocalDate today, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE " +
           "i.expiryDate IS NOT NULL AND " +
           "i.expiryDate BETWEEN :today AND :futureDate")
    List<Inventory> findItemsExpiringSoon(@Param("today") LocalDate today,
                                          @Param("futureDate") LocalDate futureDate);

    @Query("SELECT i FROM Inventory i WHERE " +
           "i.expiryDate IS NOT NULL AND " +
           "i.expiryDate BETWEEN :today AND :futureDate")
    Page<Inventory> findItemsExpiringSoon(@Param("today") LocalDate today,
                                          @Param("futureDate") LocalDate futureDate,
                                          Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE " +
           "i.unitPrice BETWEEN :minPrice AND :maxPrice")
    Page<Inventory> findByUnitPriceBetween(@Param("minPrice") Integer minPrice,
                                           @Param("maxPrice") Integer maxPrice,
                                           Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE " +
           "LOWER(i.supplier) = LOWER(:supplier)")
    List<Inventory> findBySupplier(@Param("supplier") String supplier);

    @Query("SELECT i FROM Inventory i WHERE " +
           "LOWER(i.supplier) = LOWER(:supplier)")
    Page<Inventory> findBySupplier(@Param("supplier") String supplier, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.category = :category")
    long countByCategory(@Param("category") Inventory.Category category);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.currentStock <= i.minStock")
    long countLowStockItems();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE " +
           "i.expiryDate IS NOT NULL AND i.expiryDate < :today")
    long countExpiredItems(@Param("today") LocalDate today);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE " +
           "i.expiryDate IS NOT NULL AND " +
           "i.expiryDate BETWEEN :today AND :futureDate")
    long countItemsExpiringSoon(@Param("today") LocalDate today,
                                @Param("futureDate") LocalDate futureDate);

    @Query("SELECT i.category, COUNT(i) FROM Inventory i GROUP BY i.category")
    List<Object[]> countByCategory();

    @Query("SELECT i.supplier, COUNT(i) FROM Inventory i " +
           "WHERE i.supplier IS NOT NULL " +
           "GROUP BY i.supplier")
    List<Object[]> countBySupplier();

    @Query("SELECT SUM(i.currentStock * i.unitPrice) FROM Inventory i")
    Long getTotalInventoryValue();

    @Query("SELECT SUM(i.currentStock * i.unitPrice) FROM Inventory i WHERE i.category = :category")
    Long getTotalInventoryValueByCategory(@Param("category") Inventory.Category category);
}