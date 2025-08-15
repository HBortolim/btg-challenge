package com.btg.challenge.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaystationGameDto {

    private int id;
    private String name;
    private List<String> genre;
    private List<String> developers;
    private List<String> publisher;

    @JsonProperty("release_dates")
    private Map<String, String> releaseDates;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
        this.developers = developers;
    }

    public List<String> getPublisher() {
        return publisher;
    }

    public void setPublisher(List<String> publisher) {
        this.publisher = publisher;
    }

    public Map<String, String> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(Map<String, String> releaseDates) {
        this.releaseDates = releaseDates;
    }
}
