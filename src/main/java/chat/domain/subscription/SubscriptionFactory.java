package chat.domain.subscription;

import chat.core.Room;
import chat.core.Subscription;
import chat.core.User;
import chat.infrastructure.DB;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface SubscriptionFactory {

    Subscription createSubscriptionForRoom (Subscription subscription);
}
