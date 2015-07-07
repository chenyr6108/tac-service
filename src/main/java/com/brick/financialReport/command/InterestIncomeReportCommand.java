package com.brick.financialReport.command;

import com.brick.base.command.BaseCommand;
import com.brick.financialReport.service.InterestIncomeReportService;

public class InterestIncomeReportCommand extends BaseCommand {

	private InterestIncomeReportService interestIncomeReportService;

	public InterestIncomeReportService getInterestIncomeReportService() {
		return interestIncomeReportService;
	}

	public void setInterestIncomeReportService(
			InterestIncomeReportService interestIncomeReportService) {
		this.interestIncomeReportService = interestIncomeReportService;
	}
	
}
