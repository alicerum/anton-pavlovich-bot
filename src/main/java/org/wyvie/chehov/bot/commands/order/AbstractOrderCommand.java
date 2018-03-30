package org.wyvie.chehov.bot.commands.order;

import org.wyvie.chehov.bot.commands.CommandHandler;
import org.wyvie.chehov.database.model.RestaurantOrderEntity;
import org.wyvie.chehov.database.model.UserEntity;
import org.wyvie.chehov.database.repository.OrderRepository;
import org.wyvie.chehov.database.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractOrderCommand implements CommandHandler {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public AbstractOrderCommand(OrderRepository orderRepository,
                                UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    protected void createOrder(UserEntity owner, String name) {
        RestaurantOrderEntity restaurantOrderEntity = new RestaurantOrderEntity();
        restaurantOrderEntity.setId(UUID.randomUUID());
        restaurantOrderEntity.setOwner(owner);
        restaurantOrderEntity.setName(name);

        orderRepository.save(restaurantOrderEntity);
    }

    protected Optional<UserEntity> getUserById(int id) {
        return userRepository.findById(id);
    }
}
