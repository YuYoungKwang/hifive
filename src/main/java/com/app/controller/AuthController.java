package com.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.service.LoginService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final LoginService loginService;
    private final ServletContext servletContext;

    @Autowired
    public AuthController(LoginService loginService, ServletContext servletContext) {
        this.loginService = loginService;
        this.servletContext = servletContext;
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/main";
        }
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupProcess(@RequestParam("userId") String userId,
                                @RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("userName") String userName,
                                @RequestParam("userNickName") String userNickName,
                                @RequestParam("userPhoneNumber") String userPhoneNumber,
                                @RequestParam("userRegistrationNo") String userRegistrationNo,
                                @RequestParam(value = "userGender", required = false) String userGender,
                                @RequestParam(value = "userZipCode", required = false) String userZipCode,
                                @RequestParam(value = "userBaseAddress", required = false) String userBaseAddress,
                                @RequestParam(value = "userDetailAddress", required = false) String userDetailAddress,
                                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                RedirectAttributes redirectAttributes) {
        try {
            String savedProfileImagePath = saveProfileImage(profileImage);

            loginService.register(userId, email, password, userName, userNickName, userGender, userPhoneNumber,
                    userRegistrationNo, savedProfileImagePath, userZipCode, userBaseAddress, userDetailAddress);

            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해 주세요.");
            return "redirect:/auth/login?signupSuccess=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/signup";
        } catch (Exception e) {
            Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
            String reason = root != null && root.getMessage() != null ? root.getMessage() : e.getMessage();
            log.error("회원가입 실패 userId={}, email={}", userId, email, e);
            redirectAttributes.addFlashAttribute("errorMessage", "회원가입 실패 원인: " + reason);
            return "redirect:/auth/signup";
        }
    }

    @GetMapping("/find-id")
    public String findIdPage() {
        return "auth/findId";
    }

    @PostMapping("/find-id")
    public String findIdProcess(@RequestParam("email") String email, Model model) {
        String foundId = loginService.findUserIdByEmail(email);
        model.addAttribute("foundId", foundId);
        model.addAttribute("searchedEmail", email);
        return "auth/findIdResult";
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "auth/findPassword";
    }

    @PostMapping("/find-password")
    public String findPasswordProcess(@RequestParam("userId") String userId,
                                      @RequestParam("email") String email,
                                      Model model) {
        String temporaryPassword = loginService.resetPassword(userId, email);
        if (temporaryPassword == null) {
            model.addAttribute("message", "입력한 정보와 일치하는 활성 계정을 찾을 수 없습니다.");
        } else {
            model.addAttribute("message", "임시 비밀번호가 발급되었습니다.");
            model.addAttribute("temporaryPassword", temporaryPassword);
        }
        return "auth/findPasswordResult";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private String saveProfileImage(MultipartFile profileImage) throws IOException {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }

        String contentType = profileImage.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("프로필 이미지는 이미지 파일만 업로드할 수 있습니다.");
        }

        String originalFilename = profileImage.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > -1 && dotIndex < originalFilename.length() - 1) {
                extension = originalFilename.substring(dotIndex).toLowerCase();
            }
        }

        String savedName = UUID.randomUUID().toString().replace("-", "") + extension;
        String uploadRoot = servletContext.getRealPath("/resources/uploads/profile");
        if (uploadRoot == null) {
            throw new IllegalStateException("업로드 경로를 찾을 수 없습니다. 서버 배포 경로를 확인해 주세요.");
        }
        Path uploadDir = Paths.get(uploadRoot);
        Files.createDirectories(uploadDir);

        Path targetPath = uploadDir.resolve(savedName);
        profileImage.transferTo(targetPath.toFile());

        return "/resources/uploads/profile/" + savedName;
    }
}
