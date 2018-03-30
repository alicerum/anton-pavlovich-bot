package org.wyvie.chehov.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wyvie.chehov.database.model.RestaurantOrderEntity;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<RestaurantOrderEntity, UUID> {
}
