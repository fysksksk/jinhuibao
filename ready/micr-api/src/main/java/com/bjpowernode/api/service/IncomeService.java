package com.bjpowernode.api.service;

/**
 * 收益(利息)
 */
public interface IncomeService {

    /*收益计划*/
    void generateIncomePlan();

    /*收益返还*/
    void generateIncomeBack();
}
