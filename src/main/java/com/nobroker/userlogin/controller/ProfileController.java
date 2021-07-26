package com.nobroker.userlogin.controller;
import com.nobroker.userlogin.entities.ErrorEntity;
import com.nobroker.userlogin.entities.UserEntity;
import com.nobroker.userlogin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController implements ErrorController {

    @Autowired
    private UserService userService;

    public static class InvalidUserException extends RuntimeException {
        public InvalidUserException() {
            super("Invalid request");
        }
    }
@GetMapping("/login")
public ModelAndView home(){
    ModelAndView mv = new ModelAndView("login");
    return mv;
}

@PostMapping("/userlogin")
public void dashboard(@RequestParam("username") String username,
                              @RequestParam("password") String password){
    userService.verifyUser(username,password);
}

    @GetMapping("/showdashboard")
  public ModelAndView show(HttpServletRequest request){

            String username = (String) request.getSession().getAttribute("username");
            UserEntity user = userService.findUserByName(username);
            ModelAndView mv = new ModelAndView();
            if(user.getRole().equals("user")) {
                mv.addObject("username", username);
                mv.setViewName("dashboard");
            }
            else{
                mv.addObject("users",userService.findAll());
                mv.setViewName("superuser");
            }

            return mv;
    }

    @PostMapping("/registerUser")
    public ModelAndView registerUser(@RequestParam("username") String username,
                                     @RequestParam("password") String password){

            ModelAndView mv = new ModelAndView("superuser");
            mv.addObject("users",userService.findAll());

            try {
                 userService.registerNewUser(username, password, "user");
            }
            catch(RuntimeException e){
                mv.addObject("error",e.getMessage());
                return mv;
            }
            mv.addObject("error","User registered successfully!");
            return mv;
    }


    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("username") String username){

            userService.deleteUser(username);
            return "redirect:/showdashboard";
    }

    @PostMapping("/changeRole")
    public ModelAndView changeRole(@RequestParam("username") String username,
                            @RequestParam("role") String role){
            userService.upgradeUserRole(username,role);
            ModelAndView mv = new ModelAndView();
            mv.addObject("users", userService.findAll());
            mv.setViewName("superuser");
            return mv;
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorEntity> handleExceptions(RuntimeException exception){
            String message = exception.getMessage();
            HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;

            if(exception instanceof UserService.UserNotFoundException){
                errorStatus = HttpStatus.NOT_FOUND;
            }
            return new ResponseEntity<ErrorEntity>(ErrorEntity.from(message), errorStatus);
    }

}
