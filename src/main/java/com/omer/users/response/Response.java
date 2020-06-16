package com.omer.users.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Response<T> {

    private boolean success;
    private String message;
    private T content;
}
