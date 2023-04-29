package models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private int id;
    private int team_id;
    private String departure;
    private String destination;
    private int passengers_number;
    private String plane;
}
