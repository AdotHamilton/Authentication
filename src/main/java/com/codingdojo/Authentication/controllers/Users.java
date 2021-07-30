package com.codingdojo.Authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codingdojo.Authentication.models.User;
import com.codingdojo.Authentication.services.UserService;
import com.codingdojo.Authentication.validator.UserValidator;

@Controller
public class Users {
	private final UserService userService;
	private final UserValidator userValidator;
 
 public Users(UserService userService, UserValidator userValid) {
     this.userService = userService;
     this.userValidator = userValid;
 }
 
 @RequestMapping("/registration")
 public String registerForm(@ModelAttribute("user") User user) {
     return "registrationPage.jsp";
 }
	@RequestMapping("/login")
 public String login() {
     return "loginPage.jsp";
 }
 
 @RequestMapping(value="/registration", method=RequestMethod.POST)
 public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
	userValidator.validate(user, result);
	 if(result.hasErrors()) {
		return "registrationPage.jsp";
	} else {
		User u = userService.registerUser(user);
		session.setAttribute("user_id", u.getId());
		return "redirect:/home";
	}
     
 }
 
 @RequestMapping(value="/login", method=RequestMethod.POST)
 public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
     if(userService.authenticateUser(email, password) == true) {
    	 User u = userService.findByEmail(email);
    	 session.setAttribute("user_id", u.getId());
    	 return "redirect:/home";
     } else {
    	 return "loginPage.jsp";
     }
 }
 
 @RequestMapping("/home")
 public String home(HttpSession session, Model model) {
     Long id = (Long) session.getAttribute("user_id");
     User u = userService.findUserById(id);
     model.addAttribute("user", u);
     return "homePage.jsp";
 	}
 @RequestMapping("/logout")
 public String logout(HttpSession session) {
     session.invalidate();
     return "redirect:/login";
 }
}
