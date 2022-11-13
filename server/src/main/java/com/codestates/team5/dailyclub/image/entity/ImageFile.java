package com.codestates.team5.dailyclub.image.entity;

import com.codestates.team5.dailyclub.validator.annotation.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED) //조인 테이블 방식으로 구현
@DiscriminatorColumn //default name -> "DTYPE"
public abstract class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long size;

    @Image
    private String contentType;

    private String originalName;

    @Lob
    private byte[] bytes;

    //비즈니스 메소드
     void updateImageFile(ImageFile imageFile) {
        this.size = imageFile.getSize();
        this.contentType = imageFile.getContentType();
        this.originalName = imageFile.getOriginalName();
        this.bytes = imageFile.getBytes();
    }
}
