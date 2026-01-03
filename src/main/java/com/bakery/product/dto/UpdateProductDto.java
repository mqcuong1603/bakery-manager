package com.bakery.product.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductDto {
    @Size(max = 100)
    private String name;

    @Positive
    private Double sellingPrice;

    @Positive
    private Double costPrice;

    private String imageUrl;

    private Boolean isActive;
}
