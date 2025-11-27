package knowdown.user_service.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //пользователь который отправил запрос
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //пользователь который получил запрос
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    //конструкторы
    public Friendship() {}

    public Friendship(User user, User friend, FriendshipStatus status) {
        this.user = user;
        this.friend = friend;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    //геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getFriend() { return friend; }
    public void setFriend(User friend) { this.friend = friend; }

    public FriendshipStatus getStatus() { return status; }
    public void setStatus(FriendshipStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}