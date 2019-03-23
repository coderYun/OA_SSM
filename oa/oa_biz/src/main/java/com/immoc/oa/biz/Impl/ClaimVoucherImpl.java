package com.immoc.oa.biz.Impl;

import com.immoc.oa.biz.ClaimVoucherBiz;
import com.immoc.oa.dao.ClaimVoucherDao;
import com.immoc.oa.dao.ClaimVoucherItemDao;
import com.immoc.oa.dao.DealRecordDao;
import com.immoc.oa.dao.EmployeeDao;
import com.immoc.oa.entity.ClaimVoucher;
import com.immoc.oa.entity.ClaimVoucherItem;
import com.immoc.oa.entity.DealRecord;
import com.immoc.oa.entity.Employee;
import com.immoc.oa.global.Contant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service("cliamVoucherBiz")
public class ClaimVoucherImpl implements ClaimVoucherBiz {

    @Autowired
    private ClaimVoucherDao claimVoucherDao;
    @Autowired
    private ClaimVoucherItemDao claimVoucherItemDao;
    @Autowired
    private DealRecordDao dealRecordDao;
    @Autowired
    private EmployeeDao employeeDao;

    public void save(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items) {
        claimVoucher.setCreateTime(new Date());
        claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        claimVoucher.setStatus(Contant.CLAIMVOUCHER_CREATED);
        claimVoucherDao.insert(claimVoucher);

        for (ClaimVoucherItem item : items) {
            item.setClaimVoucherId(claimVoucher.getId());
            claimVoucherItemDao.insert(item);
        }

    }

    public ClaimVoucher get(int id) {

        return claimVoucherDao.select(id);
    }

    public List<ClaimVoucherItem> getItems(int cvid) {
        return claimVoucherItemDao.selectByClaimVoucher(cvid);
    }

    public List<DealRecord> getRecords(int cvid) {
        return dealRecordDao.selectByClaimVoucher(cvid);
    }

    public List<ClaimVoucher> getForSelf(String sn) {
        return claimVoucherDao.selectByCreateSn(sn);
    }

    public List<ClaimVoucher> getForDeal(String sn) {
        return claimVoucherDao.selectByNextDealSn(sn);
    }

    public void update(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items) {
        claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        claimVoucher.setStatus(Contant.CLAIMVOUCHER_CREATED);
        claimVoucherDao.update(claimVoucher);
        //判断是否有旧的报销单条目，有的话就更新，没有的话就新建
        List<ClaimVoucherItem> Olds = claimVoucherItemDao.selectByClaimVoucher(claimVoucher.getId());
        for (ClaimVoucherItem old : Olds) {
            boolean isHave = false;
            for (ClaimVoucherItem item : items) {
                if (old.getId() == item.getId()) {
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                claimVoucherItemDao.delete(old.getId());
            }
        }

        for (ClaimVoucherItem item : items) {
            item.setClaimVoucherId(claimVoucher.getId());
            if (item.getId() != null && item.getId() > 0) {
                claimVoucherItemDao.update(item);
            } else {
                claimVoucherItemDao.insert(item);
            }

        }


    }

    public void submit(int id) {
        ClaimVoucher claimVoucher = claimVoucherDao.select(id);
        //通过报销单的创建者找到相应的员工
        Employee employee = employeeDao.select(claimVoucher.getCreateSn());

        //然后改员工的报销单肯定是要交到与该员工同部门并且职位为经理的人
        claimVoucher.setStatus(Contant.CLAIMVOUCHER_SUBMIT);

        claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(employee.getDepartmentSn(), Contant.POST_FM).get(0).getSn());
        //在更新一下
        claimVoucherDao.update(claimVoucher);

        //更新一下处理报销单记录
        DealRecord dealRecord = new DealRecord();
        dealRecord.setDealWay(Contant.DEAL_SUBMIT);
        dealRecord.setDealSn(employee.getSn());
        dealRecord.setClaimVoucherId(id);
        dealRecord.setDealResult(Contant.CLAIMVOUCHER_SUBMIT);
        dealRecord.setComment("无");
        dealRecordDao.insert(dealRecord);
    }

    public void deal(DealRecord dealRecord) {
        //先得到要处理审核的报销单，通过记录获得
        ClaimVoucher claimVoucher = claimVoucherDao.select(dealRecord.getClaimVoucherId());

        //把处理报销单的处理人拿出来
        Employee employee = employeeDao.select(dealRecord.getDealSn());
        //如果是通过的报销单就要分金额大小，小于5000或者报销单的处理人是总经理的话直接打款，大于5000就交给总经理去处理
        if (dealRecord.getDealWay().equals(Contant.DEAL_PASS)) {
            if (claimVoucher.getTotalAmount() <= Contant.LIMIT_CHECK || employee.getPost().equals(Contant.POST_GM)) {
                claimVoucher.setStatus(Contant.CLAIMVOUCHER_APPROVED);
                //交给财务人员去打款
                claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(null, Contant.POST_CASHIER).get(0).getSn());
                //再更新一下报销单处理记录
                dealRecord.setDealTime(new Date());
                dealRecord.setDealResult(Contant.CLAIMVOUCHER_APPROVED);


            } else {
                claimVoucher.setStatus(Contant.CLAIMVOUCHER_RECHECK);
                //待复审之后处理人就只能是总经理了
                claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(null, Contant.POST_GM).get(0).getSn());
                dealRecord.setDealTime(new Date());
                dealRecord.setDealResult(Contant.CLAIMVOUCHER_RECHECK);
            }

        } else if (dealRecord.getDealWay().equals(Contant.CLAIMVOUCHER_BACK)) {
            claimVoucher.setStatus(Contant.CLAIMVOUCHER_BACK);
            claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
            dealRecord.setDealTime(new Date());
            dealRecord.setDealResult(Contant.CLAIMVOUCHER_BACK);

        } else if (dealRecord.getDealWay().equals(Contant.DEAL_REJECT)) {//如果是拒絕那麼就报销单就无效，处理人也就为空
            claimVoucher.setStatus(Contant.CLAIMVOUCHER_TERMINATED);
            claimVoucher.setNextDealSn(null);
            dealRecord.setDealTime(new Date());
            dealRecord.setDealResult(Contant.CLAIMVOUCHER_TERMINATED);

        } else if (dealRecord.getDealWay().equals(Contant.DEAL_PAID)) {//打款
            claimVoucher.setStatus(Contant.CLAIMVOUCHER_PAID);
            claimVoucher.setNextDealSn(null);
            dealRecord.setDealTime(new Date());
            dealRecord.setDealResult(Contant.CLAIMVOUCHER_PAID);


        }
        claimVoucherDao.update(claimVoucher);
        dealRecordDao.insert(dealRecord);
    }
}
