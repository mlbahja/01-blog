package com.blog.blogger.service;

import com.blog.blogger.models.Comment;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.Response;
import com.blog.blogger.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @PostConstruct
    @Transactional
    public void init() {
        // Only initialize if database is empty
        if (postRepository.count() == 0) {
            initializeSamplePosts();
        }
    }

    private void initializeSamplePosts() {
        // Post 1: Getting Started with Spring Boot
        Post post1 = new Post(
            "Getting Started with Spring Boot: A Complete Guide",
            "Spring Boot has revolutionized Java development by simplifying configuration and setup. In this comprehensive guide, we'll explore the fundamentals of Spring Boot, from creating your first application to understanding dependency injection and auto-configuration. Whether you're a beginner or an experienced developer, this tutorial will help you master the essentials of building modern Java applications with Spring Boot. We'll cover project setup, creating REST APIs, connecting to databases, and deploying your application to production.",
            "Sarah Johnson",
            45,
            List.of("java", "spring-boot", "tutorial", "backend")
        );

        Comment comment1 = new Comment("Michael Chen", "Excellent tutorial! The explanations are crystal clear. Helped me get started with my first Spring Boot project.", 12);
        Response response1 = new Response("Sarah Johnson", "Thank you Michael! I'm so glad it was helpful. Let me know if you have any questions!");
        comment1.addResponse(response1);
        post1.addComment(comment1);

        Comment comment2 = new Comment("Emma Rodriguez", "This is exactly what I needed. The step-by-step approach made it so easy to follow along.", 8);
        post1.addComment(comment2);

        Comment comment3 = new Comment("David Kim", "Great article! Could you also cover Spring Security in a future post?", 5);
        Response response2 = new Response("Sarah Johnson", "Absolutely! Spring Security tutorial is coming next week. Stay tuned!");
        comment3.addResponse(response2);
        post1.addComment(comment3);

        postRepository.save(post1);

        // Post 2: The Future of AI and Machine Learning
        Post post2 = new Post(
            "The Future of AI: How Machine Learning is Transforming Our World",
            "Artificial Intelligence and Machine Learning are no longer just buzzwords - they're actively reshaping industries and our daily lives. From healthcare diagnostics to autonomous vehicles, AI is making the impossible possible. In this article, we dive deep into the current state of AI technology, explore real-world applications, and discuss what the future holds. We'll examine ethical considerations, discuss the impact on jobs, and look at emerging trends like generative AI, reinforcement learning, and neural networks. Join us as we explore how AI is revolutionizing everything from customer service to scientific research.",
            "Dr. Alex Thompson",
            78,
            List.of("ai", "machine-learning", "technology", "future")
        );

        Comment comment4 = new Comment("Lisa Wang", "Fascinating read! The section on ethical AI was particularly thought-provoking.", 15);
        Response response3 = new Response("Dr. Alex Thompson", "Thanks Lisa! Ethics in AI is crucial and often overlooked.");
        comment4.addResponse(response3);
        post2.addComment(comment4);

        Comment comment5 = new Comment("James Wilson", "I work in AI research and this is one of the most accurate overviews I've read. Well done!", 20);
        post2.addComment(comment5);

        Comment comment6 = new Comment("Nina Patel", "As someone new to AI, this article was incredibly informative without being overwhelming.", 10);
        post2.addComment(comment6);

        Comment comment7 = new Comment("Carlos Martinez", "Could you recommend some resources for learning more about neural networks?", 6);
        Response response4 = new Response("Dr. Alex Thompson", "Check out Andrew Ng's course on Coursera - it's excellent for beginners!");
        comment7.addResponse(response4);
        post2.addComment(comment7);

        postRepository.save(post2);

        // Post 3: Healthy Living and Fitness
        Post post3 = new Post(
            "10 Simple Habits That Will Transform Your Health in 2025",
            "Health and wellness don't have to be complicated. In fact, the most effective changes are often the simplest ones. In this post, we're sharing 10 science-backed habits that can dramatically improve your physical and mental well-being. From the power of morning routines to the importance of quality sleep, we'll explore practical strategies you can implement today. Learn about the benefits of intermittent fasting, discover how to build sustainable exercise habits, and understand why hydration is more important than you think. These aren't fad diets or extreme workout regimens - just simple, proven techniques that anyone can adopt for lasting health improvements.",
            "Jennifer Martinez",
            62,
            List.of("health", "fitness", "wellness", "lifestyle")
        );

        Comment comment8 = new Comment("Robert Lee", "I started implementing these habits 2 weeks ago and already feel more energetic!", 18);
        Response response5 = new Response("Jennifer Martinez", "That's wonderful Robert! Keep it up, the benefits compound over time!");
        comment8.addResponse(response5);
        post3.addComment(comment8);

        Comment comment9 = new Comment("Maria Garcia", "The tips on sleep hygiene were game-changers for me. Finally sleeping through the night!", 14);
        post3.addComment(comment9);

        Comment comment10 = new Comment("Tom Anderson", "Love the practical approach. No extreme diets, just sustainable changes.", 9);
        post3.addComment(comment10);

        postRepository.save(post3);

        // Post 4: Web Development Trends
        Post post4 = new Post(
            "Web Development in 2025: Top Trends You Can't Ignore",
            "The web development landscape is evolving faster than ever. As we move through 2025, new frameworks, tools, and methodologies are emerging that are reshaping how we build for the web. In this comprehensive overview, we'll explore the most significant trends including the rise of AI-assisted coding, the continued dominance of React and its competitors, the growing importance of Web3 technologies, and the shift towards serverless architectures. We'll also discuss progressive web apps (PWAs), the impact of WebAssembly, and why performance optimization has become more critical than ever. Whether you're a seasoned developer or just starting out, understanding these trends will help you stay ahead of the curve and build better, faster, more secure web applications.",
            "Kevin Park",
            91,
            List.of("web-development", "javascript", "programming", "trends")
        );

        Comment comment11 = new Comment("Sophie Turner", "The section on AI-assisted coding tools is spot on! GitHub Copilot has changed my workflow completely.", 22);
        Response response6 = new Response("Kevin Park", "Right? It's amazing how much it speeds up development while maintaining quality.");
        comment11.addResponse(response6);
        post4.addComment(comment11);

        Comment comment12 = new Comment("Andre Silva", "Great overview! Would love to see a deep dive on WebAssembly in a future post.", 16);
        Response response7 = new Response("Kevin Park", "WebAssembly deep dive is definitely on my list! Coming soon!");
        comment12.addResponse(response7);
        post4.addComment(comment12);

        Comment comment13 = new Comment("Yuki Tanaka", "As a frontend developer, this helped me understand where the industry is heading. Thank you!", 11);
        post4.addComment(comment13);

        Comment comment14 = new Comment("Omar Hassan", "The performance optimization tips are gold. Implementing them on my projects now.", 13);
        post4.addComment(comment14);

        Comment comment15 = new Comment("Rachel Green", "Serverless is definitely the future. Already seeing huge cost savings with our migration.", 8);
        post4.addComment(comment15);

        postRepository.save(post4);
    }

    public List<Post> getAllPosts() {
        return postRepository.findByOrderByCreatedAtDesc();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
