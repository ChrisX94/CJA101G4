package com.shakemate.shshop.util;

public class MessageForOpenAi {
    // 與Open AI 之間的傳輸用json 用這個class來做為的物件

    private String role;
    private String content;

    public MessageForOpenAi() {}

    // 建構子
    public MessageForOpenAi(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
