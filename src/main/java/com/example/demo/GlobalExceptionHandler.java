package com.example.demo;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// ★multipartのサイズ超過はSpringがハンドラー（コントローラー）を特定するより前の段階で例外化されるため、
// コントローラー個別の@ExceptionHandlerでは拾えない（handlerがnullのまま例外解決される）。
// アプリ全体を対象にする@ControllerAdviceにしないと拾えず、真っ白な413エラーになってしまう。
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleTooLarge() {
        return "redirect:/?error=bgTooLarge";
    }
}
