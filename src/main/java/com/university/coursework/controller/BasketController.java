package com.university.coursework.controller;

import com.university.coursework.domain.BasketDTO;
import com.university.coursework.domain.BasketItemDTO;
import com.university.coursework.domain.OrderDTO;
import com.university.coursework.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
@Tag(name = "Baskets", description = "APIs for managing user baskets")
public class BasketController {

    private final BasketService basketService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get basket by user ID", description = "Retrieves the basket for a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Basket retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Basket not found")
    })
    public ResponseEntity<BasketDTO> getBasketByUserId(@Parameter(description = "User ID") @PathVariable UUID userId) {
        return ResponseEntity.ok(basketService.findByUserId(userId));
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{basketId}")
    @Operation(summary = "Add item to basket", description = "Adds a new item to the user's basket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<BasketItemDTO> addItemToBasket(
            @Parameter(description = "Basket ID") @PathVariable UUID basketId,
            @RequestBody BasketItemDTO basketItemDTO) {

        return new ResponseEntity<>(basketService.addItemToBasket(basketId, basketItemDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update basket item", description = "Updates the quantity of an item in the basket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<BasketItemDTO> updateBasketItem(
            @Parameter(description = "Basket item ID") @PathVariable UUID itemId,
            @RequestBody BasketItemDTO basketItemDTO) {
        return ResponseEntity.ok(basketService.updateBasketItem(itemId, basketItemDTO));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from basket", description = "Removes an item from the user's basket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<Void> removeItemFromBasket(@Parameter(description = "Basket item ID") @PathVariable UUID itemId) {
        basketService.removeItemFromBasket(itemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{basketId}/checkout")
    @Operation(summary = "Checkout basket", description = "Processes the checkout for a user's basket and creates an order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid checkout details")
    })
    public ResponseEntity<OrderDTO> checkout(
            @Parameter(description = "Basket ID") @PathVariable UUID basketId,
            @RequestParam String address) {
        return new ResponseEntity<>(basketService.checkout(basketId, address), HttpStatus.CREATED);
    }
}
