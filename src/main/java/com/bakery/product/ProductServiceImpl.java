package com.bakery.product;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bakery.common.exception.ResourceNotFoundException;
import com.bakery.product.dto.CreateProductDto;
import com.bakery.product.dto.ProductResponseDto;
import com.bakery.product.dto.UpdateProductDto;
import com.bakery.product.entity.Product;
import com.bakery.product.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDto> getAllActive() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm " + id));
    }

    @Override
    public ProductResponseDto createProduct(CreateProductDto productRequestDto) {
        Product product = productMapper.toEntity(productRequestDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, UpdateProductDto dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + id));

        product.setName(dto.getName());
        product.setImageUrl(dto.getImageUrl());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setActive(dto.getIsActive());

        // Không cần save() nếu entity đang managed
        return productMapper.toResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + id));
        productRepository.delete(product);
    }

}
