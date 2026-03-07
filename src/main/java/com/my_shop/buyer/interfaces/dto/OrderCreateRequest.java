package com.my_shop.buyer.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotEmpty(message = "최소 1개 이상의 상품이 필요합니다.")
    private List<OrderItemRequest> items;

    @NotBlank(message = "수령인 이름은 필수입니다.")
    @Size(max = 50, message = "수령인 이름은 50자 이하여야 합니다.")
    private String receiverName;

    @NotBlank(message = "수령인 연락처는 필수입니다.")
    @Size(max = 20, message = "수령인 연락처는 20자 이하여야 합니다.")
    private String receiverPhone;

    @Size(max = 10, message = "우편번호는 10자 이하여야 합니다.")
    private String zipCode;

    @NotBlank(message = "주소는 필수입니다.")
    @Size(max = 200, message = "주소는 200자 이하여야 합니다.")
    private String address1;

    @Size(max = 200, message = "상세주소는 200자 이하여야 합니다.")
    private String address2;

    @Size(max = 500, message = "배송 메시지는 500자 이하여야 합니다.")
    private String shippingMessage;

    @Getter
    @NoArgsConstructor
    public static class OrderItemRequest {
        private Long productSeq;
        private Integer qty;
    }
}
