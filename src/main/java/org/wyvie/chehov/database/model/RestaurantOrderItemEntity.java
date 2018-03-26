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
}
