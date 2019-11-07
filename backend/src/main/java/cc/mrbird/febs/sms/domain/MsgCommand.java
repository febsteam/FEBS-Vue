package cc.mrbird.febs.sms.domain;

/**
 * 短信命令代码标识 链接请求、终止连接请求、提交短信请求、长链接激活等
 */
public interface MsgCommand {
	int CMPP_CONNECT = 0x00000001;// CMPP_CONNECT请求连接
	int CMPP_CONNECT_RESP = 0x80000001;// 请求连接应答
	int CMPP_TERMINATE = 0x00000002;// 终止连接
	int CMPP_TERMINATE_RESP = 0x80000002; // 终止连接应答
	int CMPP_SUBMIT = 0x00000004; // 提交短信
	int CMPP_SUBMIT_RESP = 0x80000004; // 提交短信应答
	int CMPP_DELIVER = 0x00000005; // 短信下发
	int CMPP_DELIVER_RESP = 0x80000005; // 下发短信应答
	int CMPP_QUERY = 0x00000006; // 发送短信状态查询
	int CMPP_QUERY_RESP = 0x80000006; // 发送短信状态查询应答
	int CMPP_CANCEL = 0x00000007; // 删除短信
	int CMPP_CANCEL_RESP = 0x80000007; // 删除短信应答
	int CMPP_ACTIVE_TEST = 0x00000008; // 激活测试
	int CMPP_ACTIVE_TEST_RESP = 0x80000008; // 激活测试应答
}
