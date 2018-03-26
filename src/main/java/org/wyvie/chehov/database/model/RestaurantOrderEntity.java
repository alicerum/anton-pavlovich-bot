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
            fetch = FetchType.EAGER)
    private List<RestaurantOrderItemEntity> orderItems;

    @Column
    private boolean isOpen;
}
