package cc.mrbird.febs.sms.domain;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import cc.mrbird.febs.sms.util.MsgUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Submit消息结构定义
 */
public class MsgSubmit extends MsgHead {
	private static Logger logger = LoggerFactory.getLogger(MsgSubmit.class);
	private long Msg_Id = 0; // 信息标示
	private byte Pk_total = 0x01; // 相同的msgId总数，从1开始
	private byte Pk_number = 0x01; // 想用的msgId序号，从1开始
	private byte Registered_Delivery = 0x00; // 是否要求返回状态报告，0不需要，1需要
	private byte Msg_level = 0x01; // 信息级别
	private String Service_Id = ""; // 业务标示，企业代码QYN4799901
	private byte Fee_UserType = 0x00;// 用户计费类型， 谁接收，计谁的费
	private String Fee_terminal_Id = ""; // 被计费的号码
	private byte Fee_terminal_type = 0x00; // 被计费号码的类型，真实号码或者虚拟号码
	private byte TP_pId = 0x00;
	private byte TP_udhi = 0x00;
	private byte Msg_Fmt = 0x08; // 信息格式 15含GB汉字
	private String Msg_src;

	// 01：对“计费用户号码”免费；
	// 02：对“计费用户号码”按条计信息费；
	// 03：对“计费用户号码”按包月收取信息费
	private String FeeType = "01";// 资费类型， 默认为按条计费
	private String FeeCode = "5";
	private String ValId_Time = "";// 暂不支持
	private String At_Time = "";// 暂不支持
	// SP的服务代码或前缀为服务代码的长号码,
	// 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，该号码最终在用户手机上显示为短消息的主叫号码。
	private String Src_Id;
	private byte DestUsr_tl = 0x01;// 不支持群发
	private String Dest_terminal_Id;// 接收手机号码，
	private byte Dest_terminal_type = 0x00;// 真实号码
	private byte Msg_Length;
	private byte[] Msg_Content; // 信息内容
	// 点播业务使用的LinkID，非点播类业务的MT流程不使用该字段
	private String LinkID = "";

	public byte[] toByteArry() {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		try {
			dous.writeInt(this.getTotal_Length());
			dous.writeInt(this.getCommand_Id());
			dous.writeInt(this.getSequence_Id());
			dous.writeLong(this.Msg_Id);// Msg_Id 信息标识，由SP接入的短信网关本身产生，本处填空
			dous.writeByte(this.Pk_total);// Pk_total 相同Msg_Id的信息总条数
			dous.writeByte(this.Pk_number);// Pk_number 相同Msg_Id的信息序号，从1开始
			dous.writeByte(this.Registered_Delivery);// Registered_Delivery 是否要求返回状态确认报告
			dous.writeByte(this.Msg_level);// Msg_level 信息级别
			MsgUtils.writeString(dous, this.Service_Id, 10);// Service_Id 业务标识，是数字、字母和符号的组合。
			dous.writeByte(this.Fee_UserType);// Fee_UserType 计费用户类型字段
												// 0：对目的终端MSISDN计费；1：对源终端MSISDN计费；2：对SP计费;3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。
			MsgUtils.writeString(dous, this.Fee_terminal_Id, 32);// Fee_terminal_Id 被计费用户的号码
			dous.writeByte(this.Fee_terminal_type);// Fee_terminal_type 被计费用户的号码类型，0：真实号码；1：伪码
			dous.writeByte(this.TP_pId);// TP_pId
			dous.writeByte(this.TP_udhi);// TP_udhi
			dous.writeByte(this.Msg_Fmt);// Msg_Fmt
			MsgUtils.writeString(dous, this.Msg_src, 6);// Msg_src 信息内容来源(SP_Id)
			MsgUtils.writeString(dous, this.FeeType, 2);// FeeType 资费类别
			MsgUtils.writeString(dous, this.FeeCode, 6);// FeeCode
			MsgUtils.writeString(dous, this.ValId_Time, 17);// 存活有效期
			MsgUtils.writeString(dous, this.At_Time, 17);// 定时发送时间
			MsgUtils.writeString(dous, this.Src_Id, 21);// Src_Id spCode
			dous.writeByte(this.DestUsr_tl);// DestUsr_tl
			MsgUtils.writeString(dous, this.Dest_terminal_Id, 32);// Dest_terminal_Id
			dous.writeByte(this.Dest_terminal_type);// Dest_terminal_type 接收短信的用户的号码类型，0：真实号码；1：伪码
			dous.writeByte(this.Msg_Length);// Msg_Length
			dous.write(this.Msg_Content);// 信息内容
			MsgUtils.writeString(dous, this.LinkID, 20);// 点播业务使用的LinkID
			dous.close();
		} catch (IOException e) {
			logger.error("封装短信发送二进制数组失败。");
		}
		byte[] b = bous.toByteArray();
		// logger.info("组装的短信报文 : " + Arrays.toString(b));

		return b;
	}

