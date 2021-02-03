package chat.model;

public class AdminDecorator extends UserDecorator{
    public AdminDecorator(User user) {
        super(user);
    }
}
