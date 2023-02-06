package com.github.vivyteam.url.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Data;

@Data
@Entity(name = "shortened_url")
public class ShortenedUrl {
	@Id
	@GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
      name = "sequence-generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "user_sequence"),
        @Parameter(name = "initial_value", value = "1000001"),
        @Parameter(name = "increment_size", value = "1")
        }
    )
	private int id;
	
	@Column(name = "full_url")
	private String fullUrl;
	
	@Column(name = "hashed_url")
	private String hashedUrl;
}
