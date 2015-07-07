package com.brick.util.nciic;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import sun.misc.BASE64Decoder;

import com.brick.base.util.LeaseUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class NciicUtil {
	
	//WebService 地址
	private static final String SOAP_ADDRESS = "https://api.nciic.com.cn/nciic_ws/services/NciicServices";
	
	//WebService QName
	private static final String SERVICE_QNAME = "https://api.nciic.org.cn/NciicServices";
	
	//调用WebService的方法
	private static final String SERVICE_METHOD_CHECK = "nciicCheck";
	
	//授权文件地址
	private static final String LICENSE_FILE = "\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\nciic\\license\\license.txt";
	
	//输出照片的路径
	private static final String XP_PATH = "\\\\"+LeaseUtil.getIPAddress()+"/home/filsoft/financelease/nciic/xp";
	
	//保存返回的结果的XML文件
	private static final String XML_PATH = "\\\\"+LeaseUtil.getIPAddress()+"/home/filsoft/financelease/nciic/xml";
	
	/**
	 * 校验身份证
	 * @param gmsfhm：身份证号
	 * @param xm：姓名
	 * @return
	 * @throws Exception
	 */
	public static NciicEntity nciicCheck(String gmsfhm, String xm) throws Exception{
		List<NciicEntity> resultList = null;
		String condition = getCondition(gmsfhm, xm);
		String resultXml = doNciicCheck(condition);
		resultList = readResult(resultXml);
		return resultList != null && resultList.size() > 0 ? resultList.get(0) : null;
	}
	
	/**
	 * 校验身份证
	 * NciicEntity(gmsfhm, xm必须输入)
	 * @return
	 * @throws Exception
	 */
	public static List<NciicEntity> nciicCheck(List<NciicEntity> nciicEntityList) throws Exception{
		List<NciicEntity> resultList = null;
		String condition = getCondition(nciicEntityList);
		String resultXml = doNciicCheck(condition);
		resultList = readResult(resultXml);
		return resultList;
	}
	
	/**
	 * 获取授权文件
	 * @return
	 * @throws IOException
	 */
	private static String getlicenseCode() throws IOException{
		String licensecode = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(LICENSE_FILE));
			licensecode = in.readLine();
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
		}
		return licensecode;
	}
	
	/**
	 * 拼装查询条件
	 * @param gmsfhm
	 * @param xm
	 * @return
	 */
	private static String getCondition(String gmsfhm, String xm){
		NciicEntity nciicEntity = new NciicEntity();
		nciicEntity.setGmsfhm(gmsfhm);
		nciicEntity.setXm(xm);
		return getCondition(nciicEntity);
	}
	
	/**
	 * 拼装查询条件
	 * @param nciicEntity
	 * @return
	 */
	private static String getCondition(NciicEntity nciicEntity){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		sb.append("<ROWS>");
		sb.append("<INFO><SBM>yrzlyrjk49959</SBM></INFO>");
		sb.append("<ROW>");
		sb.append("<GMSFHM>公民身份号码</GMSFHM><XM>姓名</XM>");
		sb.append("</ROW>");
		/*填写身份证信息*/
		sb.append("<ROW FSD=\"320500\" YWLX=\"租赁\" >");
		//身份证号
		sb.append("<GMSFHM>");
		sb.append(nciicEntity.getGmsfhm());
		sb.append("</GMSFHM>");
		//姓名
		sb.append("<XM>");
		sb.append(nciicEntity.getXm());
		sb.append("</XM>");
		sb.append("</ROW>");
		sb.append("</ROWS>");
		return sb.toString();
	}
	
	/**
	 * 拼装查询条件
	 * @param nciicEntityList
	 * @return
	 */
	public static String getCondition(List<NciicEntity> nciicEntityList){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		sb.append("<ROWS>");
		sb.append("<INFO><SBM>yrzlyrjk49959</SBM></INFO>");
		sb.append("<ROW>");
		sb.append("<GMSFHM>公民身份号码</GMSFHM><XM>姓名</XM>");
		sb.append("</ROW>");
		/*填写身份证信息*/
		for (NciicEntity nciicEntity : nciicEntityList) {
			sb.append("<ROW FSD=\"320500\" YWLX=\"租赁\" >");
			//身份证号
			sb.append("<GMSFHM>");
			sb.append(nciicEntity.getGmsfhm());
			sb.append("</GMSFHM>");
			//姓名
			sb.append("<XM>");
			sb.append(nciicEntity.getXm());
			sb.append("</XM>");
			sb.append("</ROW>");
		}
		sb.append("</ROWS>");
		return sb.toString();
	}
	
	/**
	 * 调用WebService
	 * @param condition
	 * @return
	 * @throws ServiceException
	 * @throws IOException
	 */
	private static String doNciicCheck(String condition) throws ServiceException, IOException{
		String res=null;
		Service service = new Service();
        Call call = (Call) service.createCall();   
        call.setTargetEndpointAddress(SOAP_ADDRESS);  
        //call.addParameter(new QName(SERVICE_QNAME,"inLicense"), org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
        //call.addParameter(new QName(SERVICE_QNAME,"inConditions"), org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
        call.setUseSOAPAction(true);
        //call.setReturnType(type)
        res = (String) call.invoke(SERVICE_METHOD_CHECK, new Object[]{getlicenseCode(),condition});
        return res;
	}
	
	/**
	 * 解析返回结果XML
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static List<NciicEntity> readResult(String text) throws Exception{
		List<NciicEntity> resultList = new ArrayList<NciicEntity>();
		NciicEntity result = null;
		Document d;
		XMLWriter writer = null;
		try {
			//SAXReader reader = new SAXReader();
			//d = reader.read(new File("d:/test/testxml.xml"));
			d = DocumentHelper.parseText(text);
			String dateStr = DateUtil.dateToString(new Date(), "[yyyy-MM-dd][HH-mm]");
			File xmlPath = new File(XML_PATH);
			if (!xmlPath.exists()) {
				xmlPath.mkdirs();
			}
			File xpPath = new File(XP_PATH);
			if (!xpPath.exists()) {
				xpPath.mkdirs();
			}
			writer = new XMLWriter(new FileOutputStream(new File(xmlPath, dateStr + ".xml")));
			writer.write(d);
			writer.flush();
			writer.close();
			writer = null;
			
			Element root = d.getRootElement();
			List<Element> allResult = root.elements("ROW");
			Element input = null;
			List<Element> output = null;
			String result_msg = null;
			for (Element element : allResult) {
				result = new NciicEntity();
				result_msg = null;
				input = element.element("INPUT");
				result.setGmsfhm(input.element("gmsfhm").getText());
				result.setXm(input.element("xm").getText());
				output = element.element("OUTPUT").elements("ITEM");
				for (Element out_element : output) {
					if (out_element.element("result_gmsfhm") != null) {
						result.setResult_gmsfhm(out_element.element("result_gmsfhm").getText());
					}
					if (out_element.element("result_xm") != null) {
						result.setResult_xm(out_element.element("result_xm").getText());
					}
					if (out_element.element("errormesage") != null) {
						result.setError_msg(out_element.element("errormesage").getText());
					}
					if (out_element.element("errormesagecol") != null) {
						result.setError_msg_col(out_element.element("errormesagecol").getText());
					}
					if (out_element.element("xp") != null) {
						result.setXp(out_element.element("xp").getText());
					}
					if (!StringUtils.isEmpty(result.getXp())) {
						try {
							File f = new File(xpPath, result.getGmsfhm() + "-" + result.getXm() + ".jpg");
							BufferedImage img = ImageIO.read(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(result.getXp())));
							ImageIO.write(img, "jpg", f);
							result.setXp_file(f.getPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				if ("一致".equals(result.getResult_gmsfhm()) && "一致".equals(result.getResult_xm())) {
					result_msg = "一致";
				} else {
					if ("不一致".equals(result.getResult_gmsfhm())) {
						result_msg = "身份证号不一致";
					} else if ("不一致".equals(result.getResult_xm())) {
						result_msg = "姓名不一致";
					} else if (!StringUtils.isEmpty(result.getError_msg())) {
						result_msg = result.getError_msg();
						if (!StringUtils.isEmpty(result.getError_msg_col())) {
							result_msg += "(" + result.getError_msg_col() + ")";
						}
					}
				}
				result.setResult_msg(result_msg);
				resultList.add(result);
			}
			return resultList;
		} catch (Exception e) {
			throw e;
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
				writer = null;
			}
		}
	}

	public static String getImgByCode(String name, String code) {
		String img = XP_PATH + File.separator + code + "-" + name + ".jpg";
		return img;
	}
	
	
	
}
