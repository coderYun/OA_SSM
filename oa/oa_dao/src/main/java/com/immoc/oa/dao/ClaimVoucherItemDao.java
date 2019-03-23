package com.immoc.oa.dao;
import com.immoc.oa.entity.ClaimVoucherItem;
import com.immoc.oa.entity.Department;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository("claimVoucherItemDao")
public interface ClaimVoucherItemDao {
    void insert(ClaimVoucherItem claimVoucherItem);
    void update(ClaimVoucherItem claimVoucherItem);
    void delete(int id);
    List<ClaimVoucherItem> selectByClaimVoucher(int cvid);
}
