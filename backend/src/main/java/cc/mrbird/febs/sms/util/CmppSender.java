package cc.mrbird.febs.sms.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import cc.mrbird.febs.sms.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 启动一个线程去接收和发送数据，如果队列处理完毕就关闭线程
 */
public class CmppSender {
	private static Logger logger = LoggerFactory.getLogger(CmppSender.class);
	private List<byte[]> sendData = new ArrayList<byte[]>();// 需要发出的二进制数据队列
	private List<byte[]> getData = new ArrayList<byte[]>();// 需要接受的二进制队列
	private DataOutputStream out;
	private DataInputStream in;

	public CmppSender(DataOutputStream out, DataInputStream in, List<byte[]> sendData) {
		super();
		this.sendData = sendData;
		this.out = out;
		this.in = in;
	}

	public void start() throws Exception {
		if (out != null && null != sendData) {
			for (byte[] data : sendData) {
				sendMsg(data);
				byte[] returnData = getInData();
				if (null != returnData) {
					getData.add(returnData);
				}
			}
		}
		if (in != null && null != getData) {
			for (byte[] data : getData) {
				if (data.length >= 8) {
					MsgHead head = new MsgHead(data);
					switch (head.getCommand_Id()) {
					case MsgCommand.CMPP_CONNECT_RESP:
						MsgConnectResp connectResp = new MsgConnectResp(data);
						logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "链接短信网关,状态:"	 + connectResp.getStatusStr() + " 序列号：" + connectResp.getSequence_Id());
						break;
					case MsgCommand.CMPP_ACTIVE_TEST_RESP:
						MsgActiveTestResp activeResp = new MsgActiveTestResp(data);
						logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "短信网关与短信网关进行连接检查"	+ " 序列号：" + activeResp.getSequence_Id());
						break;
					case MsgCommand.CMPP_SUBMIT_RESP:
						MsgSubmitResp submitResp = new MsgSubmitResp(data);
						logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "向用户下发短信，状态码:"	+ submitResp.getResult() + " 序列号：" + submitResp.getSequence_Id());
						break;
					case MsgCommand.CMPP_TERMINATE_RESP:
						// logger.info("数据乐园于"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
						// Date())+"拆除与ISMG的链接"+" 序列号："+head.getSequenceId());
						break;
					case MsgCommand.CMPP_CANCEL_RESP:
						logger.info("CMPP_CANCEL_RESP 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_CANCEL:
						logger.info("CMPP_CANCEL 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_DELIVER:
						MsgDeliver msgDeliver = new MsgDeliver(data);
						if (msgDeliver.getResult() == 0) {
							logger.info("CMPP_DELIVER 序列号：" + head.getSequence_Id() + "，是否消息回复" + (msgDeliver.getRegistered_Delivery() == 0 ? "不是,消息内容：" + msgDeliver.getMsg_Content()	: "是，目的手机号：" + msgDeliver.getDest_terminal_Id()));
						} else {
							logger.info("CMPP_DELIVER 序列号：" + head.getSequence_Id());
						}
						MsgDeliverResp msgDeliverResp = new MsgDeliverResp();
						msgDeliverResp.setTotal_Length(12 + 8 + 4);
						msgDeliverResp.setCommand_Id(MsgCommand.CMPP_DELIVER_RESP);
						msgDeliverResp.setSequence_Id(MsgUtils.getSequence());
						msgDeliverResp.setMsg_Id(msgDeliver.getMsg_Id());
						msgDeliverResp.setResult(msgDeliver.getResult());
						sendMsg(msgDeliverResp.toByteArry());// 进行回复
						break;
					case MsgCommand.CMPP_DELIVER_RESP:
						logger.info("CMPP_DELIVER_RESP 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_QUERY:
						logger.info("CMPP_QUERY 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_QUERY_RESP:
						logger.info("CMPP_QUERY_RESP 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_TERMINATE:
						logger.info("CMPP_TERMINATE 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_CONNECT:
						logger.info("CMPP_CONNECT 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_ACTIVE_TEST:
						logger.info("CMPP_ACTIVE_TEST 序列号：" + head.getSequence_Id());
						break;
					case MsgCommand.CMPP_SUBMIT:
						logger.info("CMPP_SUBMIT 序列号：" + head.getSequence_Id());
						break;
					default:
						logger.error("无法解析IMSP返回的包结构：包长度为" + head.getTotal_Length());
						break;
					}
				}
			}
		}
	}

	public List<byte[]> getGetData() {
		return getData;
	}

	/**
	 * 在本连结上发送已打包后的消息的字节
	 * 
	 * @param data:要发送消息的字节
	 */
	private boolean sendMsg(byte[] data) throws Exception {
		try {
			this.formatData(data);
			out.write(data);
			out.flush();
			return true;
		} catch (NullPointerException ef) {
			logger.error("在本连结上发送已打包后的消息的字节:无字节输入");
		}
		return false;
	}

	private void formatData(byte[] data) {
		for (int i = 0; i < data.length; i++) {
			int b = data[i];
			if (b < 0) {
				b += 256;
			}
			// 16进制如果不满2位则补零
			String hexString = Integer.toHexString(b);
			hexString = (hexString.length() == 1) ? "0" + hexString : hexString;
			// logger.info(hexString + "  ");
		}
		logger.info("formatData end: " + Arrays.toString(data));
	}
	
	private byte[] getInData() throws IOException {
		try {
			int len = in.readInt();
			logger.info("[InData]>>>"+len);
			//(len > 0) && (len < 500) --- null != in && 0 != len
			if ((len > 0) && (len < 500)) {
				byte[] data = new byte[len - 4];
				this.formatData(data);
				in.read(data);
				logger.info("[InData] read data is >>> "+Arrays.toString(data));
				return data;
			} else {
				return null;
			}
		} catch (NullPointerException ef) {
			logger.error("在本连结上接受字节消息:无流输入");
			return null;
		} catch (EOFException eof) {
			logger.error("在本连结上接受字节消息:" + eof.getMessage());
			return null;
		}
	}
}
