package com.ka.springbootinit.model.dto.postfavour;

import java.io.Serializable;
import lombok.Data;


@Data
public class PostFavourAddRequest implements Serializable {


    private Long postId;

    private static final long serialVersionUID = 1L;
}