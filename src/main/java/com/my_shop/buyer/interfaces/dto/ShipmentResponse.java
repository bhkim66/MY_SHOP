package com.my_shop.buyer.interfaces.dto;

import com.my_shop.delivery.domain.entity.Shipment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse {
    private String shippingCompany;
    private String trackingNumber;
    private String shippingStatus;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    public static ShipmentResponse from(Shipment shipment) {
        return ShipmentResponse.builder()
                .shippingCompany(shipment.getShippingCompany())
                .trackingNumber(shipment.getTrackingNumber())
                .shippingStatus(shipment.getShippingStatus())
                .shippedAt(shipment.getShippedAt())
                .deliveredAt(shipment.getDeliveredAt())
                .build();
    }
}
