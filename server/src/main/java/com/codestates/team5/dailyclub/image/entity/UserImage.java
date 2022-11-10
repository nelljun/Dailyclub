package com.codestates.team5.dailyclub.image.entity;

import com.codestates.team5.dailyclub.image.dto.UserImageDto;
import com.codestates.team5.dailyclub.program.entity.Program;
import com.codestates.team5.dailyclub.user.entity.User;
import com.codestates.team5.dailyclub.validator.annotation.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DiscriminatorValue("U")
public class UserImage extends ImageFile {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private UserImage(Long id, Long size, String contentType, String originalName, byte[] bytes) {
        super(id, size, contentType, originalName, bytes);
    }
    public static UserImage from(Long size, String contentType, String originalName, byte[] bytes) {
        return new UserImage(null, size, contentType, originalName, bytes);
    }

    //양방향 연관관계 편의 메소드
    public void setUser(User user) {
        //기존 관계 제거
        if (this.user != null) {
            this.user.getUserImages().remove(this);
        }
        this.user = user;

        user.getUserImages().add(this);
    }

    @Override
    public void updateImageFile(ImageFile imageFile) {
        super.updateImageFile(imageFile);
    }
}
