package cc.mrbird.febs.sms.domain;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CMPP_DELIVER操作的目的是ISMG把从短信中心或其它ISMG转发来的短信送交SP，SP以CMPP_DELIVER_RESP消息回应。
 */
public class MsgDeliver extends MsgHead {
	private static Logger logger = LoggerFactory.getLogger(MsgDeliver.class);
	private long msg_Id;
	private String dest_Id;// 21 目的号码 String
	private String service_Id;// 10 业务标识 String
	private byte tP_pid = 0;
	private byte tP_udhi = 0;
	private byte msg_Fmt = 8;
	private String src_terminal_Id;// 源终端MSISDN号码
	private byte src_terminal_type = 0;// 源终端号码类型，0：真实号码；1：伪码
	private byte registered_Delivery = 0;// 是否为状态报告 0：非状态报告1：状态报告
	private byte msg_Length;// 消息长度
	private String msg_Content;// 消息长度
	private String linkID;

	private long msg_Id_report;
	private String stat;
	private String submit_time;
	private String done_time;
	private String dest_terminal_Id;
	private int sMSC_sequence;
	private int result;// 解析结果

	public MsgDeliver(byte[] data) {
		if (data.length > 8 + 8 + 21 + 10 + 1 + 1 + 1 + 32 + 1 + 1 + 1 + 20) {// +Msg_length+
			String fmtStr = "gb2312";
			ByteArrayInputStream bins = new ByteArrayInputStream(data);
			DataInputStream dins = new DataInputStream(bins);
			try {
				this.setTotal_Length(data.length + 4);
				this.setCommand_Id(dins.readInt());
				this.setSequence_Id(dins.readInt());
				this.msg_Id = dins.readLong();// Msg_Id
				byte[] destIdByte = new byte[21];
				dins.read(destIdByte);
				this.dest_Id = new String(destIdByte);// 21 目的号码 String
				byte[] service_IdByte = new byte[10];
				dins.read(service_IdByte);
				this.service_Id = new String(service_IdByte);// 10 业务标识 String
				this.tP_pid = dins.readByte();
				this.tP_udhi = dins.readByte();
				this.msg_Fmt = dins.readByte();
				fmtStr = this.msg_Fmt == 8 ? "utf-8" : "gb2312";
				byte[] src_terminal_IdByte = new byte[32];
				dins.read(src_terminal_IdByte);
				this.src_terminal_Id = new String(src_terminal_IdByte);// 源终端MSISDN号码
				this.src_terminal_type = dins.readByte();// 源终端号码类型，0：真实号码；1：伪码
				this.registered_Delivery = dins.readByte();// 是否为状态报告 0：非状态报告1：状态报告
				this.msg_Length = dins.readByte();// 消息长度
				byte[] msg_ContentByte = new byte[msg_Length];
				dins.read(msg_ContentByte);
				if (registered_Delivery == 1) {
					this.msg_Content = new String(msg_ContentByte, fmtStr);// 消息长度
					if (msg_Length == 8 + 7 + 10 + 10 + 21 + 4) {
						ByteArrayInputStream binsC = new ByteArrayInputStream(data);
						DataInputStream dinsC = new DataInputStream(binsC);
						this.msg_Id_report = dinsC.readLong();
						byte[] startByte = new byte[7];
						dinsC.read(startByte);
						this.stat = new String(startByte, fmtStr);
						byte[] submit_timeByte = new byte[10];
						dinsC.read(submit_timeByte);
						this.submit_time = new String(submit_timeByte, fmtStr);
						byte[] done_timeByte = new byte[7];
						dinsC.read(done_timeByte);
						this.done_time = new String(done_timeByte, fmtStr);
						byte[] dest_terminal_IdByte = new byte[21];
						dinsC.read(dest_terminal_IdByte);
						this.dest_terminal_Id = new String(dest_terminal_IdByte, fmtStr);
						this.sMSC_sequence = dinsC.readInt();
						dinsC.close();
						binsC.close();
						this.result = 0;// 正确
					} else {
						this.result = 1;// 消息结构错
					}
				} else {
					this.msg_Content = new String(msg_ContentByte, fmtStr);// 消息长度
				}
				byte[] linkIDByte = new byte[20];
				this.linkID = new String(linkIDByte);
				this.result = 0;// 正确
				dins.close();
				bins.close();
			} catch (IOException e) {
				this.result = 8;// 消息结构错
			}
		} else {
			this.result = 1;// 消息结构错
			logger.info("短信网关CMPP_DELIVER,解析数据包出错，包长度不一致。长度为:" + data.length);
		}
	}

	public long getMsg_Id() {
		return msg_Id;
	}

	public void setMsg_Id(long msg_Id) {
		this.msg_Id = msg_Id;
	}

	public String getDest_Id() {
		return dest_Id;
	}

	public void setDest_Id(String dest_Id) {
		this.dest_Id = dest_Id;
	}

	public String getService_Id() {
		return service_Id;
	}

	public void setService_Id(String service_Id) {
		this.service_Id = service_Id;
	}

	public byte getTP_pid() {
		return tP_pid;
	}

	public void setTP_pid(byte tp_pid) {
		tP_pid = tp_pid;
	}

	public byte getTP_udhi() {
		return tP_udhi;
	}

	public void setTP_udhi(byte tp_udhi) {
		tP_udhi = tp_udhi;
	}

	public byte getMsg_Fmt() {
		return msg_Fmt;
	}

	public void setMsg_Fmt(byte msg_Fmt) {
		this.msg_Fmt = msg_Fmt;
	}

	public String getSrc_terminal_Id() {
		return src_terminal_Id;
	}

	public void setSrc_terminal_Id(String src_terminal_Id) {
		this.src_terminal_Id = src_terminal_Id;
	}

	public byte getSrc_terminal_type() {
		return src_terminal_type;
	}

	public void setSrc_terminal_type(byte src_terminal_type) {
		this.src_terminal_type = src_terminal_type;
	}

	public byte getRegistered_Delivery() {
		return registered_Delivery;
	}

	public void setRegistered_Delivery(byte registered_Delivery) {
		this.registered_Delivery = registered_Delivery;
	}

	public byte getMsg_Length() {
		return msg_Length;
	}

	public void setMsg_Length(byte msg_Length) {
		this.msg_Length = msg_Length;
	}

	public String getMsg_Content() {
		return msg_Content;
	}

	public void setMsg_Content(String msg_Content) {
		this.msg_Content = msg_Content;
	}

	public String getLinkID() {
		return linkID;
	}

	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}

	public long getMsg_Id_report() {
		return msg_Id_report;
	}

	public void setMsg_Id_report(long msg_Id_report) {
		this.msg_Id_report = msg_Id_report;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getSubmit_time() {
		return submit_time;
	}

	public void setSubmit_time(String submit_time) {
		this.submit_time = submit_time;
	}

	public String getDone_time() {
		return done_time;
	}

	public void setDone_time(String done_time) {
		this.done_time = done_time;
	}

	public String getDest_terminal_Id() {
		return dest_terminal_Id;
	}

	public void setDest_terminal_Id(String dest_terminal_Id) {
		this.dest_terminal_Id = dest_terminal_Id;
	}

	public int getSMSC_sequence() {
		return sMSC_sequence;
	}

	public void setSMSC_sequence(int smsc_sequence) {
		sMSC_sequence = smsc_sequence;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
