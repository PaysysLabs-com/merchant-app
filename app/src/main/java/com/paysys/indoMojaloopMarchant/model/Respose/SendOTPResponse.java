package com.paysys.indoMojaloopMarchant.model.Respose;

public class SendOTPResponse {
    private String message;

    private String token;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getToken ()
    {
        return token;
    }

    public void setToken (String token)
    {
        this.token = token;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", token = "+token+"]";
    }
}
