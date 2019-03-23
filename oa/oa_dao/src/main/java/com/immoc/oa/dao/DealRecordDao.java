package com.immoc.oa.dao;
import com.immoc.oa.entity.DealRecord;
import com.immoc.oa.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository("dealRecordDao")
public interface DealRecordDao {
    void insert(DealRecord dealRecord);
    List<DealRecord> selectByClaimVoucher(int cvid);
}
