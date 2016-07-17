package cz.neumimto.dei.entity.database.structure;

import javax.persistence.*;

/**
 * Created by ja on 8.7.16.
 */
@Entity
@Table(name = "dei_table")
public class BuildResources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
