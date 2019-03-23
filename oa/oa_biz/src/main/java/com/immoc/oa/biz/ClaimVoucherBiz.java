package com.immoc.oa.biz;
import com.immoc.oa.entity.ClaimVoucher;
import com.immoc.oa.entity.ClaimVoucherItem;
import com.immoc.oa.entity.DealRecord;

import java.util.List;

public interface ClaimVoucherBiz {
    void save(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items);
    ClaimVoucher get(int id);
    //得到报销单的条目信息
    List<ClaimVoucherItem> getItems(int cvid);
    //得到报销单的处理记录信息
    List<DealRecord> getRecords(int cvid);
    //展示个人的报销单信息
    List<ClaimVoucher> getForSelf(String sn);
    //获取个人的待处理的报销单的信息
    List<ClaimVoucher> getForDeal(String sn);
    //修改报销单
    void update(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items);

    //提交报销单
    void submit(int id);
    //审核和打款报销单
    void deal(DealRecord dealRecord);


}
