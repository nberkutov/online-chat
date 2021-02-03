package chat.model;

import chat.model.User;
import chat.model.UserDecorator;

public class ModeratorDecor extends UserDecorator {
    public ModeratorDecor(User user) {
        super(user);
    }
}
