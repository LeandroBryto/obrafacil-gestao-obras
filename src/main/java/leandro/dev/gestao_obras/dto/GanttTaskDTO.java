package leandro.dev.gestao_obras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GanttTaskDTO {
    private Long id;
    private String text;
    private String start_date;
    private String end_date;
    private Integer duration;
    private  Double progress;
    private Long parent;
    private  boolean open = true;
    private String string;
    private String real_start;
    private String real_end;
}
