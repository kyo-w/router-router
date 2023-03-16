package com.kyodream.end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Args {
    private String address;
    private Integer port;
    private String url;
}
