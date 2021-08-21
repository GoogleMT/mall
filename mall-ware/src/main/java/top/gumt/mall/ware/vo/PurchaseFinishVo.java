package top.gumt.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseFinishVo {
    @NotNull
    private Long id;
    private List<PurchaseFinishItem> items;
}
