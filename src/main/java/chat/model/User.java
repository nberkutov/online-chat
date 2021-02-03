package chat.model;

import chat.server.UserRole;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String login;
    private DataOutputStream output;
    private LocalDateTime kickDate;

    public User() {
        kickDate = LocalDateTime.MIN;
    }

    public User(User user) {
        this.login = user.login;
        this.output = user.getOutput();
        this.kickDate = user.getKickDate();
    }

    public User(String login, DataOutputStream output, UserRole role) {
        this();
        this.login = login;
        this.output = output;
    }

    public User(String login, DataOutputStream output) {
        this(login, output, UserRole.DEFAULT);
    }

    public String getLogin() {
        return login;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(login, user.login)) return false;
        if (!Objects.equals(output, user.output)) return false;
        return Objects.equals(kickDate, user.kickDate);
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (output != null ? output.hashCode() : 0);
        result = 31 * result + (kickDate != null ? kickDate.hashCode() : 0);
        return result;
    }

    public LocalDateTime getKickDate() {
        return kickDate;
    }

    public void setKickDate(LocalDateTime kickDate) {
        this.kickDate = kickDate;
    }
}