	public long getMsg_Id() {
		return Msg_Id;
	}

	public void setMsg_Id(long msg_Id) {
		Msg_Id = msg_Id;
	}

	public byte getPk_total() {
		return Pk_total;
	}

	public void setPk_total(byte pk_total) {
		Pk_total = pk_total;
	}

	public byte getPk_number() {
		return Pk_number;
	}

	public void setPk_number(byte pk_number) {
		Pk_number = pk_number;
	}

	public byte getRegistered_Delivery() {
		return Registered_Delivery;
	}

	public void setRegistered_Delivery(byte registered_Delivery) {
		Registered_Delivery = registered_Delivery;
	}

	public byte getMsg_level() {
		return Msg_level;
	}

	public void setMsg_level(byte msg_level) {
		Msg_level = msg_level;
	}

	public String getService_Id() {
		return Service_Id;
	}

	public void setService_Id(String service_Id) {
		Service_Id = service_Id;
	}

	public byte getFee_UserType() {
		return Fee_UserType;
	}

	public void setFee_UserType(byte fee_UserType) {
		Fee_UserType = fee_UserType;
	}

	public String getFee_terminal_Id() {
		return Fee_terminal_Id;
	}

	public void setFee_terminal_Id(String fee_terminal_Id) {
		Fee_terminal_Id = fee_terminal_Id;
	}

	public byte getFee_terminal_type() {
		return Fee_terminal_type;
	}

	public void setFee_terminal_type(byte fee_terminal_type) {
		Fee_terminal_type = fee_terminal_type;
	}

	public byte getTP_pId() {
		return TP_pId;
	}

	public void setTP_pId(byte tP_pId) {
		TP_pId = tP_pId;
	}

	public byte getTP_udhi() {
		return TP_udhi;
	}

	public void setTP_udhi(byte tP_udhi) {
		TP_udhi = tP_udhi;
	}

	public byte getMsg_Fmt() {
		return Msg_Fmt;
	}

	public void setMsg_Fmt(byte msg_Fmt) {
		Msg_Fmt = msg_Fmt;
	}

	public String getMsg_src() {
		return Msg_src;
	}

	public void setMsg_src(String msg_src) {
		Msg_src = msg_src;
	}

	public String getFeeType() {
		return FeeType;
	}

	public void setFeeType(String feeType) {
		FeeType = feeType;
	}

	public String getFeeCode() {
		return FeeCode;
	}

	public void setFeeCode(String feeCode) {
		FeeCode = feeCode;
	}

	public String getValId_Time() {
		return ValId_Time;
	}

	public void setValId_Time(String valId_Time) {
		ValId_Time = valId_Time;
	}

	public String getAt_Time() {
		return At_Time;
	}

	public void setAt_Time(String at_Time) {
		At_Time = at_Time;
	}

	public String getSrc_Id() {
		return Src_Id;
	}

	public void setSrc_Id(String src_Id) {
		Src_Id = src_Id;
	}

	public byte getDestUsr_tl() {
		return DestUsr_tl;
	}

	public void setDestUsr_tl(byte destUsr_tl) {
		DestUsr_tl = destUsr_tl;
	}

	public String getDest_terminal_Id() {
		return Dest_terminal_Id;
	}

	public void setDest_terminal_Id(String dest_terminal_Id) {
		Dest_terminal_Id = dest_terminal_Id;
	}

	public byte getDest_terminal_type() {
		return Dest_terminal_type;
	}

	public void setDest_terminal_type(byte dest_terminal_type) {
		Dest_terminal_type = dest_terminal_type;
	}

	public byte getMsg_Length() {
		return Msg_Length;
	}

	public void setMsg_Length(byte msg_Length) {
		Msg_Length = msg_Length;
	}

	public byte[] getMsg_Content() {
		return Msg_Content;
	}

	public void setMsg_Content(byte[] msg_Content) {
		Msg_Content = msg_Content;
	}

	public String getLinkID() {
		return LinkID;
	}

	public void setLinkID(String linkID) {
		LinkID = linkID;
	}
}
