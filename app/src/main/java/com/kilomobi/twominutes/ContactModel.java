package com.kilomobi.twominutes;

/**
 * Created by macbookpro on 10/10/2015.
 */
public class ContactModel {
    private  String mContactName="";
    private  String mContactPhone="";
    private  String Url="";

    /*********** Set Methods ******************/

    public void setContactName(String contactName) {
        this.mContactName = contactName;
    }

    public void setContactPhone(String contactPhone)
    {
        this.mContactPhone = contactPhone;
    }

    /*********** Get Methods ****************/

    public String getContactName()
    {
        return this.mContactName;
    }

    public String getContactPhone()
    {
        return this.mContactPhone;
    }
}
