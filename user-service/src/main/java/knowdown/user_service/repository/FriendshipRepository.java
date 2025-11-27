package knowdown.user_service.repository;

import knowdown.user_service.domain.Friendship;
import knowdown.user_service.domain.FriendshipStatus;
import knowdown.user_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    //поиск конкретнтй дружбу между двумя пользователями
    Optional<Friendship> findByUserAndFriend(User user, User friend);

    //найти все запросы где пользователь отправитель с определенным статусом
    List<Friendship> findByUserAndStatus(User user, FriendshipStatus status);

    //найти все запросы где пользователь получатель с определенным статусом
    List<Friendship> findByFriendAndStatus(User friend, FriendshipStatus status);

    //найти всех принятых друзей пользователя
    @Query("SELECT f FROM Friendship f WHERE (f.user = :user OR f.friend = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("user") User user);

    //проверерка существует ли уже дружба между пользователями (любого статуса)
    boolean existsByUserAndFriend(User user, User friend);

    Optional<Friendship> findByUserAndFriendAndStatus(User user, User friend, FriendshipStatus status);

}
