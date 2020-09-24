package chat.domain;

import chat.core.User;
import chat.infrastructure.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public interface UserFactory {

    User createUser (User user);

}
