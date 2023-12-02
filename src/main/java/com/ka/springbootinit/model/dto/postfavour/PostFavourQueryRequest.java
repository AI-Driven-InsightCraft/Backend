package com.ka.springbootinit.model.dto.postfavour;

import com.ka.springbootinit.common.PageRequest;
import com.ka.springbootinit.model.dto.post.PostQueryRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class PostFavourQueryRequest extends PageRequest implements Serializable {

    private PostQueryRequest postQueryRequest;

    private Long userId;

    private static final long serialVersionUID = 1L;
}