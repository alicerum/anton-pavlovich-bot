package org.wyvie.chehov.database.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_items_ordered")
public class UserOrderItemEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(targetEntity = UserEntity.class,
            fetch = FetchType.EAGER
    )
    private UserEntity user;

    @OneToOne(targetEntity = RestaurantOrderItemEntity.class,
            fetch = FetchType.EAGER
    )
    private RestaurantOrderItemEntity orderItem;

    @Column
    private int amount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public RestaurantOrderItemEntity getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(RestaurantOrderItemEntity orderItem) {
        this.orderItem = orderItem;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
