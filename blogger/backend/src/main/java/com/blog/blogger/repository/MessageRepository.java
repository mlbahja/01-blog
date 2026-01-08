// package com.blog.blogger.repository;

// import com.blog.blogger.models.Message;
// import com.blog.blogger.models.User;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface MessageRepository extends JpaRepository<Message, Long> {

//     /**
//      * Get all messages between two users (conversation)
//      * Returns messages in chronological order
//      */
//     @Query("SELECT m FROM Message m WHERE " +
//            "(m.sender = :user1 AND m.receiver = :user2) OR " +
//            "(m.sender = :user2 AND m.receiver = :user1) " +
//            "ORDER BY m.createdAt ASC")
//     List<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

//     /**
//      * Get all messages sent by a user
//      */
//     List<Message> findBySenderOrderByCreatedAtDesc(User sender);

//     /**
//      * Get all messages received by a user
//      */
//     List<Message> findByReceiverOrderByCreatedAtDesc(User receiver);

//     /**
//      * Get unread messages for a user
//      */
//     List<Message> findByReceiverAndReadFalseOrderByCreatedAtDesc(User receiver);

//     /**
//      * Count unread messages for a user
//      */
//     long countByReceiverAndReadFalse(User receiver);

//     /**
//      * Get list of users that current user has conversations with
//      * Returns distinct users (both sent to and received from)
//      */
//     @Query("SELECT DISTINCT CASE " +
//            "WHEN m.sender = :user THEN m.receiver " +
//            "ELSE m.sender END " +
//            "FROM Message m WHERE m.sender = :user OR m.receiver = :user " +
//            "ORDER BY MAX(m.createdAt) DESC")
//     List<User> findConversationPartners(@Param("user") User user);
// }
