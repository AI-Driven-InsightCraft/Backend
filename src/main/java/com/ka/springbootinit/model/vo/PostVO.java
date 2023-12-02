package com.ka.springbootinit.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ka.springbootinit.model.entity.Post;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;


@Data
public class PostVO implements Serializable {

    private final static Gson GSON = new Gson();

    private Long id;

    private String title;

    private String content;

    private Integer thumbNum;

    private Integer favourNum;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    private List<String> tagList;

    private UserVO user;

    private Boolean hasThumb;

    private Boolean hasFavour;

    public static Post voToObj(PostVO postVO) {
        if (postVO == null) {
            return null;
        }
        Post post = new Post();
        BeanUtils.copyProperties(postVO, post);
        List<String> tagList = postVO.getTagList();
        if (tagList != null) {
            post.setTags(GSON.toJson(tagList));
        }
        return post;
    }

    public static PostVO objToVo(Post post) {
        if (post == null) {
            return null;
        }
        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);
        postVO.setTagList(GSON.fromJson(post.getTags(), new TypeToken<List<String>>() {
        }.getType()));
        return postVO;
    }
}
