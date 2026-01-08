// package com.blog.blogger.service;

// import com.blog.blogger.models.Message;
// import com.blog.blogger.models.User;
// import com.blog.blogger.repository.MessageRepository;
// import com.blog.blogger.repository.UserRepository;
// import jakarta.transaction.Transactional;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// @Service
// public class MessageService {

//     @Autowired
//     private MessageRepository messageRepository;

//     @Autowired
//     private UserRepository userRepository;

//     /**
//      * Send a message from one user to another
//      */
//     @Transactional
//     public Message sendMessage(String senderUsername, Long receiverId, String content) {
//         User sender = userRepository.findByUsername(senderUsername)
//                 .orElseThrow(() -> new RuntimeException("Sender not found"));

//         User receiver = userRepository.findById(receiverId)
//                 .orElseThrow(() -> new RuntimeException("Receiver not found"));

//         // Prevent sending message to self
//         if (sender.getId().equals(receiver.getId())) {
//             throw new RuntimeException("Cannot send message to yourself");
//         }

//         Message message = Message.builder()
//                 .sender(sender)
//                 .receiver(receiver)
//                 .content(content)
//                 .read(false)
//                 .build();

//         return messageRepository.save(message);
//     }

//     /**
//      * Get conversation between current user and another user
//      */
//     public List<Map<String, Object>> getConversation(String currentUsername, Long otherUserId) {
//         User currentUser = userRepository.findByUsername(currentUsername)
//                 .orElseThrow(() -> new RuntimeException("Current user not found"));

//         User otherUser = userRepository.findById(otherUserId)
//                 .orElseThrow(() -> new RuntimeException("Other user not found"));

//         List<Message> messages = messageRepository.findConversationBetweenUsers(currentUser, otherUser);

//         return messages.stream()
//                 .map(msg -> {
//                     Map<String, Object> messageMap = new HashMap<>();
//                     messageMap.put("id", msg.getId());
//                     messageMap.put("content", msg.getContent());
//                     messageMap.put("createdAt", msg.getCreatedAt());
//                     messageMap.put("read", msg.getRead());

//                     // Include sender info
//                     Map<String, Object> senderMap = new HashMap<>();
//                     senderMap.put("id", msg.getSender().getId());
//                     senderMap.put("username", msg.getSender().getUsername());
//                     messageMap.put("sender", senderMap);

//                     // Include receiver info
//                     Map<String, Object> receiverMap = new HashMap<>();
//                     receiverMap.put("id", msg.getReceiver().getId());
//                     receiverMap.put("username", msg.getReceiver().getUsername());
//                     messageMap.put("receiver", receiverMap);

//                     // Flag if message is from current user
//                     messageMap.put("isSent", msg.getSender().getId().equals(currentUser.getId()));

//                     return messageMap;
//                 })
//                 .collect(Collectors.toList());
//     }

//     /**
//      * Get all conversations for current user
//      * Returns list of users with last message info
//      */
//     public List<Map<String, Object>> getConversations(String currentUsername) {
//         User currentUser = userRepository.findByUsername(currentUsername)
//                 .orElseThrow(() -> new RuntimeException("Current user not found"));

//         List<User> conversationPartners = messageRepository.findConversationPartners(currentUser);

//         return conversationPartners.stream()
//                 .map(partner -> {
//                     Map<String, Object> conversationMap = new HashMap<>();
//                     conversationMap.put("userId", partner.getId());
//                     conversationMap.put("username", partner.getUsername());
//                     conversationMap.put("email", partner.getEmail());

//                     // Get last message in conversation
//                     List<Message> conversation = messageRepository.findConversationBetweenUsers(currentUser, partner);
//                     if (!conversation.isEmpty()) {
//                         Message lastMessage = conversation.get(conversation.size() - 1);
//                         conversationMap.put("lastMessage", lastMessage.getContent());
//                         conversationMap.put("lastMessageTime", lastMessage.getCreatedAt());
//                         conversationMap.put("lastMessageFromMe", lastMessage.getSender().getId().equals(currentUser.getId()));
//                     }

//                     // Count unread messages from this user
//                     long unreadCount = conversation.stream()
//                             .filter(msg -> msg.getReceiver().getId().equals(currentUser.getId()) && !msg.getRead())
//                             .count();
//                     conversationMap.put("unreadCount", unreadCount);

//                     return conversationMap;
//                 })
//                 .collect(Collectors.toList());
//     }

//     /**
//      * Mark messages as read
//      */
//     @Transactional
//     public void markMessagesAsRead(String currentUsername, Long otherUserId) {
//         User currentUser = userRepository.findByUsername(currentUsername)
//                 .orElseThrow(() -> new RuntimeException("Current user not found"));

//         User otherUser = userRepository.findById(otherUserId)
//                 .orElseThrow(() -> new RuntimeException("Other user not found"));

//         List<Message> messages = messageRepository.findConversationBetweenUsers(currentUser, otherUser);

//         // Mark all messages from other user to current user as read
//         messages.stream()
//                 .filter(msg -> msg.getReceiver().getId().equals(currentUser.getId()) && !msg.getRead())
//                 .forEach(msg -> {
//                     msg.setRead(true);
//                     messageRepository.save(msg);
//                 });
//     }

//     /**
//      * Get count of unread messages for current user
//      */
//     public long getUnreadCount(String currentUsername) {
//         User currentUser = userRepository.findByUsername(currentUsername)
//                 .orElseThrow(() -> new RuntimeException("Current user not found"));

//         return messageRepository.countByReceiverAndReadFalse(currentUser);
//     }

//     /**
//      * Delete a message (only if sender)
//      */
//     @Transactional
//     public void deleteMessage(String currentUsername, Long messageId) {
//         User currentUser = userRepository.findByUsername(currentUsername)
//                 .orElseThrow(() -> new RuntimeException("Current user not found"));

//         Message message = messageRepository.findById(messageId)
//                 .orElseThrow(() -> new RuntimeException("Message not found"));

//         // Only sender can delete their message
//         if (!message.getSender().getId().equals(currentUser.getId())) {
//             throw new RuntimeException("You can only delete your own messages");
//         }

//         messageRepository.delete(message);
//     }
// }
