package com.codestates.team5.dailyclub.user.service;

import com.codestates.team5.dailyclub.image.entity.UserImage;
import com.codestates.team5.dailyclub.image.repository.UserImageRepository;
import com.codestates.team5.dailyclub.refreshToken.RefreshTokenRepository;
import com.codestates.team5.dailyclub.throwable.entity.BusinessLogicException;
import com.codestates.team5.dailyclub.throwable.entity.ExceptionCode;
import com.codestates.team5.dailyclub.user.dto.UserDto;
import com.codestates.team5.dailyclub.user.entity.User;
import com.codestates.team5.dailyclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(UserDto.Post requestBody) {
        User user = User.builder()
                .loginId(requestBody.getLoginId())
                .password(bCryptPasswordEncoder.encode(requestBody.getPassword()))
                .nickname(requestBody.getNickname())
                .email(requestBody.getEmail())
                .kind(50)
                .role(User.Role.USER)
                .build();
        return userRepository.save(user);
    }

    public User updateUser(Long loginUserId, User userFromPatchDto, Long userImageId, MultipartFile multipartFile) throws IOException {
        User findUser
                = userRepository.findById(userFromPatchDto.getId())
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        if(!findUser.getId().equals(loginUserId)) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_UPDATE_USERS);
        }

        //닉네임, 자기소개 둘 다 변경 요청
        if(userFromPatchDto.getNickname() != findUser.getNickname()
                && userFromPatchDto.getIntroduction() != findUser.getIntroduction()) {
            findUser.updateAll(userFromPatchDto.getNickname(), userFromPatchDto.getIntroduction());
            //닉네임 변경 요청
        }else if(userFromPatchDto.getNickname() != findUser.getNickname()
                && userFromPatchDto.getIntroduction() == findUser.getIntroduction()) {
            findUser.updateNickname(userFromPatchDto.getNickname());
            //자기소개 변경 요청
        }else if(userFromPatchDto.getNickname() == findUser.getNickname()
                && userFromPatchDto.getIntroduction() != findUser.getIntroduction()) {
            findUser.updateIntroduction(userFromPatchDto.getIntroduction());
        }

        if(multipartFile == null && userImageId == null) {
            return userRepository.save(findUser);
        }else if(multipartFile != null && userImageId == null) {
            UserImage userImage = parseToUserImage(multipartFile);
            userImage.setUser(findUser);
            userImageRepository.save(userImage);
        }else if (multipartFile == null && userImageId !=null) {
            userImageRepository.deleteById(userImageId);
        }else if (multipartFile != null && userImageId !=null) {
            UserImage findUserImage
                    =userImageRepository.findById(userImageId)
                    .orElseThrow(() ->
                            new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND));
            UserImage userImage = parseToUserImage(multipartFile);
            findUserImage.updateImageFile(userImage);
        }
        return userRepository.save(findUser);
    }
    private UserImage parseToUserImage(MultipartFile multipartFile) throws IOException {
        String contentType = multipartFile.getContentType();
        log.info("contentType : {}", contentType);

        long size = multipartFile.getSize();
        String originalName = multipartFile.getOriginalFilename();
        byte[] bytes = multipartFile.getBytes();

        return UserImage.from(size, contentType, originalName, bytes);
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );
    }

    public Page<User> findUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("nickname").descending()));

    }

    public void deleteUser(Long id, Long loginUserId) {
        User findUser = userRepository.findById(id).orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );

        if(!findUser.getId().equals(loginUserId)) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_DELETE_USERS);
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public void logoutUser(Long id, Long loginUserId) {
        User findUser = userRepository.findById(id).orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );
        String loginId = findUser.getLoginId();
        refreshTokenRepository.deleteByLoginId(loginId);
    }
}

