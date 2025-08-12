package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* member 테이블의 member_id(pk)를 참조하는 cart 테이블의 member_id(FK)
    * @JoinColumn()어노테이션에 들어가는 name => FK 컬럼 이름 = 부모 테이블의 PK 컬럼 이름 */
    @OneToOne               //(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    //private Long member_id ==> 이렇게 표현x

}
