package io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence;

import io.github.treasurehaenaryn.msa.inventory.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Product 엔티티 저장소.
 */
public interface ProductRepository extends JpaRepository<Product, String> {
}
