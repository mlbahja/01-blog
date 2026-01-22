package com.blog.blogger.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.blogger.dto.ChangePasswordDTO;
import com.blog.blogger.dto.UpdateProfileDTO;
import com.blog.blogger.dto.UserProfileDTO;
import com.blog.blogger.models.Role;
import com.blog.blogger.models.User;
import com.blog.blogger.repositories.NotificationRepository;
import com.blog.blogger.repository.CommentLikeRepository;
import com.blog.blogger.repository.CommentRepository;
import com.blog.blogger.repository.PostLikeRepository;
import com.blog.blogger.repository.PostRepository;
import com.blog.blogger.repository.ReportRepository;
import com.blog.blogger.repository.SubscriptionRepository;
import com.blog.blogger.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;
    private final ReportRepository reportRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PostRepository postRepository,
            CommentRepository commentRepository,
            PostLikeRepository postLikeRepository,
            CommentLikeRepository commentLikeRepository,
            SubscriptionRepository subscriptionRepository,
            NotificationRepository notificationRepository,
            ReportRepository reportRepository) { // Fixed - only once!
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
        this.reportRepository = reportRepository;
    }
  

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

  
    public Optional<User> login(String identifier, String password) {
        if (identifier == null) {
            return Optional.empty();
        }

     
        Optional<User> existingUser = userRepository.findByEmail(identifier);
        if (existingUser.isEmpty()) {
           
            existingUser = userRepository.findByUsername(identifier);
        }

        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            return existingUser;
        }

        return Optional.empty();
    }

  

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserProfileDTO> getAllUserProfiles() {
        return userRepository.findAll().stream()
                .map(this::convertToProfileDTO)
                .collect(Collectors.toList());
    }

  

    public UserProfileDTO getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToProfileDTO(user);
    }

    public UserProfileDTO updateProfile(Long id, UpdateProfileDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName().isEmpty() ? null : dto.getFullName());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio().isEmpty() ? null : dto.getBio());
        }
        if (dto.getAvatar() != null) {
            String avatar = dto.getAvatar().trim();
            user.setAvatar(avatar.isEmpty() ? null : avatar);
        }
        if (dto.getProfilePictureUrl() != null) {
            
            String profilePicUrl = dto.getProfilePictureUrl().trim();
            user.setProfilePictureUrl(profilePicUrl.isEmpty() ? null : profilePicUrl);
        }

        User updatedUser = userRepository.save(user);
        return convertToProfileDTO(updatedUser);
    }

   

    public void changePassword(Long id, ChangePasswordDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

      
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

      
        if (dto.getNewPassword().length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

       
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        notificationRepository.deleteByUser(user);

        List<com.blog.blogger.models.Subscription> asFollower = subscriptionRepository.findByFollower(user);
        subscriptionRepository.deleteAll(asFollower);

        
        List<com.blog.blogger.models.Subscription> asFollowing = subscriptionRepository.findByFollowing(user);
        subscriptionRepository.deleteAll(asFollowing);

      
        List<com.blog.blogger.models.PostLike> postLikes = postLikeRepository.findAll().stream()
                .filter(like -> like.getUser().getId().equals(id))
                .collect(Collectors.toList());
        postLikeRepository.deleteAll(postLikes);

        
        List<com.blog.blogger.models.CommentLike> commentLikes = commentLikeRepository.findAll().stream()
                .filter(like -> like.getUser().getId().equals(id))
                .collect(Collectors.toList());
        commentLikeRepository.deleteAll(commentLikes);

        
        // List<com.blog.blogger.models.Message> sentMessages = messageRepository.findBySenderOrderByCreatedAtDesc(user);
        // messageRepository.deleteAll(sentMessages);

  
        // List<com.blog.blogger.models.Message> receivedMessages = messageRepository.findByReceiverOrderByCreatedAtDesc(user);
        // messageRepository.deleteAll(receivedMessages);

        notificationRepository.deleteByUser(user);

        List<com.blog.blogger.models.Post> posts = postRepository.findByAuthor(user);
        for (com.blog.blogger.models.Post post : posts) {
            postLikeRepository.deleteByPost(post);

            List<com.blog.blogger.models.Comment> postComments = commentRepository.findByPost(post);
            for (com.blog.blogger.models.Comment comment : postComments) {
                commentLikeRepository.deleteByComment(comment);
            }
            commentRepository.deleteAll(postComments);
            reportRepository.deleteByPost(post);
        }
        postRepository.deleteAll(posts);

        reportRepository.deleteByReporter(user);

        List<com.blog.blogger.models.Comment> comments = commentRepository.findAll().stream()
                .filter(comment -> comment.getAuthor().getId().equals(id))
                .collect(Collectors.toList());
        commentRepository.deleteAll(comments);

        
        userRepository.delete(user);
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsBanned(true);
        user.setBannedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void unbanUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsBanned(false);
        user.setBannedAt(null);
        userRepository.save(user);
    }

    public void changeUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setRole(newRole);
        userRepository.save(user);
    }

   

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

 

    public UserProfileDTO convertToProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .isBanned(user.getIsBanned())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    
}
