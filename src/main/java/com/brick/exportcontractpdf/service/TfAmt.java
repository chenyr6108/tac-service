package com.brick.exportcontractpdf.service;



//人民币小写转大写 0402 胡昭卿
public class TfAmt {
  public static String[] shuri= new String[10];
  public static String[] danwei= new String[15];
  public static boolean duanzero,firstzero,jiaozero,fenzero;

  /**
   * 构造函数
   */

  public TfAmt() {
    super();
  }

  /**
   * 初始化参数
   */

  public static void initPara(){
    shuri[0] = "零";
    shuri[1] = "壹";
    shuri[2] = "贰";
    shuri[3] = "叁";
    shuri[4] = "肆";
    shuri[5] = "伍";
    shuri[6] = "陆";
    shuri[7] = "柒";
    shuri[8] = "捌";
    shuri[9] = "玖";

    danwei[0] = "分";
    danwei[1] = "角";
    danwei[2]="";     //小数点
    danwei[3] = "元";
    danwei[4] = "拾";
    danwei[5] = "佰";
    danwei[6] = "仟";
    danwei[7] = "万";
    danwei[8] = "拾";
    danwei[9] = "佰";
    danwei[10] = "仟";
    danwei[11] = "亿";
    danwei[12] = "拾";
    danwei[13] = "佰";
    danwei[14] = "仟";
  }

  /**
   * 映射小写数字到中文大写数字
   * @param numStr
   * @return
   */
  public static String Val(String numStr){
    int num = 0;
    String cnStr = null;
    try{
      num = Integer.parseInt(numStr.trim() );
      cnStr = shuri[num];
    }catch(Exception e){
      System.out.println("err in Val:"+e.toString() );
    }
    return cnStr;
  }

