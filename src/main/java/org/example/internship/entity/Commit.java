package org.example.internship.entity;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Commit {
    private String message;
    private String url;
    private String author;
    private Date timestamp;
    private List<String> added;
    private List<String> modified;
    private List<String> removed;
}
