package org.example.shelftoshelf.Dto.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponseDto {

    private long bookCount;
    private long customerCount;
    private long storeCount;
}