  /**
   * 检查金额数字是否满足格式#######.##
   * @param inAmt
   * @throws Exception
   */
  public static void chknum(String inAmt) throws Exception{
    String tmp = null;
    char[] t_char = null;
    int idx = inAmt.indexOf(".");
    tmp = inAmt.substring(0,idx);
    t_char = tmp.toCharArray();
    for(int i=0; i<t_char.length; i++)
      if(!Character.isDigit(t_char[i]))
        throw new Exception("非数字类型的错误！");
    tmp = (inAmt.substring(idx+1)).trim();
    if(tmp.length()>2)
      throw new Exception("不满足格式########.##！");
    double t_amt = Double.parseDouble(inAmt);
    if( t_amt >1000000000000.00 || t_amt < 0){
      throw new Exception("金额没有正确填写，超出正常的大小范围！");
    }

  }
  /**
   * @param inAmt
   * @return
   */
  public static String num2cn(String inAmt) throws Exception{
    String xiaoxie;
    String daxie;
    String duandanwei;
    int length;
    int tmpcount;

    //初始化参数
    initPara();

    xiaoxie = inAmt; //转换为0.00格式
    daxie = new String();
    length = xiaoxie.length();  //总长度
    tmpcount = 0;   //主记数器
    boolean data = false; //分成角分、元、万、亿四段，标记段中是否有非零数据
    try{
      chknum(inAmt);

      //处理角分
      //分
      String tmpnum = xiaoxie.substring(xiaoxie.length()-1);
      if(tmpnum.equals("0")){
        fenzero = true;
      }else{
        daxie = danwei[tmpcount]; //分
        daxie = Val(tmpnum) + daxie;
      }
      tmpcount = tmpcount + 1;
      xiaoxie = xiaoxie.substring(0,xiaoxie.length()-1);

      //角
      tmpnum = xiaoxie.substring(xiaoxie.length()-1 );
      if(tmpnum.equals("0")){
        jiaozero = true;
      }else{
        daxie = danwei[tmpcount]+daxie; //角
        daxie= Val(tmpnum) + daxie;
      }
      tmpcount = tmpcount + 1;
      xiaoxie = xiaoxie.substring(0,xiaoxie.length()-1);

      //处理小数点
      tmpnum = xiaoxie.substring(xiaoxie.length()-1);
      if(tmpnum.equals(".")){
        tmpcount = tmpcount + 1;
        xiaoxie = xiaoxie.substring(0,xiaoxie.length()-1);
      }else{
        System.out.println("---------输入金额有误-------");
        throw new Exception("输入金额有误");
      }

      //元
      if(jiaozero && fenzero){ //无角分
        daxie = "整";
      }

      duandanwei = "元";
      tmpnum = xiaoxie.substring(xiaoxie.length()-1);

      if(jiaozero == true && fenzero == false){  //0角X分
        if(!(tmpnum.equals("0")&&(xiaoxie.length() == 1)))  //0.0X元除外
          daxie="零"+daxie; //不管元位是否是0
      }

      if(tmpnum.equals("0")){
        if(jiaozero == false) //上一段第一个数字不是0(X角),本段最后一个数字是0
          if(xiaoxie.length()>1){ //0.X0元除外
        daxie = "零" + daxie;
          }
          firstzero = true;
      }else{
        daxie = danwei[tmpcount] + daxie; //元
        daxie = Val(tmpnum) + daxie;
        data = true;
        firstzero = false;
        duandanwei="";
      }
      tmpcount = tmpcount + 1;
      xiaoxie = xiaoxie.substring(0,xiaoxie.length()-1);

      //十百千位
      for(int i=0;i<3;i++){
        if(xiaoxie.length()-1<0)
          break;
        tmpnum = xiaoxie.substring(xiaoxie.length()-1);
        if(xiaoxie.length()>0){  //在有效处理长度范围内
          if(tmpnum.equals("0")){
            firstzero = true;
          }else{
            if(firstzero && data)
              daxie = "零" + daxie;
            if(!(duandanwei.equals(""))){
              daxie = duandanwei + daxie;
              duandanwei="";
            }
            daxie = danwei[tmpcount] + daxie;
            daxie = Val(tmpnum) + daxie;
            data = true;
            firstzero = false;
          }
          tmpcount = tmpcount + 1;
          xiaoxie = xiaoxie.substring(0,xiaoxie.length()-1);
        }
      }

      //元段特殊情况处理
      if(data == false && xiaoxie.length()>0){  //元段无数据且存在大于9999元的数值
        daxie = "元" + daxie;
        duanzero = true;
      }

      //处理万段和亿段
      for(int i=1; i<=2; i++){
        data = false; //该段尚位发现有效数据
        if(i == 1)
          duandanwei = "万";
        else
          duandanwei = "亿";
        if(xiaoxie.length()>0){ //万位或亿位
          tmpnum = xiaoxie.substring(xiaoxie.length()-1);
          if(firstzero && duanzero == false) //上一段非空且第一个数字为零
            daxie = "零" + daxie;
          if(tmpnum.equals("0")){
            if(firstzero == false ) //上一段第一个数字不是0
              daxie="零"+daxie;
            firstzero = true;
          }else{
            daxie = danwei[tmpcount] + daxie;
            daxie = Val(tmpnum) + daxie;
            data = true;
            firstzero = false;
            duandanwei = "";
          }
          tmpcount = tmpcount + 1;
          xiaoxie = xiaoxie.substring(0,xiaoxie.length()-1);
        }

        for(int j=1; j<=3; j++){  //处理十百千(万/亿)
          if(xiaoxie.length()>0){ //在有效处理长度内
            tmpnum = xiaoxie.substring(xiaoxie.length()-1);
            if(tmpnum.equals("0"))
              firstzero = true;
            else{
              if(firstzero && data)
                daxie = "零" + daxie;
              if(!"".equals(duandanwei)){
                daxie = duandanwei + daxie;
                duandanwei = "";
              }
              daxie = danwei[tmpcount] + daxie;
              daxie = Val(tmpnum) + daxie;
              data = true;
              firstzero = false;
            }
            tmpcount=tmpcount+1;
            xiaoxie = xiaoxie.substring(0,xiaoxie.length()-1);
          }
        }

        if(data == false){  //该段无非零数据
          if(i==1 && xiaoxie.length()>0 && duanzero == false) //万段无非零数据而元段亿段有数据插入零
            daxie = "零" + daxie;
          duanzero = true;
        }else{
          duanzero = false;
        }
      }
    }catch(Exception e){
      System.out.println("ERROR IN TfAmt.java:"+e.toString() );
      throw e;
    }
    return daxie.trim();
  }
 public static Double doubleWithSign(String sign,Double amt){
  return Double.valueOf(sign.trim()+amt.toString());
 }
  /**
   * Test Main
   * @param args
   */
 /*
  public static void main(String args[]){
    try{
      System.out.println("------2365.03-------");
      System.out.println(TfAmt.num2cn("2365.03"));
      Double amt = new Double(0);
      Double amt1 = TfAmt.doubleWithSign("+",amt);
      System.out.println(">>>"+3*amt1.doubleValue());
      
    }catch(Exception e){
      System.out.println("--------------ERROR IN TfAmt.java:"+e.toString() );
    }
  }
  */
}
