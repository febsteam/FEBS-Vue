package cc.mrbird.febs.sms.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.mrbird.febs.sms.domain.MsgCommand;
import cc.mrbird.febs.sms.domain.MsgConnect;
import cc.mrbird.febs.sms.domain.MsgHead;
import cc.mrbird.febs.sms.domain.MsgSubmit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 短信接口容器，单例获得链接对象
 *
 */
public class MsgContainer {
	private static Logger logger = LoggerFactory.getLogger(MsgContainer.class);
	private static Socket msgSocket;
	private static DataInputStream in;
	private static DataOutputStream out;

	public static DataInputStream getSocketDIS() {
		if (in == null || null == msgSocket || msgSocket.isClosed() || !msgSocket.isConnected()) {
			try {
				in = new DataInputStream(MsgContainer.getSocketInstance().getInputStream());
			} catch (IOException e) {
				in = null;
			}
		}
		return in;
	}

	public static DataOutputStream getSocketDOS() {
		if (out == null || null == msgSocket || msgSocket.isClosed() || !msgSocket.isConnected()) {
			try {
				out = new DataOutputStream(MsgContainer.getSocketInstance().getOutputStream());
			} catch (IOException e) {
				out = null;
			}
		}
		return out;
	}

	public static Socket getSocketInstance() {
		if (null == msgSocket || msgSocket.isClosed() || !msgSocket.isConnected()) {
			logger.info("---------重新连接远程短信网关系统------------");
			try {
				in = null;
				out = null;
				msgSocket = new Socket(MsgConfig.getIsmgIp(), MsgConfig.getIsmgPort());
				msgSocket.setKeepAlive(true);
				in = getSocketDIS();
				out = getSocketDOS();
				int count = 0;
				boolean result = connectISMG();
				while (!result) {
					count++;
					result = connectISMG();
					if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次连接次数超过两次则终止连接
						break;
					}
				}
			} catch (UnknownHostException e) {
				logger.error("Socket链接短信网关端口号不正确：" + e.getMessage());
				// 链接短信网关
			} catch (IOException e) {
				logger.error("Socket链接短信网关失败：" + e.getMessage());
			}
		}
		return msgSocket;
	}

	/**
	 * 创建Socket链接后请求链接ISMG
	 * 
	 * @return
	 */
	private static boolean connectISMG() {
		MsgConnect connect = new MsgConnect();
		connect.setTotal_Length(12 + 6 + 16 + 1 + 4);// 消息总长度，级总字节数:4+4+4(消息头)+6+16+1+4(消息主体)
		connect.setCommand_Id(MsgCommand.CMPP_CONNECT);// 标识创建连接
		connect.setSequence_Id(MsgUtils.getSequence());// 序列，由我们指定
		connect.setSource_Addr(MsgConfig.getSpId());// 我们的企业代码
		connect.setAuthenticatorSource(
				MsgUtils.getAuthenticatorSource(MsgConfig.getSpId(), MsgConfig.getSpSharedSecret()));// md5(企业代码+密匙+时间戳)
		connect.setTimestamp(Integer.parseInt(MsgUtils.getTimestamp()));// 时间戳(MMDDHHMMSS)
		connect.setVersion((byte) 0x30);// 版本号 高4bit为3，低4位为0
		List<byte[]> dataList = new ArrayList<byte[]>();
		dataList.add(connect.toByteArry());
		CmppSender sender = new CmppSender(getSocketDOS(), getSocketDIS(), dataList);
		try {
			sender.start();
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			return false;
		}
	}

	/**
	 * 发送SMS短信
	 * 
	 * @param msg
	 *            短信内容
	 * @param cusMsisdn
	 *            接收的手机号码
	 * @return
	 */
	public static boolean sendMsg(String msg, String cusMsisdn) throws Exception {
		try {
			if (msg.getBytes("utf-8").length < 140) {// 短短信
				boolean result = sendShortMsg(msg, cusMsisdn);
				int count = 0;
				while (!result) {
					count++;
					result = sendShortMsg(msg, cusMsisdn);
					if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次连接次数超过两次则终止连接
						break;
					}
				}
				return result;
			} else {// 长短信
				boolean result = sendLongMsg(msg, cusMsisdn);
				int count = 0;
				while (!result) {
					count++;
					result = sendLongMsg(msg, cusMsisdn);
					if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次连接次数超过两次则终止连接
						break;
					}
				}
				return result;
			}
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			return false;
		}
	}

	/**
	 * 发送web push短信
	 * 
	 * @param url
	 *            wap网址
	 * @param desc
	 *            描述
	 * @param cusMsisdn
	 *            短信
	 * @return
	 */
	public static boolean sendWapPushMsg(String url, String desc, String cusMsisdn) {
		try {
			int msgContent = 12 + 9 + 9 + url.getBytes("utf-8").length + 3 + desc.getBytes("utf-8").length + 3;
			if (msgContent < 140) {
				boolean result = sendShortWapPushMsg(url, desc, cusMsisdn);
				int count = 0;
				while (!result) {
					count++;
					result = sendShortWapPushMsg(url, desc, cusMsisdn);
					if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次连接次数超过两次则终止连接
						break;
					}
				}
				return result;
			} else {
				boolean result = sendLongWapPushMsg(url, desc, cusMsisdn);
				int count = 0;
				while (!result) {
					count++;
					result = sendLongWapPushMsg(url, desc, cusMsisdn);
					if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次连接次数超过两次则终止连接
						break;
					}
				}
				return result;
			}
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			logger.error("发送web push短信:" + e.getMessage());
			return false;
		}
	}

	public static int getWordCount(String s) {
		s = s.replaceAll("[^\\x00-\\xff]", "**");
		int length = s.length();
		return length;
	}

	/**
	 * 发送短短信
	 * 
	 * @return
	 */
	private static boolean sendShortMsg(String msg, String cusMsisdn) {
		try {
			int seq = MsgUtils.getSequence();
			MsgSubmit submit = new MsgSubmit();
			submit.setTotal_Length(12 + 8 + 1 + 1 + 1 + 1 + 10 + 1 + 32 + 1 + 1 + 1 + 1 + 6 + 2 + 6 + 17 + 17 + 21 + 1
					+ 32 + 1 + 1 + getWordCount(msg) + 20);
			submit.setCommand_Id(MsgCommand.CMPP_SUBMIT);
			submit.setSequence_Id(seq);
			submit.setMsg_Id(MsgUtils.getMsgId());
			submit.setPk_total((byte) 0x01);
			submit.setPk_number((byte) 0x01);
			submit.setRegistered_Delivery((byte) 0x00);
			submit.setMsg_level((byte) 0x01);
			submit.setFee_UserType((byte) 0x02);
			submit.setFee_terminal_Id("");
			submit.setFee_terminal_type((byte) 0x00);
			submit.setTP_pId((byte) 0x00);
			submit.setTP_udhi((byte) 0x00);
			submit.setMsg_Fmt((byte) 0x0f);
			submit.setMsg_src(MsgConfig.getSpId());
			submit.setSrc_Id(MsgConfig.getSpCode());
			submit.setDest_terminal_Id(cusMsisdn);
			submit.setMsg_Length((byte) (getWordCount(msg)));
			submit.setMsg_Content(msg.getBytes("gb2312"));
			submit.setService_Id(MsgConfig.getServiceId()); // 企业代码

			List<byte[]> dataList = new ArrayList<byte[]>();
			dataList.add(submit.toByteArry());
			CmppSender sender = new CmppSender(getSocketDOS(), getSocketDIS(), dataList);
			sender.start();
			logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "向" + cusMsisdn + "下发短短信，序列号为:"
					+ seq);
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			logger.error("发送短短信", e);
			return false;
		}
	}

	/**
	 * 发送长短信
	 * 
	 * @return
	 */
	private static boolean sendLongMsg(String msg, String cusMsisdn) {
		try {
			byte[] allByte = msg.getBytes("UTF-16BE");
			List<byte[]> dataList = new ArrayList<byte[]>();
			int msgLength = allByte.length;
			int maxLength = 140;
			int msgSendCount = msgLength % (maxLength - 6) == 0 ? msgLength / (maxLength - 6)
					: msgLength / (maxLength - 6) + 1;
			// 短信息内容头拼接
			byte[] msgHead = new byte[6];
			msgHead[0] = 0x05;
			msgHead[1] = 0x00;
			msgHead[2] = 0x03;
			msgHead[3] = (byte) MsgUtils.getSequence();
			msgHead[4] = (byte) msgSendCount;
			msgHead[5] = 0x01;
			int seqId = MsgUtils.getSequence();
			long msgId = MsgUtils.getMsgId();
			for (int i = 0; i < msgSendCount; i++) {
				// msgHead[3]=(byte)MsgUtils.getSequence();
				msgHead[5] = (byte) (i + 1);
				byte[] needMsg = null;
				// 消息头+消息内容拆分
				if (i != msgSendCount - 1) {
					int start = (maxLength - 6) * i;
					int end = (maxLength - 6) * (i + 1);
					needMsg = MsgUtils.getMsgBytes(allByte, start, end);
				} else {
					int start = (maxLength - 6) * i;
					int end = allByte.length;
					needMsg = MsgUtils.getMsgBytes(allByte, start, end);
				}
				int subLength = needMsg.length + msgHead.length;
				byte[] sendMsg = new byte[needMsg.length + msgHead.length];
				System.arraycopy(msgHead, 0, sendMsg, 0, 6);
				System.arraycopy(needMsg, 0, sendMsg, 6, needMsg.length);
				MsgSubmit submit = new MsgSubmit();
				submit.setTotal_Length(12 + 8 + 1 + 1 + 1 + 1 + 10 + 1 + 32 + 1 + 1 + 1 + 1 + 6 + 2 + 6 + 17 + 17 + 21
						+ 1 + 32 + 1 + 1 + subLength + 20);
				submit.setCommand_Id(MsgCommand.CMPP_SUBMIT);
				submit.setSequence_Id(seqId);
				submit.setMsg_Id(msgId);
				submit.setPk_total((byte) msgSendCount);
				submit.setPk_number((byte) (i + 1));
				submit.setRegistered_Delivery((byte) 0x01);
				submit.setMsg_level((byte) 0x01);
				submit.setFee_UserType((byte) 0x00);
				submit.setFee_terminal_Id("");
				submit.setFee_terminal_type((byte) 0x00);
				submit.setTP_pId((byte) 0x00);
				submit.setTP_udhi((byte) 0x01);
				submit.setMsg_Fmt((byte) 0x08);
				submit.setMsg_src(MsgConfig.getSpId());
				submit.setSrc_Id(MsgConfig.getSpCode());
				submit.setDest_terminal_Id(cusMsisdn);
				submit.setMsg_Length((byte) subLength);
				submit.setMsg_Content(sendMsg);
				dataList.add(submit.toByteArry());
			}
			CmppSender sender = new CmppSender(getSocketDOS(), getSocketDIS(), dataList);
			sender.start();
			logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "向" + cusMsisdn + "下发长短信，序列号为:"
					+ seqId);
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			logger.error("发送长短信" + e.getMessage());
			return false;
		}
	}

	/**
	 * 拆除与ISMG的链接
	 * 
	 * @return
	 */
	public static boolean cancelISMG() {
		try {
			MsgHead head = new MsgHead();
			head.setTotal_Length(12);// 消息总长度，级总字节数:4+4+4(消息头)+6+16+1+4(消息主体)
			head.setCommand_Id(MsgCommand.CMPP_TERMINATE);// 标识创建连接
			head.setSequence_Id(MsgUtils.getSequence());// 序列，由我们指定

			List<byte[]> dataList = new ArrayList<byte[]>();
			dataList.add(head.toByteArry());
			CmppSender sender = new CmppSender(getSocketDOS(), getSocketDIS(), dataList);
			sender.start();
			getSocketInstance().close();
			out.close();
			in.close();
			return true;
		} catch (Exception e) {
			try {
				out.close();
				in.close();
			} catch (IOException e1) {
				in = null;
				out = null;
			}
			logger.error("拆除与ISMG的链接", e);
			return false;
		}
	}

	/**
	 * 链路检查
	 * 
	 * @return
	 */
	public static boolean activityTestISMG() {
		try {
			MsgHead head = new MsgHead();
			head.setTotal_Length(12);// 消息总长度，级总字节数:4+4+4(消息头)+6+16+1+4(消息主体)
			head.setCommand_Id(MsgCommand.CMPP_ACTIVE_TEST);// 标识创建连接
			head.setSequence_Id(MsgUtils.getSequence());// 序列，由我们指定
			logger.info("链路检查内容 ： " + head.toString());
			List<byte[]> dataList = new ArrayList<byte[]>();
			dataList.add(head.toByteArry());
			CmppSender sender = new CmppSender(getSocketDOS(), getSocketDIS(), dataList);
			sender.start();
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			logger.error("链路检查", e);
			return false;
		}
	}

	/**
	 * 发送web push 短短信
	 * 
	 * @param url
	 *            wap网址
	 * @param desc
	 *            描述
	 * @param cusMsisdn
	 *            短信
	 * @return
	 */
	private static boolean sendShortWapPushMsg(String url, String desc, String cusMsisdn) {
		try {
			// length 12
			byte[] szWapPushHeader1 = { 0x0B, 0x05, 0x04, 0x0B, (byte) 0x84, 0x23, (byte) 0xF0, 0x00, 0x03, 0x03, 0x01,
					0x01 };
			// length 9
			byte[] szWapPushHeader2 = { 0x29, 0x06, 0x06, 0x03, (byte) 0xAE, (byte) 0x81, (byte) 0xEA, (byte) 0x8D,
					(byte) 0xCA };
			// length 9
			byte[] szWapPushIndicator = { 0x02, 0x05, 0x6A, 0x00, 0x45, (byte) 0xC6, 0x08, 0x0C, 0x03 };
			// 去除了http://前缀的UTF8编码的Url地址"的二进制编码
			byte[] szWapPushUrl = url.getBytes("utf-8");
			// length 3
			byte[] szWapPushDisplayTextHeader = { 0x00, 0x01, 0x03 };
			// 想在手机上显示的关于这个URL的文字说明,UTF8编码的二进制
			byte szMsg[] = desc.getBytes("utf-8");
			// length 3
			byte[] szEndOfWapPush = { 0x00, 0x01, 0x01 };
			int msgLength = 12 + 9 + 9 + szWapPushUrl.length + 3 + szMsg.length + 3;
			int seq = MsgUtils.getSequence();
			MsgSubmit submit = new MsgSubmit();
			submit.setTotal_Length(12 + 8 + 1 + 1 + 1 + 1 + 10 + 1 + 32 + 1 + 1 + 1 + 1 + 6 + 2 + 6 + 17 + 17 + 21 + 1
					+ 32 + 1 + 1 + msgLength + 20);
			submit.setCommand_Id(MsgCommand.CMPP_SUBMIT);
			submit.setSequence_Id(seq);
			submit.setPk_total((byte) 0x01);
			submit.setPk_number((byte) 0x01);
			submit.setRegistered_Delivery((byte) 0x01);
			submit.setMsg_level((byte) 0x01);
			submit.setFee_UserType((byte) 0x00);
			submit.setFee_terminal_Id("");
			submit.setFee_terminal_type((byte) 0x00);
			submit.setTP_pId((byte) 0x00);
			submit.setTP_udhi((byte) 0x01);
			submit.setMsg_Fmt((byte) 0x04);
			submit.setMsg_src(MsgConfig.getSpId());
			submit.setSrc_Id(MsgConfig.getSpCode());
			submit.setDest_terminal_Id(cusMsisdn);
			submit.setMsg_Length((byte) msgLength);
			byte[] sendMsg = new byte[12 + 9 + 9 + szWapPushUrl.length + 3 + szMsg.length + 3];
			System.arraycopy(szWapPushHeader1, 0, sendMsg, 0, 12);
			System.arraycopy(szWapPushHeader2, 0, sendMsg, 12, 9);
			System.arraycopy(szWapPushIndicator, 0, sendMsg, 12 + 9, 9);
			System.arraycopy(szWapPushUrl, 0, sendMsg, 12 + 9 + 9, szWapPushUrl.length);
			System.arraycopy(szWapPushDisplayTextHeader, 0, sendMsg, 12 + 9 + 9 + szWapPushUrl.length, 3);
			System.arraycopy(szMsg, 0, sendMsg, 12 + 9 + 9 + szWapPushUrl.length + 3, szMsg.length);
			System.arraycopy(szEndOfWapPush, 0, sendMsg, 12 + 9 + 9 + szWapPushUrl.length + 3 + szMsg.length, 3);
			submit.setMsg_Content(sendMsg);
			List<byte[]> dataList = new ArrayList<byte[]>();
			dataList.add(submit.toByteArry());
			CmppSender sender = new CmppSender(getSocketDOS(), getSocketDIS(), dataList);
			sender.start();
			logger.info("数据乐园于" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "向" + cusMsisdn
					+ "下发web push短短信，序列号为:" + seq);
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			logger.error("发送web push短短信", e);
			return false;
		}
	}

	/**
	 * 发送web push 长短信
	 * 
	 * @param url
	 *            wap网址
	 * @param desc
	 *            描述
	 * @param cusMsisdn
	 *            短信
	 * @return
	 */
	private static boolean sendLongWapPushMsg(String url, String desc, String cusMsisdn) {
		try {
			List<byte[]> dataList = new ArrayList<byte[]>();
			// length 12
			byte[] wdp = { 0x0B, 0x05, 0x04, 0x0B, (byte) 0x84, 0x23, (byte) 0xF0, 0x00, 0x03, 0x03, 0x01, 0x01 };
			// 需要拆分的部分
			// length 9
			byte[] wsp = { 0x29, 0x06, 0x06, 0x03, (byte) 0xAE, (byte) 0x81, (byte) 0xEA, (byte) 0x8D, (byte) 0xCA };
			// length 9
			byte[] szWapPushIndicator = { 0x02, 0x05, 0x6A, 0x00, 0x45, (byte) 0xC6, 0x08, 0x0C, 0x03 };
			// 去除了http://前缀的UTF8编码的Url地址"的二进制编码
			byte[] szWapPushUrl = url.getBytes("utf-8");
			// length 3
			byte[] szWapPushDisplayTextHeader = { 0x00, 0x01, 0x03 };
			// 想在手机上显示的关于这个URL的文字说明,UTF8编码的二进制
			byte szMsg[] = desc.getBytes("utf-8");
			// length 3
			byte[] szEndOfWapPush = { 0x00, 0x01, 0x01 };
			byte[] allByte = new byte[9 + 9 + szWapPushUrl.length + 3 + szMsg.length + 3];

			System.arraycopy(wsp, 0, allByte, 0, 9);
			System.arraycopy(szWapPushIndicator, 0, allByte, 9, 9);
			System.arraycopy(szWapPushUrl, 0, allByte, 18, szWapPushUrl.length);
			System.arraycopy(szWapPushDisplayTextHeader, 0, allByte, 18 + szWapPushUrl.length, 3);
			System.arraycopy(szMsg, 0, allByte, 18 + szWapPushUrl.length + 3, szMsg.length);
			System.arraycopy(szEndOfWapPush, 0, allByte, 18 + szWapPushUrl.length + 3 + szMsg.length, 3);
			int msgMax = 140;
			int msgCount = allByte.length % (msgMax - wdp.length) == 0 ? allByte.length / (msgMax - wdp.length)
					: allByte.length / (msgMax - wdp.length) + 1;
			wdp[10] = (byte) msgCount;
			int seqId = MsgUtils.getSequence();
			for (int i = 0; i < msgCount; i++) {
				wdp[11] = (byte) (i + 1);
				byte[] needMsg = null;
				// 消息头+消息内容拆分
				if (i != msgCount - 1) {
					int start = (msgMax - wdp.length) * i;
					int end = (msgMax - wdp.length) * (i + 1);
					needMsg = MsgUtils.getMsgBytes(allByte, start, end);
				} else {
					int start = (msgMax - wdp.length) * i;
					int end = allByte.length;
					needMsg = MsgUtils.getMsgBytes(allByte, start, end);
				}
				int msgLength = needMsg.length + wdp.length;
				MsgSubmit submit = new MsgSubmit();
				submit.setTotal_Length(12 + 8 + 1 + 1 + 1 + 1 + 10 + 1 + 32 + 1 + 1 + 1 + 1 + 6 + 2 + 6 + 17 + 17 + 21
						+ 1 + 32 + 1 + 1 + msgLength + 20);
				submit.setCommand_Id(MsgCommand.CMPP_SUBMIT);
				submit.setSequence_Id(seqId);
				submit.setPk_total((byte) msgCount);
				submit.setPk_number((byte) (i + 1));
				submit.setRegistered_Delivery((byte) 0x01);
				submit.setMsg_level((byte) 0x01);
				submit.setFee_UserType((byte) 0x00);
				submit.setFee_terminal_Id("");
				submit.setFee_terminal_type((byte) 0x00);
				submit.setTP_pId((byte) 0x00);
				submit.setTP_udhi((byte) 0x01);
				submit.setMsg_Fmt((byte) 0x04);
				submit.setMsg_src(MsgConfig.getSpId());
				submit.setSrc_Id(MsgConfig.getSpCode());
				submit.setDest_terminal_Id(cusMsisdn);
				submit.setMsg_Length((byte) msgLength);
				byte[] sendMsg = new byte[wdp.length + needMsg.length];
				System.arraycopy(wdp, 0, sendMsg, 0, wdp.length);
				System.arraycopy(needMsg, 0, sendMsg, wdp.length, needMsg.length);
				submit.setMsg_Content(sendMsg);
				dataList.add(submit.toByteArry());
			}
			CmppSender sender = new CmppSender(getSocketDOS(), getSocketDIS(), dataList);
			sender.start();
			logger.info("数据乐园于" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "向" + cusMsisdn
					+ "下发web pus长短信，序列号为:" + seqId);
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				out = null;
			}
			logger.error("发送web push长短信" + e.getMessage());
			return false;
		}
	}
}
