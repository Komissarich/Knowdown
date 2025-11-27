package knowdown.user_service.service;

import knowdown.user_service.domain.Friendship;
import java.util.List;

public interface FriendshipService {

    //отправить запрос в друзья
    boolean sendFriendRequest(String username, String friendUsername);

    //принять запрос в друзья
    boolean acceptFriendRequest(String username, String friendUsername);

    //отклонить запрос в друзья
    boolean rejectFriendRequest(String username, String friendUsername);

    //удалить из друзей
    boolean removeFriend(String username, String friendUsername);

    //получить список друзей
    List<Friendship> getFriends(String username);

    //получить входящие запросы в друзья
    List<Friendship> getPendingRequests(String username);

    //получить исходящие запросы в друзья
    List<Friendship> getSentRequests(String username);
}