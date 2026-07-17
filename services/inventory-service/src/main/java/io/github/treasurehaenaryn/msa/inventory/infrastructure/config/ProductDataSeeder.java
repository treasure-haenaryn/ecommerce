package io.github.treasurehaenaryn.msa.inventory.infrastructure.config;

import io.github.treasurehaenaryn.msa.inventory.domain.Product;
import io.github.treasurehaenaryn.msa.inventory.infrastructure.persistence.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 앱 기동 시 테스트용 상품 데이터를 시딩한다.
 */
@Component
public class ProductDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.save(new Product("prod-1", "무선 마우스", 50));
        productRepository.save(new Product("prod-2", "기계식 키보드", 30));
        productRepository.save(new Product("prod-3", "품절 한정판 굿즈", 0));
    }
}
