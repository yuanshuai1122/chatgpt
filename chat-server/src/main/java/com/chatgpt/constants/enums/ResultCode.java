package com.chatgpt.constants.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
  /**
   * 通用状态码
   */
  SUCCESS(200,"OK"),
  FAILED(-1,"FAIL"),
  /*
  参数错误状态码
   */
  PARAM_IS_INVAlID(101,"参数无效"),

  PARAM_IS_BLANK(101,"参数为空"),
  /* 用户错误  201 - 299  */
  USER_NOT_LOGIN(201,"未登录"),
  USER_NOT_EXIST(202,"用户不存在"),
  USER_LOGIN_ERROR(203,"登陆失败，账号或者密码有误"),
  NOT_PERMISSION(204,"无权限访问"),
  USER_REGISTER_REPEAT(205,"注册失败，用户已存在"),
  USER_REGISTER_ERROR(206,"注册失败"),
  USER_REGISTER_SHARE_CODE_NOT_EXIT(207,"注册失败，推广码不存在"),
  /* 业务错误 301 - 399*/
  KEY_NOT_FOUND(301,"key不存在"),
  KEY_NOT_TIMES(302,"今日提问次数不足，请联系客服咨询"),
  KEY_NOT_DATE(303,"key已过期，请联系客服咨询"),
  KEY_NOT_ACTIVE(304,"key未激活，请联系客服咨询"),
  KEY_NOT_UPDATE(305,"更新key状态失败"),



  VISIT_OUT_TIMES(401,"访问频繁，请稍后再试")


  ;

  //返回状态码
  private Integer code;

  //返回消息
  private String msg;

  private ResultCode() {

  }

  ResultCode(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
