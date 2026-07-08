package com.worksonourmachines.marketplace.module.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "modules", schema = "marketplace")
public class MarketplaceModuleEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "difficulty_hint", nullable = false, columnDefinition = "text")
    private String difficultyHint;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<MarketplaceModuleTopicEntity> topics = new ArrayList<>();

    protected MarketplaceModuleEntity() {
    }

    public MarketplaceModuleEntity(String code, String title, String description, String difficultyHint) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.difficultyHint = difficultyHint;
    }

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficultyHint() {
        return difficultyHint;
    }

    public void setDifficultyHint(String difficultyHint) {
        this.difficultyHint = difficultyHint;
    }

    public List<MarketplaceModuleTopicEntity> getTopics() {
        return topics;
    }

    public void replaceTopics(List<MarketplaceModuleTopicEntity> topics) {
        this.topics.clear();
        topics.forEach(this::addTopic);
    }

    public void addTopic(MarketplaceModuleTopicEntity topic) {
        topic.setModule(this);
        topics.add(topic);
    }
}
