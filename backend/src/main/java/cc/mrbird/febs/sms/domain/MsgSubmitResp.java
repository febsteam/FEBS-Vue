package cc.mrbird.febs.sms.domain;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Submit消息结构应答定义
 * 
 */
public class MsgSubmitResp extends MsgHead {
	private static Logger logger = LoggerFactory.getLogger(MsgSubmitResp.class);
	private long msgId;
	private int result;// 结果 0：正确 1：消息结构错 2：命令字错 3：消息序号重复 4：消息长度错 5：资费代码错 6：超过最大信息长 7：业务代码错 8：流量控制错
						// 9：本网关不负责服务此计费号码 10：Src_Id错误 11：Msg_src错误 12：Fee_terminal_Id错误
						// 13：Dest_terminal_Id错误

	public MsgSubmitResp(byte[] data) {
		if (data.length == 8 + 8 + 4) {
			ByteArrayInputStream bins = new ByteArrayInputStream(data);
			DataInputStream dins = new DataInputStream(bins);
			try {
				this.setTotal_Length(data.length + 4);
				this.setCommand_Id(dins.readInt());
				this.setSequence_Id(dins.readInt());
				this.msgId = dins.readLong();
				this.result = dins.readInt();
				dins.close();
				bins.close();
			} catch (IOException e) {
			}
		} else {
			logger.info("发送短信IMSP回复,解析数据包出错，包长度不一致。长度为:" + data.length);
		}
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
