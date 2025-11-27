package knowdown.user_service.dto;

public class FriendRequest {
    private String friendUsername;

    //конструкторы
    public FriendRequest() {}

    public FriendRequest(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    //геттеры и сеттеры
    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }
}