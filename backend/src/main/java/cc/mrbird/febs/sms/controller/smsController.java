package cc.mrbird.febs.sms.controller;

import java.util.HashMap;
import java.util.Map;

import cc.mrbird.febs.sms.util.MsgContainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/service")
public class smsController {

	private static Logger LOGGER = LoggerFactory.getLogger(smsController.class);

	@RequestMapping(value = "/send", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> sendSms(@RequestBody Map<String, String> param) {
			String msg = param.get("msg");
		String tel = param.get("tel");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 建立连接
			boolean bl = MsgContainer.sendMsg(msg, tel);
			if(bl){
				map.put("result", true);
				LOGGER.info("[TEL]" + tel + ">>>短信发送成功");
				LOGGER.info("[MSG]" + msg);
				return map;
			}else {
				LOGGER.info("[TEL]" + tel + ">>>短信发送失败");
				LOGGER.info("[MSG]" + msg);
				map.put("result", false);
			}

			return map;
		} catch (Exception e) {
			LOGGER.error("短信发送失败:", e);
			map.put("result", false);
			return map;
		}
	}

}
