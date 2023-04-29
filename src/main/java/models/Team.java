package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private int id;
    private int first_pilot_id;
    private int second_pilot_id;
    private int first_stuard_id;
    private int second_stuard_id;
    private int radio_spec_id;
    private int shturman_id;
}
