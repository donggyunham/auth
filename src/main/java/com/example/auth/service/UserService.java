package com.example.auth.service;

import com.example.auth.dto.UserProfileResponse;
import com.example.auth.dto.UserProfileUpdateRequest;
import com.example.auth.entity.User;
import com.example.auth.entity.UserProfile;
import com.example.auth.exception.InvalidCredentialException;
import com.example.auth.repository.UserProfileRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    /**
     * 새로운 User Profile 정보를 기본값으로 생성한다.
     * @param userId
     * @return
     */
    @Transactional
    public UserProfileResponse newUserProfile(Long userId) {
        //System.out.println("UserService::getUserProfile() " + userId);
        // Optional<User> optUser = userRepository.findById(userId);
        // 사용자 테이블에서 userId를 이용하여 사용자 정보를 가져온다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    return new InvalidCredentialException("존재하지 않는 사용자입니다.");
                });

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();

        // 기본값으로 저장하고 저장된 결과를 받아옴
        UserProfile saveduserProfile = userProfileRepository.save(userProfile);

        return UserProfile.toUserProfileResponse(user, saveduserProfile);
    }

    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest userProfileReq) {

        // 로그인한 사용자를 찾는다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    // 사용자를 찾지 못하면 Runtime Error를 발생시키고 끝.
                    return new InvalidCredentialException("존재하지 않는 사용자입니다.");
                });

        // User Table 정보의 값을 수정
        user.setNickname(userProfileReq.getName());
        user.setProfileImage(userProfileReq.getProfileImage());
        userRepository.save(user);
        
        System.out.println("사용자 정보 저장 완료");
        
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    System.out.println("사용자 ID : " + userId);
                    Optional<User> userOpt = userRepository.findById(userId);
                    
                    System.out.println("사용자 프로필 정보 생성 완료");

                    return UserProfile.builder()
                            .user(userOpt.get())
                            .build();
                });

        // 사용자가 입력한 정보로 데이터를 생성(수정)하고 저장.
        userProfile.setLastName(userProfileReq.getLastName());
        userProfile.setFirstName(userProfileReq.getFirstName());
        userProfile.setAddress1(userProfileReq.getAddress1());
        userProfile.setAddress2(userProfileReq.getAddress2());
        userProfile.setPhoneNumber(userProfileReq.getPhoneNumber());
        userProfile.setBgImage(userProfileReq.getBgImage());

        System.out.println("신규 프로필에 입력된 프로필 정보 수정");
        
        // 기본값으로 저장하고 저장된 결과를 받아옴
        UserProfile saveduserProfile = userProfileRepository.save(userProfile);

        System.out.println("수정한 프로필 정보 저장");

        // UserProfileRepository반환값 생성하여
        return UserProfile.toUserProfileResponse(user, saveduserProfile);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        //System.out.println("UserService::getUserProfile() " + userId);
        // Optional<User> optUser = userRepository.findById(userId);
        // 사용자 테이블에서 userId를 이용하여 사용자 정보를 가져온다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                            return new InvalidCredentialException("존재하지 않는 사용자입니다.");
                        });

        //System.out.println("UserService::getUserProfile() " + "findById() 성공" + user.getEmail());

        // 사용자 프로필 테이블에서 userId를 이용하여 프로필 정보를 가져온다.
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> {
                    return new InvalidCredentialException("존재하지 않는 사용자입니다.");
                });

        //System.out.println("UserService::getUserProfile() " + "findByUser() 성공" + user.getEmail());

        // userProfileResponse 인스턴스에 데이터를 저장하고 반환한다.
        return UserProfile.toUserProfileResponse(user, userProfile);
    }
}
