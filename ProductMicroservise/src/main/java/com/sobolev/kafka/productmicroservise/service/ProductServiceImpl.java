package com.sobolev.kafka.productmicroservise.service;

import com.sobolev.kafka.productmicroservise.dto.CreateProductDto;
import com.sobolev.spring.core.ProductCreatedEvent;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {

    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductDto createProductDto) {
        //TODO save to DB
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId,
                createProductDto.getTitle(),
                createProductDto.getPrice(),
                createProductDto.getQuantity());

        CompletableFuture<SendResult<String,ProductCreatedEvent>> future = kafkaTemplate
                .send("product-created-events-topic", productId, productCreatedEvent);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message: {}", exception.getMessage());
            }else{
                LOGGER.info("Successfully sent message: {}", result.getRecordMetadata());
            }
        });

        LOGGER.info("Return {}", productId);

        return productId;
    }
}
