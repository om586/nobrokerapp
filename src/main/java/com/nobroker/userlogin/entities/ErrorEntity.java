package com.nobroker.userlogin.entities;

import java.util.ArrayList;
import java.util.Arrays;

public class ErrorEntity {

    Errors errors;

    static class Errors{

        public ArrayList<String> getBody() {
            return body;
        }

        ArrayList<String> body;

        private Errors(ArrayList<String> body){
            this.body = body;
        }

    }

    public Errors getErrors() {
        return errors;
    }

    private ErrorEntity(Errors errors){
        this.errors = errors;
    }


    public static ErrorEntity from(String... errorMessages){
        return new ErrorEntity(
                new Errors(
                        new ArrayList<>(Arrays.asList(errorMessages))
                ));
    }
}