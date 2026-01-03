package com.bakery.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bakery.common.response.ApiResponse;
import com.bakery.common.response.ApiStatus;
import com.bakery.product.dto.CreateProductDto;
import com.bakery.product.dto.ProductResponseDto;
import com.bakery.product.dto.UpdateProductDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllActive();
        return ResponseEntity.ok(ApiResponse.of(ApiStatus.SUCCESS, products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.of(ApiStatus.SUCCESS, product));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
            @Valid @RequestBody CreateProductDto productRequestDto) {
        ProductResponseDto createdProduct = productService.createProduct(productRequestDto);
        return ResponseEntity.ok(ApiResponse.of(ApiStatus.SUCCESS, createdProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(@PathVariable Long id,
            @Valid @RequestBody UpdateProductDto productRequestDto) {

        ProductResponseDto updatedProduct = productService.updateProduct(id, productRequestDto);

        return ResponseEntity.ok(ApiResponse.of(ApiStatus.SUCCESS, updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                ApiResponse.of(ApiStatus.SUCCESS, null));
    }

}
