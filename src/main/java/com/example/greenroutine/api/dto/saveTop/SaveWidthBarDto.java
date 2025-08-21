package com.example.greenroutine.api.dto.saveTop;

import com.example.greenroutine.domain.SaveWidthBar;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveWidthBarDto {
    private Long id;
    private String category;
    private String ym;
    private Long target;
    private Long used;

    private double percent;
    private double progress;
    private String amountLabel;

    public static SaveWidthBarDto of(SaveWidthBar e) {
        long t = e.getTargetAmount() == null ? 0L : e.getTargetAmount();
        long u = e.getUsedAmount()    == null ? 0L : e.getUsedAmount();

        double pct = (t > 0) ? (u * 100.0 / t) : 0.0;
        pct = Math.max(0.0, Math.min(100.0, Math.round(pct * 10.0) / 10.0));

        double prog = (t > 0) ? (u * 1.0 / t) : 0.0;
        prog = Math.max(0.0, Math.min(1.0, prog));

        String label = String.format("%,d / %,d 원", u, t);

        return SaveWidthBarDto.builder()
                .id(e.getId())
                .category(e.getCategory())
                .ym(e.getYm())
                .target(t)
                .used(u)
                .percent(pct)
                .progress(prog)
                .amountLabel(label)
                .build();
    }
}
