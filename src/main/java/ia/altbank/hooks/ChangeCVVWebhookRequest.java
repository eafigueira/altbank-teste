package ia.altbank.hooks;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeCVVWebhookRequest {
    private String account_id;
    private String card_id;
    private String next_cvv;
    private LocalDateTime expiration_date;

}
