package com.codestates.team5.dailyclub.image.entity;

import com.codestates.team5.dailyclub.program.entity.Program;
import com.codestates.team5.dailyclub.validator.annotation.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DiscriminatorValue("P")
public class ProgramImage extends ImageFile {

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    private ProgramImage(Long id, Long size, String contentType, String originalName, byte[] bytes) {
        super(id, size, contentType, originalName, bytes);
    }
    public static ProgramImage from(Long size, String contentType, String originalName, byte[] bytes) {
        return new ProgramImage(null, size, contentType, originalName, bytes);
    }

    //양방향 연관관계 편의 메소드
    public void setProgram(Program program) {
        //기존 관계 제거
        if (this.program != null) {
            this.program.getProgramImages().remove(this);
        }

        this.program = program;
        program.getProgramImages().add(this);
    }

    @Override
    public void updateImageFile(ImageFile imageFile) {
        super.updateImageFile(imageFile);
    }
}
