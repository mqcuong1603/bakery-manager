package com.bakery.product;

import java.util.List;

import com.bakery.product.dto.CreateProductDto;
import com.bakery.product.dto.ProductResponseDto;
import com.bakery.product.dto.UpdateProductDto;

public interface ProductService {
    List<ProductResponseDto> getAllActive();

    ProductResponseDto getProductById(Long id);

    ProductResponseDto createProduct(CreateProductDto productRequestDto);

    ProductResponseDto updateProduct(Long id, UpdateProductDto productRequestDto);

    void deleteProduct(Long id);

}
