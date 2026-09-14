package com.railgo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_number", nullable = false, unique = true, length = 10)
    private String trainNumber;

    @Column(name = "train_name", nullable = false, length = 100)
    private String trainName;

    @Column(name = "source_station", nullable = false, length = 50)
    private String sourceStation;

    @Column(name = "destination_station", nullable = false, length = 50)
    private String destinationStation;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    // Default constructor is required by JPA
    public Train() {
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getSourceStation() { return sourceStation; }
    public void setSourceStation(String sourceStation) { this.sourceStation = sourceStation; }

    public String getDestinationStation() { return destinationStation; }
    public void setDestinationStation(String destinationStation) { this.destinationStation = destinationStation; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
}