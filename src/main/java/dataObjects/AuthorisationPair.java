package dataObjects;

public class AuthorisationPair {
    public int id;
    public String name;
    public String[] members;
    public int authorisationLevel;
    AuthorisationPair(int id, String name, String[] members, int authorisationLevel)
    {
        this.id=id; //ID SHOULD EQUAL INDEX;
        this.name=name;
        this.members=members;
        this.authorisationLevel=authorisationLevel;
    }
}
