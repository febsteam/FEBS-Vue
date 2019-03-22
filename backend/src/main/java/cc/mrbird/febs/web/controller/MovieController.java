package cc.mrbird.febs.web.controller;

import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.FebsResponse;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Slf4j
@Validated
@RestController
@RequestMapping("movie")
public class MovieController extends BaseController {

    private String message;

    @GetMapping("hot")
    public FebsResponse getMoiveHot() throws FebsException {
        try {
            String data = HttpUtil.sendSSLPost(FebsConstant.TIME_MOVIE_HOT_URL, "locationId=328");
            return new FebsResponse().data(data);
        } catch (Exception e) {
            message = "获取热映影片信息失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @GetMapping("coming")
    public FebsResponse getMovieComing() throws FebsException {
        try {
            String data = HttpUtil.sendSSLPost(FebsConstant.TIME_MOVIE_COMING_URL, "locationId=328");
            return new FebsResponse().data(data);
        } catch (Exception e) {
            message = "获取即将上映影片信息失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @GetMapping("detail")
    public FebsResponse getDetail(@NotBlank(message = "{required}") String id) throws FebsException {
        try {
            String data = HttpUtil.sendSSLPost(FebsConstant.TIME_MOVIE_DETAIL_URL, "locationId=328&movieId=" + id);
            return new FebsResponse().data(data);
        } catch (Exception e) {
            message = "获取影片详情失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @GetMapping("comments")
    public FebsResponse getComments(@NotBlank(message = "{required}") String id) throws FebsException {
        try {
            String data = HttpUtil.sendSSLPost(FebsConstant.TIME_MOVIE_COMMENTS_URL, "movieId=" + id);
            return new FebsResponse().data(data);
        } catch (Exception e) {
            message = "获取影片评论失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
