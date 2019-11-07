package cc.mrbird.febs.sms.domain;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import cc.mrbird.febs.sms.util.MsgUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SP请求连接到ISMG消息体定义CMPP_CONNECT操作的目的是SP向ISMG注册作为一个合法SP身份，
 * 若注册成功后即建立了应用层的连接，此后SP可以通过此ISMG接收和发送短信。<br/>
 * Source_Addr:Octet String 源地址，此处为SP_Id，即SP的企业代码。<br/>
 * AuthenticatorSource:Octet String 用于鉴别源地址。其值通过单向MD5 hash计算得出，表示如下：
 * AuthenticatorSource =MD5（Source_Addr+9 字节的0 +shared secret+timestamp） Shared
 * secret 由中国移动与源地址实体事先商定，timestamp格式为：MMDDHHMMSS，即月日时分秒，10位。<br/>
 * Version:Unsigned Integer
 * 双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号)，对于3.0的版本，高4bit为3，低4位为0<br/>
 * Timestamp:Unsigned Integer 时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐
 * 。<br/>
 */
public class MsgConnect extends MsgHead {
	private static Logger logger = LoggerFactory.getLogger(MsgConnect.class);
	private String Source_Addr;// 源地址，此处为SP_Id，即SP的企业代码。
	private byte[] AuthenticatorSource;// 用于鉴别源地址。其值通过单向MD5 hash计算得出，表示如下：AuthenticatorSource = MD5（Source_Addr+9 字节的0
										// +shared secret+timestamp） Shared secret
										// 由中国移动与源地址实体事先商定，timestamp格式为：MMDDHHMMSS，即月日时分秒，10位。
	private byte Version;// 双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号)，对于3.0的版本，高4bit为3，低4位为0
	private int Timestamp;// 时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐 。

	public byte[] toByteArry() {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		try {
			dous.writeInt(this.getTotal_Length());
			dous.writeInt(this.getCommand_Id());
			dous.writeInt(this.getSequence_Id());
			MsgUtils.writeString(dous, this.Source_Addr, 6);
			dous.write(AuthenticatorSource);
			dous.writeByte(Version);
			dous.writeInt(Timestamp);
			dous.close();
		} catch (IOException e) {
			logger.error("封装链接二进制数组失败。");
		}
		return bous.toByteArray();
	}

	public String getSource_Addr() {
		return Source_Addr;
	}

	public void setSource_Addr(String source_Addr) {
		Source_Addr = source_Addr;
	}

	public byte[] getAuthenticatorSource() {
		return AuthenticatorSource;
	}

	public void setAuthenticatorSource(byte[] authenticatorSource) {
		AuthenticatorSource = authenticatorSource;
	}

	public byte getVersion() {
		return Version;
	}

	public void setVersion(byte version) {
		Version = version;
	}

	public int getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(int timestamp) {
		Timestamp = timestamp;
	}

}
