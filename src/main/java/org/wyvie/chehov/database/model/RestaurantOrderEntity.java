package org.wyvie.chehov.database.model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "restaurant_orders")
public class RestaurantOrderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(targetEntity = UserEntity.class)
    private UserEntity owner;

    @OneToMany(targetEntity = RestaurantOrderItemEntity.class,
            mappedBy = "restaurantOrder",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    private List<RestaurantOrderItemEntity> orderItems;

    @Column
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public List<RestaurantOrderItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<RestaurantOrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
