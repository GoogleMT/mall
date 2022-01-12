package top.gumt.common.mq;

import lombok.Data;

@Data
public class StockLockedTo {
    /**
     * 库存工作单的ID
     */
    private Long id;

    /**
     * 工作单详情
     */
    private StockDetailTo detailTo;
}
