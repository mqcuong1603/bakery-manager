package com.bakery.product.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private Double sellingPrice;
    private Double costPrice;
    private String imageUrl;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;
}
