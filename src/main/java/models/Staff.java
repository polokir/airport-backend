package models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    private int id;
    private int speciality_id;
    private String name;
    private float salary;
}
