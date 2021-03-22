package com.fenix.spirometer.ble;

/**
 * 数据清洗，防止无效数据透传
 */
public class DataCleaner {
    private MeasureData preData;
    private MeasureData currentData;
    private long preTime;
    private long currentTime;

    public DataCleaner() {

    }

    // 若是异常数据，返回上次数据，或考虑增加脏数据标志
    public MeasureData clean(MeasureData measureData) {
        return measureData;
    }
}
