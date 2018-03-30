package org.wyvie.chehov.database.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    private int id;

    @Column
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    private boolean allowed;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int karma;

    @Column
    private LocalDateTime lastSetKarma;

    @Column
    private LocalDateTime lastSeen;

    @OneToMany(targetEntity = UserOrderItemEntity.class,
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<UserOrderItemEntity> orderedItems;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public LocalDateTime getLastSetKarma() {
        return lastSetKarma;
    }

    public void setLastSetKarma(LocalDateTime lastSetKarma) {
        this.lastSetKarma = lastSetKarma;
    }

    public List<UserOrderItemEntity> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<UserOrderItemEntity> orderedItems) {
        this.orderedItems = orderedItems;
    }
}
