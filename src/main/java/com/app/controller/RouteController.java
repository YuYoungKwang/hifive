package com.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.dto.CommunityDTO;
import com.app.dto.RouteDTO;
import com.app.dto.UserDTO;
import com.app.service.LoginService;
import com.app.service.UserService;
import com.app.service.impl.CommunityService;
import com.app.service.impl.RouteService;

@Controller
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CommunityService communityService;

    @GetMapping
    public String routesPage(Model model, Authentication authentication) {
        model.addAttribute("personalUserName", resolveLoggedInUserName(authentication));

        model.addAttribute("routes", routeService.getAllRoutes());
        model.addAttribute("categories", routeService.getAllTagCategories());
        model.addAttribute("travelerTypes", routeService.getAllTravelerTypes());
        return "routeList";
    }

    @GetMapping("/{routeId}")
    public String routeDetail(@PathVariable Long routeId, Model model) {
        RouteDTO route = routeService.getRouteById(routeId);
        if (route == null) {
            return "redirect:/routes";
        }

        model.addAttribute("route", route);
        model.addAttribute("reviews", communityService.getCommunityReviewsByPlanNo(routeId));
        model.addAttribute("travelerTypes", routeService.getAllTravelerTypes());
        model.addAttribute("categories", routeService.getAllTagCategories());
        return "routeDetail";
    }

    @GetMapping("/filter")
    @ResponseBody
    public List<RouteDTO> filterRoutes(@RequestParam(required = false) String category) {
        return routeService.getRoutesByCategory(category);
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveRoute(@RequestParam Long routeId) {
        return Map.of("success", true, "message", "Route saved.");
    }

    @PostMapping("/{routeId}/reviews")
    @ResponseBody
    public Map<String, Object> writeReview(
            @PathVariable Long routeId,
            @RequestParam String reviewContent,
            @RequestParam(required = false, defaultValue = "5") Integer rating,
            @RequestParam(required = false) Long userNo,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long resolvedUserNo = resolveLoggedInUserNo(authentication, userNo);
            CommunityDTO savedReview = communityService.addCommunityReview(routeId, resolvedUserNo, reviewContent, rating);
            List<CommunityDTO> reviews = communityService.getCommunityReviewsByPlanNo(routeId);

            response.put("success", true);
            response.put("review", savedReview);
            response.put("reviewCount", reviews.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/user-info")
    public String getUserInfo(@RequestParam int userNo) {
        return userService.findUserName(userNo);
    }

    private String resolveLoggedInUserName(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "여행자";
        }

        String authId = authentication.getName();
        UserDTO loginUser = loginService.findByAuthId(authId);
        if (loginUser == null) {
            return authId;
        }

        if (loginUser.getUserNickName() != null && !loginUser.getUserNickName().trim().isEmpty()) {
            return loginUser.getUserNickName().trim();
        }
        if (loginUser.getUserName() != null && !loginUser.getUserName().trim().isEmpty()) {
            return loginUser.getUserName().trim();
        }
        return authId;
    }

    private Long resolveLoggedInUserNo(Authentication authentication, Long fallbackUserNo) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            String authId = authentication.getName();
            UserDTO loginUser = loginService.findByAuthId(authId);
            if (loginUser != null && loginUser.getUserNo() != null) {
                return loginUser.getUserNo();
            }
        }

        if (fallbackUserNo != null) {
            return fallbackUserNo;
        }
        throw new IllegalStateException("로그인 사용자 정보를 찾을 수 없습니다.");
    }
}
