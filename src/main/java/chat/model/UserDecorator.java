package chat.model;

public abstract class UserDecorator extends User {
    private User user;

    public UserDecorator(User user) {
        super(user);
    }

    public User getUser() {
        return user;
    }
}
