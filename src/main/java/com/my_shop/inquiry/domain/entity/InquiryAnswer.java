package com.my_shop.inquiry.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiry_answers", indexes = {
        @Index(name = "idx_inquiry_answer_inquiry", columnList = "inquiry_seq")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_seq", nullable = false)
    private Inquiry inquiry;

    @Column(name = "answer_content", nullable = false, columnDefinition = "TEXT")
    private String answerContent;
}
