package cc.mrbird.febs.sms.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 所有请求的消息头<br/>
 * totalLength 消息总长度<br/>
 * commandId 命令类型<br/>
 * sequenceId 消息流水号,顺序累加,步长为1,循环使用（一对请求和应答消息的流水号必须相同）<br/>
 * Unsigned Integer 无符号整数<br/>
 * Integer 整数，可为正整数、负整数或零<br/>
 * Octet String 定长字符串，位数不足时，如果左补0则补ASCII表示的零以填充，如果右补0则补二进制的零以表示字符串的结束符
 * 
 */
public class MsgHead {
	private Logger logger = LoggerFactory.getLogger(MsgHead.class);
	private int Total_Length;// Unsigned Integer 消息总长度
	private int Command_Id;// Unsigned Integer 命令类型
	private int Sequence_Id;// Unsigned Integer 消息流水号,顺序累加,步长为1,循环使用（一对请求和应答消息的流水号必须相同）

	public byte[] toByteArry() {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		try {
			dous.writeInt(this.getTotal_Length());
			dous.writeInt(this.getCommand_Id());
			dous.writeInt(this.getSequence_Id());
			dous.close();
		} catch (IOException e) {
			logger.error("封装CMPP消息头二进制数组失败。");
		}
		return bous.toByteArray();
	}

	public MsgHead(byte[] data) {
		ByteArrayInputStream bins = new ByteArrayInputStream(data);
		DataInputStream dins = new DataInputStream(bins);
		try {
			this.setTotal_Length(data.length + 4);
			this.setCommand_Id(dins.readInt());
			this.setSequence_Id(dins.readInt());
			dins.close();
			bins.close();
		} catch (IOException e) {
		}
	}

	public MsgHead() {
		super();
	}

	public int getTotal_Length() {
		return Total_Length;
	}

	public void setTotal_Length(int total_Length) {
		Total_Length = total_Length;
	}

	public int getCommand_Id() {
		return Command_Id;
	}

	public void setCommand_Id(int command_Id) {
		Command_Id = command_Id;
	}

	public int getSequence_Id() {
		return Sequence_Id;
	}

	public void setSequence_Id(int sequence_Id) {
		Sequence_Id = sequence_Id;
	}

}
