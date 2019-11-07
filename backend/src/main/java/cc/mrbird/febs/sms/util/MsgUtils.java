package cc.mrbird.febs.sms.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 短信接口辅助工具类
 */
public class MsgUtils {
	private static Logger LOGGER = LoggerFactory.getLogger(MsgUtils.class);
	private static int sequenceId = 0;// 序列编号
	private static int msgIdSuffix = 0;

	/**
	 * 序列 自增
	 */
	public static int getSequence() {
		++sequenceId;
		if (sequenceId > 255) {
			sequenceId = 0;
		}
		return sequenceId;
	}

	/**
	 * 根据规则生成msgId 采用64位（8字节）的整数： （1） 时间（格式为MMDDHHMMSS，即月日时分秒）：bit64~bit39，其中
	 * bit64~bit61：月份的二进制表示； bit60~bit56：日的二进制表示； bit55~bit51：小时的二进制表示；
	 * bit50~bit45：分的二进制表示； bit44~bit39：秒的二进制表示； （2）
	 * 短信网关代码：bit38~bit17，把短信网关的代码转换为整数填写到该字段中； （3） 序列号：bit16~bit1，顺序增加，步长为1，循环使用。
	 * 
	 * @return
	 */
	public static long getMsgId() {
		++msgIdSuffix;
		if (msgIdSuffix > 65535) {
			msgIdSuffix = 0;
		}
		// String spCode = MsgConfig.getSpCode();
		String dateStr = getTimestamp();
		System.out.println(dateStr);
		byte month = Byte.parseByte(dateStr.substring(0, 2));
		byte day = Byte.parseByte(dateStr.substring(2, 4));
		byte hour = Byte.parseByte(dateStr.substring(4, 6));
		byte minute = Byte.parseByte(dateStr.substring(6, 8));
		byte second = Byte.parseByte(dateStr.substring(8, 10));

		long msgId = (month << 60) + (day << 55) + (hour << 50) + (minute << 44) + (second << 38) + msgIdSuffix;
		return msgId;
	}

	/**
	 * 时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐 。
	 */
	public static String getTimestamp() {
		DateFormat format = new SimpleDateFormat("MMddHHmmss");
		return format.format(new Date());
	}

	/**
	 * 用于鉴别源地址。其值通过单向MD5 hash计算得出，表示如下： AuthenticatorSource = MD5（Source_Addr+9 字节的0
	 * +shared secret+timestamp） Shared secret
	 * 由中国移动与源地址实体事先商定，timestamp格式为：MMDDHHMMSS，即月日时分秒，10位。
	 * 
	 * @return
	 */
	public static byte[] getAuthenticatorSource(String spId, String secret) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] data = (spId + "\0\0\0\0\0\0\0\0\0" + secret + MsgUtils.getTimestamp()).getBytes();
			return md5.digest(data);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("SP链接到ISMG拼接AuthenticatorSource失败：", e);
			return null;
		}
	}

	/**
	 * 向流中写入指定字节长度的字符串，不足时补0
	 * 
	 * @param dous:要写入的流对象
	 * @param s:要写入的字符串
	 * @param len:写入长度,不足补0
	 */
	public static void writeString(DataOutputStream dous, String s, int len) {

		try {
			byte[] data = s.getBytes("gb2312");
			if (data.length > len) {
				LOGGER.error("向流中写入的字符串超长！要写" + len + " 字符串是:" + s);
			}
			int srcLen = data.length;
			dous.write(data);
			while (srcLen < len) {
				dous.write('\0');
				srcLen++;
			}
		} catch (IOException e) {
			LOGGER.error("向流中写入指定字节长度的字符串失败：", e);
		}
	}

	/**
	 * 从流中读取指定长度的字节，转成字符串返回
	 * 
	 * @param ins:要读取的流对象
	 * @param len:要读取的字符串长度
	 * @return:读取到的字符串
	 */
	public static String readString(java.io.DataInputStream ins, int len) {
		byte[] b = new byte[len];
		try {
			ins.read(b);
			String s = new String(b);
			s = s.trim();
			return s;
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * 截取字节
	 * 
	 * @param msg
	 * @param start
	 * @param end
	 * @return
	 */
	public static byte[] getMsgBytes(byte[] msg, int start, int end) {
		byte[] msgByte = new byte[end - start];
		int j = 0;
		for (int i = start; i < end; i++) {
			msgByte[j] = msg[i];
			j++;
		}
		return msgByte;
	}

	/**
	 * UCS2解码
	 * 
	 * @param src
	 *            UCS2 源串
	 * @return 解码后的UTF-16BE字符串
	 */
	public static String DecodeUCS2(String src) {
		byte[] bytes = new byte[src.length() / 2];
		for (int i = 0; i < src.length(); i += 2) {
			bytes[i / 2] = (byte) (Integer.parseInt(src.substring(i, i + 2), 16));
		}
		String reValue = "";
		try {
			reValue = new String(bytes, "UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			reValue = "";
		}
		return reValue;

	}

	/**
	 * UCS2编码
	 * 
	 * @param src
	 *            UTF-16BE编码的源串
	 * @return 编码后的UCS2串
	 */
	public static String EncodeUCS2(String src) {
		byte[] bytes;
		try {
			bytes = src.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			bytes = new byte[0];
		}
		StringBuffer reValue = new StringBuffer();
		StringBuffer tem = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			tem.delete(0, tem.length());
			tem.append(Integer.toHexString(bytes[i] & 0xFF));
			if (tem.length() == 1) {
				tem.insert(0, '0');
			}
			reValue.append(tem);
		}
		return reValue.toString().toUpperCase();
	}
}
