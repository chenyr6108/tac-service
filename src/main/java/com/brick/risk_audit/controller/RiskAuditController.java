package com.brick.risk_audit.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brick.base.controller.BaseController;
import com.brick.risk_audit.service.RiskAuditService;
import com.brick.util.nciic.NciicEntity;
import com.brick.util.nciic.NciicUtil;

@Controller
@RequestMapping("riskAudit")
public class RiskAuditController extends BaseController {
	
	@Resource(name = "riskAuditService")
	private RiskAuditService riskAuditService;
	
	@RequestMapping("doIdcardVerified.do")
	public void doIdcardVerified(String name, String code, HttpServletResponse response, HttpServletRequest request) throws Exception{
		System.out.println(name + "--" + code);
		String userId = String.valueOf(request.getSession().getAttribute("s_employeeId"));
		NciicEntity result = riskAuditService.doIdcardVerified(name, code, userId);
		jsonObjectOutput(result, response);
	}
	
	@RequestMapping("getImg.do")
	public void getImg(HttpServletResponse response, HttpServletRequest request) throws Exception{
		String name = new String(request.getParameter("name").getBytes("ISO-8859-1"),"UTF-8");
		String code = new String(request.getParameter("code").getBytes("ISO-8859-1"),"UTF-8");
		String img = NciicUtil.getImgByCode(name, code);
		download(img, response);
	}
	
	
}
