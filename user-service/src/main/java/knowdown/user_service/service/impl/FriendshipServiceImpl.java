package knowdown.user_service.service.impl;

import knowdown.user_service.domain.Friendship;
import knowdown.user_service.domain.FriendshipStatus;
import knowdown.user_service.domain.User;
import knowdown.user_service.repository.FriendshipRepository;
import knowdown.user_service.repository.UserRepository;
import knowdown.user_service.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean sendFriendRequest(String username, String friendUsername) {
        if (username.equals(friendUsername)) {
            return false; // Нельзя добавить самого себя
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        //проверить не существует ли уже дружба в любом направлении
        if (friendshipRepository.existsByUserAndFriend(user, friend) ||
                friendshipRepository.existsByUserAndFriend(friend, user)) {
            return false; // Уже существует
        }

        //создать запрос в друзья
        Friendship friendship = new Friendship(user, friend, FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
        return true;
    }

    @Override
    public boolean acceptFriendRequest(String username, String friendUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        //ищем pending запрос где friend является отправителем, а user - получателем
        Optional<Friendship> friendshipOpt = friendshipRepository.findByUserAndFriendAndStatus(
                friend, user, FriendshipStatus.PENDING);

        if (friendshipOpt.isPresent()) {
            Friendship friendship = friendshipOpt.get();
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepository.save(friendship);
            return true;
        }

        //если не нашли, ищем в обратную сторону (где user отправитель, friend получатель)
        friendshipOpt = friendshipRepository.findByUserAndFriendAndStatus(
                user, friend, FriendshipStatus.PENDING);

        if (friendshipOpt.isPresent()) {
            Friendship friendship = friendshipOpt.get();
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipRepository.save(friendship);
            return true;
        }

        return false;
    }

    @Override
    public boolean rejectFriendRequest(String username, String friendUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Optional<Friendship> friendshipOpt = friendshipRepository.findByUserAndFriendAndStatus(
                friend, user, FriendshipStatus.PENDING);

        if (friendshipOpt.isPresent()) {
            friendshipRepository.delete(friendshipOpt.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFriend(String username, String friendUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        //найти accepted дружбу в любом направлении
        Optional<Friendship> friendship1 = friendshipRepository.findByUserAndFriendAndStatus(
                user, friend, FriendshipStatus.ACCEPTED);
        Optional<Friendship> friendship2 = friendshipRepository.findByUserAndFriendAndStatus(
                friend, user, FriendshipStatus.ACCEPTED);

        if (friendship1.isPresent()) {
            friendshipRepository.delete(friendship1.get());
            return true;
        } else if (friendship2.isPresent()) {
            friendshipRepository.delete(friendship2.get());
            return true;
        }
        return false;
    }

    @Override
    public List<Friendship> getFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return friendshipRepository.findAcceptedFriendships(user);
    }

    @Override
    public List<Friendship> getPendingRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return friendshipRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);
    }

    @Override
    public List<Friendship> getSentRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return friendshipRepository.findByUserAndStatus(user, FriendshipStatus.PENDING);
    }
}