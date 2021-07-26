package com.nobroker.userlogin.controller;
import com.nobroker.userlogin.entities.ErrorEntity;
import com.nobroker.userlogin.entities.UserEntity;
import com.nobroker.userlogin.services.JWTService;
import com.nobroker.userlogin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ProfileController implements ErrorController {

    @Autowired
    UserService userService;
    @Autowired
    JWTService jwtService;

    public static class InvalidUserException extends RuntimeException {
        public InvalidUserException() {
            super("Invalid request");
        }
    }



    @GetMapping("/signin")
    public ModelAndView home(){
             ModelAndView mv = new ModelAndView("home");
             return mv;
    }

    @PostMapping("/signin")
    public ModelAndView dashboard(@RequestParam("username") String username,
                                  @RequestParam("password") String password){
            ModelAndView mv = new ModelAndView();
            UserEntity user = null;
            try {
                 user = userService.verifyUser(username, password);
            }
            catch(RuntimeException e){
                mv.addObject("error",e.getMessage());
                mv.setViewName("home");
                return mv;
            }

            if(user.getRole().equals("user")){
                mv.addObject("username", username);
                mv.setViewName("dashboard");
            }
            else{
                mv.addObject("users", userService.findAll());
                mv.setViewName("superuser");
            }
            return mv;
    }

    // can be accessed from anywhere
    @GetMapping("/showdashboard")
    ModelAndView show(){
            ModelAndView mv = new ModelAndView("superuser");
            mv.addObject("users",userService.findAll());
            return mv;
    }

    @PostMapping("/registerUser")
    ModelAndView registerUser(@RequestParam("username") String username,
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
    String deleteUser(@RequestParam("username") String username){

            userService.deleteUser(username);
            //mv.addObject("jwtToken",jwtToken);
            return "redirect:/showdashboard";
    }

    @PostMapping("/changeRole")
    ModelAndView changeRole(@RequestParam("username") String username,
                            @RequestParam("role") String role){
            userService.upgradeUserRole(username,role);
            ModelAndView mv = new ModelAndView();
            mv.addObject("users", userService.findAll());
            mv.setViewName("superuser");
            return mv;
    }

    @ExceptionHandler({RuntimeException.class})
    ResponseEntity<ErrorEntity> handleExceptions(RuntimeException exception){
            String message = exception.getMessage();
            HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;

            if(exception instanceof UserService.UserNotFoundException){
                errorStatus = HttpStatus.NOT_FOUND;
            }
            return new ResponseEntity<ErrorEntity>(ErrorEntity.from(message), errorStatus);
    }

}
