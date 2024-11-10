package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

   @Autowired
   BeerRepository beerRepository;
   Beer testBear;

    @BeforeEach
    void setUp() {
        testBear = beerRepository.findAll().getFirst();
    }
    @Transactional
    @Test
    void testAddCategory() {
        Category savedCategory = categoryRepository.save(Category.builder()
                        .description("Ales")
                .build());
        testBear.addCategory(savedCategory);
        Beer savedBeer = beerRepository.save(testBear);

        System.out.println(savedBeer);
    }
}