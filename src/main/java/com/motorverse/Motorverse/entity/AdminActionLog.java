package com.motorverse.Motorverse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_action_logs")
public class AdminActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "admin_id", nullable = false)
    private int adminId;

    @Column(name = "action", nullable = false)
    private String action; // e.g., UPDATE_LISTING, DELETE_LISTING

    @Column(name = "entity_type", nullable = false)
    private String entityType; // e.g., LISTING, USER

    @Column(name = "entity_id", nullable = false)
    private int entityId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}