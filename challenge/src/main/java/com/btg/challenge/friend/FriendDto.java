package com.btg.challenge.friend;

public class FriendDto {
    private Long id;
    private String name;

    public FriendDto() {
    }

    public FriendDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
