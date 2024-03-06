package com.bank.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "database_sequences")
@Getter
@Setter
@ToString
public class DatabaseSequence {
    @Id
    private String id;
    private long seq;

    // Getters and setters
}
