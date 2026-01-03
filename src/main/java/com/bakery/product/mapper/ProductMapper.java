package com.bakery.product.mapper;

import java.util.List;
import org.mapstruct.Mapper;

import com.bakery.product.dto.CreateProductDto;
import com.bakery.product.dto.ProductResponseDto;
import com.bakery.product.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto toResponse(Product product);

    Product toEntity(CreateProductDto dto);

    List<ProductResponseDto> toResponseList(List<Product> products);
}
