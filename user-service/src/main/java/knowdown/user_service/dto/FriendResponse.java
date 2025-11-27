package knowdown.user_service.dto;

public class FriendResponse {
    private String username;
    private String friendshipStatus; // ACCEPTED, PENDING

    //конструкторы
    public FriendResponse() {}

    public FriendResponse(String username, String friendshipStatus) {
        this.username = username;
        this.friendshipStatus = friendshipStatus;
    }

    //геттеры и сеттеры
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }
}