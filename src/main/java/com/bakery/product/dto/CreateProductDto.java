package com.bakery.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Positive
    private Double sellingPrice;

    @NotNull
    @Positive
    private Double costPrice;

    private String imageUrl;

    @NotNull
    private Boolean isActive;
}
