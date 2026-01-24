package com.example.auth.entity;

import com.example.auth.dto.UserProfileResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor      // 기본 생성자 자동 생성
@AllArgsConstructor     // 매개변수 생성자 자동 생성
@Builder                //
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * users.id FK
     * DB 컬럼명: `user`
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", referencedColumnName = "id",nullable = false)
    private User user;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "address1", length = 100)
    private String address1;

    @Column(name = "address2", length = 100)
    private String address2;

    @Column(name = "bg_image", length = 500)
    private String bgImage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static UserProfileResponse toUserProfileResponse(User user, UserProfile userProfile) {
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUserId(user.getId());
        userProfileResponse.setEmail(user.getEmail());
        userProfileResponse.setName(user.getNickname());
        userProfileResponse.setProfileImage(user.getProfileImage());
        userProfileResponse.setProvider(user.getProvider());

        userProfileResponse.setUserProfileId(userProfile.getId());
        userProfileResponse.setLastName(userProfile.getLastName());
        userProfileResponse.setFirstName(userProfile.getFirstName());
        userProfileResponse.setPhoneNumber(userProfile.getPhoneNumber());
        userProfileResponse.setAddress1(userProfile.getAddress1());
        userProfileResponse.setAddress2(userProfile.getAddress2());
        userProfileResponse.setBgImage(userProfile.getBgImage());
        userProfileResponse.setCreatedAt(userProfile.getCreatedAt());

        return userProfileResponse;
    }

    public static UserProfileResponse toUserProfileResponse(User user) {
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUserId(user.getId());
        userProfileResponse.setEmail(user.getEmail());
        userProfileResponse.setName(user.getNickname());
        userProfileResponse.setProfileImage(user.getProfileImage());
        userProfileResponse.setProvider(user.getProvider());

        return userProfileResponse;
    }
}
