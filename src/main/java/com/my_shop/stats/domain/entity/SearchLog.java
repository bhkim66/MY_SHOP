package com.my_shop.stats.domain.entity;

import com.my_shop.member.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_logs", indexes = {
        @Index(name = "idx_search_log_keyword_date", columnList = "search_keyword, searched_at"),
        @Index(name = "idx_search_log_user_date", columnList = "user_seq, searched_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user;

    @Column(name = "search_keyword", nullable = false, length = 200)
    private String searchKeyword;

    @Column(name = "result_count")
    private Integer resultCount;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;
}
