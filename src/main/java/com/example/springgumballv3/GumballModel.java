package com.example.springgumballv3;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;



@Entity
@Table(indexes=@Index(name = "altIndex", columnList = "serialNumber", unique = true))
@Data
@RequiredArgsConstructor
class GumballModel {

    private @Id @GeneratedValue Long id;

    @Column(nullable=false)     private String serialNumber ;
                                private String modelNumber ;
                                private Integer countGumballs ;
    
}

