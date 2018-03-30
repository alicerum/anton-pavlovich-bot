package org.wyvie.chehov.database.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "restaurant_order_items",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "name_unique",
                    columnNames = {"restaurant_order_id", "name"}
            )
        }
)
public class RestaurantOrderItemEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(targetEntity = RestaurantOrderItemEntity.class)
    private RestaurantOrderEntity restaurantOrder;

    @Column
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RestaurantOrderEntity getRestaurantOrder() {
        return restaurantOrder;
    }

    public void setRestaurantOrder(RestaurantOrderEntity restaurantOrder) {
        this.restaurantOrder = restaurantOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
