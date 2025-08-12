package com.example.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/* Auditing 기능의 적용을 받음 */
@EntityListeners(AuditingEntityListener.class)
    /* 이 자체로는 엔티티(테이블과 매핑)가 아니며, 공통 속성을 상속하기 위한 매핑정보만을 담당*/
@MappedSuperclass
@Getter @Setter
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regTime;      // 생성 시각

    @LastModifiedDate
    private LocalDateTime updateTime;       //수정 시각
}
