package com.sobolev.kafka.productmicroservise.service;

import com.sobolev.kafka.productmicroservise.dto.CreateProductDto;

public interface ProductService {
    String createProduct(CreateProductDto createProductDto);
}
